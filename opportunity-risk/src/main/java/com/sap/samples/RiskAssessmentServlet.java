package com.sap.samples;

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.samples.RiskAssessment.RiskRatings;
import com.sap.samples.odata.C4CODataServiceClient;

/**
 * HTTP Endpoint for Risk Assessments
 */
@WebServlet(name="RiskAssement", urlPatterns={"/Opportunities/Risk"})
@Resource(name="connectivityConfiguration", type=com.sap.core.connectivity.api.configuration.ConnectivityConfiguration.class)
public class RiskAssessmentServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Logger LOGGER = LoggerFactory.getLogger(RiskAssessment.class);

	/**
	 * Request:
	 * 	  - URL: /Opportunities/Risk
	 *    - Method: GET
	 *    - Query string parameters: 'opportunityID' /Mandatory/
	 * Response
	 *    Success:
	 *    	 - HTTP Code 200; Content-Type: application/json; Payload Body {"RiskAssessment": "High" | "Medium" | "Low" | "Not Relevant"}
	 *    Error: 
	 *       - HTTP Code 400: Missing mandatory query string parameter "opportunityID"
	 *       - HTTP Code 404: No entity with such opportunityID was found
	 *       - HTTP Code 500: Server-side exception
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json;charset=UTF-8");
		PrintWriter out = response.getWriter();
		try {
			String opportunityID = request.getParameter("opportunityID");
			if(opportunityID != null){				
				C4CODataServiceClient client = new C4CODataServiceClient();
				Opportunity opportunity = client.getOpportunityEntity(opportunityID);
				if(opportunity == null){ 
					response.sendError(404, String.format("No entity of type Opportunity with ID %s was found", opportunityID));
				} else {
					RiskRatings rating = new RiskAssessment().assessOpportunityRisk(opportunity);		
					out.write("{\"RiskAssessment\": \"" + rating.toString() + "\"}");	
				}
			} else {
				response.sendError(400, "Missing query string parameter 'opportunityID'");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			response.sendError(500);			
		}
	}

}
