package com.carbonldp.authorization.acl;

import com.carbonldp.Consts;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.ldp.sources.RDFSourceRepository;
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
	private RDFSourceRepository sourceRepository;

	public SesameACLRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository, RDFSourceRepository sourceRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
		this.sourceRepository = sourceRepository;
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
	public ACL createACL( URI objectURI ) {
		if ( URIUtil.hasFragment( objectURI ) ) {
			throw new IllegalArgumentException( "Fragments can't be protected with an ACL" );
		}
		URI aclURI = getACLUri( objectURI );
		ACL acl = ACLFactory.create( aclURI, objectURI );
		RDFSource rdfSource = sourceRepository.get( objectURI );
		rdfSource.add( RDFSourceDescription.Property.ACCESS_CONTROL_LIST.getURI(), aclURI );
		documentRepository.addDocument( acl.getDocument() );
		return acl;
	}

	@Override
	public void grantPermissions( URI resourceURI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable ) {
		ACL acl = getResourceACL( resourceURI );
		grantPermissions( acl, subjects, permissions, inheritable );
	}

	@Override
	public void grantPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable ) {
		Map<RDFNodeEnum, Set<URI>> subjectsMap = ACLUtil.getSubjectsMap( subjects );

		for ( RDFNodeEnum subjectClass : subjectsMap.keySet() ) {
			Set<URI> subjectURIs = subjectsMap.get( subjectClass );
			grantPermissions( acl, subjectClass, subjectURIs, permissions, inheritable );
		}

		if ( inheritable ) addInheritablePermissions( acl, subjects, permissions, true );

		documentRepository.update( acl.getDocument() );
	}

	private void grantPermissions( ACL acl, RDFNodeEnum subjectClass, Collection<URI> subjectURIs, Collection<ACEDescription.Permission> permissions, boolean inheritable ) {
		Set<ACE> aces = ACLUtil.getRelatedACEs( acl, subjectClass, subjectURIs );
		if ( aces.isEmpty() ) {
			ACE ace = ACEFactory.getInstance().create( acl, subjectClass, subjectURIs, permissions, true );
			acl.addACEntry( ace.getSubject() );
		} else {
			// TODO: Implement
			throw new NotImplementedException();
		}
	}

	@Override
	public void addInheritablePermissions( URI resourceURI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean granting ) {
		ACL acl = getResourceACL( resourceURI );
		addInheritablePermissions( acl, subjects, permissions, granting );
	}

	@Override
	public void addInheritablePermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean granting ) {
		Map<RDFNodeEnum, Set<URI>> subjectsMap = ACLUtil.getSubjectsMap( subjects );

		for ( RDFNodeEnum subjectClass : subjectsMap.keySet() ) {
			Set<URI> subjectURIs = subjectsMap.get( subjectClass );
			addInheritablePermissions( acl, subjectClass, subjectURIs, permissions, granting );
		}

		documentRepository.update( acl.getDocument() );
	}

	@Override
	public void replace( ACL acl ) {
		documentRepository.update( acl.getDocument() );
	}

	private void addInheritablePermissions( ACL acl, RDFNodeEnum subjectClass, Collection<URI> subjectURIs, Collection<ACEDescription.Permission> permissions, boolean granting ) {
		Set<ACE> aces = ACLUtil.getRelatedInheritableACEs( acl, subjectClass, subjectURIs );
		if ( aces.isEmpty() ) {
			ACE ace = ACEFactory.getInstance().create( acl, subjectClass, subjectURIs, permissions, granting );
			acl.addInheritableEntry( ace.getSubject() );
		} else {
			// TODO: Implement
			throw new NotImplementedException();
		}
	}

	@Override
	public void denyPermissions( URI resourceURI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable ) {
		ACL acl = getResourceACL( resourceURI );
		denyPermissions( acl, subjects, permissions, inheritable );
	}

	@Override
	public void denyPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable ) {

		if ( inheritable ) addInheritablePermissions( acl, subjects, permissions, false );

		// TODO: FT
		throw new NotImplementedException();
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
