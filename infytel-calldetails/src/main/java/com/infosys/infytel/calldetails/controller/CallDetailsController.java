package com.infosys.infytel.calldetails.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.infosys.infytel.calldetails.dto.CallDetailsDTO;
import com.infosys.infytel.calldetails.service.CallDetailsService;

@RestController
@CrossOrigin
public class CallDetailsController {

	Log logger = LogFactory.getLog(CallDetailsController.class);

	@Autowired
	CallDetailsService callDetailsService;

	// Fetches call details of a specific customer
	@GetMapping(value = "/customers/{phoneNo}/calldetails",  produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CallDetailsDTO> getCustomerCallDetails(@PathVariable long phoneNo) {

		logger.info("Calldetails request for customer "+ phoneNo);

		return callDetailsService.getCustomerCallDetails(phoneNo);
	}

}
