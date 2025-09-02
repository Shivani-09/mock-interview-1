package com.example.mockInterview.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.mockInterview.model.SwapModel;
import com.example.mockInterview.repository.SwapRepository;
import com.example.mockInterview.service.SwapService;
import com.example.mockInterview.service.UpdateSwapRequest;

@CrossOrigin("*")
@RestController
@RequestMapping("/swap")
public class SwapController {

	private final SwapRepository swapRepository;

	@Autowired
	private SwapService swapService;

	SwapController(SwapRepository swapRepository) {
		this.swapRepository = swapRepository;
	}

	// -- get method1--

	@GetMapping
	public List<SwapModel> getSwapList() {

		return swapService.getSwapJava();
	}

	// -- get method2--
	@GetMapping("/sql")
	public List<SwapModel> getUsingNativeQuery() {

		return swapRepository.getSwappedNumsUsingNativeQuery();
	}

	// -- save method1--
//	@PostMapping
//	public ResponseEntity<SwapModel> saveSwapList(@RequestBody SwapDto request) {
//
//		long original_number1 = request.getOriginal_number1();
//		long original_number2 = request.getOriginal_number2();
//
//		if (original_number1 == 0 || original_number2 == 0) {
//			return ResponseEntity.badRequest().build();
//		}
//
//		SwapModel output = swapService.saveSwapJava(original_number1, original_number2);
//		return new ResponseEntity<>(output, HttpStatus.CREATED);
//	}

	// -- save method2--
	@PostMapping("/sql")
	public Integer saveUsingNativeQuery(@RequestBody SwapDto request) {
		
		long num1 = request.getOriginal_number1();
		long num2 = request.getOriginal_number2();
		return swapService.saveUsingSQL(num1, num2);
	}

	// -- update method1--

	 @PutMapping("/sql")
	    public ResponseEntity<Void> updateSwapList(@RequestBody UpdateSwapRequest request) {
	        try {
	            swapService.updateSwapNativeQuery(request.getId(), request.getSwapDto());
	            return new ResponseEntity<>(HttpStatus.OK);
	        } catch (RuntimeException e) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    }

	// -- update method2--

//	@PutMapping("/session")
//	public String updateUsingSession(long id, long num1, long num2) {
//		
//		return swapService.updateUsingSession(id, num1, num2);
//	}
	
	//--update method 4 -- using pathvariable---
	
//	@PutMapping("/{id}/sql")
//    public ResponseEntity<Void> updateSwapPathVariable(@PathVariable Long id, @RequestBody SwapDto swapDto) {
//        try {
//            swapService.updateSwapNativeQuery(id, swapDto);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (RuntimeException e) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
	// -- delete method1--

	@DeleteMapping("/sql")
	public Integer deleteSwapList(@RequestBody SwapDto request) {

		return swapService.deleteSwapNativeQuery(request.getId());
	}
	
//	// -- delete method2--
//	@DeleteMapping("/byid")
//	public String deleteByID(long id) {
//		
//		swapRepository.deleteById(id);
//		return "deleted!";
//	}
	
	//--upload for swap into s3 method---
	
	@PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
        }
        try {
            swapService.saveSwapFromFile(file);
            return new ResponseEntity<>("File uploaded and data saved successfully!", HttpStatus.OK);
        } catch (IOException e) {
            System.err.println("Error processing file upload: " + e.getMessage());
            return new ResponseEntity<>("Failed to process file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
