package de.jlo.talendcomp.sap.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.jlo.talendcomp.sap.http.HttpClient;
import de.jlo.talendcomp.sap.http.TableInputImpl;

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
	public void testQueryInTestMode() throws Exception {
		testSetupDummyHttpConnection();
		int testrows = 100000;
		de.jlo.talendcomp.sap.Destination dest = (de.jlo.talendcomp.sap.Destination) globalMap.get("conn");
		TableInputImpl ti = (TableInputImpl) dest.createTableInput();
		ti.setUseTestMode(true, testrows);
		ti.setTableName("dummy");
		ti.prepare();
		ti.execute();
		int count = 0;
		String v = null;
		while (ti.next()) {
			List<String> row = ti.getCurrentRow();
			v = row.get(9);
			count++;
		}
		assertEquals(testrows, count);
//		assertEquals("last value does not fit", "V99-9", v);
	}

	@Test
	public void testExtractErrorMessage1() {
		String json = "{\"message\":\"&apos;localhost&apos;\"}";
		String expected = "'localhost'";
		String actual = HttpClient.extractMessageFromJson(json);
		assertEquals("wrong extract", expected, actual);
	}
	
	@Test
	public void testExtractErrorMessage2() {
		String json = "{\"message\":\"&lt;localhost&gt;\"}";
		String expected = "<localhost>";
		String actual = HttpClient.extractMessageFromJson(json);
		assertEquals("wrong extract", expected, actual);
	}

}
