package de.jlo.talendcomp.sap.impl;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;

import de.jlo.talendcomp.sap.Driver;

public class TestTableInput {
	
	private Map<String, Object> globalMap = new HashMap<>();

	@Before
	public void testSetupHttpConnection() throws Exception {
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
	
}
