package com.example.mockInterview.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "Prime")
public class PrimeModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long Id;
	
	private int input;
	private boolean primeCheck;
	
	@Column(name = "s3_path")
    private String s3Path;
	
	public long getId() {
		return Id;
	}
	public void setId(long id) {
		Id = id;
	}
	public int getInput() {
		return input;
	}
	public void setInput(int input) {
		this.input = input;
	}
	public boolean isPrimeCheck() {
		return primeCheck;
	}
	public void setPrimeCheck(boolean primeCheck) {
		this.primeCheck = primeCheck;
	}
	public PrimeModel(int input, boolean primeCheck) {
		this.input = input;
		this.primeCheck = primeCheck;
	}
	
	public PrimeModel() {
		
	}
	public String getS3Path() {
		return s3Path;
	}
	public void setS3Path(String s3Path) {
		this.s3Path = s3Path;
	}
	public PrimeModel(int input, boolean primeCheck, String s3Path) {
		this.input = input;
		this.primeCheck = primeCheck;
		this.s3Path = s3Path;
	}
	
	
	
	
}
