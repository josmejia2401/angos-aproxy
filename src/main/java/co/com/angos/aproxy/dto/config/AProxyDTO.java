package co.com.angos.aproxy.dto.config;

import java.util.List;

public class AProxyDTO {
	private DefaultDTO defaulta;
	private List<RouteDTO> routes;

	public DefaultDTO getDefaulta() {
		return defaulta;
	}

	public void setDefaulta(DefaultDTO defaulta) {
		this.defaulta = defaulta;
	}

	public List<RouteDTO> getRoutes() {
		return routes;
	}

	public void setRoutes(List<RouteDTO> routes) {
		this.routes = routes;
	}

}
