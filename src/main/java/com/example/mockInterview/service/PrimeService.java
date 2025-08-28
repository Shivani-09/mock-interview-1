package com.example.mockInterview.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

	public int savePrimeUsingNativeQuery(int input) {

		boolean primeCheck = CSVHelper.checkForPrime(input);

		return primeRepository.savePrime(input, primeCheck);
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
	
	@Autowired
	public CSVHelper csvHelper;
	
	public void save(MultipartFile file) {
        try {
            List<PrimeModel> products = csvHelper.csvToPrime(file.getInputStream());
            primeRepository.saveAll(products);
        } catch (IOException e) {
            throw new RuntimeException("fail to store csv data: " + e.getMessage());
        }
    }

    public List<PrimeModel> getAllProducts() {
        return primeRepository.findAll();
    }

}
