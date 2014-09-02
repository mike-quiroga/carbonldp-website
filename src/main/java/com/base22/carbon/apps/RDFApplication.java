package com.base22.carbon.apps;

import java.util.UUID;

import com.base22.carbon.ldp.LDPResource;

public interface RDFApplication extends LDPResource {
	public UUID getUUID();

	public void setUUID(UUID uuid);

	public String getSlug();

	public void setSlug(String slug);

	public String getName();

	public void setName(String name);

	public String getMasterKey();

	public void setMasterKey(String masterKey);

	public String[] getDomains();

	public void setDomains(String[] allowedDomains);
}
