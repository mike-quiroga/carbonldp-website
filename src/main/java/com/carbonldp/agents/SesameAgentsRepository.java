package com.carbonldp.agents;

import com.carbonldp.ldp.containers.ContainerDescription;
import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.SPARQLUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.spring.SesameConnectionFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

/**
 * @author Nestor
 * @since 0.14.0-ALPHA
 */
public abstract class SesameAgentsRepository extends AbstractSesameRepository implements AgentRepository {
	protected final RDFSourceRepository sourceRepository;
	protected final ContainerRepository containerRepository;

	protected final Type agentsContainerType = Type.BASIC;

	public SesameAgentsRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceRepository, ContainerRepository containerRepository ) {
		super( connectionFactory );
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
	}

	private static final String emailSelector;

	static {
		emailSelector = RDFNodeUtil.generatePredicateStatement( "?members", "?email", AgentDescription.Property.EMAIL );
	}

	private static final String findByUIDQuery;

	static {
		findByUIDQuery = "" +
			"SELECT ?agentIRI" + NEW_LINE +
			"WHERE {" + NEW_LINE +
			TAB + "GRAPH ?agentContainer{" + NEW_LINE +
			TAB + TAB + SPARQLUtil.assignVar( "?hasMemberRelation", ContainerDescription.Property.HAS_MEMBER_RELATION ) + NEW_LINE +
			TAB + TAB + "?agentContainer ?hasMemberRelation ?member" + NEW_LINE +
			TAB + TAB + "?agentContainer ?member ?agentIRI" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			TAB + "GRAPH ?agentIRI{" + NEW_LINE +
			TAB + TAB + SPARQLUtil.assignVar( "?type", RDFSourceDescription.Property.TYPE ) + NEW_LINE +
			TAB + TAB + SPARQLUtil.assignVar( "?ldapAgentType", LDAPAgentDescription.Resource.CLASS ) + NEW_LINE +
			TAB + TAB + "?agentIRI ?type ?ldapAgentType" + NEW_LINE +
			TAB + TAB + SPARQLUtil.assignVar( "?userCredentials", LDAPAgentDescription.Property.USER_CREDENTIALS ) + NEW_LINE +
			TAB + TAB + "?agentIRI ?userCredentials ?bNode" + NEW_LINE +
			TAB + TAB + SPARQLUtil.assignVar( "?ldapAgentUserName", LDAPAgentDescription.UserCredentials.USER_NAME ) + NEW_LINE +
			TAB + TAB + "?bNode ?ldapAgentUserName ?user" + NEW_LINE +
			TAB + "}" + NEW_LINE +
			"}";

	}

	@Override
	public boolean exists( IRI agentIRI ) {
		return containerRepository.hasMember( getAgentsContainerIRI(), agentIRI, agentsContainerType );
	}

	@Override
	public boolean existsWithEmail( String email ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "email", SimpleValueFactory.getInstance().createLiteral( email ) );
		return containerRepository.hasMembers( getAgentsContainerIRI(), emailSelector, bindings );
	}

	@Override
	public Agent get( IRI uri ) {
		return new Agent( sourceRepository.get( uri ) );
	}

	@Override
	public Agent findByEmail( String email ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "email", SimpleValueFactory.getInstance().createLiteral( email ) );

		Set<IRI> memberIRIs = containerRepository.findMembers( getAgentsContainerIRI(), emailSelector, bindings, agentsContainerType );
		if ( memberIRIs.isEmpty() ) return null;
		if ( memberIRIs.size() > 1 ) {
			// TODO: Add error number
			throw new IllegalStateException( "Two agents with the same email were found." );
		}

		IRI agentIRI = memberIRIs.iterator().next();

		RDFSource agentSource = sourceRepository.get( agentIRI );
		if ( agentSource == null ) return null;

		return new Agent( agentSource.getBaseModel(), agentIRI );
	}

	@Override
	public Set<Agent> findByUID( String user ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "user", SimpleValueFactory.getInstance().createLiteral( user ) );

		return sparqlTemplate.executeTupleQuery( findByUIDQuery, bindings, queryResult -> {
			Set<Agent> agents = new HashSet<>();
			while ( queryResult.hasNext() ) {
				IRI agentIRI;
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "agentIRI" );
				if (! ValueUtil.isIRI( member ) ) continue;

				agentIRI = ValueUtil.getIRI( member );
				RDFSource agentSource = sourceRepository.get( agentIRI );
				if ( agentSource == null ) continue;
				agents.add( new LDAPAgent( agentSource.getBaseModel(), agentIRI ) );
			}

			return agents;
		} );
	}

	@Override
	public void create( Agent agent ) {
		containerRepository.createChild( getAgentsContainerIRI(), agent, agentsContainerType );
	}

	protected abstract IRI getAgentsContainerIRI();
}
