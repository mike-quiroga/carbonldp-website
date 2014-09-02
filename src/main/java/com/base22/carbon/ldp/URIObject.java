package com.base22.carbon.ldp;

import java.util.UUID;

import com.base22.carbon.models.UUIDObject;

public class URIObject extends UUIDObject {
	private String uri;

	public URIObject(String uri) {
		this.uri = uri;
	}

	public URIObject(String uri, UUID uuid) {
		this.uri = uri;
		this.uuid = uuid;
	}

	public String getURI() {
		return uri;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

	@Override
	public boolean equals(Object other) {
		if ( other == null || (this.getClass() != other.getClass()) ) {
			return false;
		}

		URIObject guest = (URIObject) other;
		if ( guest.getURI() == null || this.getURI() == null ) {
			return false;
		}

		return guest.getURI().equals(this.getURI());
	}

	@Override
	// TODO: Do this properly
	public int hashCode() {
		return uri.hashCode() * 31;
	}
}
