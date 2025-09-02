package com.example.mockInterview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.mockInterview.model.SwapModel;

@Repository
public interface SwapRepository extends JpaRepository<SwapModel, Long>{

	@Query(value = "select * from swap order by id desc;", nativeQuery = true)
	List<SwapModel>getSwappedNumsUsingNativeQuery();
	
	@Modifying
	@Transactional
	@Query(value = "insert into swap (original_number1, original_number2, swapped_number1, swapped_number2) Values(:original_number1, :original_number2, :swapped_number1, :swapped_number2)", nativeQuery = true)
	int saveSwappedNumUsingNativeQuery(@Param("original_number1") Long original_number1, @Param("original_number2") Long original_number2, @Param("swapped_number1") Long swapped_number1, @Param("swapped_number2") Long swapped_number2);
	
	@Modifying
	@Transactional
	@Query(value = "update swap  set original_number1 = :original_number1, original_number2 = :original_number2, swapped_number1 = :swapped_number1, swapped_number2 = :swapped_number2  where id = :id", nativeQuery = true)
	int updateSwappedNumUsingNativeQuery(@Param("id") Long id, @Param("original_number1") Long original_number1, @Param("original_number2") Long original_number2, @Param("swapped_number1") Long swapped_number1, @Param("swapped_number2") Long swapped_number2);
	
	
	@Modifying
	@Transactional
	@Query(value = "delete from swap where id = :id;", nativeQuery = true)
	int deleteSwappedNumUsingNativeQuery(@Param("id") Long id);

	
}
