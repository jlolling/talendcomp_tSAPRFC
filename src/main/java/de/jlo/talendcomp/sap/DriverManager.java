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
	 * Returns the driver implementation
	 * @return driver instance - actually a instance of DriverImpl
	 * @throws Exception
	 */
	public Driver getDriver() throws Exception {
		try {
			Class.forName("com.sap.conn.jco.JCoTable");
		} catch (ClassNotFoundException e) {
			throw new Exception("Classes from sapjco3.jar not available. Please take care the sapjco3.jar is placed in the JRE as endorsed lib", e);
		}
		// this is the part we separate the implementation from the interface
		Driver driver = (Driver) Class.forName("de.jlo.talendcomp.sap.impl.DriverImpl").getDeclaredConstructor().newInstance();
		return driver;
	}
	
}
