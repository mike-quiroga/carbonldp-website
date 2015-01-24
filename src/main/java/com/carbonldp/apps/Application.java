package com.carbonldp.apps;

import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;

import com.carbonldp.commons.models.RDFDocument;

public class Application extends RDFDocument {

	private static final long serialVersionUID = 640544665380079388L;

	public Application(AbstractModel base, URI context) {
		super(base, context);
	}

}
