package com.hbc.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom")
public class CustomConfigUtil {

	@Value("${custom.proxyModel}")
	private String proxyModel;
	
	@Value("${custom.threadNumber}")
	private String threadNumber;
	
	@Value("${custom.DB_ID_PATH}")
	private String DB_ID_PATH;
	
	
	public String getDB_ID_PATH() {
		return DB_ID_PATH;
	}

	public String getThreadNumber() {
		return threadNumber;
	}

	public String getProxyModel() {
		return proxyModel;
	}
}