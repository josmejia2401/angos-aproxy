package co.com.angos.aproxy.util;

import java.io.InputStream;

public class LoadFile {
	
	public static InputStream loadFromResources(String nameFile) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(nameFile);
		return is;
	}
}
