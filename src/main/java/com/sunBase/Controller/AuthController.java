package com.sunBase.Controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunBase.Model.Customer;
import com.sunBase.Repository.CustomerRepository;
import com.sunBase.Service.CustomerServiceImpl;
import com.sunBase.config.AppConfig;
import com.sunBase.config.JWTRequest;
import com.sunBase.config.JWTResponse;
import com.sunBase.config.SecurityConfig;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private CustomerRepository custRepo;

  

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private CustomerServiceImpl userServiceImpl;
    
    
    @Autowired
    private com.sunBase.config.JwtHelper helper;
    
    @Autowired
    private AppConfig appConfig;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute Customer customer, Model model) {
       
       customer.setPassword(appConfig.passwordEncoder().encode(customer.getPassword()));
        custRepo.save(customer);

        return "redirect:/auth/login";
    }
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "login";
    }
    @PostMapping("/login")
    public String login(@ModelAttribute JWTRequest loginRequest, Model model, HttpServletResponse response) {
        try {
            // Authenticate user
        	this.doAuthenticate(loginRequest.getEmail(), loginRequest.getPassword());

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

            // Generate token
            String token = this.helper.generateToken(userDetails);

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(10 * 60 * 60); // 10 hours
            response.addCookie(cookie);

            // Redirect to customer list or dashboard page after successful login
            return "redirect:/customers"; 


        

        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }


	private void doAuthenticate(String email, String password) {

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
		try {
			manager.authenticate(authentication);

		} catch (BadCredentialsException e) {
			throw new BadCredentialsException(" Invalid Username or Password  !!");
		}

	}
}