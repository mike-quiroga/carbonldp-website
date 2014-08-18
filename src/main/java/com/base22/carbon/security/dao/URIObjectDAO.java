package com.base22.carbon.security.dao;

import java.util.Set;

import org.springframework.security.access.prepost.PostAuthorize;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.models.URIObject;

public interface URIObjectDAO {
	public URIObject createURIObject(URIObject uriObject) throws CarbonException;

	@PostAuthorize("returnObject == null or hasPermission(returnObject, 'DISCOVER')")
	public URIObject findByURI(String uri) throws CarbonException;

	public Set<URIObject> getByURIs(String... uris) throws CarbonException;

	public void deleteURIObject(URIObject uriObject, boolean deleteChildren) throws CarbonException;

	public void deleteURIObjects(URIObject... uriObjects) throws CarbonException;
}
