package co.com.angos.aproxy.dto.config;

public class DefaultDTO {

	private int connect_timeout_millis = 10000;
	private int socket_timeout_millis = 10000;
	private int keep_alive_millis = 5000;
	private int max_total_connections = 50;
	private int max_core_connections = 1;
	private int max_queue_listen = 2500;
	private int max_length_recv = 2048;
	private boolean reuseAddress = true;
	private RetryDTO retry;

	public int getConnect_timeout_millis() {
		return connect_timeout_millis;
	}

	public void setConnect_timeout_millis(int connect_timeout_millis) {
		this.connect_timeout_millis = connect_timeout_millis;
	}

	public int getSocket_timeout_millis() {
		return socket_timeout_millis;
	}

	public void setSocket_timeout_millis(int socket_timeout_millis) {
		this.socket_timeout_millis = socket_timeout_millis;
	}

	public int getMax_total_connections() {
		return max_total_connections;
	}

	public void setMax_total_connections(int max_total_connections) {
		this.max_total_connections = max_total_connections;
	}

	public int getMax_core_connections() {
		return max_core_connections;
	}

	public void setMax_core_connections(int max_core_connections) {
		this.max_core_connections = max_core_connections;
	}

	public int getMax_queue_listen() {
		return max_queue_listen;
	}

	public void setMax_queue_listen(int max_queue_listen) {
		this.max_queue_listen = max_queue_listen;
	}

	public int getMax_length_recv() {
		return max_length_recv;
	}

	public void setMax_length_recv(int max_length_recv) {
		this.max_length_recv = max_length_recv;
	}

	public RetryDTO getRetry() {
		return retry;
	}

	public void setRetry(RetryDTO retry) {
		this.retry = retry;
	}

	public boolean isReuseAddress() {
		return reuseAddress;
	}

	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}

	public int getKeep_alive_millis() {
		return keep_alive_millis;
	}

	public void setKeep_alive_millis(int keep_alive_millis) {
		this.keep_alive_millis = keep_alive_millis;
	}

}
