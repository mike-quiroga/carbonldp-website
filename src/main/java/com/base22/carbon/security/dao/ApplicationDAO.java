package com.base22.carbon.security.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.models.Application;

public interface ApplicationDAO {

	@PreAuthorize("hasAuthority('PRIV_CREATE_APPLICATIONS')")
	public Application createApplication(Application application) throws CarbonException;

	@PostAuthorize("hasPermission(returnObject, 'READ')")
	public Application findByUUID(UUID uuid) throws CarbonException;

	@PostAuthorize("hasPermission(returnObject, 'READ')")
	public Application findBySlug(String slug) throws CarbonException;

	@PostAuthorize("hasPermission(returnObject, 'READ')")
	public Application findByIdentifier(String identifier) throws CarbonException;

	@PreAuthorize("hasAuthority('PRIV_CREATE_APPLICATIONS')")
	public boolean applicationExistsWithSlug(String slug) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ')")
	public List<Application> getApplications() throws CarbonException;
}
