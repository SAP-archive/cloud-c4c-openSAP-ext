package com.sap.samples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.authentication.AuthenticationHeader;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;


public class C4CODataServiceDelegate {
	
	Logger LOGGER = LoggerFactory.getLogger(C4CODataServiceDelegate.class);
	
	/*
	 * Destination pointing at the connection    
	 */
	private final String STANDARD_C4C_DESTINATION_NAME = "sap_cloud4customer_core_odata";
	
	
	private final String standardEntityName = "OpportunityCollection";

	/*
	 * execute the get call
	 */
	public String getOpportunities(HttpServletRequest request, HttpServletResponse response) throws IOException, NamingException, URISyntaxException {
		
		String opportunityID = request.getParameter("opportunityID");
		
		Context ctx = new InitialContext();
		
		Object obj = ctx.lookup("java:comp/env/connectivityConfiguration");
		ConnectivityConfiguration configuration = (ConnectivityConfiguration) obj;
		
		// get destination configuration for both custom and standard C4C OData services
		DestinationConfiguration standardDestConfiguration = configuration.getConfiguration(STANDARD_C4C_DESTINATION_NAME);

		// get the "myDestinationName" authentication property (example)
		String serviceURL = standardDestConfiguration.getProperty("URL");
		final String requestUrl = serviceURL
								+ standardEntityName
								+ URLEncoder.encode("?$filter=OpportunityID eq \'" + opportunityID + "\'", "UTF-8");		
		
		// create the get HttpGet
		HttpGet entityHTTPGet = new HttpGet(requestUrl);

		//setup the header for accepting the json format
		entityHTTPGet.setHeader("Accept", "application/json");
		
		BasicAuthenticationHeaderProvider basicAuthHeaderProvider = new BasicAuthenticationHeaderProvider();
		AuthenticationHeader basicAuthHeader = basicAuthHeaderProvider.getAuthenticationHeader(standardDestConfiguration);
		entityHTTPGet.setHeader(basicAuthHeader.getName(), basicAuthHeader.getValue());
		
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
		//execute the get call
		HttpResponse respo = client.execute(entityHTTPGet);
				
		// convert the response to UTF-8 
		String responseStr = EntityUtils.toString(respo.getEntity(), "UTF-8");

		return responseStr;
	}

}
