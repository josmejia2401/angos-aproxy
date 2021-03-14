package co.com.angos.aproxy.dto.config;

public class AProxyDTO {
	private DefaultDTO defaulta;
	private RouteDTO[] routes;
	
	public DefaultDTO getDefaulta() {
		return defaulta;
	}

	public void setDefaulta(DefaultDTO defaulta) {
		this.defaulta = defaulta;
	}

	public RouteDTO[] getRoutes() {
		return routes;
	}

	public void setRoutes(RouteDTO[] routes) {
		this.routes = routes;
	}

}
