package co.com.angos.aproxy.config;

import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.com.angos.aproxy.Application;
import co.com.angos.aproxy.dto.config.ConfigDTO;
import co.com.angos.aproxy.util.LoadFile;

public class Config {

	private static Logger LOGGER = LogManager.getLogger(Application.class);
	
	public static ConfigDTO load() throws IOException {
		LOGGER.info("Cargando configuracion...");
		try {
			InputStream is = LoadFile.loadFromResources("properties.json");
			ObjectMapper mapper = new ObjectMapper();
			ConfigDTO configDTO;
			configDTO = mapper.readValue(is, ConfigDTO.class);
			return configDTO;
		} catch (IOException e) {
			LOGGER.error("Error cargando la configuracion", e);
			throw e;
		} finally {
			LOGGER.info("Configuracion cargada");
		}
	}
}
