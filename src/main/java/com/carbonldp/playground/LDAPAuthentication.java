package com.carbonldp.playground;

import com.carbonldp.agents.LDAPAgent;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import java.util.List;

/**
 * @author JorgeEspinosa
 * @since 0.37.0
 */
public class LDAPAuthentication {
	public static void main( String[] args ) {
		String url = "ldap://localhost:10389";
		String base = "dc=example,dc=com";
		String userDn = "uid=admin,ou=system";
		String password = "secret";
		try {
			LdapContextSource ldapContextSource = new LdapContextSource();
			ldapContextSource.setUrl( url );
			ldapContextSource.setBase( base );
			ldapContextSource.setUserDn( userDn );
			ldapContextSource.setPassword( password );
			ldapContextSource.afterPropertiesSet();
			LdapTemplate ldapTemplate = new LdapTemplate( ldapContextSource );

			AndFilter filter = new AndFilter();
//			filter.and( new EqualsFilter( "objectClass", "person" ) );
			filter.and( new EqualsFilter( "uid", "einstein" ) );
			boolean exists = ldapTemplate.authenticate( "", filter.toString(), "elMismisimoAlbertEinstein" );

			ContactAttributeMapperJSON mapperJSON = new ContactAttributeMapperJSON();
			List<LDAPAgent> list = ldapTemplate.search( "", filter.encode(), mapperJSON );
			System.out.print( list.toString() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
