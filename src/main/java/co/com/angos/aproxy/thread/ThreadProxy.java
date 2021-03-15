package co.com.angos.aproxy.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import co.com.angos.aproxy.dto.config.ConfigDTO;
import co.com.angos.aproxy.util.FileSocket;

public class ThreadProxy extends Thread {

	private final Socket socketAccept;
	private final String CLIENT_HOST;
	private final int CLIENT_PORT;
	private final FileSocket file;
	private final ConfigDTO config;
	private Socket client;
	private InputStream inSocketAccept = null;
	private OutputStream outSocketAccept = null;
	private boolean finish = false;
	private boolean running = false;

	public ThreadProxy(ConfigDTO config, Socket socketAccept, String clientHost, int clientPort) {
		this.config = config;
		this.CLIENT_HOST = clientHost;
		this.CLIENT_PORT = clientPort;
		this.socketAccept = socketAccept;
		this.file = new FileSocket(this.config);
	}

	private void readInSocketAccept() throws IOException, Exception {
		try {
			final OutputStream stream = client.getOutputStream();
			this.file.readAndWriteHeaders(inSocketAccept, stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readOutSocketAccept() throws IOException {
		try (final InputStream is = client.getInputStream();) {
			this.file.readAndWrite(is, outSocketAccept);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void connect() {
		try {
			this.client = new Socket(this.CLIENT_HOST, this.CLIENT_PORT);
			this.client.setSoTimeout(this.getConfig().getAproxy().getDefaulta().getConnect_timeout_millis());
			System.out.println("Conectado a " + this.CLIENT_HOST);
		} catch (IOException e) {
			PrintWriter out = new PrintWriter(new OutputStreamWriter(outSocketAccept));
			out.flush();
			throw new RuntimeException(e);
		}
	}

	private void close() throws IOException {
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		socketAccept.close();
	}

	@Override
	public void run() {
		try {
			this.setFinish(false);
			this.setRunning(true);
			inSocketAccept = socketAccept.getInputStream();
			outSocketAccept = socketAccept.getOutputStream();
			this.connect();
			this.readInSocketAccept();
			this.readOutSocketAccept();
			this.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.setFinish(true);
			this.setRunning(false);
		}
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public FileSocket getFile() {
		return file;
	}

	public ConfigDTO getConfig() {
		return config;
	}

}
