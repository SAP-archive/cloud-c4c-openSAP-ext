package com.sap.samples;

import java.io.IOException;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the logic for risk assessment based on opportunity information.
 *
 */
public class RiskAssessment {
	
	Logger LOGGER = LoggerFactory.getLogger(RiskAssessment.class);	

	public enum RiskRatings {
		NOT_RELEVANT("Not Relevant"), HIGH("High"), MEDIUM("Medium");
		String value;
		RiskRatings(String value){
			this.value = value;
		}
		public String toString(){
			return this.value;
		}
	}
	
	public RiskRatings assessOpportunityRisk(Opportunity opportunity) throws NamingException, NotFoundException, IOException {		
		//logic to build the risk details
		RiskRatings riskAssessment = null;	
		if(opportunity.statusCode == "4"){
			riskAssessment = RiskRatings.NOT_RELEVANT;
		} else if(opportunity.expectedValue > 10000){
			riskAssessment = RiskRatings.HIGH;
		} else {
			riskAssessment = RiskRatings.MEDIUM;
		} 
		return riskAssessment;
	}
}