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

/**
 * A simple redirect filter.
 * @author jmhill
 *
 */
public class RedirectFilter implements Filter{

	private Map<String, RedirectData> redirectData;
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest req, ServletResponse res,	FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		// First extract the original address
		String address = request.getServerName();
		RedirectData redirect = redirectData.get(address);
		if(redirect == null){
			// if we do not have a mapping let it go through
			chain.doFilter(request, response);
			return;
		}else{
			// Send a redirect
			response.setStatus(redirect.getResponseCode());
			// Build the new url
			response.setHeader("Location", "https://"+redirect.getRedirectToHost()+"/");
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
