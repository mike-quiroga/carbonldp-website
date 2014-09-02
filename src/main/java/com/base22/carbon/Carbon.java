package com.base22.carbon;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Carbon {

	public static final String DOMAIN = "carbonldp.com";
	public static final String PROTOCOL = "http";
	public static final String URL = PROTOCOL + "://" + DOMAIN;

	public static final String EXTENDING_RESOURCE_SIGN = "#";
	public static final String EXTENDING_RESOURCE_REGEX = "#";
	public static final String SYSTEM_RESOURCE_SIGN = "#$";
	public static final String SYSTEM_RESOURCE_REGEX = "#\\$";

	public static final Map<String, String> CONFIGURED_PREFIXES;
	static {
		// TODO: Load the prefixes from a dynamic source
		Map<String, String> prefixes = new HashMap<String, String>();
		prefixes.put("acl", "http://www.w3.org/ns/auth/acl#");
		prefixes.put("api", "http://purl.org/linked-data/api/vocab#");
		prefixes.put("c", "http://carbonldp.com/ns/v1/platform#");
		prefixes.put("cs", "http://carbonldp.com/ns/v1/security#");
		prefixes.put("cc", "http://creativecommons.org/ns#");
		prefixes.put("cert", "http://www.w3.org/ns/auth/cert#");
		prefixes.put("dbp", "http://dbpedia.org/property/");
		prefixes.put("dc", "http://purl.org/dc/terms/");
		prefixes.put("dc11", "http://purl.org/dc/elements/1.1/");
		prefixes.put("dcterms", "http://purl.org/dc/terms/");
		prefixes.put("doap", "http://usefulinc.com/ns/doap#");
		prefixes.put("example", "http://example.org/ns#");
		prefixes.put("exif", "http://www.w3.org/2003/12/exif/ns#");
		prefixes.put("fn", "http://www.w3.org/2005/xpath-functions#");
		prefixes.put("foaf", "http://xmlns.com/foaf/0.1/");
		prefixes.put("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
		prefixes.put("geonames", "http://www.geonames.org/ontology#");
		prefixes.put("gr", "http://purl.org/goodrelations/v1#");
		prefixes.put("http", "http://www.w3.org/2006/http#");
		prefixes.put("ldp", "http://www.w3.org/ns/ldp#");
		prefixes.put("log", "http://www.w3.org/2000/10/swap/log#");
		prefixes.put("owl", "http://www.w3.org/2002/07/owl#");
		prefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		prefixes.put("rei", "http://www.w3.org/2004/06/rei#");
		prefixes.put("rsa", "http://www.w3.org/ns/auth/rsa#");
		prefixes.put("rss", "http://purl.org/rss/1.0/");
		prefixes.put("sfn", "http://www.w3.org/ns/sparql#");
		prefixes.put("sioc", "http://rdfs.org/sioc/ns#");
		prefixes.put("skos", "http://www.w3.org/2004/02/skos/core#");
		prefixes.put("swrc", "http://swrc.ontoware.org/ontology#");
		prefixes.put("types", "http://rdfs.org/sioc/types#");
		prefixes.put("vcard", "http://www.w3.org/2001/vcard-rdf/3.0#");
		prefixes.put("wot", "http://xmlns.com/wot/0.1/");
		prefixes.put("xhtml", "http://www.w3.org/1999/xhtml#");
		prefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");

		CONFIGURED_PREFIXES = prefixes;
	}

	public static final Set<String> RESERVED_APPLICATION_NAMES;
	static {
		// TODO: This could be loaded from a dynamic source
		HashSet<String> reservedAppNames = new HashSet<String>();

		reservedAppNames.add("applications");
		reservedAppNames.add("platform");
		// TODO: This shouldn't be a reserved application name, for now it will be to keep the sparql client working
		reservedAppNames.add("sparql");

		RESERVED_APPLICATION_NAMES = reservedAppNames;
	}
}
