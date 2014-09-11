package com.base22.carbon.ldp.models;

import com.base22.carbon.Carbon;
import com.base22.carbon.models.PrefixedURI;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public abstract class ContainerClass {

	public static final String TYPE = "http://www.w3.org/ns/ldp#Container";
	public static final String LINK_TYPE = "<" + TYPE + ">; rel=\"type\"";

	// --- Container Types

	public static enum ContainerType {
		//@formatter:off
		BASIC(
			new PrefixedURI("ldp", "BasicContainer")
		),
		DIRECT(
			new PrefixedURI("ldp", "DirectContainer")
		),
		INDIRECT(
			new PrefixedURI("ldp", "IndirectContainer")
		);
		//@formatter:on

		private final PrefixedURI prefixedURI;
		private final PrefixedURI[] prefixedURIs;
		private final Resource resource;

		ContainerType(PrefixedURI... uris) {
			this.prefixedURI = uris[0];
			this.prefixedURIs = uris;

			this.resource = ResourceFactory.createResource(this.prefixedURI.getURI());
		}

		public String getURI() {
			return prefixedURI.getURI();
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

		public static ContainerType findByURI(String uri) {
			for (ContainerType resource : ContainerType.values()) {
				for (PrefixedURI resourceURI : resource.getPrefixedURIs()) {
					if ( resourceURI.getURI().equals(uri) || resourceURI.getShortVersion().equals(uri) ) {
						return resource;
					}
				}
			}
			return null;
		}
	}

	public static final String BASIC = Carbon.CONFIGURED_PREFIXES.get("ldp") + "BasicContainer";
	public static final String INDIRECT = Carbon.CONFIGURED_PREFIXES.get("ldp") + "IndirectContainer";
	public static final String DIRECT = Carbon.CONFIGURED_PREFIXES.get("ldp") + "DirectContainer";

	// --- End: Container Types
	// --- Container Properties

	// Containment triples' predicate
	public static final String CONTAINS = Carbon.CONFIGURED_PREFIXES.get("ldp") + "contains";
	public static final Property CONTAINS_P = ResourceFactory.createProperty(CONTAINS);

	// Property that contains the resource where membership triples will be created
	public static final String MEMBERSHIP_RESOURCE = Carbon.CONFIGURED_PREFIXES.get("ldp") + "membershipResource";
	public static final Property MEMBERSHIP_RESOURCE_P = ResourceFactory.createProperty(MEMBERSHIP_RESOURCE);

	// Property that has the membership triples' predicate
	public static final String HAS_MEMBER_RELATION = Carbon.CONFIGURED_PREFIXES.get("ldp") + "hasMemberRelation";
	public static final Property HAS_MEMBER_RELATION_P = ResourceFactory.createProperty(HAS_MEMBER_RELATION);

	// Property that has the inverse membership triples' predicate
	public static final String MEMBER_OF_RELATION = Carbon.CONFIGURED_PREFIXES.get("ldp") + "memberOfRelation";
	public static final Property MEMBER_OF_RELATION_P = ResourceFactory.createProperty(MEMBER_OF_RELATION);

	// Inserted Content Relation (ICR), indicates the membership triples' object to use
	public static final String ICR = Carbon.CONFIGURED_PREFIXES.get("ldp") + "insertedContentRelation";
	public static final Property ICR_P = ResourceFactory.createProperty(ICR);

	// Default interaction model (DIM)
	public static final String DIM = Carbon.CONFIGURED_PREFIXES.get("c") + "defaultInteractionModel";
	public static final Property DIM_P = ResourceFactory.createProperty(DIM);

	// --- End: Container Properties
	// --- Default values

	public static final String DEFAULT_HAS_MEMBER_RELATION = Carbon.CONFIGURED_PREFIXES.get("ldp") + "member";
	public static final String DEFAULT_ICR = Carbon.CONFIGURED_PREFIXES.get("ldp") + "MemberSubject";

	// --- End: Default values
}
