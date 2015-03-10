package com.carbonldp.authorization.acl;

import com.carbonldp.Consts;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.models.RDFDocument;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import com.carbonldp.utils.URIUtil;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SesameACLRepository extends AbstractSesameLDPRepository implements ACLRepository {

	public SesameACLRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	@Override
	public ACL getResourceACL( URI resourceURI ) {
		// TODO: Decide. Should we validate the document?
		URI aclURI = getACLUri( resourceURI );
		RDFDocument document = documentRepository.getDocument( aclURI );
		if ( document == null ) return null;
		return new ACL( document.getBaseModel(), aclURI );
	}

	private URI getACLUri( URI objectURI ) {
		String objectURIString = URIUtil.getDocumentURI( objectURI.stringValue() );

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( objectURIString );
		if ( ! objectURIString.endsWith( Consts.SLASH ) ) stringBuilder.append( Consts.SLASH );
		stringBuilder.append( Consts.ACL_RESOURCE_SUFFIX ).append( Consts.SLASH );

		return new URIImpl( stringBuilder.toString() );
	}
}
