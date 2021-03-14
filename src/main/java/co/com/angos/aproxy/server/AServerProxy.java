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
import co.com.angos.aproxy.queue.AQueue;

public class AServerProxy {

	private static Logger LOGGER = LogManager.getLogger(Application.class);
	
	private final AQueue _QUEUE;
	private final ConfigDTO _CONFIG;
	private ServerSocket server;
	private final int localport;
	private final int backlog;

	public AServerProxy(int localport, int backlog) throws IOException {
		if (localport < 0) {
			throw new IllegalArgumentException("insuficient arguments");
		}
		if (backlog < 0) {
			this.backlog = 200;
		} else {
			this.backlog = backlog;
		}
		this.localport = localport;
		this._CONFIG = Config.load();
		this._QUEUE = new AQueue(_CONFIG);
	}

	private void start() throws IOException {
		this.server = new ServerSocket(this.localport, this.backlog);
	}

	public void run() {
		LOGGER.info("Iniciando el servidor...");
		try {
			//String host = "www.google.com";
			//int remoteport = 80;
			this.start();
			while (true) {
				final Socket socketAccept = server.accept();
				RequestDTO r = new RequestDTO();
				r.setSocket(socketAccept);
				this.get_QUEUE().add_item(r);
				this.get_QUEUE().run();
				//ThreadProxy threadProxy = new ThreadProxy(socketAccept, host, remoteport);
				//threadProxy.start();
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

	public AQueue get_QUEUE() {
		return _QUEUE;
	}

	public ConfigDTO get_CONFIG() {
		return _CONFIG;
	}
	
	
}