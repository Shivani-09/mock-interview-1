package com.example.mockInterview.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mockInterview.exceptions.ResourceNotFoundException;
import com.example.mockInterview.model.FactorialModel;
import com.example.mockInterview.repository.FactorialRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class FactorialService {

	@Autowired
	private FactorialRepository factorialRepository;

	@PersistenceContext
	private EntityManager entityManager;
	
	public List<FactorialModel> getFactorialResult() {
		return factorialRepository.getFactNumUsingNativeQuery();
	}

	public int saveFactorialResult(int factInput) {

		long factorialOutput = createFactorialOutput(factInput);
		
		return factorialRepository.saveFactNumUsingNativeQuery(factInput, factorialOutput);
		 
	}

	public Integer updateFactorialResult(long factId, int factInput) {
		Optional<FactorialModel> searchID = factorialRepository.findById(factId);

		if (searchID.isEmpty()) {
			throw new ResourceNotFoundException("Factorial record with ID " + factId + " not found.");
		}
		
			long factOutput = createFactorialOutput(factInput);
			
			FactorialModel factorialModel = searchID.get();
			factorialModel.setFactorialInput(factInput);
			factorialModel.setFactorialResult(factOutput);
			
			return factorialRepository.updateFactNumUsingNativeQuery(factId, factInput, factOutput);
		
	}
	
	public int updateFactUsingNativeQuery(Long id, int entity) {
		
		long output = createFactorialOutput(entity);
		
		int result = factorialRepository.updateFactNumUsingNativeQuery(id, entity, output);
		return result;
	}
	
	@Transactional
	public String updateUsingEntity (long id, int input) {
		
		Optional<FactorialModel> searchId = factorialRepository.findById(id);	
		
		if(searchId.isPresent()) {
			
			FactorialModel existingRow = searchId.get() ;
			
			existingRow.setFactorialInput(input);
			existingRow.setFactorialResult(createFactorialOutput(input));

			//no need to use merge here, it directly sets the result value 
			
			return "updated using session!";
		} else {
			return "record not found!";
		}
	
	}
		
	public String deleteFactorialResult(long factID) {
		
		factorialRepository.deleteFactNumUsingNativeQuery(factID);
		
		return "deleted!";
	}

	public long createFactorialOutput(int factInput) {

		if (factInput < 0) {
			throw new IllegalArgumentException("Factorial cannot be calculated for negative numbers");
		}
		
		long result=1;
		for (int i = 1; i <= factInput; i++) {
			result *= i;
		}
		return result;
	}
}
