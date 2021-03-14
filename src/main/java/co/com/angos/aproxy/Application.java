package co.com.angos.aproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.angos.aproxy.thread.ThreadProxy;

public class Application {

	private static Logger LOGGER = LogManager.getLogger(Application.class);
	
	private ServerSocket server;
	private final int localport;
	private final int backlog;

	public Application(int localport, int backlog) {
		if (localport < 0) {
			throw new IllegalArgumentException("insuficient arguments");
		}
		if (backlog < 0) {
			this.backlog = 200;
		} else {
			this.backlog = backlog;
		}
		this.localport = localport;
	}

	private void start() throws IOException {
		this.server = new ServerSocket(this.localport, this.backlog);
	}

	public void run() {
		try {
			String host = "www.google.com";
			int remoteport = 80;
			System.out.println("Starting proxy for " + host + ":" + remoteport + " on port " + localport);

			this.start();

			while (true) {
				final Socket socketAccept = server.accept();
				ThreadProxy threadProxy = new ThreadProxy(socketAccept, host, remoteport);
				threadProxy.start();
			}
		} catch (Exception e) {
			System.err.println(e);
			System.err.println("Usage: java ProxyMultiThread " + "<host> <remoteport> <localport>");
		} finally {
			this.close();
		}
	}

	private void close() {
		try {
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		LOGGER.info("INICIANDO...");
		Application aServer = new Application(8081, 0);
		aServer.run();
		LOGGER.info("INICIADO...");
	}
}