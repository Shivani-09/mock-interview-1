package com.example.mockInterview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.mockInterview.model.FactorialModel;

@Repository
public interface FactorialRepository extends JpaRepository<FactorialModel, Long>{
	
	@Query(value = "select * from factorial_results order by fact_id desc;", nativeQuery = true)
	List<FactorialModel>getFactNumUsingNativeQuery();
	
	@Modifying
	@Transactional
	@Query(value = "insert into factorial_results (fact_num, fact_result) Values(:fact_num, :fact_result)", nativeQuery = true)
	int saveFactNumUsingNativeQuery(@Param("fact_num") int fact_num, @Param("fact_result") Long fact_result);
	
	@Modifying
	@Transactional
	@Query(value = "update factorial_results  set fact_num = :fact_num, fact_result = :fact_result where fact_id = :fact_id", nativeQuery = true)
	int updateFactNumUsingNativeQuery(@Param("fact_id") Long fact_id, @Param("fact_num") int fact_num, @Param("fact_result") Long fact_result);
	
	
	@Modifying
	@Transactional
	@Query(value = "delete from factorial_results where fact_id = :fact_id;", nativeQuery = true)
	int deleteFactNumUsingNativeQuery(@Param("fact_id") long fact_id);
	
}
