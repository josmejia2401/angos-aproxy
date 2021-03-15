package co.com.angos.aproxy.dto.socket;

import java.net.Socket;

@Deprecated
public class RequestDTO {

	private Socket socket;

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
