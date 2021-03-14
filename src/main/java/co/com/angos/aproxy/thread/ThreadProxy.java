package co.com.angos.aproxy.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import co.com.angos.aproxy.util.ReplacingInputStream;

public class ThreadProxy extends Thread {

	private final Socket socketAccept;
	private final String CLIENT_HOST;
	private final int CLIENT_PORT;
	private final byte[] request = new byte[1024];
	private final byte[] response = new byte[4096];
	private Socket client;
	private InputStream inSocketAccept = null;
	private OutputStream outSocketAccept = null;
	private boolean finish = false;
	private boolean running = false;

	public ThreadProxy(Socket socketAccept, String clientHost, int clientPort) {
		this.CLIENT_HOST = clientHost;
		this.CLIENT_PORT = clientPort;
		this.socketAccept = socketAccept;
	}

	private void readInSocketAccept() throws IOException {
		// Thread writeResponse = new Thread(() -> {
		try {
			int bytes_read;
			final OutputStream stream = client.getOutputStream();
			
			/*InputStream ris = new ReplacingInputStream(inSocketAccept, "localhost:8081", "jsonplaceholder.typicode.com");
			while (client.isConnected() && (bytes_read = ris.read(request)) != -1) {
				stream.write(request, 0, bytes_read);
				System.out.println("request" + new String(request));
	        	stream.flush();
				if (inSocketAccept.available() == 0) {
					break;
				}
	        }
			*/
			while (client.isConnected() && (bytes_read = inSocketAccept.read(request)) != -1) {
				stream.write(request, 0, bytes_read);
				stream.flush();
				System.out.println("request=>" + bytes_read + "-"+ new String(request));
				if (inSocketAccept.available() == 0) {
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		 //});
		 //writeResponse.start();
		// inSocketAccept.transferTo(stream);
		// stream.flush();
	}

	private void readOutSocketAccept() throws IOException {
		try (final InputStream is = client.getInputStream();) {
			int bytes_read;
			while (client.isConnected() && (bytes_read = is.read(response)) != -1) {
				outSocketAccept.write(response, 0, bytes_read);
				outSocketAccept.flush();
				if (is.available() == 0) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// byte[] buffer = new byte[stream.available()];
		// stream.read(buffer);
		// outSocketAccept.write(buffer);
		// outSocketAccept.flush();
	}

	private void connect() {
		try {
			client = new Socket(this.CLIENT_HOST, this.CLIENT_PORT);
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

}
