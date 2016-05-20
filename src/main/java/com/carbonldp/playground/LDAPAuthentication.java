package com.carbonldp.playground;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class LDAPAuthentication {
	public static void main( String[] args ) {
		String url = "ldap://localhost:10389";
		String base = "dc=Apache2,dc=Org2";
		String userDn = "uid=admin,ou=system ";
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
//			filter.and( new EqualsFilter( "dc", "Apache2" ) );
			filter.and( new EqualsFilter( "dc", "Apache2" ) );
			boolean exists = ldapTemplate.authenticate( "", filter.toString(), "" );


			ContactAttributeMapperJSON mapperJSON = new ContactAttributeMapperJSON();
//			List<LDAPAgent> list = ldapTemplate.search( "", filter.encode(), mapperJSON );
//			System.out.print( list.toString() );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
