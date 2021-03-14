package co.com.angos.aproxy.dto.config;

public class RouteDTO {

	private String id;
	private String id_service;
	private String path;
	private String[] url;
	private boolean retry = true;
	private String desc = "https://jsonplaceholder.typicode.com/";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId_service() {
		return id_service;
	}

	public void setId_service(String id_service) {
		this.id_service = id_service;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String[] getUrl() {
		return url;
	}

	public void setUrl(String[] url) {
		this.url = url;
	}

	public boolean isRetry() {
		return retry;
	}

	public void setRetry(boolean retry) {
		this.retry = retry;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
