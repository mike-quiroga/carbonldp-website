package com.carbonldp.playground;

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
			boolean exists = ldapTemplate.authenticate( "", filter.toString(), "password" );
			List<String> list = ldapTemplate.search( "", filter.encode(), new ContactAttributeMapperJSON() );
			System.out.print( list.toString() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
