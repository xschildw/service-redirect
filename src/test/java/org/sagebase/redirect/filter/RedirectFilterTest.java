package org.sagebase.redirect.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import org.mockito.Mockito;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Matchers.anyString;

public class RedirectFilterTest {
	
	HttpServletRequest mockReq;
	HttpServletResponse mockResp;
	FilterChain mockChain;
	RedirectFilter filter;
	PrintWriter mockPrintWriter;
	static final String expectedScript = 
			"<!DOCTYPE html PUBLIC\"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
			"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
			"<script type=\"text/javascript\">\n" +
			"var hash = \"\"; if(window.location.hash) {hash = \"/\" + window.location.hash;} window.location.replace(\"https://www.synapse.org/\" + hash)\n" +
			"</script> </html>";
	
	public RedirectFilterTest() {
	}
	
	@Before
	public void setUp() throws ServletException {
		mockReq = Mockito.mock(HttpServletRequest.class);
		mockResp = Mockito.mock(HttpServletResponse.class);
		mockChain = Mockito.mock(FilterChain.class);
		mockPrintWriter = Mockito.mock(PrintWriter.class);
		filter = new RedirectFilter();
		filter.init(null);
	}
	
	@After
	public void tearDown() {
	}
	
	@Test
	public void testDoFilterNoMapping() throws IOException, ServletException {
		String expectedScheme = "http";
		String expectedPath = "/index.html";
		String expectedServerName = "server.org";
		StringBuffer expectedUrl = new StringBuffer(expectedScheme + "://" + expectedServerName + expectedPath);
		
		when(mockReq.getRequestURL()).thenReturn(expectedUrl);
		when(mockReq.getScheme()).thenReturn(expectedScheme);
		when(mockReq.getServletPath()).thenReturn(expectedPath);
		when(mockReq.getServerName()).thenReturn(expectedServerName);
		
		filter.doFilter(mockReq, mockResp, mockChain);
		
		verify(mockChain).doFilter(mockReq, mockResp);
		
	}
	
	@Test
	public void testDoFilterOnlySynapse() throws Exception {
		String expectedScheme = "http";
		String expectedPath = "/";
		String expectedQueryString = "";
		String expectedServerName = "synapse.org";
		StringBuffer expectedUrl = new StringBuffer(expectedScheme + "://" + expectedServerName + expectedPath + expectedQueryString);
		
		when(mockReq.getRequestURL()).thenReturn(expectedUrl);
		when(mockReq.getScheme()).thenReturn(expectedScheme);
		when(mockReq.getServletPath()).thenReturn(expectedPath);
		when(mockReq.getServerName()).thenReturn(expectedServerName);
		when(mockResp.getWriter()).thenReturn(mockPrintWriter);

		filter.doFilter(mockReq, mockResp, mockChain);
	
		verify(mockChain, never()).doFilter(mockReq, mockResp);
		verify(mockPrintWriter).println(expectedScript);
		verify(mockPrintWriter).flush();

	}
	
	@Test
	public void testDoFilterSynapseWithFriendlyUrl() throws Exception {
		String expectedScheme = "http";
		String expectedPath = "/mySyn123";
		String expectedQueryString = "";
		String expectedServerName = "synapse.org";
		StringBuffer expectedUrl = new StringBuffer(expectedScheme + "://" + expectedServerName + expectedPath + expectedQueryString);
		final String expectedScript2 = 
				"<!DOCTYPE html PUBLIC\"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
				"<script type=\"text/javascript\">\n" +
				"var hash = \"\"; if(window.location.hash) {hash = \"/\" + window.location.hash;} window.location.replace(\"https://www.synapse.org/mySyn123\" + hash)\n" +
				"</script> </html>";
		
		when(mockReq.getRequestURL()).thenReturn(expectedUrl);
		when(mockReq.getScheme()).thenReturn(expectedScheme);
		when(mockReq.getServletPath()).thenReturn(expectedPath);
		when(mockReq.getServerName()).thenReturn(expectedServerName);
		when(mockResp.getWriter()).thenReturn(mockPrintWriter);

		filter.doFilter(mockReq, mockResp, mockChain);
	
		verify(mockChain, never()).doFilter(mockReq, mockResp);
		verify(mockPrintWriter).println(expectedScript2);
		verify(mockPrintWriter).flush();

	}
	
	@Test
	public void testSagebase() throws Exception {
		String expectedScheme = "http";
		String expectedPath = "";
		String expectedQueryString = "";
		String expectedServerName = "sagebase.org";
		StringBuffer expectedUrl = new StringBuffer(expectedScheme + "://" + expectedServerName + expectedPath + expectedQueryString);
		final String expectedScript2 = 
				"<!DOCTYPE html PUBLIC\"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
				"<script type=\"text/javascript\">\n" +
				"var hash = \"\"; if(window.location.hash) {hash = \"/\" + window.location.hash;} window.location.replace(\"http://www.sagebase.org\" + hash)\n" +
				"</script> </html>";
		

		when(mockReq.getRequestURL()).thenReturn(expectedUrl);
		when(mockReq.getScheme()).thenReturn(expectedScheme);
		when(mockReq.getServletPath()).thenReturn(expectedPath);
		when(mockReq.getServerName()).thenReturn(expectedServerName);
		when(mockResp.getWriter()).thenReturn(mockPrintWriter);

		filter.doFilter(mockReq, mockResp, mockChain);
	
		verify(mockChain, never()).doFilter(mockReq, mockResp);
		verify(mockPrintWriter, never()).println(expectedScript2);
		verify(mockPrintWriter, never()).flush();
		verify(mockResp).setStatus(301);
		verify(mockResp).setHeader("Location", "http://www.sagebase.org");
	}

}
