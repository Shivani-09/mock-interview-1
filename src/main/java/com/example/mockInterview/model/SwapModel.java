package com.example.mockInterview.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Swap")
public class SwapModel {

	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "original_number1")
	private long original_number1;
	@Column(name = "original_number2")
	private long original_number2;
	@Column(name = "swapped_number1")
	private long swapped_number1;
	@Column(name = "swapped_number2")
	private long swapped_number2;
	@Column(name = "s3_Path")
	private String s3_path;
	
	public String getS3_path() {
		return s3_path;
	}
	public void setS3_path(String s3_path) {
		this.s3_path = s3_path;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOriginal_number1() {
		return original_number1;
	}
	public void setOriginal_number1(long original_number1) {
		this.original_number1 = original_number1;
	}
	public long getOriginal_number2() {
		return original_number2;
	}
	public void setOriginal_number2(long original_number2) {
		this.original_number2 = original_number2;
	}
	public long getSwapped_number1() {
		return swapped_number1;
	}
	public void setSwapped_number1(long swapped_number1) {
		this.swapped_number1 = swapped_number1;
	}
	public long getSwapped_number2() {
		return swapped_number2;
	}
	public void setSwapped_number2(long swapped_number2) {
		this.swapped_number2 = swapped_number2;
	}
	public SwapModel(long original_number1, long original_number2, long swapped_number1, long swapped_number2) {
		super();
		this.original_number1 = original_number1;
		this.original_number2 = original_number2;
		this.swapped_number1 = swapped_number1;
		this.swapped_number2 = swapped_number2;
	}
	public SwapModel(long original_number1, long original_number2, long swapped_number1, long swapped_number2,
			String s3_path) {
		super();
		this.original_number1 = original_number1;
		this.original_number2 = original_number2;
		this.swapped_number1 = swapped_number1;
		this.swapped_number2 = swapped_number2;
		this.s3_path = s3_path;
	}
	public SwapModel() {}
}
