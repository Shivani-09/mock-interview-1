package com.example.mockInterview.service;


import com.example.mockInterview.service.FileParser; // Use the new class
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.mockInterview.model.PrimeModel;
import com.example.mockInterview.repository.PrimeRepository;

@Service
public class PrimeService {

	@Autowired
	public PrimeRepository primeRepository;

	public List<PrimeModel> getPrimeUsingNativeQuery() {
		return primeRepository.getPrime();
	}

	public int savePrimeUsingNativeQuery(int input, String s3path) {

		boolean primeCheck = CSVHelper.checkForPrime(input);
		return primeRepository.savePrime(input, primeCheck, s3path);
	}

	public String updatePrimeUsingNativeQuery(long id, int input) {

		Optional<PrimeModel> searchId = primeRepository.findById(id);

		if (searchId.isEmpty()) {
			return "record not found!";
		} else {

			PrimeModel existingRow = searchId.get();
			existingRow.setInput(input);
			existingRow.setPrimeCheck(CSVHelper.checkForPrime(input));

			primeRepository.save(existingRow);
			return "updated!";
		}
	}

	public int updateDB(long id, int input) {

		boolean result = CSVHelper.checkForPrime(input);
		return primeRepository.updatePrime(id, input, result);
	}

	public String deletePrime(long id) {
		
			primeRepository.deletePrime(id);
			System.out.println("service done");

			return "deleted!";
		} 
	
	
	
//	--------- Uploadfile concept -------
//	
//	@Autowired
//	public CSVHelper csvHelper;
//	
//	public void save(MultipartFile file) {
//        try {
//            List<PrimeModel> products = csvHelper.csvToPrime(file.getInputStream());
//            primeRepository.saveAll(products);
//        } catch (IOException e) {
//            throw new RuntimeException("fail to store csv data: " + e.getMessage());
//        }
//    }
//
//    public List<PrimeModel> getAllProducts() {
//        return primeRepository.findAll();
//    }
    
    
//	------ using s3 cloud -------

	    @Autowired
	    private FileParser fileParser;
	    
	    @Autowired
	    private S3_Service s3_Service;
	    
	    @Transactional
	    public void savePrimesFromFile(MultipartFile file) throws IOException {
	        // Step 1: Read the file's content into a byte array ONCE
	        byte[] fileBytes = file.getBytes();
	        
	        // Step 2: Parse the file and get the list of PrimeModels
	        List<PrimeModel> primes = fileParser.parseFile(fileBytes, file.getOriginalFilename());
	        
	        // Step 3: Upload the file to S3 and get the path
	        String s3StoragePath = s3_Service.uploadFile(fileBytes, file.getOriginalFilename());
	        
	        // Step 4: Save each number from the list to the database with the s3Path
	        for (PrimeModel prime : primes) {
	            this.savePrimeUsingNativeQuery(prime.getInput(), s3StoragePath);
	        }
	    }
}
