package co.com.angos.aproxy.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.angos.aproxy.Application;
import co.com.angos.aproxy.config.Config;
import co.com.angos.aproxy.dto.config.ConfigDTO;
import co.com.angos.aproxy.queue.ThreadPoolCustom;
import co.com.angos.aproxy.util.LoadFile;

public class AServerProxy {

	private static Logger LOGGER = LogManager.getLogger(Application.class);

	private final ThreadPoolCustom pool;
	private final ConfigDTO config;
	private ServerSocket server;

	public AServerProxy() throws IOException {
		LoadFile.printBanner();
		this.config = Config.load();
		this.pool = new ThreadPoolCustom(config);
	}

	private void start() throws IOException {
		this.server = new ServerSocket(this.getConfig().getServer().getPort(), this.getConfig().getAproxy().getDefaulta().getMax_queue_listen());
		this.server.setReuseAddress(this.getConfig().getAproxy().getDefaulta().isReuseAddress());
	}

	public void run() {
		LOGGER.info("Iniciando el servidor...");
		try {
			this.start();
			while (true) {
				final Socket socketAccept = server.accept();
				this.getPool().addItem(socketAccept);
			}
		} catch (Exception e) {
			System.err.println(e);
			System.err.println("Usage: java ProxyMultiThread " + "<host> <remoteport> <localport>");
		} finally {
			this.close();
			LOGGER.info("Finaliza el servidor...");
		}
	}

	private void close() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ThreadPoolCustom getPool() {
		return pool;
	}

	public ConfigDTO getConfig() {
		return config;
	}
	
	
}