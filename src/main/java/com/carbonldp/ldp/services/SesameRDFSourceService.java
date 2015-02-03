package com.carbonldp.ldp.services;

import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.commons.models.Container;
import com.carbonldp.commons.models.RDFSource;

@Transactional
public class SesameRDFSourceService extends AbstractSesameLDPService implements RDFSourceService {

	public SesameRDFSourceService(SesameConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	@Override
	public RDFSource exists(URI sourceURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RDFSource get(URI sourceURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime touch(URI sourceURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DateTime touch(URI sourceURI, DateTime modified) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAccessPoint(URI sourceURI, Container accessPoint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(RDFSource source) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(URI sourceURI) {
		// TODO Auto-generated method stub

	}

}
