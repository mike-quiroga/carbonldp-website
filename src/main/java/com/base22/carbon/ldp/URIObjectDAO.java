package com.base22.carbon.ldp;

import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;

import com.base22.carbon.CarbonException;
import com.base22.carbon.jdbc.UpdateTransactionTemplate;
import com.base22.carbon.ldp.models.URIObject;

public interface URIObjectDAO {
	public URIObject createURIObject(URIObject uriObject) throws CarbonException;

	public List<URIObject> createURIObjects(List<URIObject> uriObject) throws CarbonException;

	public boolean uriObjectsExist(final List<String> uris) throws CarbonException;

	@PostAuthorize("returnObject == null or hasPermission(returnObject, 'DISCOVER')")
	public URIObject findByURI(String uri) throws CarbonException;

	public List<URIObject> getByURIs(final List<String> uris) throws CarbonException;

	public void deleteURIObject(URIObject uriObject, boolean deleteChildren) throws CarbonException;

	public void deleteURIObject(URIObject uriObject, boolean deleteChildren, UpdateTransactionTemplate template) throws CarbonException;

	public void deleteURIObjects(URIObject... uriObjects) throws CarbonException;
}
