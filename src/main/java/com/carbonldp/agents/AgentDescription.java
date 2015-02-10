package com.carbonldp.agents;

import com.carbonldp.commons.descriptions.RDFNodeEnum;
import com.carbonldp.commons.models.PrefixedURI;

public final class AgentDescription {
	private AgentDescription() {
		// Meaning non-instantiable
	}

	public static enum Resource implements RDFNodeEnum {
		//@formatter:off
		CLASS("http://carbonldp.com/ns/v1/security#Agent")
		;
		//@formatter:on

		private final PrefixedURI[] prefixedURIs;

		private Resource(final String... uris) {
			if ( uris.length <= 0 ) throw new IllegalArgumentException("At least one uri needs to be specified");
			this.prefixedURIs = new PrefixedURI[uris.length];
			int i = 0;
			for (String uri : uris) {
				this.prefixedURIs[i] = new PrefixedURI(uri);
				i++;
			}
		}

		@Override
		public PrefixedURI getURI() {
			return this.prefixedURIs[0];
		}

		@Override
		public PrefixedURI[] getURIs() {
			return this.prefixedURIs.clone();
		}
	}

	public static enum Property implements RDFNodeEnum {
		//@formatter:off
		NAME("http://carbonldp.com/ns/v1/security#name"),
		EMAIL("http://www.w3.org/2001/vcard-rdf/3.0#email"),
		PASSWORD("http://carbonldp.com/ns/v1/security#password"),
		SALT("http://carbonldp.com/ns/v1/security#salt"),
		PLATFORM_ROLE("http://carbonldp.com/ns/v1/security#platformRole")
		;
		//@formatter:on

		private final PrefixedURI[] prefixedURIs;

		private Property(String... uris) {
			if ( uris.length <= 0 ) throw new IllegalArgumentException("At least one uri needs to be specified");
			this.prefixedURIs = new PrefixedURI[uris.length];
			int i = 0;
			for (String uri : uris) {
				this.prefixedURIs[i] = new PrefixedURI(uri);
				i++;
			}
		}

		@Override
		public PrefixedURI getURI() {
			return this.prefixedURIs[0];
		}

		@Override
		public PrefixedURI[] getURIs() {
			return this.prefixedURIs.clone();
		}
	}
}
