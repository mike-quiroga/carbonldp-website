package com.base22.carbon.ldp.models;

import com.base22.carbon.APIPreferences.InteractionModel;

public interface Container extends RDFSource {

	public String getTypeOfContainer();

	public String getMembershipResourceURI();

	public String getMembershipTriplesPredicate();

	public String getMemberOfRelation();

	public String getInsertedContentRelation();

	public InteractionModel getDefaultInteractionModel();

	public String[] listContainedResourceURIs();

	public void removeContainerTriples();

	public void removeContainmentTriples();
}
