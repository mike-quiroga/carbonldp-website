package com.carbonldp.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.carbonldp.ConfigurationRepository;

public abstract class AbstractRequestHandler {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());
	protected ConfigurationRepository configurationRepository;

	protected String getTargetURI(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		String platformDomain = configurationRepository.getPlatformURL();
		StringBuilder targetURIBuilder = new StringBuilder();
		targetURIBuilder.append(platformDomain.substring(0, platformDomain.length() - 1)).append(requestURI);
		return targetURIBuilder.toString();
	}

	@Autowired
	public void setConfigurationRepository(ConfigurationRepository configurationRepository) {
		this.configurationRepository = configurationRepository;
	}
}
