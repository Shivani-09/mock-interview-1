package com.example.mockInterview.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.mockInterview.model.PrimeModel;
import com.example.mockInterview.service.CSVHelper;
import com.example.mockInterview.service.FileParser;
import com.example.mockInterview.service.PrimeService;
import com.example.mockInterview.service.S3_Service;

@CrossOrigin(origins = "http://localhost:8089")
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
		
		int result = primeService.savePrimeUsingNativeQuery(request.getInput(),null);
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
	
	@Autowired
	private S3_Service s3_Service;
	
	@Autowired
	private FileParser fileParser;
	
	@PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 1. Upload the file to S3 and get the path
            String s3Path = s3_Service.uploadFile(file.getBytes(), file.getOriginalFilename());

            // 2. Parse the file and get the list of PrimeModels
            List<PrimeModel> primesFromFile = fileParser.parseFile(file.getBytes(), file.getOriginalFilename());

            // 3. Save each PrimeModel with the correct s3Path
            for (PrimeModel prime : primesFromFile) {
                primeService.savePrimeUsingNativeQuery(prime.getInput(), s3Path);
            }

            return ResponseEntity.ok("File uploaded and numbers processed successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload and process file: " + e.getMessage());
        }
    }
	
}
