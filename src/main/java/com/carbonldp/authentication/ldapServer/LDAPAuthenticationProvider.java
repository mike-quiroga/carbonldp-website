package com.carbonldp.authentication.ldapServer;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.LDAPAgent;
import com.carbonldp.agents.LDAPAgentDescription;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.context.RunInAppContext;
import com.carbonldp.authentication.LDAPServer;
import com.carbonldp.authentication.SesameUsernamePasswordAuthenticationProvider;
import com.carbonldp.authentication.token.JWTUtil;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.RunWith;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFBlankNode;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.LDAPUtil;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since 0.37.0
 */
public class LDAPAuthenticationProvider extends SesameUsernamePasswordAuthenticationProvider {

	protected RDFSourceRepository sourceRepository;
	protected TransactionWrapper transactionWrapper;

	public LDAPAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Transactional
	@RunWith( platformRoles = {Platform.Role.SYSTEM} )
	@RunInAppContext
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		if ( AppContextHolder.getContext().isEmpty() ) return null;
		String username = getUsername( authentication );
		String password = getPassword( authentication );

		Set<Agent> agents = agentRepository.findByUID( username );
		if ( agents.isEmpty() ) throw new BadCredentialsException( "Wrong credentials" );
		for ( Agent agent : agents ) {
			if ( authenticate( agent, username, password ) ) return createAgentAuthenticationToken( agent );
		}
		throw new BadCredentialsException( "Wrong credentials" );
	}

	public boolean authenticate( Agent agent, String username, String password ) {
		if ( ! ( agent instanceof LDAPAgent ) ) return false;
		LDAPAgent ldapAgent = (LDAPAgent) agent;
		RDFSource sourceServer = transactionWrapper.runInPlatformContext( () -> sourceRepository.get( ldapAgent.getLDAPServer() ) );
		LDAPServer ldapServer = new LDAPServer( sourceServer );
		String encodedPassword = ldapServer.getPassword();
		ldapServer.setPassword( JWTUtil.decode( encodedPassword ) );
		LdapTemplate ldapTemplate;
		try {
			ldapTemplate = LDAPUtil.getLDAPTemplate( ldapServer );
		} catch ( UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e ) {
			throw new BadCredentialsException( "LDAP exception, wrong credentials", e );
		}

		Set<RDFBlankNode> blankNodes = ldapAgent.getUserCredentials();
		for ( RDFBlankNode blankNode : blankNodes ) {
			String bNodeUsername = blankNode.getString( LDAPAgentDescription.UserCredentials.USER_NAME.getIRI() );
			if ( ! bNodeUsername.equals( username ) ) continue;
			AndFilter filter = new AndFilter();
			String field = blankNode.getString( LDAPAgentDescription.UserCredentials.USER_NAME_FIELD.getIRI() );
			filter.and( new EqualsFilter( field, bNodeUsername ) );
			if ( ldapTemplate.authenticate( "", filter.toString(), password ) ) return true;
		}
		return false;

	}

	@Override
	public boolean supports( Class<?> authentication ) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom( authentication );
	}

	@Autowired
	public void setSourceRepository( RDFSourceRepository sourceRepository ) {
		this.sourceRepository = sourceRepository;
	}

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {this.transactionWrapper = transactionWrapper; }

}
