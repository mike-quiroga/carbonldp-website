package com.base22.carbon.models;

import java.util.UUID;

import com.base22.carbon.ldp.models.RDFResource;

public interface GenericRequestRDF extends RDFResource {
	public UUID[] getUUIDs();
}
