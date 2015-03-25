package com.carbonldp.ldp.sources;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.spring.TransactionWrapper;
import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public class SesameRDFSourceService extends AbstractSesameLDPService implements RDFSourceService {

	public SesameRDFSourceService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
	}

	@Override
	public boolean exists( URI sourceURI ) {
		return sourceRepository.exists( sourceURI );
	}

	@Override
	public RDFSource get( URI sourceURI ) {
		return sourceRepository.get( sourceURI );
	}

	@Override
	public DateTime getModified( URI sourceURI ) {
		return sourceRepository.getModified( sourceURI );
	}

	@Override
	public URI getDefaultInteractionModel( URI sourceURI ) {
		return sourceRepository.getDefaultInteractionModel( sourceURI );
	}

	@Override
	public DateTime createAccessPoint( URI parentURI, AccessPoint accessPoint ) {
		// TODO: Move controller validation here
		DateTime creationTime = DateTime.now();

		accessPoint.setTimestamps( creationTime );
		sourceRepository.createAccessPoint( parentURI, accessPoint );
		sourceRepository.touch( parentURI, creationTime );

		return creationTime;
	}

	@Override
	public void touch( URI sourceURI, DateTime now ) {
		// TODO: Check that the RDFSource exists
		sourceRepository.touch( sourceURI, now );
	}

	@Override
	public DateTime replace( RDFSource source ) {
		// TODO: Check that the RDFSource exists

		DateTime modifiedTime = DateTime.now();

		RDFSource originalSource = get( source.getURI() );
		RDFDocument originalDocument = originalSource.getDocument();
		RDFDocument newDocument = source.getDocument();

		Set<Statement> addedStatements = newDocument.stream().filter( statement -> ! originalDocument.contains( statement ) ).collect( Collectors.toSet() );
		Set<Statement> removedStatements = originalDocument.stream().filter( statement -> ! newDocument.contains( statement ) ).collect( Collectors.toSet() );

		LOG.debug( "{}", addedStatements );
		LOG.debug( "{}", removedStatements );
		// TODO: Validate added/removed Statements

		sourceRepository.replace( source );
		sourceRepository.touch( source.getURI(), modifiedTime );

		return modifiedTime;
	}

	@Override
	public void delete( URI sourceURI ) {
		// TODO: Check that the RDFSource exists

		sourceRepository.delete( sourceURI );
		sourceRepository.deleteOccurrences( sourceURI, true );
	}
}
