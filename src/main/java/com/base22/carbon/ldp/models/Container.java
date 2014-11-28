package com.base22.carbon.ldp.models;

import java.util.List;

import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.APIPreferences.RetrieveContainerPreference;

public interface Container extends RDFSource {

	public String getTypeOfContainer();

	public String getMembershipResourceURI();

	public String getMembershipTriplesPredicate();

	public String getMemberOfRelation();

	public String getInsertedContentRelation();

	public InteractionModel getDefaultInteractionModel();

	public List<RetrieveContainerPreference> listDefaultRetrievePreferences();

	public String[] listContainedResourceURIs();

	public void removeContainerTriples();

	public void removeContainmentTriples();
}
