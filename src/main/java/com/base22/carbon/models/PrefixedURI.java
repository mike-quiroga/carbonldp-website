package com.base22.carbon.models;

import com.base22.carbon.Carbon;


public class PrefixedURI {
	private final String prefix;
	private final String baseURI;
	private final String slug;
	private final String uri;
	private final String resourceURI;
	private final String shortVersion;

	// TODO: Make it safe (prefixes could be missing)
	public PrefixedURI(String prefix, String slug) {
		this.prefix = prefix;
		this.baseURI = Carbon.CONFIGURED_PREFIXES.get(prefix);
		this.slug = slug;
		this.uri = baseURI.concat(slug);

		StringBuilder resourceURI = new StringBuilder();
		resourceURI.append("<").append(this.getURI()).append(">");
		this.resourceURI = resourceURI.toString();

		this.shortVersion = prefix.concat(":").concat(slug);
	}

	public String getPrefix() {
		return prefix;
	}

	public String getBaseURI() {
		return baseURI;
	}

	public String getSlug() {
		return slug;
	}

	public String getURI() {
		return uri;
	}

	public String getResourceURI() {
		return resourceURI;
	}

	public String getShortVersion() {
		return shortVersion;
	}
}
