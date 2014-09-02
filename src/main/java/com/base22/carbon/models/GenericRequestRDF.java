package com.base22.carbon.models;

import java.util.UUID;

import com.base22.carbon.ldp.models.LDPResource;

public interface GenericRequestRDF extends LDPResource {
	public UUID[] getUUIDs();
}
