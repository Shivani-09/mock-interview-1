package com.example.mockInterview.model;

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
	
	
	
}
