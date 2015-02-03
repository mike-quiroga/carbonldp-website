package com.carbonldp.ldp.services;

import org.joda.time.DateTime;
import org.openrdf.model.URI;

import com.carbonldp.commons.models.Container;
import com.carbonldp.commons.models.RDFSource;

public interface RDFSourceService {
	public RDFSource exists(URI sourceURI);

	public RDFSource get(URI sourceURI);

	public DateTime touch(URI sourceURI);

	public DateTime touch(URI sourceURI, DateTime modified);

	public void addAccessPoint(URI sourceURI, Container accessPoint);

	public void update(RDFSource source);

	public void delete(URI sourceURI);
}
