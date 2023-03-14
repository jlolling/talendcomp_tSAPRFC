package de.jlo.talendcomp.sap.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.jlo.talendcomp.sap.http.HttpClient;

public class TestTableInput {
	
	private Map<String, Object> globalMap = new HashMap<>();

	public void testSetupDummyHttpConnection() throws Exception {
		String user = "user";
		String password = "secret";
		String url = "http://localhost:9999";
		de.jlo.talendcomp.sap.Driver driver = de.jlo.talendcomp.sap.DriverManager.getInstance().getDriverService(url, user, password);
	    de.jlo.talendcomp.sap.ConnectionProperties properties = null;
	    properties = new de.jlo.talendcomp.sap.ApplicationServerProperties()
				.setClient("001")
				.setHost("localhost")
				.setLanguage("EN")
				.setSystemNumber("01")
				.setUser("user")
				.setPassword("mypassword");
		de.jlo.talendcomp.sap.Destination dest = driver.createDestination(properties);
		globalMap.put("conn", dest);
	}
	
	@Test
	public void ping() throws Exception {
		testSetupDummyHttpConnection();
		de.jlo.talendcomp.sap.Destination dest = (de.jlo.talendcomp.sap.Destination) globalMap.get("conn");
		dest.ping();
	}
	
	@Test
	public void testExtractErrorMessage() {
		String json = "{\"message\":\"&apos;localhost&apos;\"}";
		String expected = "'localhost'";
		String actual = HttpClient.extractMessageFromJson(json);
		assertEquals("wrong extract", expected, actual);
	}
	
}
