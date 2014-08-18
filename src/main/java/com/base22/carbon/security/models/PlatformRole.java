package com.base22.carbon.security.models;

import java.util.List;

public class PlatformRole {

	private long id;
	private String name;
	private String description;
	private List<Privilege> permissions;

	public long getID() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Privilege> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Privilege> permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Role [name=");
		builder.append(name);
		builder.append(", privileges=");
		builder.append(permissions);
		builder.append("]");
		return builder.toString();
	}
}