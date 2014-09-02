package com.base22.carbon.models;

import java.util.UUID;

import com.base22.carbon.ldp.LDPResource;

public interface RDFGenericRequest extends LDPResource {
	public UUID[] getUUIDs();
}
