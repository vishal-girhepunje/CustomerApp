package com.sunBase.Service;

import java.util.List;
import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.sunBase.Model.Customer;
import com.sunBase.Repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService, UserDetailsService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private RestTemplate restTemplate;

	public Customer saveCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	public Customer updateCustomer(Long id, Customer customerDetails) {
		Optional<Customer> optionalCustomer = customerRepository.findById(id);

		if (!optionalCustomer.isPresent()) {
			// Handle the case where the customer does not exist (throw an exception or
			// return a specific result)
			throw new RuntimeException("Customer with ID " + id + " not found");
		}

		Customer existingCustomer = optionalCustomer.get();

		// Update the existing customer with the new details
		if (customerDetails.getFirst_name() != null) {
			existingCustomer.setFirst_name(customerDetails.getFirst_name());
		}
		if (customerDetails.getEmail() != null) {
			existingCustomer.setEmail(customerDetails.getEmail());
		}
		if (customerDetails.getPhone() != null) {
			existingCustomer.setPhone(customerDetails.getPhone());
		}
		if (customerDetails.getLast_name() != null) {
			existingCustomer.setLast_name(customerDetails.getLast_name());
		}
		if (customerDetails.getCity() != null) {
			existingCustomer.setCity(customerDetails.getCity());
		}
		if (customerDetails.getAddress() != null) {
			existingCustomer.setAddress(customerDetails.getAddress());
		}
		if (customerDetails.getState() != null) {
			existingCustomer.setState(customerDetails.getState());
		}
		if (customerDetails.getStreet() != null) {
			existingCustomer.setStreet(customerDetails.getStreet());
		}
		// Update other fields as necessary

		// Save the updated customer
		return customerRepository.save(existingCustomer);
	}

//    public Page<Customer> getCustomers(Pageable pageable, String search) {
//        return customerRepository.findByFirstNameContainingOrLastNameContaining(search, search, pageable);
//    }

	public Customer getCustomerById(Long id) {
		Optional<Customer> cus = customerRepository.findById(id);
		return cus.get();
	}

	public void deleteCustomer(Long id) {
		customerRepository.deleteById(id);
	}

//	public List<Customer> findAllCustomer(String search, Pageable pageable) {
//		if (search != null && !search.isEmpty()) {
//			return customerRepository.findByFirstNameContaining(search,pageable);
//		}
//		return customerRepository.findAll(pageable);
//	}
	
	public Page<Customer> findAllCustomer(String search, Pageable pageable) {
	    if (search != null && !search.isEmpty()) {
	        return customerRepository.findByFirstNameContaining(search, pageable);
	    }
	    return customerRepository.findAll(pageable);
	}
	
	public List<Customer> findByLastName(String value) {
		if (value != null && !value.isEmpty()) {
			return customerRepository.findByLastName(value);
		}
		return customerRepository.findAll();
	}

	public void syncCustomers() {
		
		String token="dGVzdEBzdW5iYXNlZGF0YS5jb206VGVzdEAxMjM=";
		 String url = "https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp";
		try {
            // Build URL with parameters
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("cmd", "get_customer_list");

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Log request
            System.out.println("Sending request to URL: " + builder.toUriString());
            System.out.println("Headers: " + headers);

            // Send request
            ResponseEntity<Customer[]> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity,
                Customer[].class
            );

            // Check response
            System.out.println("Response Status Code: " + response.getStatusCode());
            System.out.println("Response Body: " + response.getBody());

            // Process customers
            Customer[] customers = response.getBody();
            if (customers != null) {
                for (Customer externalCustomer : customers) {
                    Optional<Customer> existingCustomer = customerRepository.findByEmail(externalCustomer.getEmail());
                    if (existingCustomer.isPresent()) {
                        Customer customer = existingCustomer.get();
                        customer.setFirst_name(externalCustomer.getFirst_name());
                        customer.setLast_name(externalCustomer.getLast_name());
                        // set other fields
                        customerRepository.save(customer);
                    } else {
                        customerRepository.save(externalCustomer);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during sync: " + e.getMessage());
        }
    }
	
	
	
	
	
	
	
	//for getting username i.e email from database to authenticate 

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// Load user by username from database

		Customer user = customerRepository.findByEmail(username)
				// if useename not avalble it will throws error
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found !!!"));

		return user;
	}


}
