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
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.IRI;
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

	public void setAppRoleContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.containerSlug = slug;
	}

	public void setAgentsContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.agentsContainerSlug = slug;
	}

	@Override
	public AppRole get( IRI appRoleIRI ) {
		Assert.notNull( appRoleIRI );

		RDFSource roleSource = sourceRepository.get( appRoleIRI );
		if ( roleSource == null ) return null;
		return new AppRole( roleSource );
	}

	@Override
	public Set<AppRole> get( Agent agent ) {
		Assert.notNull( agent );

		if ( AppContextHolder.getContext().isEmpty() ) throw new IllegalStateException( "This method needs to be called inside of an appContext." );
		App app = AppContextHolder.getContext().getApplication();

		IRI appRolesContainerIRI = getContainerIRI( app.getRootContainerIRI() );

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "agent", agent.getIRI() );

		Set<IRI> appRoleIRIs = containerRepository.findMembers( appRolesContainerIRI, getByAgent_query, bindings, appRolesContainerType );
		Set<RDFSource> appRoleSources = sourceRepository.get( appRoleIRIs );
		return AppRoleFactory.getInstance().get( appRoleSources );
	}

	@Override
	@RunInAppContext
	public Set<AppRole> get( App app, Agent agent ) {
		return get( agent );
	}

	@Override
	public void addAgent( IRI appRoleIRI, Agent agent ) {
		IRI agentsContainer = getAgentsContainerIRI( appRoleIRI );
		containerRepository.addMember( agentsContainer, agent.getIRI() );
	}

	@Override
	public Container createAppRolesContainer( IRI rootContainerIRI ) {
		IRI appRolesContainerIRI = getContainerIRI( rootContainerIRI );
		BasicContainer appRolesContainer = BasicContainerFactory.getInstance().create( new RDFResource( appRolesContainerIRI ) );
		containerRepository.createChild( rootContainerIRI, appRolesContainer );
		return appRolesContainer;
	}

	@Override
	public boolean exists( IRI appRoleIRI ) {
		return sourceRepository.exists( appRoleIRI );
	}

	public IRI getContainerIRI() {
		AppContext appContext = AppContextHolder.getContext();
		if ( appContext.isEmpty() ) throw new IllegalStateException( "The rootContainerIRI cannot be retrieved from the platform context." );
		IRI rootContainerIRI = appContext.getApplication().getRootContainerIRI();
		if ( rootContainerIRI == null ) throw new IllegalStateException( "The app in the AppContext doesn't have a rootContainerIRI." );
		return getContainerIRI( rootContainerIRI );
	}

	private IRI getContainerIRI( IRI rootContainerIRI ) {
		return IRIUtil.createChildIRI( rootContainerIRI, containerSlug );
	}

	public IRI getAgentsContainerIRI( IRI appRoleIRI ) {
		return IRIUtil.createChildIRI( appRoleIRI, agentsContainerSlug );
	}

	static {
		getParentsQuery = "SELECT ?parentIRI\n" +
			"WHERE {\n" +
			"  ?childIRI <" + AppRoleDescription.Property.PARENT_ROLE.getIRI().stringValue() + ">+ ?parentIRI\n" +
			"}";
	}

	@Override
	public Set<IRI> getParentsIRI( IRI appRoleIRI ) {
		Map<String, Value> bindings = new LinkedHashMap<>();

		bindings.put( "childIRI", appRoleIRI );
		return sparqlTemplate.executeTupleQuery( getParentsQuery, bindings, queryResult -> {
			Set<IRI> parents = new HashSet<>();
			while ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "parentIRI" );
				if ( ValueUtil.isIRI( member ) ) parents.add( ValueUtil.getIRI( member ) );
			}

			return parents;
		} );
	}

	@Override
	public void delete( IRI appRoleIRI ) {
		sourceRepository.delete( appRoleIRI );
	}

}
