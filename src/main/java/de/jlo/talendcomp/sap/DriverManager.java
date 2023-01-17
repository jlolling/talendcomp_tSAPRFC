package de.jlo.talendcomp.sap;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class DriverManager {
		
	private static DriverManager INSTANCE = null;
	private static JarClassLoader jarClassLoader = null;
	private static final Object lock = new Object();
	private static boolean sapjcoJarLoaded = false;
	
	private DriverManager() {
		synchronized(lock) {
			if (jarClassLoader == null) {
				jarClassLoader = new JarClassLoader(new URL[0], this.getClass().getClassLoader());
			}
		}
	}
	
	public static DriverManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DriverManager();
		}
		return INSTANCE;
	}
	
	/**
	 * load the sapjco3.jar
	 * @param path to the jar file
	 * @throws Exception if something went wrong
	 */
	public void loadSapJco3Jar(String path) throws Exception {
		synchronized(lock) {
			if (sapjcoJarLoaded == false) {
				if (path == null || path.trim().isEmpty()) {
					throw new IllegalArgumentException("path cannot be null or empty");
				}
				File jarFile = new File(path);
				if (jarFile.getName().equals("sapjco3.jar") == false) {
					throw new Exception("The file: " + jarFile.getAbsolutePath() + " must be named: sapjco3.jar because the driver checks this and will refuse to work otherwise. Stupid? Yes, but SAP request it.");
				}
				if (jarFile.exists() == false) {
					throw new IOException("SAP JCo jar file: " + jarFile.getAbsolutePath() + " does not exist");
				}
				//loadLibrary(jarFile);
				jarClassLoader.addJarFile(jarFile);
				sapjcoJarLoaded = true;
			}
		}
	}
	
	public static void loadLibrary(File file) throws Exception {
	    try {
	    	java.net.URL url = file.toURI().toURL();
	        URLClassLoader jarLoader = new URLClassLoader(new URL[0], Thread.currentThread().getContextClassLoader());
	        java.lang.reflect.Method method = java.net.URLClassLoader.class.getDeclaredMethod("addURL", java.net.URL.class);
	        method.setAccessible(true);
	        method.invoke(jarLoader, url);
	    } catch (Exception ex) {
	        throw new Exception("Cannot load library from jar file '" + file.getAbsolutePath() + "'. Reason: " + ex.getMessage(), ex);
	    }
	}
	
	/**
	 * Returns the driver implementation
	 * @return driver instance - actually a instance of DriverImpl
	 * @throws Exception
	 */
	public Driver getDriver() throws Exception {
		// this is the part we separate the implementation from the interface
		Driver driver = (Driver) Class.forName("de.jlo.talendcomp.sap.impl.DriverImpl").getDeclaredConstructor().newInstance();
		return driver;
	}
	
}
