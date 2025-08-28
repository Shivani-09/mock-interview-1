package com.example.mockInterview.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mockInterview.model.Palindrome;
import com.example.mockInterview.model.ReverseString;
import com.example.mockInterview.service.PalindromeService;
import com.example.mockInterview.service.ReverseStringService;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UserController {
	
	@Autowired
	public ReverseStringService reverseStringService; 
	
	@GetMapping("/reverse")
	public List<ReverseString> getString() {
		return reverseStringService.getString();
	}
	
//	@PostMapping("/reverse/add")
//	public void addString(@RequestBody Map<String, String> request) {
//		String a = request.get("input");
//		reverseStringService.reverseAndSave(a);
//	}
	
	@PutMapping("/reverse/{id}")
	public ResponseEntity<ReverseString> updatedString(@PathVariable long id, @RequestBody InputDto inputDto){
		ReverseString updatedString = reverseStringService.update(id, inputDto);
		if(updatedString != null) {
			return ResponseEntity.ok(updatedString);
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@PutMapping("/reverse/update")
	public String reverse1(@RequestBody Map<String, String> edit1) {
		Long id = Long.valueOf(edit1.get("id").toString()); 
		String input = edit1.get("inputDto");
		return reverseStringService.edit(id, input);
	}
	
	@DeleteMapping("/reverse/{id}")
	public ResponseEntity<Object> deleteString(@PathVariable long id){
		reverseStringService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@DeleteMapping("/reverse")
	public String deleteString1(@RequestBody Map<String, Object> request){
		Long id = Long.valueOf(request.get("id").toString());
		return reverseStringService.delete(id);
	}
	
	//-- this method gives sql query function--
	@PostMapping("/reverse/SQL")
    public ResponseEntity<ReverseString> reverseString(@RequestBody InputDto request) {
        // The controller's responsibility is to delegate the business logic.
        // It calls the service layer to reverse the string and handle the saving.
        String reversedString = reverseStringService.reverseAndSaveString(request.getInputDto());
        
        // Return a successful response entity containing the reversed string.
        return ResponseEntity.ok(new ReverseString(request.getInputDto(), reversedString));
    }
	
	
	//Palindrome starts here
	
	@Autowired
	public PalindromeService palindromeService;
	
	@GetMapping("/palindrome")
	public List<Palindrome> getPalindrome() {
		return palindromeService.getPalindrome();
	}
	
	@PostMapping("/palindrome")
	public Palindrome addPalindrome1(@RequestBody InputDto input){
		return palindromeService.savePalindrome(input);
	}
	
	public ResponseEntity<Palindrome> addPalindrome(@RequestBody InputDto inputDto){
		Palindrome palindrome = palindromeService.savePalindrome(inputDto);
        if (palindrome != null) {
            return ResponseEntity.ok(palindrome);
        } else {
            return ResponseEntity.noContent().build();
        }
	}
	
	@PutMapping("/palindrome/{id}")
	public String updatePalindrome(@PathVariable long id, @RequestBody InputDto inputDto) {
		return palindromeService.updatePalindrome(id, inputDto);
	}
	
	@PutMapping("/palindrome")
	public String updatePalindrome(@RequestBody Long id, InputDto input ) {
		return palindromeService.updatePalindrome(id, input);
	}
	
	@DeleteMapping("/palindrome")
	public String deleteString(@RequestBody Map<String, Object> request){
		Long id = Long.valueOf(request.get("id").toString());
		return palindromeService.deletePalindrome(id);
	}
}