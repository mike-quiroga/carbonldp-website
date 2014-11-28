package com.base22.carbon.ldp.models;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.APIPreferences.RetrieveContainerPreference;
import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ContainerFactory extends RDFSourceFactory {

	public Container create(RDFSource ldpRSource) throws CarbonException {
		if ( ! isContainer(ldpRSource) ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< create() > The resource is not a container.");
			}
			throw new FactoryException("The resource is not a container.");
		}

		return new LDPContainerImpl(ldpRSource.getResource());
	}

	public Container create(String resourceURI, Model resourceModel) throws CarbonException {
		Container container = null;

		RDFSource ldpRSource = null;
		RDFSourceFactory factory = new RDFSourceFactory();
		ldpRSource = factory.create(resourceURI, resourceModel);

		container = create(ldpRSource);

		return container;
	}

	public Container createBasicContainer(String containerURI) throws CarbonException {
		return createBasicContainer(containerURI, null);
	}

	public Container createBasicContainer(String containerURI, String memberOfRelation) throws CarbonException {
		return createContainer(containerURI, ContainerClass.BASIC, null, null, memberOfRelation, null, null);
	}

	public Container createDirectContainer(String containerURI, String membershipResourceURI) throws CarbonException {
		return createDirectContainer(containerURI, membershipResourceURI, null, null, null);
	}

	public Container createDirectContainer(String containerURI, String membershipResourceURI, String hasMemberRelation) throws CarbonException {
		return createDirectContainer(containerURI, membershipResourceURI, hasMemberRelation, null, null);
	}

	public Container createDirectContainer(String containerURI, String membershipResourceURI, String hasMemberRelation, String memberOfRelation)
			throws CarbonException {
		return createDirectContainer(containerURI, membershipResourceURI, hasMemberRelation, memberOfRelation, null);
	}

	public Container createDirectContainer(String containerURI, String membershipResourceURI, String hasMemberRelation, String memberOfRelation,
			String defaultInteractionModel) throws CarbonException {
		return createContainer(containerURI, ContainerClass.DIRECT, membershipResourceURI, hasMemberRelation, memberOfRelation, null, defaultInteractionModel);
	}

	public Container createIndirectContainer(String containerURI, String membershipResourceURI, String insertedContentRelation) throws CarbonException {
		return createIndirectContainer(containerURI, membershipResourceURI, insertedContentRelation, null, null, null);
	}

	public Container createIndirectContainer(String containerURI, String membershipResourceURI, String insertedContentRelation, String hasMemberRelation)
			throws CarbonException {
		return createIndirectContainer(containerURI, membershipResourceURI, insertedContentRelation, hasMemberRelation, null, null);
	}

	public Container createIndirectContainer(String containerURI, String membershipResourceURI, String insertedContentRelation, String hasMemberRelation,
			String memberOfRelation) throws CarbonException {
		return createIndirectContainer(containerURI, membershipResourceURI, insertedContentRelation, hasMemberRelation, memberOfRelation, null);
	}

	public Container createIndirectContainer(String containerURI, String membershipResourceURI, String insertedContentRelation, String hasMemberRelation,
			String memberOfRelation, String defaultInteractionModel) throws CarbonException {
		return createContainer(containerURI, ContainerClass.INDIRECT, membershipResourceURI, hasMemberRelation, memberOfRelation, insertedContentRelation,
				defaultInteractionModel);
	}

	private Container createContainer(String uri, String type, String membershipResourceURI, String hasMemberRelation, String memberOfRelation,
			String insertedContentRelation, String defaultInteractionModel) throws CarbonException {
		Container container = null;

		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(uri);

		resource.addProperty(RDFResourceClass.Properties.RDF_TYPE.getProperty(), model.createResource(ContainerClass.TYPE));
		resource.addProperty(RDFResourceClass.Properties.RDF_TYPE.getProperty(), model.createResource(type));

		if ( membershipResourceURI != null ) {
			resource.addProperty(ContainerClass.MEMBERSHIP_RESOURCE_P, model.createResource(membershipResourceURI));
		}
		if ( hasMemberRelation != null ) {
			resource.addProperty(ContainerClass.HAS_MEMBER_RELATION_P, model.createResource(hasMemberRelation));
		}
		if ( memberOfRelation != null ) {
			resource.addProperty(ContainerClass.MEMBER_OF_RELATION_P, model.createResource(memberOfRelation));
		}
		if ( insertedContentRelation != null ) {
			resource.addProperty(ContainerClass.ICR_P, model.createResource(insertedContentRelation));
		}
		if ( defaultInteractionModel != null ) {
			resource.addProperty(ContainerClass.DIM_P, model.createResource(defaultInteractionModel));
		}

		container = create(uri, model);

		return container;
	}

	public boolean isContainer(RDFSource ldpRSource) {

		if ( ldpRSource.isOfType(ContainerClass.TYPE) ) {
			return true;
		} else if ( ldpRSource.isOfType(ContainerClass.BASIC) ) {
			return true;
		} else if ( ldpRSource.isOfType(ContainerClass.DIRECT) ) {
			return true;
		} else if ( ldpRSource.isOfType(ContainerClass.INDIRECT) ) {
			return true;
		}
		return false;
	}

	public boolean isValidContainer(RDFSource ldpRSource) {
		List<String> violations = validateLDPContainer(ldpRSource);
		return violations.isEmpty();
	}

	public String getTypeFromAnonymousContainer(RDFSource ldpRSource) {
		String resourceURI = ldpRSource.getURI();

		// Check if it is a basicContainer
		Statement membershipResourceStmt = ldpRSource.getResource().getProperty(ContainerClass.MEMBERSHIP_RESOURCE_P);
		if ( membershipResourceStmt == null ) {
			return ContainerClass.BASIC;
		}
		if ( membershipResourceStmt.getObject().isURIResource() ) {
			if ( membershipResourceStmt.getResource().getURI().equals(resourceURI) ) {
				return ContainerClass.BASIC;
			}
		}

		// Check if it is an directContainer
		Statement insertedContentRelationStmt = ldpRSource.getResource().getProperty(ContainerClass.MEMBERSHIP_RESOURCE_P);
		if ( insertedContentRelationStmt == null ) {
			return ContainerClass.DIRECT;
		}
		if ( insertedContentRelationStmt.getObject().isURIResource() ) {
			if ( membershipResourceStmt.getResource().getURI().equals(resourceURI) ) {
				return ContainerClass.DIRECT;
			}
		}

		return ContainerClass.INDIRECT;
	}

	public List<String> validateLDPContainer(RDFSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		if ( ! isContainer(ldpRSource) ) {
			violations.add("The resource is not a container.");
			return violations;
		}

		String containerType = null;
		if ( ldpRSource.isOfType(ContainerClass.BASIC) ) {
			containerType = ContainerClass.BASIC;
		}
		if ( ldpRSource.isOfType(ContainerClass.DIRECT) ) {
			if ( containerType != null ) {
				violations.add("The resource has multiple conflicting types.");
				return violations;
			}
			containerType = ContainerClass.DIRECT;
		}
		if ( ldpRSource.isOfType(ContainerClass.INDIRECT) ) {
			if ( containerType != null ) {
				violations.add("The resource has multiple conflicting types.");
				return violations;
			}
			containerType = ContainerClass.INDIRECT;
		}

		if ( containerType == null ) {
			// The container has an anonymous type, get the real one
			containerType = getTypeFromAnonymousContainer(ldpRSource);
		}

		if ( containerType.equals(ContainerClass.BASIC) ) {
			violations.addAll(validateLDPBasicContainer(ldpRSource));
		} else if ( containerType.equals(ContainerClass.DIRECT) ) {
			violations.addAll(validateLDPDirectContainer(ldpRSource));
		} else if ( containerType.equals(ContainerClass.INDIRECT) ) {
			violations.addAll(validateLDPIndirectContainer(ldpRSource));
		}

		return violations;
	}

	public List<String> validateLDPBasicContainer(RDFSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		String resourceURI = ldpRSource.getURI();

		// membershipResource checks
		StmtIterator membershipResourceIterator = ldpRSource.getResource().listProperties(ContainerClass.MEMBERSHIP_RESOURCE_P);
		if ( membershipResourceIterator.hasNext() ) {
			Statement membershipResource = membershipResourceIterator.next();
			if ( ! membershipResource.getObject().isURIResource() ) {
				violations.add("membershipResource > Doesn't point to an object.");
			} else {
				if ( ! membershipResource.getResource().getURI().equals(resourceURI) ) {
					violations.add("membershipResource > Doesn't point to itself.");
				}
			}
			if ( membershipResourceIterator.hasNext() ) {
				violations.add("membershipResource > Points to multiple values.");
			}
		}

		if ( ldpRSource.getResource().hasProperty(ContainerClass.MEMBER_OF_RELATION_P) ) {
			violations.addAll(checkMemberOfRelation(ldpRSource));
		}
		if ( ldpRSource.getResource().hasProperty(ContainerClass.HAS_MEMBER_RELATION_P) ) {
			violations.addAll(checkHasMemberRelation(ldpRSource));
		}
		if ( ldpRSource.getResource().hasProperty(ContainerClass.DIM_P) ) {
			violations.addAll(checkDefaultInteractionModel(ldpRSource));
		}

		// insertedContentRelation checks
		if ( ldpRSource.getResource().hasProperty(ContainerClass.ICR_P) ) {
			violations.addAll(checkInsertedContentRelation(ldpRSource));
		}

		return violations;
	}

	public List<String> validateLDPDirectContainer(RDFSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		String resourceURI = ldpRSource.getURI();

		// membershipResource checks
		StmtIterator membershipResourceIterator = ldpRSource.getResource().listProperties(ContainerClass.MEMBERSHIP_RESOURCE_P);
		if ( membershipResourceIterator.hasNext() ) {
			Statement membershipResource = membershipResourceIterator.next();
			if ( ! membershipResource.getObject().isURIResource() ) {
				violations.add("membershipResource > Doesn't point to an object.");
			} else {
				if ( membershipResource.getResource().getURI().equals(resourceURI) ) {
					violations.add("membershipResource > Points to itself.");
				}
			}
			if ( membershipResourceIterator.hasNext() ) {
				violations.add("membershipResource > Points to multiple values.");
			}
		} else {
			violations.add("membershipResource > Isn't defined.");
		}

		if ( ldpRSource.getResource().hasProperty(ContainerClass.MEMBER_OF_RELATION_P) ) {
			violations.addAll(checkMemberOfRelation(ldpRSource));
		}

		if ( ldpRSource.getResource().hasProperty(ContainerClass.HAS_MEMBER_RELATION_P) ) {
			violations.addAll(checkHasMemberRelation(ldpRSource));
		} else {
			violations.add(MessageFormat.format("{0} > Isn't defined.", ContainerClass.Properties.HAS_MEMBER_RELATION.getPrefixedURI().getShortVersion()));
		}

		if ( ldpRSource.getResource().hasProperty(ContainerClass.DIM_P) ) {
			violations.addAll(checkDefaultInteractionModel(ldpRSource));
		}

		// insertedContentRelation checks
		if ( ldpRSource.getResource().hasProperty(ContainerClass.ICR_P) ) {
			violations.addAll(checkInsertedContentRelation(ldpRSource));
		}

		return violations;
	}

	public List<String> validateLDPIndirectContainer(RDFSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		String resourceURI = ldpRSource.getURI();

		// membershipResource checks
		StmtIterator membershipResourceIterator = ldpRSource.getResource().listProperties(ContainerClass.MEMBERSHIP_RESOURCE_P);
		if ( membershipResourceIterator.hasNext() ) {
			Statement membershipResource = membershipResourceIterator.next();
			if ( ! membershipResource.getObject().isURIResource() ) {
				violations.add("membershipResource > Doesn't point to an object.");
			} else {
				if ( membershipResource.getResource().getURI().equals(resourceURI) ) {
					violations.add("membershipResource > Points to itself.");
				}
			}
			if ( membershipResourceIterator.hasNext() ) {
				violations.add("membershipResource > Points to multiple values.");
			}
		} else {
			violations.add("membershipResource > Isn't defined.");
		}

		if ( ldpRSource.getResource().hasProperty(ContainerClass.MEMBER_OF_RELATION_P) ) {
			violations.addAll(checkMemberOfRelation(ldpRSource));
		}

		if ( ldpRSource.getResource().hasProperty(ContainerClass.HAS_MEMBER_RELATION_P) ) {
			violations.addAll(checkHasMemberRelation(ldpRSource));
		} else {
			violations.add(MessageFormat.format("{0} > Isn't defined.", ContainerClass.Properties.HAS_MEMBER_RELATION.getPrefixedURI().getShortVersion()));
		}

		if ( ldpRSource.getResource().hasProperty(ContainerClass.DIM_P) ) {
			violations.addAll(checkDefaultInteractionModel(ldpRSource));
		}

		// insertedContentRelation checks
		StmtIterator insertedContentRelationIterator = ldpRSource.getResource().listProperties(ContainerClass.MEMBERSHIP_RESOURCE_P);
		if ( insertedContentRelationIterator.hasNext() ) {
			Statement insertedContentRelation = insertedContentRelationIterator.next();
			if ( ! insertedContentRelation.getObject().isURIResource() ) {
				violations.add("insertedContentRelation > Doesn't point to an object.");
			} else {
				if ( insertedContentRelation.getResource().getURI().equals(ContainerClass.DEFAULT_ICR) ) {
					violations.add("insertedContentRelation > Points to the default insertedContentRelation.");
				}

			}
			if ( insertedContentRelationIterator.hasNext() ) {
				violations.add("insertedContentRelation > Points to multiple values.");
			}
		}

		return violations;
	}

	private List<String> checkHasMemberRelation(RDFSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		// hasMember checks
		StmtIterator hasMemberIterator = ldpRSource.getResource().listProperties(ContainerClass.HAS_MEMBER_RELATION_P);
		if ( hasMemberIterator.hasNext() ) {
			Statement hasMember = hasMemberIterator.next();
			if ( ! hasMember.getObject().isURIResource() ) {
				violations.add("hasMember > Doesn't point to an object.");
			}
			if ( hasMemberIterator.hasNext() ) {
				violations.add("hasMember > Points to multiple values.");
			}
		}

		return violations;
	}

	private List<String> checkMemberOfRelation(RDFSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		// memberOf checks
		StmtIterator memberOfIterator = ldpRSource.getResource().listProperties(ContainerClass.MEMBER_OF_RELATION_P);
		if ( memberOfIterator.hasNext() ) {
			Statement memberOf = memberOfIterator.next();
			if ( ! memberOf.getObject().isURIResource() ) {
				violations.add("memberOf > Doesn't point to an object.");
			}
			if ( memberOfIterator.hasNext() ) {
				violations.add("memberOf > Points to multiple values.");
			}
		}

		return violations;
	}

	private List<String> checkDefaultInteractionModel(RDFSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		StmtIterator defaultInteractionModelIterator = ldpRSource.getResource().listProperties(ContainerClass.DIM_P);
		if ( defaultInteractionModelIterator.hasNext() ) {
			Statement defaultInteractionModel = defaultInteractionModelIterator.next();
			if ( ! defaultInteractionModel.getObject().isURIResource() ) {
				violations.add("defaultInteractionModel > Doesn't point to an object.");
			} else {
				String defaultInteractionModelURI = defaultInteractionModel.getResource().getURI();
				if ( ! (defaultInteractionModelURI.equals(RDFSourceClass.TYPE) || defaultInteractionModelURI.equals(ContainerClass.TYPE)) ) {
					violations.add("defaultInteractionModel > Doesn't point to ldp:RDFSource or ldp:Container.");
				}
			}
			if ( defaultInteractionModelIterator.hasNext() ) {
				violations.add("defaultInteractionModel > Points to multiple values.");
			}
		}

		return violations;
	}

	private List<String> checkInsertedContentRelation(RDFSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		StmtIterator insertedContentRelationIterator = ldpRSource.getResource().listProperties(ContainerClass.ICR_P);
		if ( insertedContentRelationIterator.hasNext() ) {
			Statement insertedContentRelation = insertedContentRelationIterator.next();
			if ( ! insertedContentRelation.getObject().isURIResource() ) {
				violations.add("insertedContentRelation > Doesn't point to an object.");
			} else {
				if ( ! insertedContentRelation.getResource().getURI().equals(ContainerClass.DEFAULT_ICR) ) {
					violations.add("insertedContentRelation > Doesn't point to the default insertedContentRelation.");
				}

			}
			if ( insertedContentRelationIterator.hasNext() ) {
				violations.add("insertedContentRelation > Points to multiple values.");
			}
		}

		return violations;
	}

	// === LDPContainerImpl

	private class LDPContainerImpl extends LDPRSourceImpl implements Container {
		public LDPContainerImpl(Resource resource) {
			super(resource);
		}

		public String getTypeOfContainer() {
			if ( isOfType(ContainerClass.BASIC) ) return ContainerClass.BASIC;
			if ( isOfType(ContainerClass.DIRECT) ) return ContainerClass.DIRECT;
			if ( isOfType(ContainerClass.INDIRECT) ) return ContainerClass.INDIRECT;
			return null;
		}

		@Override
		public List<String> getLinkTypes() {
			List<String> types = super.getLinkTypes();
			types.add(ContainerClass.LINK_TYPE);
			types.add("<" + this.getTypeOfContainer() + ">; rel=\"type\"");
			return types;
		}

		public String getMembershipResourceURI() {
			String membershipResourceURI;
			if ( ! this.resource.hasProperty(ContainerClass.MEMBERSHIP_RESOURCE_P) ) {
				// If no membership resource is specified, it is assumed the membershipResource is itself
				membershipResourceURI = this.resource.getURI();
			} else {
				try {
					membershipResourceURI = this.resource.getProperty(ContainerClass.MEMBERSHIP_RESOURCE_P).getResource().getURI();
				} catch (Exception exception) {
					// The property isn't a URI, thus it is invalid
					membershipResourceURI = this.resource.getURI();
				}
			}
			return membershipResourceURI;
		}

		public String getMembershipTriplesPredicate() {
			String predicate;

			if ( ! this.resource.hasProperty(ContainerClass.HAS_MEMBER_RELATION_P) ) {
				// It doesn't have one specified, returning default
				predicate = ContainerClass.DEFAULT_HAS_MEMBER_RELATION;
			} else {
				try {
					predicate = this.resource.getPropertyResourceValue(ContainerClass.HAS_MEMBER_RELATION_P).getURI();
				} catch (Exception exception) {
					// The property isn't a URI, thus it is invalid
					predicate = ContainerClass.DEFAULT_HAS_MEMBER_RELATION;
				}
			}

			return predicate;
		}

		public String getMemberOfRelation() {
			String memberOfRelation = null;

			if ( this.resource.hasProperty(ContainerClass.MEMBER_OF_RELATION_P) ) {
				try {
					memberOfRelation = this.resource.getPropertyResourceValue(ContainerClass.MEMBER_OF_RELATION_P).getURI();
				} catch (Exception exception) {
					// The property isn't a URI, thus it is invalid
				}
			}

			return memberOfRelation;
		}

		public String getInsertedContentRelation() {
			String icr;

			if ( ! this.resource.hasProperty(ContainerClass.ICR_P) ) {
				// It doesn't have one specified, returning default
				icr = ContainerClass.DEFAULT_ICR;
			} else {
				try {
					icr = this.resource.getPropertyResourceValue(ContainerClass.ICR_P).getURI();
				} catch (Exception exception) {
					// The property isn't a URI, thus it is invalid
					icr = ContainerClass.DEFAULT_ICR;
				}
			}

			return icr;
		}

		public InteractionModel getDefaultInteractionModel() {
			if ( ! this.getResource().hasProperty(ContainerClass.DIM_P) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(ContainerClass.DIM_P);
			RDFNode node = statement.getObject();
			if ( node.isURIResource() ) {
				return InteractionModel.findByURI(node.asResource().getURI());
			}

			return null;
		}

		public List<RetrieveContainerPreference> listDefaultRetrievePreferences() {
			List<RetrieveContainerPreference> preferences = new ArrayList<RetrieveContainerPreference>();

			Property defaultRetrievePreference = ContainerClass.Properties.DEFAULT_RETRIEVE_PREFERENCE.getProperty();
			if ( ! this.getResource().hasProperty(defaultRetrievePreference) ) return preferences;

			String[] uris = this.getURIProperties(defaultRetrievePreference);
			for (String uri : uris) {
				RetrieveContainerPreference preference = RetrieveContainerPreference.findByURI(uri);
				if ( preference == null ) continue;
				preferences.add(preference);
			}

			return preferences;
		}

		public String[] listContainedResourceURIs() {
			return this.getURIProperties(ContainerClass.Properties.CONTAINS.getProperty());
		}

		public void removeContainerTriples() {
			StmtIterator stmtIterator = this.resource.listProperties();
			while (stmtIterator.hasNext()) {
				Statement statement = stmtIterator.next();
				Property propertyToRemove = statement.getPredicate();
				if ( propertyToRemove.getURI() != ContainerClass.CONTAINS ) this.resource.removeAll(propertyToRemove);
			}
		}

		public void removeContainmentTriples() {
			this.resource.removeAll(ContainerClass.CONTAINS_P);
		}
	}
}
