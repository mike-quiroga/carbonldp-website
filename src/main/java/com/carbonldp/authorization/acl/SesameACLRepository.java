package com.carbonldp.authorization.acl;

import com.carbonldp.Consts;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.ACLUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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

	@Override
	public ACL createACL( RDFDocument objectDocument ) {
		URI objectURI = objectDocument.getDocumentResource().getURI();
		URI aclURI = getACLUri( objectURI );
		ACL acl = ACLFactory.create( aclURI, objectURI );
		documentRepository.addDocument( acl.getDocument() );
		return acl;
	}

	@Override
	public void grantPermissions( URI resourceURI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions ) {
		ACL acl = getResourceACL( resourceURI );
		grantPermissions( acl, subjects, permissions );
	}

	@Override
	public void grantPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions ) {
		Map<RDFNodeEnum, Set<URI>> subjectsMap = ACLUtil.getSubjectsMap( subjects );

		for ( RDFNodeEnum subjectClass : subjectsMap.keySet() ) {
			Set<URI> subjectURIs = subjectsMap.get( subjectClass );
			grantPermissions( acl, subjectClass, subjectURIs, permissions );
		}

		documentRepository.update( acl.getDocument() );
	}

	private void grantPermissions( ACL acl, RDFNodeEnum subjectClass, Collection<URI> subjectURIs, Collection<ACEDescription.Permission> permissions ) {
		Set<ACE> aces = ACLUtil.getRelevantACEs( acl, subjectClass, subjectURIs );
		if ( aces.isEmpty() ) {
			ACE ace = ACEFactory.create( acl, subjectClass, subjectURIs, permissions, true );
			acl.addACEntry( ace.getURI() );
		} else {
			// Implement
			throw new NotImplementedException();
		}
	}

	@Override
	public void denyPermissions( URI resourceURI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions ) {
		ACL acl = getResourceACL( resourceURI );
		denyPermissions( acl, subjects, permissions );
	}

	@Override
	public void denyPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions ) {
		// TODO
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
