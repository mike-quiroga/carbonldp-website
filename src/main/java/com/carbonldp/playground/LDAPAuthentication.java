package com.carbonldp.playground;

import com.carbonldp.agents.LDAPAgent;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class LDAPAuthentication {
	public static void main( String[] args ) {
		String url = "ldaps://AD04.corp.base22.com:636";
		String base = "ou=Base22,ou=Accounts,dc=corp,dc=base22,dc=com";
		String userDn = "directory.carbondev@corp.base22.com";
		String password = "EMxG2nJ6UZxZBs";
		try {
			LdapContextSource ldapContextSource = new LdapContextSource();
			ldapContextSource.setUrl( url );
			ldapContextSource.setBase( base );
			ldapContextSource.setUserDn( userDn );
			ldapContextSource.setPassword( password );
			ldapContextSource.afterPropertiesSet();
			LdapTemplate ldapTemplate = new LdapTemplate( ldapContextSource );

			AndFilter filter = new AndFilter();
			filter.and(new EqualsFilter("objectclass", "Person"));
//			OrFilter orFilter = new OrFilter();
//			orFilter.or( new PresentFilter( "uid" ) );
//			boolean exists = ldapTemplate.authenticate( "", filter.toString(), "password" );
			ContactAttributeMapperJSON mapperJSON = new ContactAttributeMapperJSON();
//			mapperJSON.setAgentsContainerIRI( SimpleValueFactory.getInstance().createIRI( "http://www.example.org/" ) );
//			Set<String> fields = new HashSet<>();
//			fields.add( "uid" );
//			fields.add( "mail" );
//			mapperJSON.setUsernameFields( fields );
			List<LDAPAgent> list = ldapTemplate.search( "", filter.encode(), mapperJSON );
			System.out.print( list.toString() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
