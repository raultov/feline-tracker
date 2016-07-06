package com.ayoza.feline.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCORSFilter implements Filter {


	public SimpleCORSFilter() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
	
	    HttpServletRequest request = (HttpServletRequest) req;
	   // String url = getFullURL(request);
	   // if (url.startsWith("/oauth/token", url.length() - 12)) {
	
		    HttpServletResponse response = (HttpServletResponse) res;
		
		    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		    response.setHeader("Access-Control-Allow-Credentials", "true");
		    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		    response.setHeader("Access-Control-Max-Age", "10");
		    response.setHeader("Access-Control-Allow-Headers", "Content-Type,Accept,X-Requested-With,Authorization");
		    
		    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
		        response.setStatus(HttpServletResponse.SC_OK);
		    } else {
		        chain.doFilter(req, res);
		    }
	    /*} else {
	    	chain.doFilter(req, res);
	    }*/
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}
/*
	private static String getFullURL(HttpServletRequest request) {
	    StringBuffer requestURL = request.getRequestURL();
	    String queryString = request.getQueryString();

	    if (queryString == null) {
	        return requestURL.toString();
	    } else {
	        return requestURL.append('?').append(queryString).toString();
	    }
	}*/
}