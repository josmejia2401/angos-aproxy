package co.com.angos.aproxy.dto.config;

public class RetryDTO {
	private int max_auto_retry;
	private int[] retryable_status_code = new int[] {};

	public int getMax_auto_retry() {
		return max_auto_retry;
	}

	public void setMax_auto_retry(int max_auto_retry) {
		this.max_auto_retry = max_auto_retry;
	}

	public int[] getRetryable_status_code() {
		return retryable_status_code;
	}

	public void setRetryable_status_code(int[] retryable_status_code) {
		this.retryable_status_code = retryable_status_code;
	}

}
