package com.example.mockInterview.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mockInterview.model.FibonacciModel;
import com.example.mockInterview.repository.FibonacciRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class FibonacciService {

	@Autowired
	public final FibonacciRepository fibonacciRepository;

	public FibonacciService(FibonacciRepository fibonacciRepository) {
		this.fibonacciRepository = fibonacciRepository;
	}

	public List<FibonacciModel> getFiboSeries() {
		
		return fibonacciRepository.getFiboSeriesUsingNativeQuery();
	}
	
	public FibonacciModel addAndSaveFibo(int n) {
		long fibNumber = createFiboIterationNum(n);
		FibonacciModel entity = new FibonacciModel( n, fibNumber);
		return fibonacciRepository.save(entity);
	}
	
	@PersistenceContext
	public EntityManager entityManager;
	
	@Transactional
	public String updateFiboSeries(long id, int n) {
		Optional<FibonacciModel> fiboNumber = fibonacciRepository.findById(id);
		
		if(fiboNumber.isPresent()) {
			FibonacciModel fiboNum = fiboNumber.get();
			fiboNum.setFibNumber(createFiboIterationNum(n));
			fiboNum.setN(n);
			
			Session session = (Session) entityManager;
			session.merge(fiboNum);

			return "updated successfully!";
			
		} else {
			return "id doesn't exist";
		}
	}
	
	public String deleteFiboSeries(long id) {
		fibonacciRepository.deleteUsingNativeQuery(id);
		return "deleted successfully!";
	}
	
	public long createFiboIterationNum(int n) {

		if (n <= 0) {
			return n;
		}
		long a = 0;
		long b = 1;
		for (int i = 2; i < n; i++) {
			long temp;
			temp = a + b;
			a = b;
			b = temp;
		}
		return b;

	}

}
