package com.base22.carbon.models;

import com.base22.carbon.constants.APIPreferences.InteractionModel;

public interface LDPContainer extends LDPRSource {

	public String getTypeOfContainer();

	public String getMembershipResourceURI();

	public String getMembershipTriplesPredicate();

	public String getMemberOfRelation();

	public String getInsertedContentRelation();

	public InteractionModel getDefaultInteractionModel();

	public void removeContainerTriples();

	public void removeContainmentTriples();
}
