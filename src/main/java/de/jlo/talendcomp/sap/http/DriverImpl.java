package de.jlo.talendcomp.sap.http;

import de.jlo.talendcomp.sap.ConnectionProperties;
import de.jlo.talendcomp.sap.Destination;
import de.jlo.talendcomp.sap.Driver;

public class DriverImpl implements Driver {
	
	private String serviceBaseUrl = null;
	private String httpUser = null;
	private String httpPassword = null;
	private int readTimeout = 0;
	
	public DriverImpl() {}
	
	private HttpClient createHttpClient() throws Exception {
		if (serviceBaseUrl == null) {
			throw new IllegalStateException("serviceBaseUrl not set!");
		}
		return new HttpClient(serviceBaseUrl, httpUser, httpPassword, 10000, readTimeout);
	}
	
	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		if (readTimeout != null && readTimeout > 0) {
			this.readTimeout = readTimeout;
		}
	}

	@Override
	public Destination createDestination(ConnectionProperties connProp) throws Exception {
		DestinationImpl destination = new DestinationImpl(createHttpClient(), connProp);
		return destination;
	}

	public String getServiceBaseUrl() {
		return serviceBaseUrl;
	}

	public void setServiceBaseUrl(String serviceBaseUrl) {
		if (serviceBaseUrl == null || serviceBaseUrl.trim().isEmpty()) {
			throw new IllegalStateException("serviceBaseUrl cannot be null or empty!");
		}
		this.serviceBaseUrl = serviceBaseUrl;
	}

	public String getHttpUser() {
		return httpUser;
	}

	public void setHttpUser(String httpUser) {
		this.httpUser = httpUser;
	}

	public String getHttpPassword() {
		return httpPassword;
	}

	public void setHttpPassword(String httpPassword) {
		this.httpPassword = httpPassword;
	}

}
