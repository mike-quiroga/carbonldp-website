package com.base22.carbon.security.models;

import org.springframework.security.core.GrantedAuthority;

public class Privilege implements GrantedAuthority {
	private static final long serialVersionUID = - 8768384240803819769L;

	public static final String PRIVILEGE_PREFIX = "PRIV_";

	private long id;
	private String name;

	public Privilege(long id, String name) {
		setId(id);
		setName(name);
	}

	public Privilege(String name) {
		setName(name);
	}

	public long getID() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		name = name.toUpperCase();
		name = name.startsWith(PRIVILEGE_PREFIX) ? name : PRIVILEGE_PREFIX.concat(name);
		this.name = name;
	}

	@Override
	public String getAuthority() {
		return this.getName();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Privilege [name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
}
