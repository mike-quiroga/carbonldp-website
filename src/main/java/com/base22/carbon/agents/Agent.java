package com.base22.carbon.agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.base22.carbon.CarbonException;
import com.base22.carbon.PrefixedURI;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.authorization.PlatformRole;
import com.base22.carbon.authorization.Privilege;
import com.base22.carbon.authorization.acl.AceSR;
import com.base22.carbon.groups.Group;
import com.base22.carbon.ldp.LDPRSource;
import com.base22.carbon.ldp.LDPResource;
import com.base22.carbon.models.RDFPropertyEnum;
import com.base22.carbon.models.RDFRepresentable;
import com.base22.carbon.models.RDFResourceEnum;
import com.base22.carbon.models.UUIDObject;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class Agent extends UUIDObject implements UserDetails, CredentialsContainer, RDFRepresentable<AgentRDFRepresentation> {

	private static final long serialVersionUID = 1849818115736717525L;

	private String fullName;
	private List<String> emails;

	private String password;
	private String salt;
	private String key;
	private boolean enabled;

	private HashSet<ApplicationRole> applicationRoles;
	private HashSet<Group> groups;

	private HashSet<PlatformRole> platformRoles;
	private HashSet<Privilege> privileges;

	private HashSet<? extends GrantedAuthority> authorities;

	private LDPRSource globalDescription;
	private Map<String, LDPRSource> localDescriptions;

	public Agent() {
		this.emails = new ArrayList<String>();
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getMainEmail() {
		if ( this.emails.isEmpty() ) {
			return null;
		}
		return this.emails.get(0);
	}

	public void setMainEmail(String email) {
		this.emails.add(0, email);
	}

	public List<String> getEmails() {
		return this.emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	// TODO: Get this dynamically
	public String getGlobalRepresentationURI() {
		return "http://carbonldp.com/api/ldp/carbon/agents/" + this.getUuidString();
	}

	public String getLocalRepresentationURI(UUID applicationUUID) {
		StringBuilder uriStringBuilder = new StringBuilder();
		uriStringBuilder.append("http://carbonldp.com/api/").append(applicationUUID.toString()).append("/users/").append(this.getUuidString());
		return uriStringBuilder.toString();
	}

	@Override
	public void eraseCredentials() {
		this.setPassword(null);
		this.setKey(null);
		this.setSalt(null);
	}

	@Override
	public String getUsername() {
		return AceSR.SubjectType.AGENT.getName() + ": " + getUuidString();
	}

	@Override
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public HashSet<ApplicationRole> getApplicationRoles() {
		return applicationRoles;
	}

	public Set<ApplicationRole> getApplicationRoles(Application application) {
		Set<ApplicationRole> applicationRoles = new HashSet<ApplicationRole>();
		for (ApplicationRole applicationRole : getApplicationRoles()) {
			if ( applicationRole.getApplicationUUID().equals(application.getUuid()) ) {
				applicationRoles.add(applicationRole);
			}
		}
		return applicationRoles;
	}

	public void setApplicationRoles(HashSet<ApplicationRole> applicationRoles) {
		this.applicationRoles = applicationRoles;
	}

	public HashSet<Group> getGroups() {
		return groups;
	}

	public void setGroups(HashSet<Group> groups) {
		this.groups = groups;
	}

	public HashSet<PlatformRole> getRoles() {
		return platformRoles;
	}

	public void setRoles(HashSet<PlatformRole> roles) {
		this.platformRoles = roles;
	}

	public HashSet<Privilege> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(HashSet<Privilege> privileges) {
		this.privileges = privileges;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(HashSet<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return false;
	}

	// --- End: Getters/Setters

	public LDPRSource getGlobalDescription() {
		return globalDescription;
	}

	public void setGlobalDescription(LDPRSource globalDescription) {
		this.globalDescription = globalDescription;
	}

	public Map<String, LDPRSource> getLocalDescriptions() {
		return localDescriptions;
	}

	public void setLocalDescriptions(Map<String, LDPRSource> localDescriptions) {
		this.localDescriptions = localDescriptions;
	}

	public static enum Resources implements RDFResourceEnum {
		//@formatter:off
		CLASS(
			new PrefixedURI("cs", "Agent")
		);
		//@formatter:on

		private final PrefixedURI[] prefixedURIs;
		private final Resource[] resources;

		Resources(PrefixedURI... uris) {
			this.prefixedURIs = uris;

			this.resources = new Resource[uris.length];
			for (int i = 0; i < uris.length; i++) {
				this.resources[i] = ResourceFactory.createResource(uris[i].getURI());
			}
		}

		public PrefixedURI getPrefixedURI() {
			return prefixedURIs[0];
		}

		public PrefixedURI[] getPrefixedURIs() {
			return this.prefixedURIs;
		}

		public Resource getResource() {
			return this.resources[0];
		}

		public Resource[] getResources() {
			return this.resources;
		}

		public static Resources findByURI(String uri) {
			for (Resources resource : Resources.values()) {
				for (PrefixedURI resourceURI : resource.getPrefixedURIs()) {
					if ( resourceURI.getURI().equals(uri) || resourceURI.getShortVersion().equals(uri) ) {
						return resource;
					}
				}
			}
			return null;
		}
	}

	// TODO: Finish Vocabulary
	public static enum Properties implements RDFPropertyEnum {
		//@formatter:off
		UUID(
			new PrefixedURI("c", "uuid")
		),
		FULL_NAME(
			new PrefixedURI("foaf", "name")
		),
		EMAIL(
			new PrefixedURI("vcard", "email")
		),
		PASSWORD(
			new PrefixedURI("cs", "password")
		),
		API_KEY(
			new PrefixedURI("cs", "apiKey")
		);
		//@formatter:on

		private final PrefixedURI[] prefixedURIs;
		private final Property[] properties;

		Properties(PrefixedURI... uris) {
			this.prefixedURIs = uris;

			this.properties = new Property[uris.length];
			for (int i = 0; i < uris.length; i++) {
				this.properties[i] = ResourceFactory.createProperty(uris[i].getURI());
			}
		}

		public PrefixedURI getPrefixedURI() {
			return prefixedURIs[0];
		}

		public PrefixedURI[] getPrefixedURIs() {
			return this.prefixedURIs;
		}

		public Property getProperty() {
			return this.properties[0];
		}

		public static Properties findByURI(String uri) {
			for (Properties property : Properties.values()) {
				for (PrefixedURI propertyURI : property.getPrefixedURIs()) {
					if ( propertyURI.getURI().equals(uri) || propertyURI.getShortVersion().equals(uri) ) {
						return property;
					}
				}
			}
			return null;
		}
	}

	@Override
	public void recoverFromLDPR(LDPResource ldpResource) throws CarbonException {
		AgentRDFFactory factory = new AgentRDFFactory();
		AgentRDFRepresentation rdfAgent = factory.create(ldpResource.getResource());

		this.setUuid(rdfAgent.getUUID());

		this.setFullName(rdfAgent.getFullName());
		this.setMainEmail(rdfAgent.getMainEmail());

		this.setPassword(rdfAgent.getPassword());
		this.setKey(rdfAgent.getAPIKey());
	}

	@Override
	public AgentRDFRepresentation createRDFRepresentation() {
		AgentRDFFactory factory = new AgentRDFFactory();
		return factory.create(this);
	}

}
