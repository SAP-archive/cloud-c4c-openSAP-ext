package com.sap.odata;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import com.sap.core.connectivity.api.DestinationException;
import com.sap.core.connectivity.api.http.HttpDestination;


public class ODataCall {
	
	private final String DESTINATION_NAME = "sap_cloud4customer_core_odata";
	
	//instantiate the local context for making the post call
	private BasicHttpContext localContext = new BasicHttpContext();

	/*
	 * constructor
	 */
	public ODataCall(HttpServletResponse response) throws IOException {
	}
	
	/*
	 * execute the get call
	 */
	public void executeGet(HttpServletRequest request, HttpServletResponse response) throws IOException, NamingException, DestinationException {
		// create the get HttpGet
		HttpGet httpGet = new HttpGet("/OpportunityCollection");
		//setup the header for accepting the json format
		httpGet.setHeader("Accept", "application/json");
		//execute the get call
		HttpResponse respo = getClient().execute(httpGet);
		
		// convert the response to UTF-8 
		String responseStr = EntityUtils.toString(respo.getEntity(), "UTF-8");
		//return the data to the javascript application
		response.getWriter().println(responseStr);
	}
	
	/*
	 * execute the post call
	 */
	public void executePost(HttpServletRequest request, HttpServletResponse response) throws ClientProtocolException, IOException, NamingException, DestinationException {
		//create the Http post
		HttpPost post = new HttpPost("/OpportunityCollection");
		//set the sending content type to json
		post.setHeader("Content-Type", "application/json");
		//set the returning content type to json
		post.setHeader("Accept", "application/json");
		post.setHeader("Accept-Charset", "utf-8");
		
		// get the CSRF token 	
		String m_csrfToken = getCsrfToken();
		// set the CSRF token to the Http post
		post.setHeader("X-CSRF-Token", m_csrfToken);
		
		// get the input from the javascript UI application
		String json = request.getParameter("json");
		// create the entity 
		StringEntity entity;
		entity = new StringEntity(json);
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		// set entity in the post
		post.setEntity(entity);

		//execute the post call
		HttpResponse localResponse = getClient().execute(post, localContext);
		String responseStr = null;
		//get the response in the UTF format
		responseStr = EntityUtils.toString(localResponse.getEntity(), "UTF-8");
		// return the response to javascript UI
		response.getWriter().println("Response /n"+responseStr);
	}

	/*
	 * get CSRFToken
	 */
	private String getCsrfToken() throws ClientProtocolException, IOException, NamingException, DestinationException {
		String m_csrfToken;
		// create the Http get request to get the CSRF token
		HttpGet httpGet = new HttpGet("/OpportunityCollection");
		httpGet.setHeader("Content-Type", "application/atom+xml");
		// set the header parameter for CSRF token to Fetch
		httpGet.setHeader("X-CSRF-Token", "Fetch");
		
		// execute the get call
		HttpResponse response = getClient().execute(httpGet, localContext);
		try {
			m_csrfToken = response.getFirstHeader("X-CSRF-Token").getValue();
		} catch (Exception e) {
			m_csrfToken = "none";
		}
		return m_csrfToken;
	}
		
	/*
	 * get the http client 
	 * look up the destination (the destination is mentioned in the web.xml)
	 * 
	 */
	private HttpClient getClient() throws NamingException, DestinationException {		
        // Get HTTP destination
        Context ctx = new InitialContext();
        HttpDestination destination = (HttpDestination) ctx.lookup("java:comp/env/" +  DESTINATION_NAME);
        // Create HTTP client
        return destination.createHttpClient();
	}

}
