package com.carbonldp.authorization.acl;

import com.carbonldp.Consts;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.ACLUtil;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@CacheConfig( cacheNames = "acl" )
@Transactional
public class SesameACLRepository extends AbstractSesameLDPRepository implements ACLRepository {
	private RDFSourceRepository sourceRepository;

	public SesameACLRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository, RDFSourceRepository sourceRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
		this.sourceRepository = sourceRepository;
	}

	@Override
	public ACL getResourceACL( IRI resourceIRI ) {
		IRI aclIRI = getACLUri( resourceIRI );
		RDFDocument document = documentRepository.getDocument( aclIRI );
		if ( document == null || document.isEmpty() ) return null;
		return new ACL( document.getBaseModel(), aclIRI );
	}

	@CacheEvict
	@Override
	public ACL createACL( IRI objectIRI ) {
		if ( IRIUtil.hasFragment( objectIRI ) ) {
			throw new IllegalArgumentException( "Fragments can't be protected with an ACL" );
		}
		if ( ! sourceRepository.exists( objectIRI ) ) {
			throw new RuntimeException( "Unable to find document to protect" );
		}
		IRI aclIRI = getACLUri( objectIRI );
		ACL acl = ACLFactory.create( aclIRI, objectIRI );
		resourceRepository.add( objectIRI, RDFSourceDescription.Property.ACCESS_CONTROL_LIST.getIRI(), aclIRI );
		documentRepository.addDocument( acl.getDocument() );
		return acl;
	}

	@CacheEvict
	@Override
	public void grantPermissions( IRI resourceIRI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable ) {
		ACL acl = getResourceACL( resourceIRI );
		grantPermissions( acl, subjects, permissions, inheritable );
	}

	@CacheEvict
	@Override
	public void grantPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable ) {
		Map<RDFNodeEnum, Set<IRI>> subjectsMap = ACLUtil.getSubjectsMap( subjects );

		for ( RDFNodeEnum subjectClass : subjectsMap.keySet() ) {
			Set<IRI> subjectIRIs = subjectsMap.get( subjectClass );
			grantPermissions( acl, subjectClass, subjectIRIs, permissions, inheritable );
		}

		if ( inheritable ) addInheritablePermissions( acl, subjects, permissions, true );

		documentRepository.update( acl.getDocument() );
	}

	private void grantPermissions( ACL acl, RDFNodeEnum subjectClass, Collection<IRI> subjectIRIs, Collection<ACEDescription.Permission> permissions, boolean inheritable ) {
		Set<ACE> aces = ACLUtil.getRelatedACEs( acl, subjectClass, subjectIRIs );
		if ( aces.isEmpty() ) {
			ACE ace = ACEFactory.getInstance().create( acl, subjectClass, subjectIRIs, permissions, true );
			acl.addACEntry( ace.getSubject() );
		} else {
			Set<Resource> subjectsACEs = new HashSet<>();
			for ( ACE ace : aces ) {
				subjectsACEs.add( ace.getSubject() );
			}
			acl.setACEntries( subjectsACEs );
		}
	}

	@CacheEvict
	@Override
	public void addInheritablePermissions( IRI resourceIRI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean granting ) {
		ACL acl = getResourceACL( resourceIRI );
		addInheritablePermissions( acl, subjects, permissions, granting );
	}

	@CacheEvict
	@Override
	public void addInheritablePermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean granting ) {
		Map<RDFNodeEnum, Set<IRI>> subjectsMap = ACLUtil.getSubjectsMap( subjects );

		for ( RDFNodeEnum subjectClass : subjectsMap.keySet() ) {
			Set<IRI> subjectIRIs = subjectsMap.get( subjectClass );
			addInheritablePermissions( acl, subjectClass, subjectIRIs, permissions, granting );
		}

		documentRepository.update( acl.getDocument() );
	}

	@CacheEvict
	@Override
	public void replace( ACL acl ) {
		documentRepository.update( acl.getDocument() );
	}

	private void addInheritablePermissions( ACL acl, RDFNodeEnum subjectClass, Collection<IRI> subjectIRIs, Collection<ACEDescription.Permission> permissions, boolean granting ) {
		Set<ACE> aces = ACLUtil.getRelatedInheritableACEs( acl, subjectClass, subjectIRIs );
		if ( aces.isEmpty() ) {
			ACE ace = ACEFactory.getInstance().create( acl, subjectClass, subjectIRIs, permissions, granting );
			acl.addInheritableEntry( ace.getSubject() );
		} else {
			Set<Resource> subjectsACEs = new HashSet<>();
			for ( ACE ace : aces ) {
				subjectsACEs.add( ace.getSubject() );
			}
			acl.setACEntries( subjectsACEs );
		}
	}

	@CacheEvict
	@Override
	public void denyPermissions( IRI resourceIRI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable ) {
		ACL acl = getResourceACL( resourceIRI );
		denyPermissions( acl, subjects, permissions, inheritable );
	}

	@CacheEvict
	@Override
	public void denyPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable ) {

		if ( inheritable ) addInheritablePermissions( acl, subjects, permissions, false );

		// TODO: FT
		throw new NotImplementedException();
	}

	private IRI getACLUri( IRI objectIRI ) {
		String objectIRIString = IRIUtil.getDocumentIRI( objectIRI.stringValue() );

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append( objectIRIString );
		if ( ! objectIRIString.endsWith( Consts.SLASH ) ) stringBuilder.append( Consts.SLASH );
		stringBuilder.append( Consts.ACL_RESOURCE_SUFFIX ).append( Consts.SLASH );

		return SimpleValueFactory.getInstance().createIRI( stringBuilder.toString() );
	}
}
