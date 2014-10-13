package com.base22.carbon.sparql;

import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.HTTPHeaders;
import com.base22.carbon.models.PrefixedURI;
import com.base22.carbon.models.RDFResourceEnum;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class SPARQLEndpoint {
	public static enum Resources implements RDFResourceEnum {
		//@formatter:off
		CLASS(
			new PrefixedURI("sd", "Service")
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

	public static final String[] NEGATED_LINKS;
	static {
		List<String> links = new ArrayList<String>();

		for (PrefixedURI prefixedURI : Resources.CLASS.getPrefixedURIs()) {
			StringBuilder linkBuilder = new StringBuilder();
			//@formatter:off
			linkBuilder
				.append(HTTPHeaders.LINK)
				.append("!=")
				.append(prefixedURI.getURI())
			;
			//@formatter:on
			links.add(linkBuilder.toString());

			linkBuilder = new StringBuilder();
			//@formatter:off
			linkBuilder
				.append(HTTPHeaders.LINK)
				.append("!=")
				.append(prefixedURI.getShortVersion())
			;
			//@formatter:on
			links.add(linkBuilder.toString());
		}

		NEGATED_LINKS = links.toArray(new String[links.size()]);
	}
}
