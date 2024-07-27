package com.sunBase.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunBase.Model.Customer;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
	
	   @Query("SELECT c FROM Customer c WHERE c.first_name LIKE %:value%")
	    List<Customer> findByFirstName(@Param("value") String value);

	    @Query("SELECT c FROM Customer c WHERE c.last_name LIKE %:value%")
	    List<Customer> findByLastName(@Param("value") String value);

	    @Query("SELECT c FROM Customer c WHERE c.city LIKE %:value%")
	    List<Customer> findByCity(@Param("value") String value);

	    @Query("SELECT c FROM Customer c WHERE c.phone LIKE %:value%")
	    List<Customer> findByPhone(@Param("value") String value);
	   
	  
	
	Optional<Customer> findByEmail(String email);
	  
	  Page<Customer> findAll(Pageable pageable);
	    Page<Customer> findByFirstNameContaining(String search, Pageable pageable);
}