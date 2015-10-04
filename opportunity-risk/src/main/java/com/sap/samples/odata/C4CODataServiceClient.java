package com.sap.samples.odata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.authentication.AuthenticationHeader;
import com.sap.core.connectivity.api.configuration.ConnectivityConfiguration;
import com.sap.core.connectivity.api.configuration.DestinationConfiguration;
import com.sap.samples.NotFoundException;
import com.sap.samples.Opportunity;

public class C4CODataServiceClient {
	
	Logger LOGGER = LoggerFactory.getLogger(C4CODataServiceClient.class);
	
	/*
	 * Destination pointing at the connection    
	 */
	private static final String STANDARD_C4C_DESTINATION_NAME = "sap_cloud4customer_core_odata";
	
	private static final int COPY_CONTENT_BUFFER_SIZE = 1024;
	
	
	private final String standardEntityName = "OpportunityCollection";	
	
	/**
	 * Retrieves an opportunity from back-end C4C OData service.
	 * 
	 * @param opportunityID A valid opportunity ID
	 * @return An Opportunity object constructed form the OData payload returned from the request to the C4C OData service 
	 * @throws NamingException in case of problems with the destination configuration lookup
	 * @throws NotFoundException In case the destination was not found or an entity with such id was not found.
	 * @throws IOException In case of problems around the network streaming in and out operations
	 */
	public Opportunity getOpportunityEntity(String opportunityID) throws NamingException, NotFoundException, IOException {
		
		Context ctx = new InitialContext();
		
		Object obj = ctx.lookup("java:comp/env/connectivityConfiguration");
		ConnectivityConfiguration configuration = (ConnectivityConfiguration) obj;
		
		
		// get destination configuration for both custom and standard C4C OData services
		DestinationConfiguration standardDestConfiguration = configuration.getConfiguration(STANDARD_C4C_DESTINATION_NAME);
		if (standardDestConfiguration == null) {
			throw new NotFoundException("Destination " + STANDARD_C4C_DESTINATION_NAME + " is not found. Hint: Make sure to have the destination configured.");
		}

		//prepare request to c4c odata service
		String serviceURL = standardDestConfiguration.getProperty("URL");
		final String requestUrlString = serviceURL
								+ standardEntityName + "?$filter=OpportunityID"
								+ URLEncoder.encode(" eq \'" + opportunityID + "\'", "UTF-8");	
		URL requestUrl = new URL(requestUrlString);
		
		Proxy proxy = getProxy();
		
		HttpURLConnection urlConnection = (HttpURLConnection) requestUrl.openConnection(proxy);
		this.setHeaders(urlConnection, standardDestConfiguration);

		//read remote resource response into buffer
		InputStream in = urlConnection.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		this.copyStream(in, out);

		//convert response buffer to string
		String odataJsonPayload = new String(out.toByteArray(), "UTF-8");
		
		//deserialize form JSON into Java object model
		JsonFormat formatter = new JsonFormat();
		Opportunity opportunity = formatter.fromJson(odataJsonPayload);
		return opportunity;
	}
	
	/**
	 * Set the required request headers for the C4C OData service request.
	 * 
	 * @param urlConnection The connection to setup with headers
	 * @param destinationCfg The destination configuration to use as source for authorization related properties 
	 */
	private void setHeaders(HttpURLConnection urlConnection, DestinationConfiguration destinationCfg){
		//setup the header for accepting the json format
		urlConnection.setRequestProperty("Accept", "application/json");
		//setup the header for BASIC authentication
		BasicAuthenticationHeaderProvider basicAuthHeaderProvider = new BasicAuthenticationHeaderProvider();
		AuthenticationHeader basicAuthHeader = basicAuthHeaderProvider.getAuthenticationHeader(destinationCfg);
		urlConnection.setRequestProperty(basicAuthHeader.getName(), basicAuthHeader.getValue());		
	}
	
	/**
	 * Get the configured proxy for this HCP Java runtime environment for outgoing Internet connections. 
	 * 
	 * @return HCP Java runtime environment proxy
	 */
	private Proxy getProxy() {        
        String proxyHost = System.getProperty("http.proxyHost");
        int proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));
        
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
    }
	
	/**
	 * Utility to pipe input and output streams.
	 * 
	 * @param inStream input stream
	 * @param outStream output stream
	 * @throws IOException any streaming exception
	 */
	private void copyStream(InputStream inStream, OutputStream outStream) throws IOException {
		try {
			byte[] buffer = new byte[COPY_CONTENT_BUFFER_SIZE];
			int len;
			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
		} finally {
			inStream.close();
		}
	}
}
