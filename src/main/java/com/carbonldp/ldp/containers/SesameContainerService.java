package com.carbonldp.ldp.containers;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Set;

@Transactional
public class SesameContainerService extends AbstractSesameLDPService implements ContainerService {

	public SesameContainerService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
	}

	@Override
	public Container get( URI containerURI, Set<APIPreferences.ContainerRetrievalPreference> containerRetrievalPreferences ) {
		return containerRepository.get( containerURI, containerRetrievalPreferences );
	}

	@Override
	public Set<APIPreferences.ContainerRetrievalPreference> getRetrievalPreferences( URI containerURI ) {
		return containerRepository.getRetrievalPreferences( containerURI );
	}

	@Override
	public ContainerDescription.Type getContainerType( URI containerURI ) {
		return containerRepository.getContainerType( containerURI );
	}

	@Override
	public DateTime createChild( URI containerURI, BasicContainer basicContainer ) {
		DateTime creationTime = DateTime.now();

		basicContainer.setTimestamps( creationTime );
		containerRepository.createChild( containerURI, basicContainer );
		aclRepository.createACL( basicContainer.getDocument() );

		sourceRepository.touch( containerURI, creationTime );

		return creationTime;
	}

	@Override
	public void createNonRDFResource( URI targetURI, URI resourceURI, File resourceFile, String mediaType ) {
		// TODO: Throw the right exceptions
		if ( ! sourceRepository.exists( targetURI ) ) throw new RuntimeException( "The target URI does not exists" );
		if ( sourceRepository.exists( resourceURI ) ) throw new RuntimeException( "The resource already exists" );

		containerRepository.createNonRDFResource( targetURI, resourceURI, resourceFile, mediaType );
	}

	@Override
	public void addMember( URI containerURI, RDFSource member ) {
		// TODO: Check if the container exists
		// TODO: Check if the member exists
		containerRepository.addMember( containerURI, member );
	}

	@Override
	public void removeMembers( URI targetURI ) {
		// TODO: Should the resource be touched here?
		containerRepository.removeMembers( targetURI );
	}

	@Override
	public void deleteContainedResources( URI targetURI ) {
		containerRepository.deleteContainedResources( targetURI );
	}

	@Override
	public void delete( URI targetURI ) {
		sourceRepository.delete( targetURI );
	}
}
