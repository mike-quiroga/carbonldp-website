package com.base22.carbon.apps;

import java.util.HashSet;
import java.util.UUID;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.authentication.AuthenticationUtil;
import com.base22.carbon.ldp.models.RDFResource;
import com.base22.carbon.models.RDFRepresentable;
import com.base22.carbon.models.UUIDObject;

public class Application extends UUIDObject implements RDFRepresentable<ApplicationRDF> {

	private UUID datasetUuid;
	private String slug;
	private String name;
	private String masterKey;

	private HashSet<ApplicationRole> applicationRoles;

	public UUID getDatasetUuid() {
		return datasetUuid;
	}

	public void setDatasetUuid(UUID datasetUuid) {
		this.datasetUuid = datasetUuid;
	}

	public void setDatasetUuid(String datasetName) {
		this.datasetUuid = AuthenticationUtil.restoreUUID(datasetName);
	}

	public String getDatasetName() {
		if ( datasetUuid != null ) {
			return this.datasetUuid.toString();
		} else {
			return null;
		}
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getIdentifier() {
		if ( this.slug != null ) {
			return this.slug;
		} else {
			return getUuidString();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMasterKey() {
		return masterKey;
	}

	public void setMasterKey(String masterKey) {
		this.masterKey = masterKey;
	}

	public HashSet<ApplicationRole> getApplicationRoles() {
		return applicationRoles;
	}

	public void setApplicationRoles(HashSet<ApplicationRole> applicationRoles) {
		this.applicationRoles = applicationRoles;
	}

	@Override
	public void recoverFromLDPR(RDFResource ldpResource) throws CarbonException {
		ApplicationRDFFactory factory = new ApplicationRDFFactory();
		ApplicationRDF rdfApplication = factory.create(ldpResource.getResource());

		this.setUuid(rdfApplication.getUUID());
		this.setSlug(rdfApplication.getSlug());
		this.setName(rdfApplication.getName());

	}

	@Override
	public ApplicationRDF createRDFRepresentation() {
		ApplicationRDFFactory factory = new ApplicationRDFFactory();
		return factory.create(this);
	}
}
