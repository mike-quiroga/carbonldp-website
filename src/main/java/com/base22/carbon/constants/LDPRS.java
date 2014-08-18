package com.base22.carbon.constants;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public abstract class LDPRS {
	public static final String TYPE = Carbon.CONFIGURED_PREFIXES.get("ldp").concat("RDFSource");
	public static final String LINK_TYPE = "<" + TYPE + ">; rel=\"type\"";

	public static final String CREATED = Carbon.CONFIGURED_PREFIXES.get("c") + "created";
	public static final Property CREATED_P = ResourceFactory.createProperty(CREATED);

	public static final String MODIFIED = Carbon.CONFIGURED_PREFIXES.get("c") + "modified";
	public static final Property MODIFIED_P = ResourceFactory.createProperty(MODIFIED);

	public static final String HAS_ACCESS_POINT = Carbon.CONFIGURED_PREFIXES.get("c") + "accessPoint";
	public static final Property HAS_ACCESS_POINT_P = ResourceFactory.createProperty(HAS_ACCESS_POINT);

	// TODO: Move this?
	public static final String ACCESS_POINT_CLASS = Carbon.CONFIGURED_PREFIXES.get("c") + "AccessPoint";
	public static final String ACCESS_POINT_PREFIX = Carbon.SYSTEM_RESOURCE_SIGN + "accessPoint-";
	public static final String CONTAINER = Carbon.CONFIGURED_PREFIXES.get("c") + "container";
	public static final Property CONTAINER_P = ResourceFactory.createProperty(CONTAINER);

	public static final String FOR_PROPERTY = Carbon.CONFIGURED_PREFIXES.get("c") + "forProperty";
	public static final Property FOR_PROPERTY_P = ResourceFactory.createProperty(FOR_PROPERTY);

}
