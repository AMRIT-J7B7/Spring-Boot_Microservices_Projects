package com.infosys.infytel.customer.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.infosys.infytel.customer.controller.CustPlanFeign;
import com.infosys.infytel.customer.dto.PlanDTO;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class CustCircuitBreakerService {

	@Autowired
	RestTemplate template;
	
	@Autowired
	CustPlanFeign planFeign;
	
//	@CircuitBreaker(name = "customerService")
//	public PlanDTO getSpecificPlan(Integer planId){
//		return template.getForObject("http://PlanMS"+"/plans/"+planId, PlanDTO.class);
//	}
	@CircuitBreaker(name = "customerService")
	public CompletableFuture<PlanDTO> getSpecificPlan(Integer planId){
		return  CompletableFuture.supplyAsync(() -> planFeign.getSpecificPlan(planId));
	}
	
	@SuppressWarnings("unchecked")
	@CircuitBreaker(name = "customerService")
	public List<Long> getSpecificFriends(Long phoneNo){
		return template.getForObject("http://FriendMS/customers/"+phoneNo+"/friends", List.class);
	}
}
