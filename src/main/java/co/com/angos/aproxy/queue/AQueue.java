package co.com.angos.aproxy.queue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.angos.aproxy.dto.config.ConfigDTO;
import co.com.angos.aproxy.dto.socket.RequestDTO;
import co.com.angos.aproxy.thread.ThreadProxy;

public class AQueue {

	private static final Logger LOGGER = LogManager.getLogger(AQueue.class);

	private final static Queue<RequestDTO> _QUEUE = new LinkedList<>();
	private final List<ThreadProxy> pool_thread_proxy = new ArrayList<ThreadProxy>();
	private final List<Thread> core_thread = new ArrayList<Thread>();
	private final ConfigDTO config;
	private boolean runnig = false;

	public AQueue(ConfigDTO config) {
		this.config = config;
	}

	public void add_item(RequestDTO request) {
		getQueue().add(request);
	}

	public void stop() {
		this.setIsRunnig(false);
		for (ThreadProxy thread : this.getPool_thread_proxy()) {
			if (thread.isAlive()) {
				thread.interrupt();
			}
		}
		for (Thread thread : this.getCore_thread()) {
			if (thread.isAlive()) {
				thread.interrupt();
			}
		}
	}

	public void run() {
		if (!this.isRunnig()) {
			this.setIsRunnig(true);
			this.start();
			this.start_main();
		}
	}

	private void start() {
		int core_conn = this.getConfig().getAproxy().getDefaulta().getMax_core_connections();
		for (int i : IntStream.range(0, core_conn).toArray()) {
			this.createCoreThread(i);
		}
	}

	private void createCoreThread(int i) {
		Thread thread = new Thread(() -> {
			while (isRunnig()) {
				try {
					if (!getQueue().isEmpty()) {
						RequestDTO request = getQueue().poll();
						ThreadProxy thP = new ThreadProxy(request.getSocket(), "jsonplaceholder.typicode.com", 80);
						this.getPool_thread_proxy().add(thP);
					} else {
						Thread.sleep(500);
					}
				} catch (InterruptedException e) {
					LOGGER.error("Error al intentar ejecutar los hilos dormidos", e);
				}
			}
		});
		thread.start();
		this.getCore_thread().add(thread);
	}

	private void clear_thread_dead() {
		this.getPool_thread_proxy().removeIf(p -> p.isFinish() && !p.isRunning());
	}

	private int get_total_thread_running() {
		return this.getPool_thread_proxy().size();
	}

	private void run_thread_proxy() {
		if (this.getPool_thread_proxy().isEmpty()) {
			return;
		}
		final int max_total_conn = this.getConfig().getAproxy().getDefaulta().getMax_total_connections();
		this.getPool_thread_proxy().forEach(p -> {
			if (!p.isFinish() && !p.isRunning() && get_total_thread_running() < max_total_conn) {
				System.out.println("inciando el th" + p.getName());
				p.start();
			}
		});
		this.clear_thread_dead();
	}

	public void start_main() {
		Thread thread = new Thread(() -> {
			while (isRunnig()) {
				try {
					this.run_thread_proxy();
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					LOGGER.error("Error al intentar ejecutar los hilos dormidos", e);
				}
			}
		});
		thread.start();
	}

	public boolean isRunnig() {
		return runnig;
	}

	public void setIsRunnig(boolean is_runnig) {
		this.runnig = is_runnig;
	}

	public List<ThreadProxy> getPool_thread_proxy() {
		return pool_thread_proxy;
	}

	public List<Thread> getCore_thread() {
		return core_thread;
	}

	public static Queue<RequestDTO> getQueue() {
		return _QUEUE;
	}

	public ConfigDTO getConfig() {
		return config;
	}

}
