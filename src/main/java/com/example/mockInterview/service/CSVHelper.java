package com.example.mockInterview.service;

import com.example.mockInterview.model.PrimeModel; 
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVHelper {

//	@Autowired
//	private PrimeRepository primeRepository; // Your repository for saving data

//	public static final String TYPE = "text/csv";
	public static final String TYPE_CSV = "text/csv";
	public static final String TYPE_TXT = "text/plain";
	public static final String TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";


	// You can customize the CSV format here if needed
//	private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder().setHeader() // Assuming your CSV has a
//																						// header row
//			.build();
//
//	public void processAndSaveCsv(MultipartFile file) {
//		// Use try-with-resources to ensure the parser is always closed
//		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
//				CSVParser csvParser = new CSVParser(fileReader, CSV_FORMAT)) {
//
//			// Define the batch size for saving to the database
//			final int BATCH_SIZE = 10000;
//			List<PrimeModel> primeBatch = new ArrayList<>(BATCH_SIZE);
//
//			// Iterate over the records directly from the parser, keeping memory low
//			for (CSVRecord csvRecord : csvParser) {
//				
//				int inputNumber = Integer.parseInt(csvRecord.get("input"));
//				boolean isPrime = checkForPrime(inputNumber);
//				PrimeModel prime = new PrimeModel(inputNumber, isPrime);
//				
//
//				primeBatch.add(prime);
//
//				// If the batch is full, save to the database
//				if (primeBatch.size() >= BATCH_SIZE) {
//					primeRepository.saveAll(primeBatch);
//					primeBatch.clear(); // Clear the list for the next batch
//				}
//			}
//
//			// Save any remaining records that didn't fill the last batch
//			if (!primeBatch.isEmpty()) {
//				primeRepository.saveAll(primeBatch);
//			}
//
//		} catch (Exception e) {
//			// Log the error and handle it appropriately
//			throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
//		}
//	}

//	static String[] HEADERS = { "input", "prime_check" };
	static String[] HEADERS = { "input"};
	
	public static boolean hasSupportedType(MultipartFile file) {
	    String contentType = file.getContentType();
	    return contentType != null && 
	           (contentType.equals("text/csv") || 
	            contentType.equals("text/plain") ||
	            contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
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
