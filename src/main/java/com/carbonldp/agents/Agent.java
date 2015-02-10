package com.carbonldp.agents;

import java.util.Collection;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.carbonldp.commons.models.BasicContainer;

public class Agent extends BasicContainer implements UserDetails, CredentialsContainer {

	private static final long serialVersionUID = - 789740820771320672L;

	public Agent(AbstractModel model, URI subj) {
		super(model, subj);
	}

	public String getName() {
		return this.getString(AgentDescription.Property.NAME);
	}

	public Set<String> getEmails() {
		return this.getStrings(AgentDescription.Property.EMAIL);
	}

	public String getSalt() {
		return this.getString(AgentDescription.Property.SALT);
	}

	@Override
	public String getPassword() {
		return this.getString(AgentDescription.Property.PASSWORD);
	}

	public Set<URI> getPlatformRoles() {
		return this.getURIs(AgentDescription.Property.PLATFORM_ROLE);
	}

	public boolean addEmail(String email) {
		return this.set(AgentDescription.Property.EMAIL.getURI(), email);
	}

	public boolean addPlatformRole(URI platformRole) {
		return this.add(AgentDescription.Property.PLATFORM_ROLE.getURI(), platformRole);
	}

	public boolean setName(String name) {
		return this.set(AgentDescription.Property.NAME.getURI(), name);
	}

	public boolean setEmail(String email) {
		return this.set(AgentDescription.Property.EMAIL.getURI(), email);
	}

	public boolean setEmails(Set<String> emails) {
		this.remove(AgentDescription.Property.EMAIL.getURI());
		for (String email : emails) {
			if ( ! this.add(AgentDescription.Property.EMAIL.getURI(), email) ) return false;
		}
		return true;
	}

	public boolean setSalt(String salt) {
		return this.set(AgentDescription.Property.SALT.getURI(), salt);
	}

	public boolean setPassword(String password) {
		return this.set(AgentDescription.Property.PASSWORD.getURI(), password);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUsername() {
		return this.getURI().toString();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void eraseCredentials() {
		this.remove(AgentDescription.Property.PASSWORD.getURI());
		this.remove(AgentDescription.Property.SALT.getURI());
	}

}
