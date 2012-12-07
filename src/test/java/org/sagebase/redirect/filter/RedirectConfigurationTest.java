package org.sagebase.redirect.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class RedirectConfigurationTest {
	
	List<RedirectData> list;
	
	@Before
	public void before(){
		list = new LinkedList<RedirectData>();
		// One example
		RedirectData data = new RedirectData();
		data.setResponseCode(301);
		data.setRedirectFromHost("google.com");
		data.setRedirectToHost("my.google.com");
		list.add(data);
		// another
		data = new RedirectData();
		data.setResponseCode(309);
		data.setRedirectFromHost("example.com");
		data.setRedirectToHost("space.org");
		list.add(data);
	}
	
	@Test
	public void testRoundTrip(){
		// Write this to a stream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		RedirectConfiguration.writeToXml(list, baos);
		String xml = new String(baos.toByteArray());
		System.out.println(xml);
		ByteArrayInputStream bin = new ByteArrayInputStream(baos.toByteArray());
		List<RedirectData> parsed = RedirectConfiguration.readFromXml(bin);
		assertEquals(list, parsed);
	}

	@Test
	public void testClassPathRead(){
		List<RedirectData> parsed = RedirectConfiguration.getConfiguration();
		System.out.println(parsed);
		assertNotNull(parsed);
	}
}
