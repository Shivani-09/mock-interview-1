package com.example.mockInterview.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mockInterview.exceptions.ResourceNotFoundException;
import com.example.mockInterview.model.FactorialModel;
import com.example.mockInterview.repository.FactorialRepository;

@Service
public class FactorialService {

	@Autowired
	private FactorialRepository factorialRepository;

	public List<FactorialModel> getFactorialResult() {
		return factorialRepository.getFactNumUsingNativeQuery();
	}

	public int  saveFactorialResult(int factInput) {

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
