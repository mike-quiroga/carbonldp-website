package com.base22.carbon.repository;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.base22.carbon.repository.services.RepositoryService;

public class RepositoryServiceFactory {

	static ApplicationContext applicationContext = null;
	static final Logger LOG = LoggerFactory.getLogger(RepositoryServiceFactory.class);

	public static RepositoryService create(String dbToUse, Map<String, RepositoryService> repositoryServices) {
		return repositoryServices.get(dbToUse);
	}

}