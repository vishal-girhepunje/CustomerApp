package com.sunBase.Service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sunBase.Model.Customer;

public interface CustomerService {
	
	  public Customer saveCustomer(Customer customer);
	  
	  public Customer updateCustomer(Long id, Customer customerDetails);
	  
	  public Page<Customer> findAllCustomer(String search, Pageable pageable);
	
	   public Customer getCustomerById(Long id) ;
	
	   public void deleteCustomer(Long id) ;
	   
	   public List<Customer> findByLastName(String value);

}
