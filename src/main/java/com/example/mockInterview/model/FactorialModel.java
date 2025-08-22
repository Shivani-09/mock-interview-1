package com.example.mockInterview.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "factorial_results")
public class FactorialModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fact_id")
	private long id;

	@Column(name = "fact_num")
	private int factorialInput;

	@Column(name = "fact_result")
	private long factorialResult;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getFactorialInput() {
		return factorialInput;
	}

	public void setFactorialInput(int factorialInput) {
		this.factorialInput = factorialInput;
	}

	public long getFactorialResult() {
		return factorialResult;
	}

	public void setFactorialResult(long factorialResult) {
		this.factorialResult = factorialResult;
	}

	public FactorialModel(int factorialInput, long factorialResult) {
		this.factorialInput = factorialInput;
		this.factorialResult = factorialResult;
	}

	public FactorialModel() {

	}
}
