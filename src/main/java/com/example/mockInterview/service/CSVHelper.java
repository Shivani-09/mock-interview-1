package com.example.mockInterview.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.mockInterview.model.PrimeModel;


@Service
public class CSVHelper {

	public static String TYPE = "text/csv";

//	static String[] HEADERS = { "input", "prime_check" };
	static String[] HEADERS = { "input"};
	public static boolean hasCSVType(MultipartFile file) {

		if (file.getContentType() != null) {
			return file.getContentType().startsWith(TYPE);
		}
		return false;
	}
		
//		---- in this way we only give input and pass to check_prime func which calculates the result ----
		
	public List<PrimeModel> csvToPrime(InputStream is) {

		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

				CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader(HEADERS)
						.setIgnoreHeaderCase(true).setTrim(true).build());) {

			List<PrimeModel> primeList = new ArrayList<>();
			Iterable<CSVRecord> csvRecords = csvParser.getRecords();

			for (CSVRecord csvRecord : csvRecords) {
				PrimeModel primeNumbers = new PrimeModel(); 
				
				int input = Integer.parseInt(csvRecord.get("input"));
				
                primeNumbers.setInput(input);

                boolean isPrime = checkForPrime(input);
                primeNumbers.setPrimeCheck(isPrime);
                
				primeList.add(primeNumbers);
			}

			return primeList;
		} catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        } catch (Exception e) { 
            throw new RuntimeException("Error processing CSV data: " + e.getMessage(), e);
        }
		
	}
	
	public static boolean checkForPrime(int input) {

		if (input <= 1) {
			return false;
		}

		if (input == 2) {
			return true;
		}

		if (input % 2 == 0) {
			return false;
		}

		for (int i = 3; i < Math.sqrt(input); i++) {

			if (input % i == 0) {
				return false;
			}
		}
		return true;
	}
}
