package org.sagebase.redirect.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * A simple redirect filter.
 * @author jmhill
 *
 */
public class RedirectFilter implements Filter{

	private Map<String, RedirectData> redirectData;
	private static Logger logger = Logger.getLogger(RedirectFilter.class);
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void doFilter(ServletRequest req, ServletResponse res,	FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		// This just gets the URL (i.e. no query or fragment)
		StringBuffer reqUrl = request.getRequestURL();
		logger.debug("request URL" + reqUrl.toString());
		String scheme = request.getScheme();
		logger.debug("scheme: " + scheme);
		String servletPath = request.getServletPath();
		logger.debug("servletPath: " + servletPath);
		// If no servletpath specified then the API returns index.htm.
		// This does not work for portal.
		if ("/index.html".equals(servletPath)) {
			servletPath = "";
		}
		String query = request.getQueryString();
		logger.debug("query: " + query);
		// First extract the original address
		String address = request.getServerName();
		logger.debug("address: " + address);

		RedirectData redirect = redirectData.get(address);
		String newHost = null;
		URI uri = null;
		
		if(redirect == null){
			// if we do not have a mapping let it go through
			logger.debug("No mapping for host");
			chain.doFilter(request, response);
			return;
		}else{
			newHost = redirect.getRedirectToHost();
			logger.debug("newHost: " + newHost);
			try {
				uri = new URI("https", null, newHost, -1, servletPath, query, null);
				logger.debug("new URI:" + uri.toString());
			} catch (URISyntaxException ex) {
				java.util.logging.Logger.getLogger(RedirectFilter.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex.getMessage());
			}
			// Send a redirect
			response.setStatus(redirect.getResponseCode());
			// Build the new url
			response.setHeader("Location", uri.toString());
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
