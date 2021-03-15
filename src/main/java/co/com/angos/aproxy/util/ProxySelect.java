package co.com.angos.aproxy.util;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

import co.com.angos.aproxy.dto.config.ConfigDTO;
import co.com.angos.aproxy.dto.config.RouteDTO;

public class ProxySelect {
	private final ConfigDTO config;

	public ProxySelect(ConfigDTO config) {
		this.config = config;
	}
	
	public RouteDTO getRoute(String path) throws Exception {
		try {
			if (this.getConfig().getAproxy().getRoutes() != null && !this.getConfig().getAproxy().getRoutes().isEmpty()) {
				for (RouteDTO route : this.getConfig().getAproxy().getRoutes()) {
					FileSystem fileSystem = FileSystems.getDefault();
					PathMatcher pathMatcher = fileSystem.getPathMatcher("glob:" + route.getPath());
					Path pathCompare = Paths.get(path);
					boolean isMatch = pathMatcher.matches(pathCompare);
					if (isMatch) {
						return route;
					}
				}
				throw new Exception("No existen routes (1) = " + path);
			} else {
				throw new Exception("No existen routes (2) = " + path);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public ConfigDTO getConfig() {
		return config;
	}
}
