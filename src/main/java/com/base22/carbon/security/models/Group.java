package com.base22.carbon.security.models;

import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;

import com.base22.carbon.security.constants.AceSR;

public class Group extends UUIDObject implements GrantedAuthority {
	private static final long serialVersionUID = 2757550358514935551L;

	private String name;
	private String description;

	private Group parent;
	private HashSet<Agent> agents;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Group getParent() {
		return parent;
	}

	public void setParent(Group parent) {
		this.parent = parent;
	}

	public HashSet<Agent> getAgents() {
		return agents;
	}

	public void setAgents(HashSet<Agent> agents) {
		this.agents = agents;
	}

	@Override
	public String getAuthority() {
		return AceSR.SubjectType.GROUP.getName() + ": " + this.getUuidString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Group [name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}
}
