package com.example.mockInterview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.mockInterview.model.FibonacciModel;

@Repository
public interface FibonacciRepository extends JpaRepository<FibonacciModel, Long> {

	@Query(value = "select * from fibonacci_results order by fib_id desc;", nativeQuery = true)
	List<FibonacciModel>getFiboSeriesUsingNativeQuery();

	@Modifying
	@Transactional
	@Query(value = "insert into fibonacci_results (n, fib_number) Values(:n, :fibNumber))", nativeQuery = true)
	int saveUsingNativeQuery(@Param("n") int n, @Param("fibNumber") Long fibNumber);

	@Modifying
	@Transactional
	@Query(value = "UPDATE fibonacci_results SET n = :n, fib_number = :fibNumber WHERE id = :id", nativeQuery = true)
	int updateUsingNativeQuery(@Param("id") Long id, @Param("n") int n, @Param("fibNumber") Long fibNumber);

	@Modifying
    @Transactional
    @Query(value = "DELETE FROM fibonacci_results WHERE fib_id = :id", nativeQuery = true)
    int deleteUsingNativeQuery(@Param("id") Long id);

}
