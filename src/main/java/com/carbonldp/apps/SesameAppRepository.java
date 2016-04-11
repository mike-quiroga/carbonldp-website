package com.carbonldp.apps;

import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.repository.RepositoryService;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.IRIUtil;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Transactional
public class SesameAppRepository extends AbstractSesameRepository implements AppRepository {
	private final RDFDocumentRepository documentRepository;
	private final RDFSourceRepository sourceRepository;
	private final ContainerRepository containerRepository;
	private final RepositoryService appRepositoryService;
	private URI appsContainerURI;
	private String appsEntryPoint;
	protected FileRepository fileRepository;

	private final Type appsContainerType = Type.BASIC;

	public SesameAppRepository( SesameConnectionFactory connectionFactory, RDFDocumentRepository documentRepository, RDFSourceRepository sourceRepository,
		ContainerRepository containerRepository, RepositoryService appRepositoryService ) {
		super( connectionFactory );
		this.documentRepository = documentRepository;
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
		this.appRepositoryService = appRepositoryService;
	}

	@Override
	public boolean exists( URI appURI ) {
		// TODO: This method should ask specifically for an Application Source
		return sourceRepository.exists( appURI );
	}

	@Override
	public App get( URI appURI ) {
		if ( ! containerRepository.hasMember( appsContainerURI, appURI, appsContainerType ) ) return null;

		RDFSource appSource = sourceRepository.get( appURI );
		if ( appSource == null ) return null;
		return new App( appSource.getBaseModel(), appSource.getIRI() );
	}

	private static final String findByRootContainer_selector = "" +
		RDFNodeUtil.generatePredicateStatement( "?members", "?rootContainer", AppDescription.Property.ROOT_CONTAINER );

	@Override
	public App findByRootContainer( URI rootContainerURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "rootContainer", rootContainerURI );

		Set<URI> memberURIs = containerRepository.findMembers( appsContainerURI, findByRootContainer_selector, bindings, appsContainerType );
		if ( memberURIs.isEmpty() ) return null;
		if ( memberURIs.size() > 1 ) {
			// TODO: Add error number
			throw new IllegalStateException( "Two apps with the same root container were found." );
		}

		URI appURI = memberURIs.iterator().next();
		return get( appURI );
	}

	@Override
	public App createPlatformAppRepository( App app ) {
		String repositoryID = generateAppRepositoryID();
		appRepositoryService.createRepository( repositoryID );
		app.setRepositoryID( repositoryID );

		URI rootContainerURI = forgeRootContainerURI( app );
		app.setRootContainerIRI( rootContainerURI );

		return app;
	}

	public void delete( URI appURI ) {
		App app = this.get( appURI );
		sourceRepository.delete( appURI );
		deleteAppRepository( app );
		deleteAppFileDirectory( app );
	}

	@Override
	public URI getPlatformAppContainerURI() {
		return appsContainerURI;
	}

	private void deleteAppRepository( App app ) {
		appRepositoryService.deleteRepository( app.getRepositoryID() );
	}

	private void deleteAppFileDirectory( App app ) {
		fileRepository.deleteDirectory( app );
	}

	private String generateAppRepositoryID() {
		return UUID.randomUUID().toString();
	}

	private URI forgeRootContainerURI( App app ) {
		String appSlug = IRIUtil.getSlug( app.getIRI() );
		return new URIImpl( appsEntryPoint + appSlug );
	}

	public void setAppsContainerURI( URI appsContainerURI ) {
		this.appsContainerURI = appsContainerURI;
	}

	public void setAppsEntryPoint( String appsEntryPoint ) {
		this.appsEntryPoint = appsEntryPoint;
	}

	@Autowired
	public void setFileRepository( FileRepository fileRepository ) {this.fileRepository = fileRepository; }

}
