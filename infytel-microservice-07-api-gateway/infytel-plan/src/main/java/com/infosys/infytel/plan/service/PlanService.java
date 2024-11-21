package com.infosys.infytel.plan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infosys.infytel.plan.dto.PlanDTO;
import com.infosys.infytel.plan.entity.Plan;
import com.infosys.infytel.plan.repository.PlanRepository;

@Service
public class PlanService {
	Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	PlanRepository planRepo;

	// Fetches all plan details
	public List<PlanDTO> getAllPlans() {

		List<Plan> plans = planRepo.findAll();
		List<PlanDTO> planDTOs = new ArrayList<>();

		for (Plan plan : plans) {
			PlanDTO planDTO = PlanDTO.valueOf(plan);
			planDTOs.add(planDTO);
		}

		logger.info("Plan details : "+planDTOs);
		return planDTOs;
	}

	// Fetch specific plan details
	public PlanDTO getSpecificPlan(int planId) {
		logger.info("Plan details : "+ planId);
		PlanDTO planDTO = null;
		Optional<Plan> optPlan = planRepo.findById(planId);
		if (optPlan.isPresent()) {
			Plan plan = optPlan.get();
			planDTO = PlanDTO.valueOf(plan);
		}

		return planDTO;

	}

}
