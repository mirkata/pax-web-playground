package com.objectiflune.servlets.purl.demo;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PurlServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;
	private String m_alias;

    public PurlServlet( final String alias )
    {
        m_alias = alias;
    }

    protected void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
    	final String uri = request.getRequestURI();
        response.setContentType( "text/html" );
        response.setStatus( HttpServletResponse.SC_OK );
        response.getWriter().println( "<h1>Hello I'm serving PURLs</h1>" );
        response.getWriter().println( "request alias: " + m_alias + "<br/>");
        response.getWriter().println( "request uri: " + uri + "<br/><br/>");
        
        String name;
        String value;
     // Get all request headers
        Enumeration<String> enumm = request.getHeaderNames();
        for (; enumm.hasMoreElements(); ) {
            // Get the name of the request header
            name = (String)enumm.nextElement();
            response.getWriter().println(name  + ": ");

            // Get a value of the request header
            value = request.getHeader(name);

            // If the request header can appear more than once, get all values
            Enumeration<String> valuesEnum = request.getHeaders(name);
            for (; valuesEnum.hasMoreElements(); ) {
                // Get a value of the request header
                value = (String)valuesEnum.nextElement();

                response.getWriter().println("    " + value);
            }
            
            response.getWriter().println("<br/>");
        }
    }

}
