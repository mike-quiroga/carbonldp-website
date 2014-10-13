package com.base22.carbon.authorization.acl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.FactoryException;
import com.base22.carbon.authentication.AuthenticationUtil;
import com.base22.carbon.authorization.PermissionImpl;
import com.base22.carbon.authorization.acl.AceSR.SubjectType;
import com.base22.carbon.authorization.acl.CarbonACLPermissionFactory.CarbonPermission;
import com.base22.carbon.ldp.models.RDFResource;
import com.base22.carbon.ldp.models.SystemRDFResource;
import com.base22.carbon.ldp.models.SystemRDFResourceFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class ACESystemResourceFactory extends SystemRDFResourceFactory {

	public ACESystemResource create(Resource resource) throws CarbonException {
		SystemRDFResource systemResource = super.create(resource);
		if ( ! this.isACESystemResource(systemResource) ) {
			throw new FactoryException("The resource isn't an AccessControlEntry object.");
		}
		return new ACESystemResourceImpl(systemResource.getResource());
	}

	public ACESystemResource create(String aclURI, Model model) throws CarbonException {
		SystemRDFResource systemResource = super.create(aclURI, model);
		if ( ! this.isACESystemResource(systemResource) ) {
			throw new FactoryException("The resource isn't an AccessControlEntry object.");
		}
		return new ACESystemResourceImpl(systemResource.getResource());
	}

	public ACESystemResource create(ACLSystemResource aclSR, UUID subjectUUID, SubjectType subjectType, boolean granting) {
		return create(aclSR, subjectUUID, subjectType, granting, new ArrayList<CarbonPermission>());
	}

	public ACESystemResource create(ACLSystemResource aclSR, UUID subjectUUID, SubjectType subjectType, boolean granting, List<CarbonPermission> permissions) {
		ACESystemResource ace = null;

		StringBuilder uriBuilder = new StringBuilder();
		//@formatter:off
		uriBuilder
			.append(aclSR.getBaseURI())
			.append(Carbon.SYSTEM_RESOURCE_SIGN)
			.append(AceSR.PREFIX)
			.append(aclSR.getACEntries().size())
		;
		//@formatter:on
		String uri = uriBuilder.toString();

		Model model = aclSR.getResource().getModel();
		Resource resource = model.createResource(uri);

		ace = new ACESystemResourceImpl(resource);
		ace.setType(AceSR.Resources.CLASS.getResource());
		ace.setSubjectUUID(subjectUUID);
		ace.setSubjectType(subjectType);
		ace.setGranting(granting);
		ace.setPermissions(permissions);

		// Add reference to the ACL
		aclSR.addACEntry(ace);

		return ace;
	}

	public List<String> validate(ACESystemResource aceSR) {
		List<String> violations = new ArrayList<String>();

		// subject validation
		UUID subjectUUID = aceSR.getSubjectUUID();
		if ( subjectUUID == null ) {
			StringBuilder violationBuilder = new StringBuilder();
			violationBuilder.append(AceSR.Properties.SUBJECT.getSlug()).append(" > Is missing or invalid. Should be a UUID string.");
			violations.add(violationBuilder.toString());
		}

		// subjectType validation
		SubjectType subjectType = aceSR.getSubjectType();
		if ( subjectType == null ) {
			StringBuilder violationBuilder = new StringBuilder();
			violationBuilder.append(AceSR.Properties.SUBJECT.getSlug()).append(" > Is missing or invalid. Should be one of the following URI resources: ");

			SubjectType[] subjectTypes = SubjectType.values();
			int i = 1;
			for (SubjectType type : subjectTypes) {
				violationBuilder.append(type.getURI());
				if ( i < subjectTypes.length ) {
					violationBuilder.append(", ");
				}
				i++;
			}

			violations.add(violationBuilder.toString());
		}

		// mode validation
		List<CarbonPermission> modes = aceSR.getPermissions();
		if ( modes.isEmpty() ) {
			StringBuilder violationBuilder = new StringBuilder();
			violationBuilder.append(AceSR.Properties.MODE.getSlug()).append(" > The ACE doesn't have valid Modes attached to it.");
			violations.add(violationBuilder.toString());
		}

		// granting violation
		Boolean granting = aceSR.isGranting();
		if ( granting == null ) {
			StringBuilder violationBuilder = new StringBuilder();
			violationBuilder.append(AceSR.Properties.GRANTING.getSlug()).append(" > Is missing or invalid. Should be a boolean value.");
			violations.add(violationBuilder.toString());
		}

		return violations;
	}

	public boolean isACESystemResource(RDFResource ldpResource) {
		return ldpResource.isOfType(AceSR.Resources.CLASS.getUri());
	}

	protected class ACESystemResourceImpl extends LDPSystemResourceImpl implements ACESystemResource {

		public ACESystemResourceImpl(Resource resource) {
			super(resource);
		}

		@Override
		public UUID getSubjectUUID() {
			if ( ! this.getResource().hasProperty(AceSR.Properties.SUBJECT.getProperty()) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(AceSR.Properties.SUBJECT.getProperty());
			if ( statement == null ) {
				return null;
			}

			String uuidString = null;
			try {
				uuidString = statement.getString();
			} catch (Exception ignore) {
				return null;
			}

			if ( ! AuthenticationUtil.isUUIDString(uuidString) ) {
				return null;
			}
			return AuthenticationUtil.restoreUUID(uuidString);
		}

		@Override
		public void setSubjectUUID(UUID subjectUUID) {
			if ( this.getResource().hasProperty(AceSR.Properties.SUBJECT.getProperty()) ) {
				this.getResource().removeAll(AceSR.Properties.SUBJECT.getProperty());
			}
			this.getResource().addProperty(AceSR.Properties.SUBJECT.getProperty(), subjectUUID.toString());
		}

		@Override
		public SubjectType getSubjectType() {
			if ( ! this.getResource().hasProperty(AceSR.Properties.SUBJECT_TYPE.getProperty()) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(AceSR.Properties.SUBJECT_TYPE.getProperty());
			if ( statement == null ) {
				return null;
			}

			RDFNode node = statement.getObject();
			if ( ! node.isURIResource() ) {
				return null;
			}

			String subjectTypeURI = node.asResource().getURI();
			return SubjectType.findByURI(subjectTypeURI);
		}

		@Override
		public void setSubjectType(SubjectType subjectType) {
			if ( this.getResource().hasProperty(AceSR.Properties.SUBJECT_TYPE.getProperty()) ) {
				this.getResource().removeAll(AceSR.Properties.SUBJECT_TYPE.getProperty());
			}
			this.getResource().addProperty(AceSR.Properties.SUBJECT_TYPE.getProperty(), subjectType.getResource());
		}

		@Override
		public List<CarbonPermission> getPermissions() {
			List<CarbonPermission> modes = new ArrayList<CarbonPermission>();
			if ( ! this.getResource().hasProperty(AceSR.Properties.MODE.getProperty()) ) {
				return modes;
			}

			List<String> uris = new ArrayList<String>();
			StmtIterator properties = this.getResource().listProperties(AceSR.Properties.MODE.getProperty());
			while (properties.hasNext()) {
				Statement statement = properties.next();
				RDFNode node = statement.getObject();
				if ( node.isURIResource() ) {
					uris.add(node.asResource().getURI());
				}
			}
			return CarbonPermission.findByURIs(uris);
		}

		@Override
		public List<CarbonACLPermission> getACLPermissions() {
			return CarbonPermission.getACLPermissionList(this.getPermissions());
		}

		@Override
		public PermissionImpl getCombinedACLPermissionMask() {
			int combinedMask = 0;
			for (CarbonACLPermission permission : this.getACLPermissions()) {
				combinedMask = permission.getMask() | combinedMask;
			}
			return new PermissionImpl(combinedMask);
		}

		@Override
		public void addPermission(CarbonPermission mode) {
			this.getResource().addProperty(AceSR.Properties.MODE.getProperty(), mode.getResource());
		}

		@Override
		public void setPermissions(List<CarbonPermission> modes) {
			if ( this.getResource().hasProperty(AceSR.Properties.MODE.getProperty()) ) {
				this.getResource().removeAll(AceSR.Properties.MODE.getProperty());
			}
			for (CarbonPermission mode : modes) {
				this.addPermission(mode);
			}
		}

		@Override
		public void removePermission(CarbonPermission mode) {
			if ( ! this.getResource().hasProperty(AceSR.Properties.MODE.getProperty(), mode.getResource()) ) {
				return;
			}
			List<CarbonPermission> modes = this.getPermissions();
			modes.remove(mode);
			this.setPermissions(modes);
		}

		@Override
		public Boolean isGranting() {
			if ( ! this.getResource().hasProperty(AceSR.Properties.GRANTING.getProperty()) ) {
				return null;
			}

			Statement statement = this.getResource().getProperty(AceSR.Properties.GRANTING.getProperty());
			try {
				return statement.getBoolean();
			} catch (Exception ignore) {
				return null;
			}
		}

		@Override
		public void setGranting(boolean granting) {
			if ( this.getResource().hasProperty(AceSR.Properties.GRANTING.getProperty()) ) {
				this.getResource().removeAll(AceSR.Properties.GRANTING.getProperty());
			}
			this.getResource().addLiteral(AceSR.Properties.GRANTING.getProperty(), granting);

		}
	}
}
