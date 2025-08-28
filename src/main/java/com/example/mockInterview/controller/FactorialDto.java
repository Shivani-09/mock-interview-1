package com.example.mockInterview.controller;

public class FactorialDto {

	private long id;
	private int input;
	private long FactResult;
	
	public long getFactResult() {
		return FactResult;
	}
	public void setFactResult(long FactResult) {
		this.FactResult = FactResult;
	}
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
	
	
}
