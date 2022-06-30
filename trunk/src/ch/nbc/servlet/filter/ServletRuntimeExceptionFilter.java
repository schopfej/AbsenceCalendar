/**
 * Copyright (C) 2014 - NOVO Business Consultants AG
 * 
 * $LastChangedDate$
 * $LastChangedRevision$
 * $LastChangedBy$
 */
package ch.nbc.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletRuntimeExceptionFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		try {
			chain.doFilter(request, response);
		} catch (ServletException ex) {
			Throwable rootCause = ex.getRootCause();
			if (rootCause instanceof ClassNotFoundException) {
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				// Redirect on ViewExpiredException to the same URL
				String requestUrl = httpRequest.getRequestURL().toString();
				httpResponse.sendRedirect(httpResponse.encodeRedirectURL(requestUrl));
			} else if (rootCause instanceof RuntimeException) {
				// This is true for any FacesException.
				throw (RuntimeException) rootCause;
			} else {
				// Throw wrapped RuntimeException instead of ServletException.
				throw ex;
			}
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		// remains empty
	}

	@Override
	public void destroy() {
		// renains empty
	}

}
