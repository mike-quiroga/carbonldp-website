package com.base22.carbon.ldp.models;

public class LDPContainerQueryOptions {

	private boolean containerProperties;
	private boolean containmentTriples;
	private boolean membershipTriples;
	private boolean memberResources;
	private boolean containedResources;

	public static enum METHOD {
		GET, DELETE
	}

	public LDPContainerQueryOptions(METHOD method) {
		switch (method) {
			case DELETE:
				this.containerProperties = false;
				this.containmentTriples = false;
				this.membershipTriples = true;
				this.memberResources = false;
				this.containedResources = false;
				break;
			case GET:
			default:
				this.containerProperties = true;
				this.containmentTriples = false;
				this.membershipTriples = true;
				this.memberResources = false;
				this.containedResources = false;
				break;
		}

	}

	public boolean includeContainerProperties() {
		return containerProperties;
	}

	public void setContainerProperties(boolean containerProperties) {
		this.containerProperties = containerProperties;
	}

	public boolean includeContainmentTriples() {
		return containmentTriples;
	}

	public void setContainmentTriples(boolean containmentTriples) {
		this.containmentTriples = containmentTriples;
	}

	public boolean includeMembershipTriples() {
		return membershipTriples;
	}

	public void setMembershipTriples(boolean membershipTriples) {
		this.membershipTriples = membershipTriples;
	}

	public boolean includeMemberResources() {
		return memberResources;
	}

	public void setMemberResources(boolean memberResources) {
		this.memberResources = memberResources;
	}

	public boolean includeContainedResources() {
		return containedResources;
	}

	public void setContainedResources(boolean containedResources) {
		this.containedResources = containedResources;
	}

}
