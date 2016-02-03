package com.carbonldp.ldp;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFBlankNode;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.repository.AbstractSesameService;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Set;

public abstract class AbstractSesameLDPService<E extends BasicContainer> extends AbstractSesameService {
	protected final RDFSourceRepository sourceRepository;
	protected final ContainerRepository containerRepository;
	protected final ACLRepository aclRepository;

	public AbstractSesameLDPService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository ) {
		super( transactionWrapper );
		Assert.notNull( sourceRepository );
		Assert.notNull( containerRepository );
		Assert.notNull( aclRepository );
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
		this.aclRepository = aclRepository;
	}

	protected RDFDocument normalizeBNodes( RDFDocument originalDocument, RDFDocument newDocument ) {
		URI documentUri = newDocument.getDocumentResource().getURI();

		Set<String> originalDocumentBlankNodesIdentifier = originalDocument.getBlankNodesIdentifier();
		Set<String> newDocumentBlankNodesIdentifiers = newDocument.getBlankNodesIdentifier();
		for ( String newDocumentBlankNodeIdentifier : newDocumentBlankNodesIdentifiers ) {
			if ( ! originalDocumentBlankNodesIdentifier.contains( newDocumentBlankNodeIdentifier ) ) continue;
			RDFBlankNode newBlankNode = newDocument.getBlankNode( newDocumentBlankNodeIdentifier );

			BNode originalBNode = originalDocument.getBlankNode( newDocumentBlankNodeIdentifier ).getSubject();
			Map<URI, Set<Value>> propertiesMap = newBlankNode.getPropertiesMap();
			Set<URI> properties = propertiesMap.keySet();

			for ( URI property : properties ) {
				Set<Value> objects = propertiesMap.get( property );
				for ( Value object : objects ) {
					newDocument.getBaseModel().add( originalBNode, property, object, documentUri );
				}
			}
			newDocument.subjects().remove( newBlankNode.getSubject() );

		}
		return newDocument;
	}

}
