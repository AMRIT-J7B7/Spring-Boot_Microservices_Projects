package com.infosys.infytel.customer.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.infytel.customer.dto.CustomerDTO;
import com.infosys.infytel.customer.dto.LoginDTO;
import com.infosys.infytel.customer.dto.PlanDTO;
import com.infosys.infytel.customer.service.CustCircuitBreakerService;
import com.infosys.infytel.customer.service.CustomerService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
public class CustomerController {

	Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	CustomerService custService;
	
	@Autowired
	CustCircuitBreakerService custCircuitService;
	
	
	// Create a new customer
	@PostMapping(value = "/customers",  consumes = MediaType.APPLICATION_JSON_VALUE)
	public void createCustomer(@RequestBody CustomerDTO custDTO) {
		logger.info("Creation request for customer "+ custDTO);
		custService.createCustomer(custDTO);
	}

	// Login
	@PostMapping(value = "/login",consumes = MediaType.APPLICATION_JSON_VALUE)
	public boolean login(@RequestBody LoginDTO loginDTO) {
		logger.info("Login request for customer "+loginDTO.getPhoneNo()+" with password "+loginDTO.getPassword());
		return custService.login(loginDTO);
	}

	// Fetches full profile of a specific customer
	@CircuitBreaker(name = "customerService",fallbackMethod="getCustomerProfileFallback")
	@GetMapping(value = "/customers/{phoneNo}",  produces = MediaType.APPLICATION_JSON_VALUE)
	public CustomerDTO getCustomerProfile(@PathVariable Long phoneNo) {
		logger.info("Profile request for customer " +phoneNo);
		long overAllStart = System.currentTimeMillis();
		CustomerDTO custDTO=custService.getCustomerProfile(phoneNo);

		
		long planStartTime = System.currentTimeMillis();
		
		CompletableFuture<PlanDTO> planDTO= custCircuitService.getSpecificPlan(custDTO.getCurrentPlan().getPlanId());
		
		long planEndTime = System.currentTimeMillis();
		
		long friendsStartTime = System.currentTimeMillis();
		
		List<Long> friends= custCircuitService.getSpecificFriends(phoneNo);
		
		long friendsEndTime = System.currentTimeMillis();
		
		planDTO.thenAccept(plan -> {
            System.out.println("Received plan & Adding to CustDTO: " + plan);
    		custDTO.setCurrentPlan(plan);
        }).exceptionally(ex -> {
            System.out.println("Failed to get plan: " + ex.getMessage());
            return null;
        });

		custDTO.setFriendAndFamily(friends);
		
		long overAllStop = System.currentTimeMillis();
		
		logger.info("total time for plan: " +(planEndTime-planStartTime));
		logger.info("total time for friend: " +(friendsEndTime-friendsStartTime));
		logger.info("total time for Overall request: " +(overAllStop-overAllStart));
		return custDTO;
	}
	
	public CustomerDTO getCustomerProfileFallback(Long phoneNo, Throwable throwable){
		logger.info("==========FALLING BACK BROIIII===========");
		return new CustomerDTO();
		
	}
	

}
