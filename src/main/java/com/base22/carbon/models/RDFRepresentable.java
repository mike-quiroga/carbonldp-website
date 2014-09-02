package com.base22.carbon.models;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.LDPResource;

public interface RDFRepresentable<O extends LDPResource> {
	public void recoverFromLDPR(LDPResource ldpResource) throws CarbonException;

	public O createRDFRepresentation();
}
