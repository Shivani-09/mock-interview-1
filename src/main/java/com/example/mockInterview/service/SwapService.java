package com.example.mockInterview.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.mockInterview.controller.SwapDto;
import com.example.mockInterview.model.SwapModel;
import com.example.mockInterview.repository.SwapRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.websocket.Session;

@Service
public class SwapService {

	@Autowired
	private SwapRepository swapRepository;

	private Object swapped_number1;

	public List<SwapModel> getSwapJava() {
		return swapRepository.findAll();
	}

	public SwapModel saveSwapJava(long original_number1, long original_number2) {

		SwapModel output = createSwapNums(original_number1, original_number2);

		return swapRepository.save(output);
	}

	public int saveUsingSQL(long num1, long num2) {

		long swap1 = num2;
		long swap2 = num1;
		return swapRepository.saveSwappedNumUsingNativeQuery(num1, num2, swap1, swap2);
	}

	public void updateSwapNativeQuery(long id, SwapDto request) {

		Optional<SwapModel> existingId = swapRepository.findById(id);

		SwapModel existingRow = existingId.get();
		 existingRow.setOriginal_number1(request.getOriginal_number1());
		 existingRow.setOriginal_number2(request.getOriginal_number2());

		 existingRow.setSwapped_number1(request.getOriginal_number2());
		 existingRow.setSwapped_number2(request.getOriginal_number1());
		 swapRepository.updateSwappedNumUsingNativeQuery(
	                id,
	                existingRow.getOriginal_number1(),
	                existingRow.getOriginal_number2(),
	                existingRow.getSwapped_number1(),
	                existingRow.getSwapped_number2()
	            );}

	
	//---method 3.save for update---
//	@Transactional
//    public String update(Long id, InputDto inputDto) {
//        Optional<UserRecord> optionalRecord = userRepository.findById(id);
//
//        if (optionalRecord.isEmpty()) {
//            return "Record not found";
//        }
//
//        UserRecord record = optionalRecord.get();
//        record.setOriginalNumber1(inputDto.getOriginalNumber1());
//        record.setOriginalNumber2(inputDto.getOriginalNumber2());
//
//        // Perform the swap logic here
//        record.setSwappedNumber1(inputDto.getOriginalNumber2());
//        record.setSwappedNumber2(inputDto.getOriginalNumber1());
//
//        userRepository.save(record);
//        return "Successfully updated";
//    }
	
	@PersistenceContext
	public EntityManager entityManager;

	public String updateUsingSession(long id, long num1, long num2) {
		Optional<SwapModel> existingId = swapRepository.findById(id);

		if (existingId.isPresent()) {
			SwapModel existingRow = existingId.get();

			existingRow.setOriginal_number1(num1);
			existingRow.setOriginal_number2(num2);

			Session session = (Session) entityManager;
			((EntityManager) session).merge(existingRow);
		}
		return "updated!";
	}

	public int deleteSwapNativeQuery(long id) {
		if (id == 0) {
			return 0;
		}
		return swapRepository.deleteSwappedNumUsingNativeQuery(id);
	}

	public SwapModel createSwapNums(long original_number1, long original_number2) {

		long a = original_number1;
		long b = original_number2;
		a = a + b;
		b = a - b;
		a = a - b;

		SwapModel output = new SwapModel(original_number1, original_number2, a, b);
		return output;
	}

//	------ using s3 cloud -------

	@Autowired
	private FileParser fileParser;

	@Autowired
	private S3_Service s3_Service;

	public void saveSwapFromFile(MultipartFile file) throws IOException {
		// Step 1: Read the file's content into a byte array ONCE
		byte[] fileBytes = file.getBytes();

		// Step 2: Parse the file and get the list of PrimeModels
		List<SwapModel> swapList = fileParser.parseFileWithTwoNumbers(fileBytes, file.getOriginalFilename(),
				(input1, input2) -> {
					SwapModel swapNumbers = new SwapModel();
					swapNumbers.setOriginal_number1(input1);
					swapNumbers.setOriginal_number2(input2);
					swapNumbers.setSwapped_number1(input2);
					swapNumbers.setSwapped_number2(input1);
					return swapNumbers;
				});
		// Step 3: Upload the file to S3 and get the path
		String s3StoragePath = s3_Service.uploadFile(fileBytes, file.getOriginalFilename());

		// Step 4: Save each number from the list to the database with the s3Path
		for (SwapModel swap : swapList) {
			this.saveSwapUsingNativeQuery(swap, s3StoragePath);
		}
	}
	
    public SwapService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	
	private JdbcTemplate jdbcTemplate;
	
	private void saveSwapUsingNativeQuery(SwapModel swap, String s3Path) {
		
		String query = "insert into swap (original_number1, original_number2, swapped_number1, swapped_number2, s3_path) VALUES (?, ?, ?, ?, ?)";
		
		jdbcTemplate.update(query,
				swap.getOriginal_number1(),
				swap.getOriginal_number2(),
				swap.getSwapped_number1(),
				swap.getSwapped_number2(),
				extractedS3FileName(s3Path)
				);
	}
	
	public String extractedS3FileName(String s3Path) {
		
		if (s3Path == null || s3Path.isEmpty()) {
            return "";
        }
		int lastSlashIndex = s3Path.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < s3Path.length() - 1) {
            return s3Path.substring(lastSlashIndex + 1);
        }
        return s3Path; 
	}
}
