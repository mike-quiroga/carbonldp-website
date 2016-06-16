package com.carbonldp.apps;

import com.carbonldp.Vars;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.repository.RepositoryService;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.RunnableFuture;

// TODO: make sure that get app methods are running in platform context
@Transactional
public class SesameAppRepository extends AbstractSesameRepository implements AppRepository {
	private final RDFDocumentRepository documentRepository;
	private final RDFSourceRepository sourceRepository;
	private final ContainerRepository containerRepository;
	private final RepositoryService appRepositoryService;
	private IRI appsContainerIRI;
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
	public boolean exists( IRI appIRI ) {
		// TODO: This method should ask specifically for an Platform Source
		return sourceRepository.exists( appIRI );
	}

	@Override
	public App get( IRI appIRI ) {
		if ( ! containerRepository.hasMember( appsContainerIRI, appIRI, appsContainerType ) ) return null;

		RDFSource appSource = sourceRepository.get( appIRI );
		if ( appSource == null ) return null;
		return new App( appSource.getBaseModel(), appSource.getIRI() );
	}

	@Override
	public Set<App> get( Set<IRI> appIRIs ) {
		Set<RDFSource> appSet = sourceRepository.get( appIRIs );
		Set<App> apps = new HashSet<>();
		for ( RDFSource currentApp : appSet ) {
			App app = new App( currentApp );
			apps.add( app );
		}
		return apps;
	}

	@Override
	public Set<App> getAll() {
		ValueFactory valueFactory = SimpleValueFactory.getInstance();
		IRI platformAppsContainer = valueFactory.createIRI( Vars.getInstance().getHost() + Vars.getInstance().getMainContainer() + Vars.getInstance().getAppsContainer() );
		Set<IRI> appIRIs = containerRepository.getContainedIRIs( platformAppsContainer );
		return get( appIRIs );
	}

	private static final String findByRootContainer_selector = "" +
		RDFNodeUtil.generatePredicateStatement( "?members", "?rootContainer", AppDescription.Property.ROOT_CONTAINER );

	@Override
	public App findByRootContainer( IRI rootContainerIRI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "rootContainer", rootContainerIRI );

		Set<IRI> memberIRIs = containerRepository.findMembers( appsContainerIRI, findByRootContainer_selector, bindings, appsContainerType );
		if ( memberIRIs.isEmpty() ) return null;
		if ( memberIRIs.size() > 1 ) {
			// TODO: Add error number
			throw new IllegalStateException( "Two apps with the same root container were found." );
		}

		IRI appIRI = memberIRIs.iterator().next();
		return get( appIRI );
	}

	@Override
	public App createPlatformAppRepository( App app ) {
		String repositoryID = generateAppRepositoryID();
		appRepositoryService.createRepository( repositoryID );
		app.setRepositoryID( repositoryID );

		IRI rootContainerIRI = forgeRootContainerIRI( app );
		app.setRootContainerIRI( rootContainerIRI );

		return app;
	}

	public void delete( IRI appIRI ) {
		App app = this.get( appIRI );
		sourceRepository.delete( appIRI, true );
		deleteAppRepository( app );
		deleteAppFileDirectory( app );
	}

	@Override
	public IRI getPlatformAppContainerIRI() {
		return appsContainerIRI;
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

	private IRI forgeRootContainerIRI( App app ) {
		String appSlug = IRIUtil.getSlug( app.getIRI() );
		return SimpleValueFactory.getInstance().createIRI( appsEntryPoint + appSlug );
	}

	public void setAppsContainerIRI( IRI appsContainerIRI ) {
		this.appsContainerIRI = appsContainerIRI;
	}

	public void setAppsEntryPoint( String appsEntryPoint ) {
		this.appsEntryPoint = appsEntryPoint;
	}

	@Autowired
	public void setFileRepository( FileRepository fileRepository ) {this.fileRepository = fileRepository; }

}
