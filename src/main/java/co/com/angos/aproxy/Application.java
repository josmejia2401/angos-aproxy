package co.com.angos.aproxy;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.angos.aproxy.server.AServerProxy;

public class Application {

	private static final Logger LOGGER = LogManager.getLogger(Application.class);

	public static void main(String[] args) {
		LOGGER.info("INICIANDO...");
		
		try {
			AServerProxy aServer = new AServerProxy(8081, 0);
			aServer.run();
		} catch (IOException e) {
			LOGGER.error("Error al iniciar el servidor", e);
		}
		LOGGER.info("INICIADO...");
	}
}