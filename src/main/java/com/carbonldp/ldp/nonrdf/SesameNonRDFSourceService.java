package com.carbonldp.ldp.nonrdf;

import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.FileRepository;
import org.joda.time.DateTime;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.UUID;

public class SesameNonRDFSourceService extends AbstractSesameLDPService implements NonRDFSourceService {

	protected FileRepository fileRepository;
	protected RDFResourceRepository resourceRepository;

	@Override
	public File getResource( RDFRepresentation rdfRepresentation ) {
		String uuidString = rdfRepresentation.getIdentifier();
		UUID uuid = UUID.fromString( uuidString );

		return fileRepository.get( uuid );
	}

	@Override
	public boolean isRDFRepresentation( IRI targetIRI ) {
		RDFSource source = sourceRepository.get( targetIRI );
		return source.getTypes().contains( RDFRepresentationDescription.Resource.CLASS.getIRI() );
	}

	@Override
	public void replace( RDFRepresentation rdfRepresentation, File requestEntity, String contentType ) {
		DateTime modifiedTime = DateTime.now();
		UUID identifierToAdd = fileRepository.save( requestEntity );

		String identifierToDelete = rdfRepresentation.getIdentifier();
		UUID uuidToDelete = UUID.fromString( identifierToDelete );
		fileRepository.delete( uuidToDelete );

		setFileIdentifier( rdfRepresentation, identifierToAdd );
		setContentType( rdfRepresentation, contentType );
		setSize( rdfRepresentation, requestEntity );

		sourceRepository.touch( rdfRepresentation.getIRI(), modifiedTime );

	}

	private void setContentType( RDFRepresentation rdfRepresentation, String contentType ) {
		IRI rdfRepresentationUri = rdfRepresentation.getIRI();
		resourceRepository.remove( rdfRepresentationUri, RDFRepresentationDescription.Property.MEDIA_TYPE.getIRI() );
		resourceRepository.add( rdfRepresentationUri, RDFRepresentationDescription.Property.MEDIA_TYPE.getIRI(), contentType );
	}

	private void setSize( RDFRepresentation rdfRepresentation, File requestEntity ) {
		IRI rdfRepresentationUri = rdfRepresentation.getIRI();
		resourceRepository.remove( rdfRepresentationUri, RDFRepresentationDescription.Property.SIZE.getIRI() );
		resourceRepository.add( rdfRepresentationUri, RDFRepresentationDescription.Property.SIZE.getIRI(), requestEntity.length() );
	}

	private void setFileIdentifier( RDFRepresentation rdfRepresentation, UUID uuid ) {
		IRI rdfRepresentationUri = rdfRepresentation.getIRI();
		resourceRepository.remove( rdfRepresentationUri, RDFRepresentationDescription.Property.FILE_IDENTIFIER.getIRI() );
		resourceRepository.add( rdfRepresentationUri, RDFRepresentationDescription.Property.FILE_IDENTIFIER.getIRI(), uuid.toString() );
	}

	@Autowired
	public void setFileRepository( FileRepository fileRepository ) { this.fileRepository = fileRepository; }

	@Autowired
	public void setResourceRepository( RDFResourceRepository resourceRepository ) { this.resourceRepository = resourceRepository; }
}
