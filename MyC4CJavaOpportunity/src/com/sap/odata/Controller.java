package com.sap.odata;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Controller
 */
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private ODataCall odataCls = null;

    /**
     * Default constructor. 
     */
    public Controller() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			if (odataCls == null) {
				// instantiate the ODataCall
				odataCls = new ODataCall(response);
			}
	
			odataCls.executeGet(request, response);
		} catch(Exception e) {
			// throw the exception to script in case of issues
			throw new ServletException(e.getMessage());
		}
	}

}
