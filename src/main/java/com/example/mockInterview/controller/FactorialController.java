package com.example.mockInterview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mockInterview.exceptions.ResourceNotFoundException;
import com.example.mockInterview.model.FactorialModel;
import com.example.mockInterview.service.FactorialService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class FactorialController {

	@Autowired
	private FactorialService factorialService;

	@GetMapping("/factorial")
	public List<FactorialModel> getFactResult() {
		return factorialService.getFactorialResult();
	}

	@PostMapping("/factorial")
	public ResponseEntity<Integer> saveFactResult(@RequestBody FactorialDto request) {

		Integer result = factorialService.saveFactorialResult(request.getInput());

		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@PutMapping("/factorial")
	public ResponseEntity<Integer> updateFactResult(@RequestBody FactorialDto request) {

		Integer factorialModel = factorialService.updateFactorialResult(request.getId(), request.getInput());

		return ResponseEntity.ok(factorialModel);
	}
	
	@PutMapping("/factorial/UsingNativeQuery")
	public ResponseEntity<Integer> updateFactUsingNativeQuery(@RequestBody FactorialDto request) {
		
		Integer result = factorialService.updateFactorialResult(request.getId(), request.getInput());
		
		return ResponseEntity.ok(result);
	}
	
	@PutMapping("/factorial/UsingSession")
	public ResponseEntity<String> updateFactUsingSession(@RequestBody FactorialDto request) {
		
		String result = factorialService.updateUsingEntity(request.getId(), request.getInput());
		
		return ResponseEntity.ok(result);
	}	

	@DeleteMapping("/factorial")
	public ResponseEntity<Void> deleteFactResult(@RequestBody FactorialDto request) {

		factorialService.deleteFactorialResult(request.getId());
		return ResponseEntity.noContent().build();
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}
}
