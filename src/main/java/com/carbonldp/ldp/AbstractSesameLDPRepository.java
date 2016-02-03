package com.carbonldp.ldp;

import com.carbonldp.rdf.RDFBlankNodeRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.AbstractSesameRepository;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSesameLDPRepository extends AbstractSesameRepository {

	protected RDFResourceRepository resourceRepository;
	protected RDFDocumentRepository documentRepository;
	protected RDFBlankNodeRepository blankNodeRepository;

	public AbstractSesameLDPRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository,
		RDFDocumentRepository documentRepository ) {
		super( connectionFactory );
		this.resourceRepository = resourceRepository;
		this.documentRepository = documentRepository;
	}

	@Autowired
	public void setBlankNodeRepository( RDFBlankNodeRepository blankNodeRepository ) {
		this.blankNodeRepository = blankNodeRepository;
	}

}
