package de.jlo.talendcomp.sap.impl;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.junit.Before;
import org.junit.Test;

import de.jlo.talendcomp.sap.ApplicationServerProperties;
import de.jlo.talendcomp.sap.ConnectionProperties;
import de.jlo.talendcomp.sap.Destination;
import de.jlo.talendcomp.sap.Driver;
import de.jlo.talendcomp.sap.DriverManager;
import de.jlo.talendcomp.sap.TableInput;

public class TestTableInput {
	
	private Map<String, Object> globalMap = new HashMap<>();

	private void setupConnection() throws Exception {
		DriverManager.loadSapJco3Jar("/Users/jan/development/eclipse-workspace-talendcomp/sapjco3-mock/sapjco3.jar");
		Driver driver = DriverManager.getInstance().getDriver();
		ConnectionProperties p = new ApplicationServerProperties()
				.setClient("003")
				.setHost("localhost")
				.setLanguage("DE")
				.setSystemNumber("03")
				.setUser("user")
				.setPassword("secret");
		Destination dest = driver.createDestination(p);
		globalMap.put("destination", dest);
	}

	@Test
	public void query() throws Exception {
		setupConnection();
		Destination dest = (Destination) globalMap.get("destination");
		TableInput ti = dest.createTableInput();
		ti.setTableName("EDIDC");
		ti.addField("DOCNUM");
		ti.addField("MANDT");
		ti.setRowsToSkip(10);
		ti.setMaxRows(10);
		ti.setFilter("SNDPOR = 'LOBSTER'");
		ti.execute();
		System.out.println(ti.getTotalRowCount());
		while (ti.next()) {
			System.out.println(ti.getCurrentRowIndex() + "=" + ti.getCurrentRawDataEscaped());
		}
	}

	@Test
	public void testSplit() {
		String s = "1100003204\b"
				+ "000010\b"
				+ "ZA02\b"
				+ "20160111\b"
				+ "000000000010135055\b"
				+ "4110080040010190  \b"
				+ "         1.000 \b"
				+ "PAK\b"
				+ "       700.000 \b"
				+ "       700.000 \b"
				+ "G  \b"
				+ "          \b"
				+ "000000\b"
				+ "          \b"
				+ "000000\b"
				+ "       83.50 \b"
				+ "       38.41 \b"
				+ "        0.00 \b"
				+ "        0.00 \b"
				+ "       38.41 \b"
				+ "         38.41 \b"
				+ "CHF  \b"
				+ "1710\b"
				+ "50\b"
				+ "000000\b"
				+ "20210908\b"
				+ "         39.53 \b"
				+ "GL01  \b"
				+ "HOBOTEC SeHoBoShr St vz 4-25TX20        \b"
				+ "          \b"
				+ "\b";
//		StringTokenizer st = new StringTokenizer(s, "\b");
//		int index = 0;
//		while (st.hasMoreElements()) {
//			System.out.println(index++ + ": " + st.nextElement());
//		}
		if (s.endsWith("\b")) { // prevent ignoring the last field is empty
			s = s + " ";
		}
		String[] array = s.split("\b");
		int index = 0;
		for (String v : array) {
			System.out.println(index++ + ": " + v);
		}		
		assertTrue("Split does not work here", index == 32);
	}
	
}
