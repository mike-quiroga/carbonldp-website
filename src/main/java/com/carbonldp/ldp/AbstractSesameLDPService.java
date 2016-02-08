package com.carbonldp.ldp;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFBlankNodeRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.repository.AbstractSesameService;
import com.carbonldp.spring.TransactionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public abstract class AbstractSesameLDPService<E extends BasicContainer> extends AbstractSesameService {
	protected final RDFSourceRepository sourceRepository;
	protected final ContainerRepository containerRepository;
	protected final ACLRepository aclRepository;
	protected RDFDocumentRepository documentRepository;
	protected RDFBlankNodeRepository blankNodeRepository;

	public AbstractSesameLDPService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository ) {
		super( transactionWrapper );
		Assert.notNull( sourceRepository );
		Assert.notNull( containerRepository );
		Assert.notNull( aclRepository );
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
		this.aclRepository = aclRepository;
	}

	@Autowired
	public void setDocumentRepository( RDFDocumentRepository documentRpository ) {
		this.documentRepository = documentRpository;
	}

	@Autowired
	public void setBlankNodeRepository( RDFBlankNodeRepository blankNodeRepository ) {
		this.blankNodeRepository = blankNodeRepository;
	}

}
