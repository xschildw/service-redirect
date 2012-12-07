package org.sagebase.redirect.filter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.thoughtworks.xstream.XStream;

/**
 * Loads the redirect data from xml
 * @author jmhill
 *
 */
public class RedirectConfiguration {
	
	public static final String REDIRECT_CONFIG_FILE_NAME = "redirect-config.xml";
	/**
	 * Load the redirect configuration from the classpath.
	 * @return
	 */
	public static List<RedirectData> getConfiguration(){
		// Get the file from the classpath
		InputStream in = RedirectConfiguration.class.getClassLoader().getResourceAsStream(REDIRECT_CONFIG_FILE_NAME);
		if(in == null) throw new IllegalArgumentException("Cannot find '"+REDIRECT_CONFIG_FILE_NAME+"' on the classpath");
		return readFromXml(in);
	}

	/**
	 * Read the config from XML
	 * @param in
	 * @return
	 */
	public static List<RedirectData> readFromXml(InputStream in) {
		XStream xstream = createXstream();
		return (List<RedirectData>)xstream.fromXML(in);
	}
	
	/**
	 * Write a list to a stream.
	 * @param list
	 * @param out
	 */
	public static void writeToXml(List<RedirectData> list, OutputStream out){
		XStream xstream = createXstream();
		xstream.toXML(list, out);
	}

	/**
	 * Setup Xstream
	 * @return
	 */
	public static XStream createXstream() {
		XStream xstream = new XStream();
		xstream.alias("redirect", RedirectData.class);
		return xstream;
	}

}
