package com.carbonldp.ldp;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFBlankNodeRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.repository.AbstractSesameService;
import com.carbonldp.spring.TransactionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSesameLDPService extends AbstractSesameService {
	protected RDFSourceRepository sourceRepository;
	protected ContainerRepository containerRepository;
	protected ACLRepository aclRepository;
	protected RDFDocumentRepository documentRepository;
	protected RDFBlankNodeRepository blankNodeRepository;

	@Autowired
	public void setRDFSourceRepository( RDFSourceRepository sourceRepository ) { this.sourceRepository = sourceRepository; }

	@Autowired
	public void setContainerRepository( ContainerRepository containerRepository ) { this.containerRepository = containerRepository; }

	@Autowired
	public void setACLRepository( ACLRepository aclRepository ) { this.aclRepository = aclRepository; }

	@Autowired
	public void setDocumentRepository( RDFDocumentRepository documentRpository ) { this.documentRepository = documentRpository; }

	@Autowired
	public void setBlankNodeRepository( RDFBlankNodeRepository blankNodeRepository ) { this.blankNodeRepository = blankNodeRepository; }

}
