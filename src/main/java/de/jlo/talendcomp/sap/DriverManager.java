package de.jlo.talendcomp.sap;

public class DriverManager {
		
	private static DriverManager INSTANCE = null;

	private DriverManager() {
	}
	
	public static DriverManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DriverManager();
		}
		return INSTANCE;
	}
		
	/**
	 * Returns the driver implementation for using sapjco3 driver
	 * @return driver instance - actually a instance of DriverImpl
	 * @throws Exception
	 */
	public Driver getDriverSAPJCO() throws Exception {
		try {
			Class.forName("com.sap.conn.jco.JCoTable");
		} catch (ClassNotFoundException e) {
			throw new Exception("Classes from sapjco3.jar not available.", e);
		}
		// this is the part we separate the implementation from the interface
		Driver driver = (Driver) Class.forName("de.jlo.talendcomp.sap.sapjco.DriverImpl").getDeclaredConstructor().newInstance();
		return driver;
	}
	
	/**
	 * Returns the driver implementation for using service
	 * @return driver instance - actually a instance of DriverImpl
	 * @throws Exception
	 */
	public Driver getDriverService(String serviceUrl, String serviceUser, String servicePassword) throws Exception {
		// Because this class does not expect sapjco3 classes, we can use it here directly
		de.jlo.talendcomp.sap.http.DriverImpl driver = new de.jlo.talendcomp.sap.http.DriverImpl();
		driver.setServiceBaseUrl(serviceUrl);
		driver.setHttpUser(serviceUser);
		driver.setHttpPassword(servicePassword);
		return driver;
	}

}
