package co.com.angos.aproxy.queue;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import co.com.angos.aproxy.dto.config.ConfigDTO;
import co.com.angos.aproxy.thread.ThreadProxy;

public class ThreadPoolCustom {

	private static final Logger LOGGER = LogManager.getLogger(ThreadPoolCustom.class);

	private final ConfigDTO config;
	private final ThreadPoolExecutor executorPool;

	public ThreadPoolCustom(ConfigDTO config) {
		this.config = config;
		int max = this.getConfig().getAproxy().getDefaulta().getMax_total_connections() <= 0 ? 1 : this.getConfig().getAproxy().getDefaulta().getMax_total_connections();
		int core = this.getConfig().getAproxy().getDefaulta().getMax_core_connections() <= 0 ? 1 : this.getConfig().getAproxy().getDefaulta().getMax_core_connections();
		int maxQueue = this.getConfig().getAproxy().getDefaulta().getMax_queue_listen() <= 0 ? 1 : this.getConfig().getAproxy().getDefaulta().getMax_queue_listen();
		int keepAlive  = this.getConfig().getAproxy().getDefaulta().getKeep_alive_millis() <= 0 ? 5000 : this.getConfig().getAproxy().getDefaulta().getKeep_alive_millis();
		this.executorPool = new ThreadPoolExecutor(core, max, keepAlive, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(maxQueue));
		LOGGER.info("Se crea El POOL");
	}

	public void addItem(Socket request) {
		Runnable task = new ThreadProxy(this.getConfig(), request);
		this.executorPool.submit(task);
	}

	public void stop() {
		LOGGER.info("Inicia detener pool");
		this.executorPool.shutdownNow();
		LOGGER.info("Finaliza detener pool");
	}

	public ConfigDTO getConfig() {
		return config;
	}

}
