package de.jlo.talendcomp.sap.http;

import de.jlo.talendcomp.sap.ConnectionProperties;
import de.jlo.talendcomp.sap.Destination;
import de.jlo.talendcomp.sap.Driver;

public class DriverImpl implements Driver {
	
	private String serviceBaseUrl = null;
	
	public DriverImpl(String serviceBaseUrl) throws Exception {
		this.serviceBaseUrl = serviceBaseUrl;
	}
	
	private HttpClient createHttpClient() throws Exception {
		return new HttpClient(serviceBaseUrl, null, null, 100, 100);
	}
	
	@Override
	public Destination createDestination(ConnectionProperties connProp) throws Exception {
		DestinationImpl destination = new DestinationImpl(createHttpClient(), connProp);
		return destination;
	}

}
