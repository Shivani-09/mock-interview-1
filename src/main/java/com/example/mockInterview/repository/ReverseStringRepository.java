package com.example.mockInterview.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.mockInterview.model.ReverseString;

@Repository
public interface ReverseStringRepository extends JpaRepository<ReverseString, Long>{

	List<ReverseString> findAllByOrderByIdDesc();
	
	@Modifying
	@Transactional
	@Query(value = "insert into reverse_string (original_string, reverse_string) Values(:original_string, :reverse_string)", nativeQuery = true)
    void addReverseString(@Param("original_string") String originalString, @Param("reverse_string") String reversedString);

	
	
	
//	static final String url = "jdbc:mysql://localhost:3306/mock_interview";
//	static final String username = "root";
//	static final String password = "123456789";
//	@Query
//	public static void reverseStringSave(String a, String b) {
//		String query = "insert into reverse_string (original_string, reverse_string) Values(?,?)";
//		try (Connection conn = DriverManager.getConnection(url,username,password);
//				PreparedStatement preparedStatement = conn.prepareStatement(query))
//			{preparedStatement.setString(1,a);
//			 preparedStatement.setString(2,b);
//			 preparedStatement.executeUpdate();
//			}
//		catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
}
