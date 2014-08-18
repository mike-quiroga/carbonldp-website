package com.base22.carbon.models;

import java.util.ArrayList;
import java.util.List;

import com.base22.carbon.constants.APIPreferences.InteractionModel;
import com.base22.carbon.constants.LDPC;
import com.base22.carbon.constants.LDPR;
import com.base22.carbon.constants.LDPRS;
import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.exceptions.FactoryException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class LDPContainerFactory extends LDPRSourceFactory {

	public LDPContainer create(LDPRSource ldpRSource) throws CarbonException {
		if ( ! isValidContainer(ldpRSource) ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< create() > The resource is not a container.");
			}
			throw new FactoryException("The resource is not a container.");
		}

		return new LDPContainerImpl(ldpRSource.getResource());
	}

	public LDPContainer create(String resourceURI, Model resourceModel) throws CarbonException {
		LDPContainer container = null;

		LDPRSource ldpRSource = null;
		LDPRSourceFactory factory = new LDPRSourceFactory();
		ldpRSource = factory.create(resourceURI, resourceModel);

		container = create(ldpRSource);

		return container;
	}

	public LDPContainer createBasicContainer(String containerURI) throws CarbonException {
		return createBasicContainer(containerURI, null);
	}

	public LDPContainer createBasicContainer(String containerURI, String memberOfRelation) throws CarbonException {
		return createContainer(containerURI, LDPC.BASIC, null, null, memberOfRelation, null, null);
	}

	public LDPContainer createDirectContainer(String containerURI, String membershipResourceURI) throws CarbonException {
		return createDirectContainer(containerURI, membershipResourceURI, null, null, null);
	}

	public LDPContainer createDirectContainer(String containerURI, String membershipResourceURI, String hasMemberRelation) throws CarbonException {
		return createDirectContainer(containerURI, membershipResourceURI, hasMemberRelation, null, null);
	}

	public LDPContainer createDirectContainer(String containerURI, String membershipResourceURI, String hasMemberRelation, String memberOfRelation)
			throws CarbonException {
		return createDirectContainer(containerURI, membershipResourceURI, hasMemberRelation, memberOfRelation, null);
	}

	public LDPContainer createDirectContainer(String containerURI, String membershipResourceURI, String hasMemberRelation, String memberOfRelation,
			String defaultInteractionModel) throws CarbonException {
		return createContainer(containerURI, LDPC.DIRECT, membershipResourceURI, hasMemberRelation, memberOfRelation, null, defaultInteractionModel);
	}

	public LDPContainer createIndirectContainer(String containerURI, String membershipResourceURI, String insertedContentRelation) throws CarbonException {
		return createIndirectContainer(containerURI, membershipResourceURI, insertedContentRelation, null, null, null);
	}

	public LDPContainer createIndirectContainer(String containerURI, String membershipResourceURI, String insertedContentRelation, String hasMemberRelation)
			throws CarbonException {
		return createIndirectContainer(containerURI, membershipResourceURI, insertedContentRelation, hasMemberRelation, null, null);
	}

	public LDPContainer createIndirectContainer(String containerURI, String membershipResourceURI, String insertedContentRelation, String hasMemberRelation,
			String memberOfRelation) throws CarbonException {
		return createIndirectContainer(containerURI, membershipResourceURI, insertedContentRelation, hasMemberRelation, memberOfRelation, null);
	}

	public LDPContainer createIndirectContainer(String containerURI, String membershipResourceURI, String insertedContentRelation, String hasMemberRelation,
			String memberOfRelation, String defaultInteractionModel) throws CarbonException {
		return createContainer(containerURI, LDPC.INDIRECT, membershipResourceURI, hasMemberRelation, memberOfRelation, insertedContentRelation,
				defaultInteractionModel);
	}

	private LDPContainer createContainer(String uri, String type, String membershipResourceURI, String hasMemberRelation, String memberOfRelation,
			String insertedContentRelation, String defaultInteractionModel) throws CarbonException {
		LDPContainer container = null;

		Model model = ModelFactory.createDefaultModel();
		Resource resource = model.createResource(uri);

		resource.addProperty(LDPR.Properties.RDF_TYPE.getProperty(), model.createResource(LDPC.TYPE));
		resource.addProperty(LDPR.Properties.RDF_TYPE.getProperty(), model.createResource(type));

		if ( membershipResourceURI != null ) {
			resource.addProperty(LDPC.MEMBERSHIP_RESOURCE_P, model.createResource(membershipResourceURI));
		}
		if ( hasMemberRelation != null ) {
			resource.addProperty(LDPC.HAS_MEMBER_RELATION_P, model.createResource(hasMemberRelation));
		}
		if ( memberOfRelation != null ) {
			resource.addProperty(LDPC.MEMBER_OF_RELATION_P, model.createResource(memberOfRelation));
		}
		if ( insertedContentRelation != null ) {
			resource.addProperty(LDPC.ICR_P, model.createResource(insertedContentRelation));
		}
		if ( defaultInteractionModel != null ) {
			resource.addProperty(LDPC.DIM_P, model.createResource(defaultInteractionModel));
		}

		container = create(uri, model);

		return container;
	}

	public boolean isContainer(LDPRSource ldpRSource) {

		if ( ldpRSource.isOfType(LDPC.TYPE) ) {
			return true;
		} else if ( ldpRSource.isOfType(LDPC.BASIC) ) {
			return true;
		} else if ( ldpRSource.isOfType(LDPC.DIRECT) ) {
			return true;
		} else if ( ldpRSource.isOfType(LDPC.INDIRECT) ) {
			return true;
		}
		return false;
	}

	public boolean isValidContainer(LDPRSource ldpRSource) {
		List<String> violations = validateLDPContainer(ldpRSource);
		return violations.isEmpty();
	}

	public String getTypeFromAnonymousContainer(LDPRSource ldpRSource) {
		String resourceURI = ldpRSource.getURI();

		// Check if it is a basicContainer
		Statement membershipResourceStmt = ldpRSource.getResource().getProperty(LDPC.MEMBERSHIP_RESOURCE_P);
		if ( membershipResourceStmt == null ) {
			return LDPC.BASIC;
		}
		if ( membershipResourceStmt.getObject().isURIResource() ) {
			if ( membershipResourceStmt.getResource().getURI().equals(resourceURI) ) {
				return LDPC.BASIC;
			}
		}

		// Check if it is an directContainer
		Statement insertedContentRelationStmt = ldpRSource.getResource().getProperty(LDPC.MEMBERSHIP_RESOURCE_P);
		if ( insertedContentRelationStmt == null ) {
			return LDPC.DIRECT;
		}
		if ( insertedContentRelationStmt.getObject().isURIResource() ) {
			if ( membershipResourceStmt.getResource().getURI().equals(resourceURI) ) {
				return LDPC.DIRECT;
			}
		}

		return LDPC.INDIRECT;
	}

	public List<String> validateLDPContainer(LDPRSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		if ( ! isContainer(ldpRSource) ) {
			violations.add("The resource is not a container.");
			return violations;
		}

		String containerType = null;
		if ( ldpRSource.isOfType(LDPC.BASIC) ) {
			containerType = LDPC.BASIC;
		}
		if ( ldpRSource.isOfType(LDPC.DIRECT) ) {
			if ( containerType != null ) {
				violations.add("The resource has multiple conflicting types.");
				return violations;
			}
			containerType = LDPC.DIRECT;
		}
		if ( ldpRSource.isOfType(LDPC.INDIRECT) ) {
			if ( containerType != null ) {
				violations.add("The resource has multiple conflicting types.");
				return violations;
			}
			containerType = LDPC.INDIRECT;
		}

		if ( containerType == null ) {
			// The container has an anonymous type, get the real one
			containerType = getTypeFromAnonymousContainer(ldpRSource);
		}

		if ( containerType.equals(LDPC.BASIC) ) {
			violations.addAll(validateLDPBasicContainer(ldpRSource));
		} else if ( containerType.equals(LDPC.DIRECT) ) {
			violations.addAll(validateLDPDirectContainer(ldpRSource));
		} else if ( containerType.equals(LDPC.INDIRECT) ) {
			violations.addAll(validateLDPIndirectContainer(ldpRSource));
		}

		return violations;
	}

	public List<String> validateLDPBasicContainer(LDPRSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		String resourceURI = ldpRSource.getURI();

		// membershipResource checks
		StmtIterator membershipResourceIterator = ldpRSource.getResource().listProperties(LDPC.MEMBERSHIP_RESOURCE_P);
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

		if ( ldpRSource.getResource().hasProperty(LDPC.MEMBER_OF_RELATION_P) ) {
			violations.addAll(checkMemberOfRelation(ldpRSource));
		}
		if ( ldpRSource.getResource().hasProperty(LDPC.HAS_MEMBER_RELATION_P) ) {
			violations.addAll(checkHasMemberRelation(ldpRSource));
		}
		if ( ldpRSource.getResource().hasProperty(LDPC.DIM_P) ) {
			violations.addAll(checkDefaultInteractionModel(ldpRSource));
		}

		// insertedContentRelation checks
		if ( ldpRSource.getResource().hasProperty(LDPC.ICR_P) ) {
			violations.addAll(checkInsertedContentRelation(ldpRSource));
		}

		return violations;
	}

	public List<String> validateLDPDirectContainer(LDPRSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		String resourceURI = ldpRSource.getURI();

		// membershipResource checks
		StmtIterator membershipResourceIterator = ldpRSource.getResource().listProperties(LDPC.MEMBERSHIP_RESOURCE_P);
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
		}

		if ( ldpRSource.getResource().hasProperty(LDPC.MEMBER_OF_RELATION_P) ) {
			violations.addAll(checkMemberOfRelation(ldpRSource));
		}
		if ( ldpRSource.getResource().hasProperty(LDPC.HAS_MEMBER_RELATION_P) ) {
			violations.addAll(checkHasMemberRelation(ldpRSource));
		}
		if ( ldpRSource.getResource().hasProperty(LDPC.DIM_P) ) {
			violations.addAll(checkDefaultInteractionModel(ldpRSource));
		}

		// insertedContentRelation checks
		if ( ldpRSource.getResource().hasProperty(LDPC.ICR_P) ) {
			violations.addAll(checkInsertedContentRelation(ldpRSource));
		}

		return violations;
	}

	public List<String> validateLDPIndirectContainer(LDPRSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		String resourceURI = ldpRSource.getURI();

		// membershipResource checks
		StmtIterator membershipResourceIterator = ldpRSource.getResource().listProperties(LDPC.MEMBERSHIP_RESOURCE_P);
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
		}

		if ( ldpRSource.getResource().hasProperty(LDPC.MEMBER_OF_RELATION_P) ) {
			violations.addAll(checkMemberOfRelation(ldpRSource));
		}
		if ( ldpRSource.getResource().hasProperty(LDPC.HAS_MEMBER_RELATION_P) ) {
			violations.addAll(checkHasMemberRelation(ldpRSource));
		}
		if ( ldpRSource.getResource().hasProperty(LDPC.DIM_P) ) {
			violations.addAll(checkDefaultInteractionModel(ldpRSource));
		}

		// insertedContentRelation checks
		StmtIterator insertedContentRelationIterator = ldpRSource.getResource().listProperties(LDPC.MEMBERSHIP_RESOURCE_P);
		if ( insertedContentRelationIterator.hasNext() ) {
			Statement insertedContentRelation = insertedContentRelationIterator.next();
			if ( ! insertedContentRelation.getObject().isURIResource() ) {
				violations.add("insertedContentRelation > Doesn't point to an object.");
			} else {
				if ( insertedContentRelation.getResource().getURI().equals(LDPC.DEFAULT_ICR) ) {
					violations.add("insertedContentRelation > Points to the default insertedContentRelation.");
				}

			}
			if ( insertedContentRelationIterator.hasNext() ) {
				violations.add("insertedContentRelation > Points to multiple values.");
			}
		}

		return violations;
	}

	private List<String> checkHasMemberRelation(LDPRSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		// hasMember checks
		StmtIterator hasMemberIterator = ldpRSource.getResource().listProperties(LDPC.HAS_MEMBER_RELATION_P);
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

	private List<String> checkMemberOfRelation(LDPRSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		// memberOf checks
		StmtIterator memberOfIterator = ldpRSource.getResource().listProperties(LDPC.MEMBER_OF_RELATION_P);
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

	private List<String> checkDefaultInteractionModel(LDPRSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		StmtIterator defaultInteractionModelIterator = ldpRSource.getResource().listProperties(LDPC.DIM_P);
		if ( defaultInteractionModelIterator.hasNext() ) {
			Statement defaultInteractionModel = defaultInteractionModelIterator.next();
			if ( ! defaultInteractionModel.getObject().isURIResource() ) {
				violations.add("defaultInteractionModel > Doesn't point to an object.");
			} else {
				String defaultInteractionModelURI = defaultInteractionModel.getResource().getURI();
				if ( ! (defaultInteractionModelURI.equals(LDPRS.TYPE) || defaultInteractionModelURI.equals(LDPC.TYPE)) ) {
					violations.add("defaultInteractionModel > Doesn't point to ldp:RDFSource or ldp:Container.");
				}
			}
			if ( defaultInteractionModelIterator.hasNext() ) {
				violations.add("defaultInteractionModel > Points to multiple values.");
			}
		}

		return violations;
	}

	private List<String> checkInsertedContentRelation(LDPRSource ldpRSource) {
		List<String> violations = new ArrayList<String>();

		StmtIterator insertedContentRelationIterator = ldpRSource.getResource().listProperties(LDPC.ICR_P);
		if ( insertedContentRelationIterator.hasNext() ) {
			Statement insertedContentRelation = insertedContentRelationIterator.next();
			if ( ! insertedContentRelation.getObject().isURIResource() ) {
				violations.add("insertedContentRelation > Doesn't point to an object.");
			} else {
				if ( ! insertedContentRelation.getResource().getURI().equals(LDPC.DEFAULT_ICR) ) {
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

	private class LDPContainerImpl extends LDPRSourceImpl implements LDPContainer {
		public LDPContainerImpl(Resource resource) {
			super(resource);
		}

		public String getTypeOfContainer() {
			if ( isOfType(LDPC.BASIC) )
				return LDPC.BASIC;
			if ( isOfType(LDPC.DIRECT) )
				return LDPC.DIRECT;
			if ( isOfType(LDPC.INDIRECT) )
				return LDPC.INDIRECT;
			return null;
		}

		@Override
		public List<String> getLinkTypes() {
			List<String> types = super.getLinkTypes();
			types.add(LDPC.LINK_TYPE);
			types.add("<" + this.getTypeOfContainer() + ">; rel=\"type\"");
			return types;
		}

		public String getMembershipResourceURI() {
			String membershipResourceURI;
			if ( ! this.resource.hasProperty(LDPC.MEMBERSHIP_RESOURCE_P) ) {
				// If no membership resource is specified, it is assumed the membershipResource is itself
				membershipResourceURI = this.resource.getURI();
			} else {
				try {
					membershipResourceURI = this.resource.getProperty(LDPC.MEMBERSHIP_RESOURCE_P).getResource().getURI();
				} catch (Exception exception) {
					// The property isn't a URI, thus it is invalid
					membershipResourceURI = this.resource.getURI();
				}
			}
			return membershipResourceURI;
		}

		public String getMembershipTriplesPredicate() {
			String predicate;

			if ( ! this.resource.hasProperty(LDPC.HAS_MEMBER_RELATION_P) ) {
				// It doesn't have one specified, returning default
				predicate = LDPC.DEFAULT_HAS_MEMBER_RELATION;
			} else {
				try {
					predicate = this.resource.getPropertyResourceValue(LDPC.HAS_MEMBER_RELATION_P).getURI();
				} catch (Exception exception) {
					// The property isn't a URI, thus it is invalid
					predicate = LDPC.DEFAULT_HAS_MEMBER_RELATION;
				}
			}

			return predicate;
		}

		public String getMemberOfRelation() {
			String memberOfRelation = null;

			if ( this.resource.hasProperty(LDPC.MEMBER_OF_RELATION_P) ) {
				try {
					memberOfRelation = this.resource.getPropertyResourceValue(LDPC.MEMBER_OF_RELATION_P).getURI();
				} catch (Exception exception) {
					// The property isn't a URI, thus it is invalid
				}
			}

			return memberOfRelation;
		}

		public String getInsertedContentRelation() {
			String icr;

			if ( ! this.resource.hasProperty(LDPC.ICR_P) ) {
				// It doesn't have one specified, returning default
				icr = LDPC.DEFAULT_ICR;
			} else {
				try {
					icr = this.resource.getPropertyResourceValue(LDPC.ICR_P).getURI();
				} catch (Exception exception) {
					// The property isn't a URI, thus it is invalid
					icr = LDPC.DEFAULT_ICR;
				}
			}

			return icr;
		}

		public InteractionModel getDefaultInteractionModel() {
			if ( ! this.getResource().hasProperty(LDPC.DIM_P) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(LDPC.DIM_P);
			RDFNode node = statement.getObject();
			if ( node.isURIResource() ) {
				return InteractionModel.findByURI(node.asResource().getURI());
			}

			return null;
		}

		public void removeContainerTriples() {
			StmtIterator stmtIterator = this.resource.listProperties();
			while (stmtIterator.hasNext()) {
				Statement statement = stmtIterator.next();
				Property propertyToRemove = statement.getPredicate();
				if ( propertyToRemove.getURI() != LDPC.CONTAINS )
					this.resource.removeAll(propertyToRemove);
			}
		}

		public void removeContainmentTriples() {
			this.resource.removeAll(LDPC.CONTAINS_P);
		}
	}
}
