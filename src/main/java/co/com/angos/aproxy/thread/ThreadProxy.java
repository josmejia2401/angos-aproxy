package co.com.angos.aproxy.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.angos.aproxy.dto.config.ConfigDTO;
import co.com.angos.aproxy.dto.config.RouteDTO;
import co.com.angos.aproxy.util.UtilidadSocket;

public class ThreadProxy extends Thread {

	private static final Logger LOGGER = LogManager.getLogger(ThreadProxy.class);
	
	private Socket socketAccept;
	private final UtilidadSocket utilidadSocket;
	private final ConfigDTO config;
	private Socket client;
	private InputStream inSocketAccept = null;
	private OutputStream outSocketAccept = null;

	public ThreadProxy(ConfigDTO config, Socket socketAccept) {
		this.config = config;
		this.socketAccept = socketAccept;
		this.utilidadSocket = new UtilidadSocket(this.getConfig());
	}

	private void readInSocketAccept(byte[] bytesAll) throws IOException, Exception {
		try {
			this.getUtilidadSocket().write(bytesAll, client.getOutputStream());
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		}
	}
	private boolean readOutSocketAccept(boolean lastRetry) throws IOException {
		try (final InputStream is = client.getInputStream();) {
			return this.utilidadSocket.readAndWrite(is, outSocketAccept, this.getConfig().getAproxy().getDefaulta().getRetry().getRetryable_status_code(), lastRetry);
		} catch (IOException e) {
			LOGGER.error(e);
			throw e;
		}
	}

	private void closeClient() {
		try {
			if (client != null) {
				client.close();
			}
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	private void close() {
		try {
			this.closeClient();
			if (socketAccept != null) {
				socketAccept.close();
			}
		} catch (IOException e) {
			LOGGER.error(e);
		}
	}
	
	public void cleanAll() {
		this.closeClient();
		socketAccept = null;
		client = null;
		inSocketAccept = null;
		outSocketAccept = null;
	}

	@Override
	public void run() {
		byte[] bytesAll = null;
		try {
			inSocketAccept = socketAccept.getInputStream();
			outSocketAccept = socketAccept.getOutputStream();
			//Object[] result = this.getUtilidadSocket().getBytesAndRouteAndInfo(inSocketAccept);
			Object[] result = this.getUtilidadSocket().getBytesAndRouteAndInfoHeaders(inSocketAccept);
			final RouteDTO route = (RouteDTO) result[0];
			//RequestInfoDTO requestInfo = (RequestInfoDTO) result[1];
			bytesAll = (byte[]) result[2];
			boolean lastRetry = false;
			if (this.getConfig().getAproxy().getDefaulta().getRetry().getMax_auto_retry() > 0) {
				for (int i : IntStream.range(0, this.getConfig().getAproxy().getDefaulta().getRetry().getMax_auto_retry()).toArray()) {
					try {
						this.client = this.getUtilidadSocket().connect(route, i);
						this.readInSocketAccept(bytesAll);
						if (i == (this.getConfig().getAproxy().getDefaulta().getRetry().getMax_auto_retry() -1)) {
							lastRetry = true;
						}
						boolean retry = this.readOutSocketAccept(lastRetry);
						if (retry) {
							continue;
						}
						break;
					} catch (Exception e) {
						this.utilidadSocket.responseWithCode(outSocketAccept, 500, "", "");
					} finally {
						this.closeClient();
					}
				}
			} else {
				try {
					this.client = this.getUtilidadSocket().connect(route, 0);
					this.readInSocketAccept(bytesAll);
					this.readOutSocketAccept(true);
				} catch (Exception e) {
					this.utilidadSocket.responseWithCode(outSocketAccept, 500, "", "");
				} finally {
					this.closeClient();
				}
			}
		} catch (IOException e) {
			LOGGER.error(e);
			this.utilidadSocket.responseWithCode(outSocketAccept, 500, "", "");
		} catch (Exception e) {
			LOGGER.error(e);
			this.utilidadSocket.responseWithCode(outSocketAccept, 500, "", "");
		} finally {
			bytesAll = null;
			this.close();
			this.cleanAll();
		}
	}

	public UtilidadSocket getUtilidadSocket() {
		return utilidadSocket;
	}

	public ConfigDTO getConfig() {
		return config;
	}

	public Socket getSocketAccept() {
		return socketAccept;
	}

	public void setSocketAccept(Socket socketAccept) {
		this.socketAccept = socketAccept;
	}
}
