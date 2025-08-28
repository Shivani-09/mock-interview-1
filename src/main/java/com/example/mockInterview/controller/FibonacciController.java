package com.example.mockInterview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mockInterview.model.FibonacciModel;
import com.example.mockInterview.service.FibonacciService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class FibonacciController {
	
	@Autowired
	public FibonacciService fibonacciService;
	
	@GetMapping("/fibonacci")
	public List<FibonacciModel> getFibonacci(){
		return fibonacciService.getFiboSeries();
	}
	
	@PostMapping("/fibonacci")
	public ResponseEntity<FibonacciModel> addFibonacci(@RequestBody FibonacciRequestDto inputNum) {
		
		FibonacciModel savedModel = fibonacciService.addAndSaveFibo(inputNum.getN());
        return ResponseEntity.ok(savedModel);
	}
	
	@PutMapping("/fibonacci")
	public String updateFibonacci(@RequestBody FibonacciRequestDto requestDto) {
		
		long id = requestDto.getId();
        int inputNum = requestDto.getN();
		
		fibonacciService.updateFiboSeries(id, inputNum);
		return ("updated successfully!");
	}
	
	@DeleteMapping("/fibonacci")
	public String deleteFibonacci(@RequestBody FibonacciRequestDto requestDto) {
		fibonacciService.deleteFiboSeries(requestDto.getId());
		return "deleted!";
	}
}
