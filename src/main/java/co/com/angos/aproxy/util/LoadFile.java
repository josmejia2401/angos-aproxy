package co.com.angos.aproxy.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoadFile {

	private static Logger LOGGER = LogManager.getLogger(LoadFile.class);
	
	public static InputStream loadFromResources(String nameFile) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream(nameFile);
		return is;
	}

	private static String getFileAsString(String name) {
		try (InputStream in = loadFromResources(name); BufferedReader br = new BufferedReader(new InputStreamReader(in));){
			StringBuilder sb = new StringBuilder();
			String read;
			while ((read = br.readLine()) != null) {
				sb.append(read);
				if (read != null) {
					LOGGER.info(read);
				}
			}
			return sb.toString();
		} catch (IOException e) {
			LOGGER.error(e);
		}
		return "";
	}
	
	public static void printBanner() {
		getFileAsString("banner.txt");
	}
}
