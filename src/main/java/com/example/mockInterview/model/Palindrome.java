package com.example.mockInterview.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="palindrome_check")
public class Palindrome {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String originalString;
	private String reverseString;
	private Boolean palindrome;
	
	public Palindrome() {
    }
	
	public Boolean getPalindrome() {
		return palindrome;
	}

	public void setPalindrome(Boolean palindrome) {
		this.palindrome = palindrome;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getOriginalString() {
		return originalString;
	}
	public void setOriginalString(String string) {
		this.originalString = string;
	}
	public String getReverseString() {
		return reverseString;
	}
	public void setReverseString(String reverseString) {
		this.reverseString = reverseString;
	}

	public Palindrome(String originalString, String reverseString, Boolean palindrome) {
		this.originalString = originalString;
		this.reverseString = reverseString;
		this.palindrome = palindrome;
	}
	
}
