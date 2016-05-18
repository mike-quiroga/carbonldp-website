package com.carbonldp.playground;

import com.carbonldp.agents.LDAPAgent;
import org.openrdf.model.impl.SimpleValueFactory;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class LDAPAuthentication {
	public static void main( String[] args ) {
		String url = "ldap://ldap.forumsys.com:389";
		String base = "dc=example,dc=com";
		String userDn = "cn=read-only-admin,dc=example,dc=com";
		String password = "password";
		try {
			LdapContextSource ldapContextSource = new LdapContextSource();
			ldapContextSource.setUrl( url );
			ldapContextSource.setBase( base );
			ldapContextSource.setUserDn( userDn );
			ldapContextSource.setPassword( password );
			ldapContextSource.afterPropertiesSet();
			LdapTemplate ldapTemplate = new LdapTemplate( ldapContextSource );

			AndFilter filter = new AndFilter();
			filter.and( new EqualsFilter( "uid", "newton" ) );
			OrFilter orFilter = new OrFilter();
			orFilter.or( new PresentFilter( "uid" ) );
			boolean exists = ldapTemplate.authenticate( "", filter.toString(), "password" );
			ContactAttributeMapperJSON mapperJSON = new ContactAttributeMapperJSON();
//			mapperJSON.setAgentsContainerIRI( SimpleValueFactory.getInstance().createIRI( "http://www.example.org/" ) );
//			Set<String> fields = new HashSet<>();
//			fields.add( "uid" );
//			fields.add( "mail" );
//			mapperJSON.setUsernameFields( fields );
			List<LDAPAgent> list = ldapTemplate.search( "", orFilter.encode(), mapperJSON );
			System.out.print( list.toString() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
