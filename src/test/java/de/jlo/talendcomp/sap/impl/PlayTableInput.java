package de.jlo.talendcomp.sap.impl;

import java.util.HashMap;
import java.util.Map;

import de.jlo.talendcomp.sap.ApplicationServerProperties;
import de.jlo.talendcomp.sap.ConnectionProperties;
import de.jlo.talendcomp.sap.Destination;
import de.jlo.talendcomp.sap.Driver;
import de.jlo.talendcomp.sap.DriverManager;
import de.jlo.talendcomp.sap.TableInput;

public class PlayTableInput {
	
	private Map<String, Object> globalMap = new HashMap<>();
	
	public void setupConnection() throws Exception {
		DriverManager.getInstance().loadSapJco3Jar("C:\\TALEND\\TALEND_Custom_Component\\SAP_JAVA_Treiber\\sapjco3-NTintel-3.0.16\\sapjco3.jar");
		Driver driver = DriverManager.getInstance().getDriver();
		ConnectionProperties p = new ApplicationServerProperties()
				.setClient("003")
				.setHost("10.61.0.142")
				.setLanguage("DE")
				.setSystemNumber("03")
				.setUser("RFC_Talend")
				.setPassword("z72hfzQY0Uko1s");
		Destination dest = driver.createDestination(p);
		globalMap.put("destination", dest);
	}

	public void query() throws Exception {
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

	public static void main(String[] args) {
		PlayTableInput pi = new PlayTableInput();
		try {
			System.out.println("Connect...");
			pi.setupConnection();
			System.out.println("Query...");
			pi.query();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
