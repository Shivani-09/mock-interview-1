package com.example.mockInterview.controller;

public class PrimeDto {
	
	private long id;
	private int input;
	private boolean primeCheck;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public PrimeDto(int input, boolean primeCheck) {
		this.input = input;
		this.primeCheck = primeCheck;
	}
}
