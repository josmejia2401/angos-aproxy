package co.com.angos.aproxy.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.Collector;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import co.com.angos.aproxy.dto.config.ConfigDTO;
import co.com.angos.aproxy.dto.config.RouteDTO;
import co.com.angos.aproxy.dto.socket.RequestInfoDTO;
import co.com.angos.aproxy.dto.socket.ResponseInfoDTO;

public class UtilidadSocket {

	private final byte[] request;
	private final byte[] response;

	private final ConfigDTO config;
	private final ProxySelect proxySelect;
	
	private int indexCurrentHost = 0;

	public UtilidadSocket(ConfigDTO config) {
		this.config = config;
		this.request = new byte[this.config.getAproxy().getDefaulta().getMax_length_recv()];
		this.response = new byte[this.config.getAproxy().getDefaulta().getMax_length_recv()];
		this.proxySelect = new ProxySelect(getConfig());
	}
	
	public Socket connect(RouteDTO route, int index) {
		try {
			String currentUrl = route.getUrl()[index]; 
			final URL url = new URL(currentUrl);
			int port = url.getPort();
			if (port == -1) {
				if (url.getProtocol().equalsIgnoreCase("https")) {
					port = 443;
					SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
			        SSLSocket client = (SSLSocket)factory.createSocket(url.getHost(), 443);
			        client.startHandshake();
			        client.setSoTimeout(this.getConfig().getAproxy().getDefaulta().getConnect_timeout_millis());
			        return client;
				} else {
					Socket client = new Socket(url.getHost(), 80);
					client.setSoTimeout(this.getConfig().getAproxy().getDefaulta().getConnect_timeout_millis());
					return client;
				}
			} else {
				Socket client = new Socket(url.getHost(), port);
				client.setSoTimeout(this.getConfig().getAproxy().getDefaulta().getConnect_timeout_millis());
				return client;
			}
		} catch (IOException e) {
			//PrintWriter out = new PrintWriter(new OutputStreamWriter(outSocketAccept));
			//out.flush();
			throw new RuntimeException(e);
		}
	}
	
	private RequestInfoDTO getRequestInfo(byte[] newRequest) {
		String requestAsString = new String(newRequest);
		String[] allLine = requestAsString.split("\n");
		String firstLine = allLine[0];
		String method = firstLine.split(" ")[0];
		String path = firstLine.split(" ")[1];
		String protocol = firstLine.split(" ")[2];
		RequestInfoDTO requestInfo = new RequestInfoDTO();
		requestInfo.setMethod(method);
		requestInfo.setPath(path);
		requestInfo.setProtocol(protocol);
		return requestInfo;
	}
	
	private byte[] updateRequest(String host, String newHost) {
		byte[] find = host.getBytes(); 
		byte[] replace = newHost.getBytes();
		byte[] newRequest = this.replace(request, find, replace);
		return newRequest;
	}
	
	private RouteDTO getRoute(RequestInfoDTO requestInfo) throws Exception {
		return this.proxySelect.getRoute(requestInfo.getPath());
	}
	
	
	public void responseWithCode(OutputStream os, int statusCode, String statusText, String body) {
		try {
			os.write(String.format("HTTP/1.1 %d %s\r\n", statusCode, statusText).getBytes());
	        os.write("Content-Type: text/plain\r\n".getBytes());
	        os.write("Connection: close\r\n".getBytes());
	        os.write("\r\n".getBytes());
	        os.write(String.format("%s", body).getBytes());
		} catch (IOException e) {
			
		}
	}
	
	public Object[] getBytes(InputStream is) throws IOException, Exception {
		try {
			if (is == null) {
				throw new IllegalArgumentException("insuficient arguments");
			}
			int bytesRead;
			int available = is.available();
			boolean replaceHost = false;
			ByteBuffer buff = null;
			RequestInfoDTO requestInfo = null;
			RouteDTO route = null;
			while ((bytesRead = is.read(request)) != -1) {
				if (!replaceHost) {
					replaceHost = true;
					String host = String.format("%s:%s", this.getConfig().getServer().getHostname(), this.getConfig().getServer().getPort());
					requestInfo = this.getRequestInfo(request);
					route = this.getRoute(requestInfo);
					String newHost = route.getUrl()[indexCurrentHost];
					newHost = new URL(newHost).getHost();
					byte[] newRequest = updateRequest(host, newHost);
					if (newRequest.length >= available) {
						buff = ByteBuffer.wrap(new byte[newRequest.length]);
					} else if (newRequest.length <= bytesRead) {
						int diff = Math.abs(bytesRead - newRequest.length);
						int newAvailable = available - diff;
						buff = ByteBuffer.wrap(new byte[newAvailable]);
					} else  {
						int diff = Math.abs(bytesRead - newRequest.length);
						int newAvailable = available + diff;
						buff = ByteBuffer.wrap(new byte[newAvailable]);
					}
					buff.put(newRequest);
				} else {
					buff.put(request);
				}
				if (is.available() == 0) {
					break;
				}
			}
			return new Object[] { route, requestInfo, buff.array() };
		} catch (IOException e) {
			throw e;
		}
	}
	
	
	

	public void readAndWriteHeaders(InputStream is, OutputStream os) throws IOException, Exception {
		try {
			if (is == null || os == null ) {
				throw new IllegalArgumentException("insuficient arguments");
			}
			int bytesRead;
			boolean replaceHost = false;
			byte[] requestAsByte = null;
			while ((bytesRead = is.read(request)) != -1) {
				if (!replaceHost) {
					replaceHost = true;
					String host = String.format("%s:%s", this.getConfig().getServer().getHostname(), this.getConfig().getServer().getPort());
					RequestInfoDTO requestInfo = this.getRequestInfo(request);
					RouteDTO route = this.getRoute(requestInfo);
					String newHost = route.getUrl()[indexCurrentHost];
					newHost = new URL(newHost).getHost();
					byte[] newRequest = updateRequest(host, newHost);
					bytesRead = newRequest.length;
					os.write(newRequest);
				} else {
					os.write(request, 0, bytesRead);
				}
				os.flush();
				if (is.available() == 0) {
					break;
				}
			}
		} catch (IOException e) {
			throw e;
		}
	}

	
	public void write(byte[] is, OutputStream os) throws IOException {
		try {
			if (is == null || os == null ) {
				throw new IllegalArgumentException("insuficient arguments");
			}
			os.write(is, 0, is.length);
			os.flush();
		} catch (IOException e) {
			throw e;
		}
	}
	
	private ResponseInfoDTO getRequestInfoResponse(byte[] newRequest) {
		String requestAsString = new String(newRequest);
		String[] allLine = requestAsString.split("\n");
		String firstLine = allLine[0];
		String protocol = firstLine.split(" ")[0];
		String statusCode = firstLine.split(" ")[1];
		String statusText = firstLine.split(" ")[2];
		ResponseInfoDTO requestInfo = new ResponseInfoDTO();
		requestInfo.setProtocol(protocol);
		requestInfo.setStatusCode(statusCode);
		requestInfo.setStatusText(statusText);
		return requestInfo;
	}
	
	public boolean readAndWrite(InputStream is, OutputStream os, int[] codesRetry, boolean lastRetry) throws IOException {
		try {
			if (is == null || os == null ) {
				throw new IllegalArgumentException("insuficient arguments");
			}
			int bytesRead;
			boolean getStatus = false;
			while ((bytesRead = is.read(request)) != -1) {
				if (!getStatus && !lastRetry) {
					getStatus = true;
					ResponseInfoDTO responseInfo = this.getRequestInfoResponse(request);
					if (codesRetry != null && codesRetry.length > 0) {
						for (int retry : codesRetry) {
							if (String.valueOf(retry).equalsIgnoreCase(responseInfo.getStatusCode())) {
								return true;
							}
						}
					}
				}
				os.write(request, 0, bytesRead);
				os.flush();
				if (is.available() == 0) {
					break;
				}
			}
			return false;
		} catch (IOException e) {
			throw e;
		}
	}

	public byte[] replace(byte[] src, byte[] find, byte[] replace) {
		String replaced = cutBrackets(Arrays.toString(src)).replace(cutBrackets(Arrays.toString(find)), cutBrackets(Arrays.toString(replace)));
		return Arrays.stream(replaced.split(", ")).map(Byte::valueOf).collect(toByteArray());
	}

	private String cutBrackets(String s) {
		return s.substring(1, s.length() - 1);
	}

	private Collector<Byte, ?, byte[]> toByteArray() {
		return Collector.of(ByteArrayOutputStream::new, ByteArrayOutputStream::write, (baos1, baos2) -> {
			try {
				baos2.writeTo(baos1);
				return baos1;
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}, ByteArrayOutputStream::toByteArray);
	}

	public byte[] getRequest() {
		return request;
	}

	public byte[] getResponse() {
		return response;
	}

	public ConfigDTO getConfig() {
		return config;
	}
}
