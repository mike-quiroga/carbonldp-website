package com.carbonldp.playground;

import com.carbonldp.agents.LDAPAgent;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.IRIUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class ContactAttributeMapperJSON implements AttributesMapper {
	public Object mapFromAttributes( Attributes attributes ) throws NamingException {
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
		return jsonObject.toString();
	}

//	ValueFactory valueFactory = SimpleValueFactory.getInstance();
//	IRI agentsContainerIRI;
//	Set<String> usernameFields;
//
//	@Override
//	public LDAPAgent mapFromAttributes( Attributes attributes ) throws NamingException {
//		IRI agentIRI = valueFactory.createIRI( agentsContainerIRI.stringValue() + IRIUtil.createRandomSlug() + "/" );
//		NamingEnumeration<String> ids = attributes.getIDs();
//		RDFResource resource = new RDFResource( agentIRI );
//		LDAPAgent ldapAgent = new LDAPAgent( resource );
//
//		while ( ids.hasMoreElements() ) {
//			String id = ids.next();
//			if(!usernameFields.contains( id ))continue;
//			ldapAgent.addUserCredentials( id, attributes.get( id ).get().toString() );
//		}
//		return ldapAgent;
//	}
//
//	public void setAgentsContainerIRI( IRI agentsContainerIRI ) {
//		this.agentsContainerIRI = agentsContainerIRI;
//	}
//
//	public void setUsernameFields( Set<String> usernameFields ) {
//		this.usernameFields = usernameFields;
//	}
}