package co.com.angos.aproxy.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.angos.aproxy.Application;
import co.com.angos.aproxy.config.Config;
import co.com.angos.aproxy.dto.config.ConfigDTO;
import co.com.angos.aproxy.dto.socket.RequestDTO;
import co.com.angos.aproxy.queue.Pool;

public class AServerProxy {

	private static Logger LOGGER = LogManager.getLogger(Application.class);

	private final Pool pool;
	private final ConfigDTO config;
	private ServerSocket server;

	public AServerProxy() throws IOException {
		this.config = Config.load();
		this.pool = new Pool(config);
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
				RequestDTO r = new RequestDTO();
				r.setSocket(socketAccept);
				this.getPool().add_item(r);
				this.getPool().run();
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

	public Pool getPool() {
		return pool;
	}

	public ConfigDTO getConfig() {
		return config;
	}
	
	
}