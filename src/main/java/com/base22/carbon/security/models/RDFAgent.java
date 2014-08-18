package com.base22.carbon.security.models;

import java.util.UUID;

import com.base22.carbon.models.LDPResource;

public interface RDFAgent extends LDPResource {
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
