package com.example.mockInterview.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mockInterview.controller.InputDto;
import com.example.mockInterview.model.Palindrome;
import com.example.mockInterview.repository.PalindromeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class PalindromeService {

	@Autowired
	private PalindromeRepository palindromeRepository;
	
	@PersistenceContext
	private EntityManager entityManager;

	public boolean isPalindrome(InputDto input) {
		if (input == null) {
			return false;
		}
		String cleanString = (input.getInputDto()).toLowerCase().replaceAll("[^a-z0-9]", "");
		String reversedString = new StringBuilder(cleanString).reverse().toString();

		return cleanString.equals(reversedString);
	}

	public Palindrome savePalindrome(InputDto inputDto) {
		if (inputDto == null) {
			return null;
		}
		String input = inputDto.getInputDto();
		boolean isPalindrome = isPalindrome(inputDto);

		String reversedString = new StringBuilder(inputDto.getInputDto()).reverse().toString();
		Palindrome entity = new Palindrome(input, reversedString, isPalindrome);

		return palindromeRepository.save(entity);

	}

	public List<Palindrome> getPalindrome() {
		return palindromeRepository.findAllByOrderByIdDesc();
	}

	@Transactional
	public String updatePalindrome(long id, InputDto inputDto) {

		Optional<Palindrome> existingUserOptional = palindromeRepository.findById(id);

		if (existingUserOptional.isEmpty()) {
			return "Record not found!";
		}

		String input = inputDto.getInputDto();
		boolean isPalindrome = isPalindrome(inputDto);
		String reversedString = new StringBuilder(input).reverse().toString();

		Palindrome record = existingUserOptional.get();
		record.setOriginalString(input);
		record.setReverseString(reversedString);
		record.setPalindrome(isPalindrome);

		palindromeRepository.save(record);

		return "Updated successfully!";

	}
	
	@Transactional
	public String edit(Long id, String inputDto) {
		Palindrome record = palindromeRepository.findById(id).orElse(null);
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
	
	public String deletePalindrome(long id) {
		palindromeRepository.deleteById(id);
		return "Deleted successfully!";
	}
}

