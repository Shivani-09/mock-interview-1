package com.example.mockInterview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.mockInterview.model.PrimeModel;

@Repository
public interface PrimeRepository extends JpaRepository<PrimeModel, Long>{
	
	@Query(value = "select * from Prime order by id desc;", nativeQuery = true)
	List<PrimeModel> getPrime();
	
	@Modifying
	@Transactional
	@Query(value = "insert into Prime (input, prime_check, s3_path) Values (:input, :prime_check, :s3_path);", nativeQuery = true)
	int savePrime(@Param ("input") int input, @Param("prime_check") boolean prime_check, @Param("s3_path") String s3Path);
	
	@Modifying
	@Transactional
	@Query(value = "update prime set input = :input, prime_check = :prime_check where id = :id;", nativeQuery = true)
	int updatePrime(@Param ("id") long id, @Param("input") int input, @Param("prime_check") boolean prime_check);
	
	@Modifying
	@Transactional
	@Query(value = "delete from prime where id = :id", nativeQuery = true)
	int deletePrime(@Param("id") long id);
}
