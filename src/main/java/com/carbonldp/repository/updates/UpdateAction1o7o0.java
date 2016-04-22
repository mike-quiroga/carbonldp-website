package com.carbonldp.repository.updates;

import com.carbonldp.authorization.acl.ACLDescription;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.rdf.RDFBlankNode;
import com.carbonldp.rdf.RDFBlankNodeDescription;
import org.openrdf.model.vocabulary.RDF;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class UpdateAction1o7o0 extends AbstractUpdateAction {
	final String updateACLTripleQuery = "" +
		"DELETE {" + NEW_LINE +
		TAB + "?target <" + RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getIRI().stringValue() + "> ?bNodeID." + NEW_LINE +
		"} WHERE {" + NEW_LINE +
		TAB + "?target <" + RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getIRI().stringValue() + "> ?bNodeID." + NEW_LINE +
		TAB + "?target <" + RDF.FIRST.stringValue() + "> ?first." + NEW_LINE +
		TAB + "?target <" + RDF.REST.stringValue() + "> ?rest." + NEW_LINE +
		"FILTER isBlank(?target)" + NEW_LINE +
		"}";

	@Override
	protected void execute() throws Exception {

	}
}
