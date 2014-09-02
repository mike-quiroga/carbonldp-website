package com.base22.carbon;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public abstract class APIPreferences {

	public static enum InteractionModel {
		//@formatter:off
		RDF_SOURCE( 
			new PrefixedURI("ldp", "RDFSource"),
			new PrefixedURI("ldp", "Resource")
		),
		CONTAINER( 
			new PrefixedURI("ldp", "Container"),
			new PrefixedURI("ldp", "BasicContainer"),
			new PrefixedURI("ldp", "DirectContainer"),
			new PrefixedURI("ldp", "IndirectContainer")
		),
		LDPNR(
			new PrefixedURI("ldp", "NonRDFSource")
		),
		WRAPPER_FOR_LDPNR(
			new PrefixedURI("c", "WrapperForLDPNR")
		);
		
		//@formatter:on

		private final PrefixedURI prefixedURI;
		private final PrefixedURI[] prefixedURIs;
		private final Resource resource;

		InteractionModel(PrefixedURI... uris) {
			this.prefixedURI = uris[0];
			this.prefixedURIs = uris;

			this.resource = ResourceFactory.createResource(this.prefixedURI.getURI());
		}

		public PrefixedURI getPrefixedURI() {
			return prefixedURI;
		}

		public PrefixedURI[] getPrefixedURIs() {
			return this.prefixedURIs;
		}

		public Resource getResource() {
			return resource;
		}

		public static InteractionModel findByURI(String uri) {
			for (InteractionModel preference : InteractionModel.values()) {
				for (PrefixedURI preferenceURI : preference.getPrefixedURIs()) {
					if ( preferenceURI.getURI().equals(uri) || preferenceURI.getShortVersion().equals(uri) || preferenceURI.getResourceURI().equals(uri) ) {
						return preference;
					}
				}
			}
			return null;
		}
	}

	public static enum RetrieveContainerPreference {
		//@formatter:off
		CONTAINER_PROPERTIES( 
			new PrefixedURI("ldp", "PreferMinimalContainer"),
			new PrefixedURI("ldp", "PreferEmptyContainer")
		),
		CONTAINMENT_TRIPLES(
			new PrefixedURI("ldp", "PreferContainment"),
			new PrefixedURI("c", "PreferContainmentTriples")
		),
		CONTAINED_RESOURCES(
			new PrefixedURI("c", "PreferContainmentResources")
		),
		MEMBERSHIP_TRIPLES( 
			new PrefixedURI("ldp", "PreferMembership"),
			new PrefixedURI("c", "PreferMembershipTriples")
		),
		MEMBER_RESOURCES(
			new PrefixedURI("c", "PreferMembershipResources")
		);
		
		//@formatter:on

		private final PrefixedURI prefixedURI;
		private final PrefixedURI[] prefixedURIs;
		private final Resource resource;

		RetrieveContainerPreference(PrefixedURI... uris) {
			this.prefixedURI = uris[0];
			this.prefixedURIs = uris;

			this.resource = ResourceFactory.createResource(this.prefixedURI.getURI());
		}

		public PrefixedURI getPrefixedURI() {
			return prefixedURI;
		}

		public PrefixedURI[] getPrefixedURIs() {
			return this.prefixedURIs;
		}

		public Resource getResource() {
			return resource;
		}

		public static RetrieveContainerPreference findByURI(String uri) {
			for (RetrieveContainerPreference preference : RetrieveContainerPreference.values()) {
				for (PrefixedURI preferenceURI : preference.getPrefixedURIs()) {
					if ( preferenceURI.getURI().equals(uri) || preferenceURI.getShortVersion().equals(uri) || preferenceURI.getResourceURI().equals(uri) ) {
						return preference;
					}
				}
			}
			return null;
		}
	}

	public static enum DeleteContainerPreference {
		//@formatter:off
		CONTAINER(
			new PrefixedURI("c", "PreferContainer")
		),
		CONTAINED_RESOURCES(
			new PrefixedURI("c", "PreferContainedResources")
		),
		MEMBERSHIP_TRIPLES(
			new PrefixedURI("c", "PreferMembershipTriples")
		),
		MEMBERSHIP_RESOURCES(
			new PrefixedURI("c", "PreferMembershipResources")
		);
		//@formatter:on

		private final PrefixedURI uri;
		private final PrefixedURI[] uris;
		private final Resource resource;

		DeleteContainerPreference(PrefixedURI... uris) {
			this.uri = uris[0];
			this.uris = uris;

			this.resource = ResourceFactory.createResource(this.uri.getURI());
		}

		public PrefixedURI getPrefixedURI() {
			return uri;
		}

		public PrefixedURI[] getPrefixedURIs() {
			return this.uris;
		}

		public Resource getResource() {
			return resource;
		}

		public static DeleteContainerPreference findByURI(String uri) {
			for (DeleteContainerPreference preference : DeleteContainerPreference.values()) {
				for (PrefixedURI preferenceURI : preference.getPrefixedURIs()) {
					if ( preferenceURI.getURI().equals(uri) || preferenceURI.getShortVersion().equals(uri) || preferenceURI.getResourceURI().equals(uri) ) {
						return preference;
					}
				}
			}
			return null;
		}
	}

	public static enum AuthenticationPreference {
		//@formatter:off
		COOKIE(
			new PrefixedURI("cs", "Cookie")
		);
		//@formatter:on

		private final PrefixedURI uri;
		private final PrefixedURI[] uris;
		private final Resource resource;

		AuthenticationPreference(PrefixedURI... uris) {
			this.uri = uris[0];
			this.uris = uris;

			this.resource = ResourceFactory.createResource(this.uri.getURI());
		}

		public PrefixedURI getPrefixedURI() {
			return uri;
		}

		public PrefixedURI[] getPrefixedURIs() {
			return this.uris;
		}

		public Resource getResource() {
			return resource;
		}

		public static AuthenticationPreference findByURI(String uri) {
			for (AuthenticationPreference preference : AuthenticationPreference.values()) {
				for (PrefixedURI preferenceURI : preference.getPrefixedURIs()) {
					if ( preferenceURI.getURI().equals(uri) || preferenceURI.getShortVersion().equals(uri) || preferenceURI.getResourceURI().equals(uri) ) {
						return preference;
					}
				}
			}
			return null;
		}
	}
}
