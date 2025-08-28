package com.example.mockInterview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.mockInterview.model.PrimeModel;
import com.example.mockInterview.service.CSVHelper;
import com.example.mockInterview.service.PrimeService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class PrimeController {

	@Autowired
	private PrimeService primeService;
	
	public PrimeController(PrimeService primeService) {
		this.primeService = primeService;
	}
	
	@GetMapping("prime")
	public ResponseEntity<List<PrimeModel>> getPrimResponseEntity(){
		return ResponseEntity.ok(primeService.getPrimeUsingNativeQuery());
	}
	
	@PostMapping("prime")
	public ResponseEntity<Integer> saveResponseEntity(@RequestBody PrimeDto request){
		
		int result = primeService.savePrimeUsingNativeQuery(request.getInput());
		return ResponseEntity.ok(result);
	}
	
	@PutMapping("/prime/nativeQuery")
	public ResponseEntity<String> updatResponseEntity(@RequestBody PrimeDto request){
		
		String result = primeService.updatePrimeUsingNativeQuery(request.getId(), request.getInput());
		return ResponseEntity.ok(result);
	}
	
	@PutMapping("/prime/database")
	public ResponseEntity<Integer> updateUsingdb(@RequestBody PrimeDto request){

		int output = primeService.updateDB(request.getId(), request.getInput());
		return ResponseEntity.ok(output);
	}
	
	@DeleteMapping("/prime")
	public ResponseEntity<String> deleteUsingNativeQuery(@RequestBody PrimeDto request){
		
		System.out.println("contorller done");
		return ResponseEntity.ok(primeService.deletePrime(request.getId()));
	}
	
	
//	---- upload file concept ---
	
	
	@PostMapping("/uploadFile")
	public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file) {
	    if (CSVHelper.hasCSVType(file)) { 
	        try {
	            primeService.save(file);
	            return ResponseEntity.status(HttpStatus.OK).body("Uploaded the file successfully: " + file.getOriginalFilename());
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Could not upload the file: " + file.getOriginalFilename() + "!");
	        }
	    }
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a csv file!");
	}
	
}
