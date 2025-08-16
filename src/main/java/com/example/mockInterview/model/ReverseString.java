package com.example.mockInterview.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="reverse_string")
public class ReverseString {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String originalString;
	private String reverseString;
	
	public ReverseString() {
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

	public ReverseString(String originalString, String reverseString) {
		this.originalString = originalString;
		this.reverseString = reverseString;
	}
	
}