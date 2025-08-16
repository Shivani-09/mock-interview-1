package com.example.mockInterview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mockInterview.model.Palindrome;

public interface PalindromeRepository extends JpaRepository<Palindrome, Long>{
	
	List<Palindrome> findAllByOrderByIdDesc();
	
}
