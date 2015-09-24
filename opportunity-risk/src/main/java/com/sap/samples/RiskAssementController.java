package com.sap.samples;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class RiskAssementProvider
 */
@WebServlet("/RiskAssementProvider")
public class RiskAssementController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger LOGGER = LoggerFactory.getLogger(C4CODataServiceDelegate.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RiskAssementController() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		String responsePayload = null;
		try {
			responsePayload = new C4CODataServiceDelegate().getOpportunities(request, response);
			out.write(responsePayload);
		} catch (NamingException | URISyntaxException e) {
			LOGGER.error(e.getMessage(), e);
			response.sendError(500);			
		}
	}

}
