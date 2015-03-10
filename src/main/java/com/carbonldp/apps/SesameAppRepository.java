package com.carbonldp.apps;

import com.carbonldp.apps.context.RunInAppContext;
import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.BasicContainer;
import com.carbonldp.models.BasicContainerFactory;
import com.carbonldp.models.RDFSource;
import com.carbonldp.models.RDFSourceFactory;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.RepositoryService;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.URIUtil;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Transactional
public class SesameAppRepository extends AbstractSesameRepository implements AppRepository {
	private final RDFDocumentRepository documentRepository;
	private final RDFSourceRepository sourceService;
	private final ContainerRepository containerRepository;
	private final RepositoryService appRepositoryService;
	private URI appsContainerURI;
	private String appsEntryPoint;

	private final Type appsContainerType = Type.BASIC;

	public SesameAppRepository( SesameConnectionFactory connectionFactory, RDFDocumentRepository documentRepository, RDFSourceRepository sourceService,
		ContainerRepository containerRepository, RepositoryService appRepositoryService ) {
		super( connectionFactory );
		this.documentRepository = documentRepository;
		this.sourceService = sourceService;
		this.containerRepository = containerRepository;
		this.appRepositoryService = appRepositoryService;
	}

	@Override
	public boolean exists( URI appURI ) {
		// TODO: This method should ask specifically for an Application Source
		return sourceService.exists( appURI );
	}

	@Override
	public App get( URI appURI ) {
		if ( ! containerRepository.isMember( appsContainerURI, appURI, appsContainerType ) ) return null;

		RDFSource appSource = sourceService.get( appURI );
		if ( appSource == null ) return null;
		return new App( appSource.getBaseModel(), appSource.getURI() );
	}

	private static final String findByRootContainer_selector;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( RDFNodeUtil.generatePredicateStatement( "?members", "?rootContainer", AppDescription.Property.ROOT_CONTAINER ) )
		;
		//@formatter:on
		findByRootContainer_selector = queryBuilder.toString();
	}

	@Override
	public App findByRootContainer( URI rootContainerURI ) {
		Map<String, Value> bindings = new HashMap<String, Value>();
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
	public App create( App app ) {
		createAppRepository( app );

		URI rootContainerURI = forgeRootContainerURI( app );
		app.setRootContainerURI( rootContainerURI );

		containerRepository.createChild( appsContainerURI, app, appsContainerType );

		return app;
	}

	@Override
	@RunInAppContext
	public void initialize( App app ) {
		RDFSource containerSource = RDFSourceFactory.create( app.getRootContainerURI() );
		BasicContainer rootContainer = BasicContainerFactory.create( containerSource );
		documentRepository.addDocument( rootContainer.getDocument() );

		// TODO: Create default resources in the Application's repository
		// -- TODO: Root Container
		// -- TODO: Application Roles Container
		// -- TODO: ACLs
	}

	private void createAppRepository( App app ) {
		String repositoryID = generateAppRepositoryID( app );
		appRepositoryService.createRepository( repositoryID );
		app.setRepositoryID( repositoryID );
	}

	private String generateAppRepositoryID( App app ) {
		return UUID.randomUUID().toString();
	}

	private URI forgeRootContainerURI( App app ) {
		String appSlug = URIUtil.getSlug( app.getURI() );
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append( appsEntryPoint ).append( appSlug );
		return new URIImpl( uriBuilder.toString() );
	}

	public void setAppsContainerURI( URI appsContainerURI ) {
		this.appsContainerURI = appsContainerURI;
	}

	public void setAppsEntryPoint( String appsEntryPoint ) {
		this.appsEntryPoint = appsEntryPoint;
	}
}
