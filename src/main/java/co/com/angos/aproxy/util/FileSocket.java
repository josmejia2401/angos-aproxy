package co.com.angos.aproxy.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collector;

import co.com.angos.aproxy.dto.config.ConfigDTO;
import co.com.angos.aproxy.dto.config.RouteDTO;
import co.com.angos.aproxy.dto.socket.RequestInfoDTO;

public class FileSocket {

	private final byte[] request;
	private final byte[] response;

	private final ConfigDTO config;
	private final ProxySelect proxySelect;
	
	private int indexCurrentHost = 0;

	public FileSocket(ConfigDTO config) {
		this.config = config;
		this.request = new byte[this.config.getAproxy().getDefaulta().getMax_length_recv()];
		this.response = new byte[this.config.getAproxy().getDefaulta().getMax_length_recv()];
		this.proxySelect = new ProxySelect(getConfig());
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

	public void readAndWriteHeaders(InputStream is, OutputStream os) throws IOException, Exception {
		try {
			if (is == null || os == null ) {
				throw new IllegalArgumentException("insuficient arguments");
			}
			int bytesRead;
			boolean replaceHost = false;
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
					System.out.println("==>" + host + "-" + new String(newRequest));
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


	
	public void readAndWrite(InputStream is, OutputStream os) throws IOException {
		try {
			if (is == null || os == null ) {
				throw new IllegalArgumentException("insuficient arguments");
			}
			int bytesRead;
			while ((bytesRead = is.read(request)) != -1) {
				os.write(request, 0, bytesRead);
				os.flush();
				if (is.available() == 0) {
					break;
				}
			}
		} catch (IOException e) {
			throw e;
		}
	}

	public InputStream replaceValueAndInputStream(InputStream is, OutputStream os) throws IOException {
		try {
			//DataInputStream dis = new DataInputStream(is);
			//byte[] targetArray = new byte[is.available()];
			//is.read(targetArray);
			int bytesRead;
			while (true) {
				bytesRead = is.read(request);
				if (bytesRead < 0) {
					break;
				}
				this.replace(request, response, request);
				
				os.write(request, 0, bytesRead);
				os.flush();
				System.out.println("request=>" + bytesRead + "-" + new String(request));
				if (is.available() == 0) {
					break;
				}
			}

		} catch (IOException e) {
			throw e;
		}
		return is;
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
