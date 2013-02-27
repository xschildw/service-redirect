package org.sagebase.redirect.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Formatter;
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
	private static final String scriptTemplate = 
		"<!DOCTYPE html PUBLIC\"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
		"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
		"<script type=\"text/javascript\">\n" +
		"var hash = \"\"; if(window.location.hash) {hash = window.location.hash.substring(1);} window.location.replace(\"%s\" + hash)\n" +
		"</script> </html>";
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
		int respCode = 0;
		
		if(redirect == null){
			// if we do not have a mapping let it go through
			logger.debug("No mapping for host");
			chain.doFilter(request, response);
			return;
		}else{
			newHost = redirect.getRedirectToHost();
			respCode = redirect.getResponseCode();
			logger.debug("newHost: " + newHost);
			
			try {
				uri = new URI("https", null, newHost, -1, servletPath, query, null);
				logger.debug("new URI:" + uri.toString());
			} catch (URISyntaxException ex) {
				java.util.logging.Logger.getLogger(RedirectFilter.class.getName()).log(Level.SEVERE, null, ex);
				throw new RuntimeException(ex.getMessage());
			}
			
			if ((300 < respCode) && (400 > respCode)) {
				// Send a redirect
				response.setStatus(respCode);
				// Build the new url
				response.setHeader("Location", uri.toString());
			} else if (200 == respCode) { // 200
				String script = String.format(scriptTemplate, uri.toString() + "/#");
				logger.debug("Script: " + script);
				response.setStatus(respCode);
				response.setContentType("text/html");
				PrintWriter pw = response.getWriter();
				pw.println(script);
				pw.flush();
			} else {
				java.util.logging.Logger.getLogger(RedirectFilter.class.getName()).log(Level.SEVERE, "Response code not supported:" + respCode);
				throw new RuntimeException("Response code not supported" + respCode);
			}
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
