package com.example.mockInterview.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mockInterview.controller.InputDto;
import com.example.mockInterview.model.ReverseString;
import com.example.mockInterview.repository.ReverseStringRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.hibernate.Session;

@Service
public class ReverseStringService {

	@Autowired
	private ReverseStringRepository reverseStringRepository;

	@PersistenceContext
	private EntityManager entityManager;

	public ReverseString reverseAndSave(String inputDto) {

		String reversedString = new StringBuilder(inputDto).reverse().toString();
		ReverseString entity = new ReverseString(inputDto, reversedString);
		return reverseStringRepository.save(entity);
	}

	public List<ReverseString> getString() {
		return reverseStringRepository.findAllByOrderByIdDesc();

	}

	public ReverseString update(long id, InputDto inputDto) {
		Optional<ReverseString> existingUserOptional = reverseStringRepository.findById(id);

		if (existingUserOptional.isPresent()) {
			ReverseString existingUser = existingUserOptional.get();
			existingUser.setOriginalString(inputDto.getInputDto());
			existingUser.setReverseString(new StringBuilder(inputDto.getInputDto()).reverse().toString());
			return reverseStringRepository.save(existingUser); // use upsert-mysql
		} else {
			return null;
		}
	}

	@Transactional
	public String edit(Long id, String inputDto) {
		ReverseString record = reverseStringRepository.findById(id).orElse(null);
		if (record == null) {
			return "Record not found";
		}

		String reversed = new StringBuilder(inputDto).reverse().toString();
		record.setOriginalString(inputDto);
		record.setReverseString(reversed);

		Session session = (Session) entityManager;
		session.merge(record);

		return "Successfully updated using session.merge()";
	}

	public String delete(long id) {
		reverseStringRepository.deleteById(id);
		return "Successfully deleted.!";
	}
}