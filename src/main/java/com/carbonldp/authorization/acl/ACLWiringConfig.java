package com.carbonldp.authorization.acl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Configuration that wires ACLPermissionVoters with the ACLRepository. This is needed because the
 * ACLConfig is loaded before any other configuration file. If this wiring was done there it would cause
 * an early loading of the ACLRepository and it wouldn't get wrapped in a proxy. Thus making it incompatible
 * with any AspectJ annotation (@Transactional, @Cacheable, etc).
 *
 * @author MiguelAraCo
 * @see <a href="https://jira.base22.com/browse/LDP-705">LDP-705</a>
 * @since 0.37.1
 */
@Configuration
public class ACLWiringConfig {
	@Autowired
	@Qualifier( "directACLPermissionVoter" )
	private AbstractACLPermissionVoter directACLPermissionVoter;

	@Autowired
	@Qualifier( "inheritanceACLPermissionVoter" )
	private AbstractACLPermissionVoter inheritanceACLPermissionVoter;

	@Autowired
	private ACLRepository aclRepository;

	@PostConstruct
	public void wireACLPermissionVoters() {
		directACLPermissionVoter.setACLRepository( aclRepository );
		inheritanceACLPermissionVoter.setACLRepository( aclRepository );
	}
}
