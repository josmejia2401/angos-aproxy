package co.com.angos.aproxy.dto.config;

public class ConfigDTO {
	private ApplicationDTO application;
	private ServerDTO server;
	private AProxyDTO aproxy;

	public ApplicationDTO getApplication() {
		return application;
	}

	public void setApplication(ApplicationDTO application) {
		this.application = application;
	}

	public ServerDTO getServer() {
		return server;
	}

	public void setServer(ServerDTO server) {
		this.server = server;
	}

	public AProxyDTO getAproxy() {
		return aproxy;
	}

	public void setAproxy(AProxyDTO aproxy) {
		this.aproxy = aproxy;
	}

}
