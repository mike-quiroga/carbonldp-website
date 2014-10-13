package com.base22.carbon.models;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.models.RDFResource;

public interface RDFRepresentable<O extends RDFResource> {
	public void recoverFromLDPR(RDFResource ldpResource) throws CarbonException;

	public O createRDFRepresentation();
}
