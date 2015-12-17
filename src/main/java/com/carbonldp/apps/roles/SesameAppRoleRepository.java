package com.carbonldp.apps.roles;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.context.RunInAppContext;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.*;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

@Transactional
public class SesameAppRoleRepository extends AbstractSesameLDPRepository implements AppRoleRepository {

	private final RDFSourceRepository sourceRepository;
	private final ContainerRepository containerRepository;

	private String containerSlug;
	private String agentsContainerSlug;

	private static String getParentsQuery;

	private final ContainerDescription.Type appRolesContainerType = ContainerDescription.Type.BASIC;

	public SesameAppRoleRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository, RDFSourceRepository sourceRepository, ContainerRepository containerRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );

		Assert.notNull( sourceRepository );
		Assert.notNull( documentRepository );
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
	}

	private static final String getByAgent_query;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder
			.append( RDFNodeUtil.generatePredicateStatement( "?members", "?agent", AppRoleDescription.Property.AGENT ) )
		;
		getByAgent_query = queryBuilder.toString();
	}

	@Override
	public AppRole get( URI appRoleURI ) {
		Assert.notNull( appRoleURI );

		RDFSource roleSource = sourceRepository.get( appRoleURI );
		if ( roleSource == null ) return null;
		return new AppRole( roleSource );
	}

	@Override
	public Set<AppRole> get( Agent agent ) {
		Assert.notNull( agent );

		if ( AppContextHolder.getContext().isEmpty() ) throw new IllegalStateException( "This method needs to be called inside of an appContext." );
		App app = AppContextHolder.getContext().getApplication();

		URI appRolesContainerURI = getContainerURI( app.getRootContainerURI() );

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "agent", agent.getURI() );

		Set<URI> appRoleURIs = containerRepository.findMembers( appRolesContainerURI, getByAgent_query, bindings, appRolesContainerType );
		Set<RDFSource> appRoleSources = sourceRepository.get( appRoleURIs );
		return AppRoleFactory.getInstance().get( appRoleSources );
	}

	@Override
	@RunInAppContext
	public Set<AppRole> get( App app, Agent agent ) {
		return get( agent );
	}

	@Override
	public void addAgent( URI appRoleURI, Agent agent ) {
		URI agentsContainer = getAgentsContainerURI( appRoleURI );
		containerRepository.addMember( agentsContainer, agent.getURI() );
	}

	@Override
	public Container createAppRolesContainer( URI rootContainerURI ) {
		URI appRolesContainerURI = getContainerURI( rootContainerURI );
		BasicContainer appRolesContainer = BasicContainerFactory.getInstance().create( new RDFResource( appRolesContainerURI ) );
		containerRepository.createChild( rootContainerURI, appRolesContainer );
		return appRolesContainer;
	}

	@Override
	public boolean exists( URI appRoleURI ) {
		return sourceRepository.exists( appRoleURI );
	}

	@Override
	public void delete( URI appRoleURI ) {
		sourceRepository.delete( appRoleURI );
	}

	public void setAppRoleContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.containerSlug = slug;
	}

	public void setAgentsContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.agentsContainerSlug = slug;
	}

	public URI getContainerURI() {
		AppContext appContext = AppContextHolder.getContext();
		if ( appContext.isEmpty() ) throw new IllegalStateException( "The rootContainerURI cannot be retrieved from the platform context." );
		URI rootContainerURI = appContext.getApplication().getRootContainerURI();
		if ( rootContainerURI == null ) throw new IllegalStateException( "The app in the AppContext doesn't have a rootContainerURI." );
		return getContainerURI( rootContainerURI );
	}

	private URI getContainerURI( URI rootContainerURI ) {
		return URIUtil.createChildURI( rootContainerURI, containerSlug );
	}

	public URI getAgentsContainerURI( URI appRoleURI ) {
		return URIUtil.createChildURI( appRoleURI, agentsContainerSlug );
	}

	static {
		getParentsQuery = "SELECT ?parentURI\n" +
			"WHERE {\n" +
			"  ?childURI <" + AppRoleDescription.Property.PARENT_ROLE.getURI().stringValue() + ">+ ?parentURI\n" +
			"}";
	}

	@Override
	public Set<URI> getParentsURI( URI appRoleURI ) {
		Map<String, Value> bindings = new LinkedHashMap<>();

		bindings.put( "childURI", appRoleURI );
		return sparqlTemplate.executeTupleQuery( getParentsQuery, bindings, queryResult -> {
			Set<URI> parents = new HashSet<>();
			while ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "parentURI" );
				if ( ValueUtil.isURI( member ) ) parents.add( ValueUtil.getURI( member ) );
			}

			return parents;
		} );
	}

}
