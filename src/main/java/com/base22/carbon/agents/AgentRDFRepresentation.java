package com.base22.carbon.agents;

import java.util.UUID;

import com.base22.carbon.ldp.models.LDPResource;

public interface AgentRDFRepresentation extends LDPResource {
	public UUID getUUID();

	public void setUUID(UUID roleUUID);

	public String getFullName();

	public void setFullName(String fullName);

	public String getMainEmail();

	public void setMainEmail(String mainMail);

	public String getPassword();

	public void setPassword(String password);

	public String getAPIKey();

	public void setAPIKey(String apiKey);

}
