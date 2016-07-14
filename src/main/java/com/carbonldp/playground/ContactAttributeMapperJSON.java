package com.carbonldp.playground;

import com.carbonldp.agents.LDAPAgent;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 * @author JorgeEspinosa
 * @since 0.37.0
 */
public class ContactAttributeMapperJSON implements AttributesMapper<LDAPAgent> {
	public LDAPAgent mapFromAttributes( Attributes attributes ) throws NamingException {
		NamingEnumeration<String> ids = attributes.getIDs();
		JSONObject jsonObject = new JSONObject();
		while ( ids.hasMoreElements() ) {
			String id = ids.next();
			try {
				jsonObject.put( id, attributes.get( id ).get() );
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
		}
		// return jsonObject.toString();
		return null;
	}
}