package de.jlo.talendcomp.sap;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class JarClassLoader extends URLClassLoader {
	
	public JarClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}
	
	public void addJarFile(File jarFile) throws Exception {
		URL url = jarFile.toURI().toURL();
		System.out.println("Add url: " + url);
		super.addURL(url);
	}
	
}
