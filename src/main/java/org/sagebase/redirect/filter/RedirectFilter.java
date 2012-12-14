package org.sagebase.redirect.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * A simple redirect filter.
 * @author jmhill
 *
 */
public class RedirectFilter implements Filter{
	
	private static Logger logger = Logger.getLogger(RedirectFilter.class);

	private Map<String, RedirectData> redirectData;
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest req, ServletResponse res,	FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		logger.log(Level.DEBUG, "Http request: " + req);
		
		// First extract the original address
		String scheme = request.getScheme();
		String address = request.getServerName();
		int port = request.getServerPort();
		String contextPath = request.getContextPath();
		String servletPath = request.getServletPath();
		String pathInfo = request.getPathInfo();
		String query = request.getQueryString();
		RedirectData redirect = redirectData.get(address);
		
		StringBuilder s = new StringBuilder();
		s.append("scheme: ").append(scheme);
		s.append("\taddress: ").append(address);
		s.append("\tcontext: ").append(contextPath);
		s.append("servlet: ").append(servletPath);
		if (pathInfo != null) {
			s.append("pathinfo: ").append(pathInfo);
		}
		if (query != null) {
			s.append("query: ").append(query);
		}
		logger.log(Level.DEBUG, s);
		System.out.println(s);
		
		if(redirect == null){
			// if we do not have a mapping let it go through
			chain.doFilter(request, response);
			return;
		}else{
			// Send a redirect
			response.setStatus(redirect.getResponseCode());
			// Build the new url
			StringBuffer newUrl = new StringBuffer("https://");
			newUrl.append(redirect.getRedirectToHost());
			newUrl.append(contextPath);
			newUrl.append(servletPath);
			if (pathInfo != null) {
				newUrl.append(pathInfo);
			}
			if (query != null) {
				newUrl.append("?").append(query);
			}
			response.setHeader("Location", newUrl.toString());
		}
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		// Load the redirect configuration from the classpath
		List<RedirectData> list = RedirectConfiguration.getConfiguration();
		// Map using the from address
		redirectData = new HashMap<String, RedirectData>();
		if(list != null){
			for(RedirectData data: list){
				redirectData.put(data.getRedirectFromHost(), data);
			}
		}
		
	}

}
