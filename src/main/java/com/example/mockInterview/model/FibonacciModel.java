package com.example.mockInterview.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fibonacci_results")
public class FibonacciModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name ="fib_id")
	private Long id;
	
	@Column(name ="n")
	private int n;
	
	@Column(name ="fib_number")
	private Long fibNumber;

	public FibonacciModel() {

	}

	public FibonacciModel(int n, Long fibNumber) {
		this.n = n;
		this.fibNumber = fibNumber;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public Long getFibNumber() {
		return fibNumber;
	}

	public void setFibNumber(Long fibNumber) {
		this.fibNumber = fibNumber;
	}

}
