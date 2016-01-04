package com.carbonldp.ldp.nonrdf;

import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.FileRepository;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.UUID;

public class SesameNonRDFSourceService extends AbstractSesameLDPService implements NonRDFSourceService {

	protected FileRepository fileRepository;
	protected RDFResourceRepository resourceRepository;

	@Override
	public File getResource( RDFRepresentation rdfRepresentation ) {
		String uuidString = rdfRepresentation.getUUID();
		UUID uuid = UUID.fromString( uuidString );

		return fileRepository.get( uuid );

	}

	@Override
	public boolean isRDFRepresentation( URI targetURI ) {
		RDFSource source = sourceRepository.get( targetURI );
		return source.getTypes().contains( RDFRepresentationDescription.Resource.CLASS.getURI() );
	}

	@Override
	public void deleteResource( RDFRepresentation rdfRepresentation ) {
		String uuidString = rdfRepresentation.getUUID();
		UUID uuid = UUID.fromString( uuidString );
		fileRepository.delete( uuid );
	}

	@Override
	public void replace( RDFRepresentation rdfRepresentation, File requestEntity, String contentType ) {
		DateTime modifiedTime = DateTime.now();

		UUID uuid = fileRepository.save( requestEntity );
		deleteResource( rdfRepresentation );
		setUuid( rdfRepresentation, uuid );
		setContentType( rdfRepresentation, contentType );
		setSize( rdfRepresentation, requestEntity );

		sourceRepository.touch( rdfRepresentation.getURI(), modifiedTime );

	}

	private void setContentType( RDFRepresentation rdfRepresentation, String contentType ) {
		URI rdfRepresentationUri = rdfRepresentation.getURI();
		resourceRepository.remove( rdfRepresentationUri, RDFRepresentationDescription.Property.MEDIA_TYPE.getURI() );
		resourceRepository.add( rdfRepresentationUri, RDFRepresentationDescription.Property.MEDIA_TYPE.getURI(), contentType );
	}

	private void setSize( RDFRepresentation rdfRepresentation, File requestEntity ) {
		URI rdfRepresentationUri = rdfRepresentation.getURI();
		resourceRepository.remove( rdfRepresentationUri, RDFRepresentationDescription.Property.SIZE.getURI() );
		resourceRepository.add( rdfRepresentationUri, RDFRepresentationDescription.Property.SIZE.getURI(), requestEntity.length() );
	}

	private void setUuid( RDFRepresentation rdfRepresentation, UUID uuid ) {
		URI rdfRepresentationUri = rdfRepresentation.getURI();
		resourceRepository.remove( rdfRepresentationUri, RDFRepresentationDescription.Property.UUID.getURI() );
		resourceRepository.add( rdfRepresentationUri, RDFRepresentationDescription.Property.UUID.getURI(), uuid.toString() );
	}

	@Autowired
	public void setFileRepository( FileRepository fileRepository ) { this.fileRepository = fileRepository; }

	@Autowired
	public void setResourceRepository( RDFResourceRepository resourceRepository ) { this.resourceRepository = resourceRepository; }
}
