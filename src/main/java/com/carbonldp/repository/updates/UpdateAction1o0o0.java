package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.authorization.acl.ACLDescription;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.utils.ValueUtil;
import org.apache.commons.io.IOUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.algebra.Str;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.spring.RepositoryConnectionFactory;
import org.openrdf.spring.SesameConnectionFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public class UpdateAction1o0o0 extends AbstractUpdateAction {

	private static final String resourcesFile = "platform-default.trig";

	final String emptyRepositoryQuery =
		"DELETE { " +
			"GRAPH ?c { " +
			"?s ?p ?o." +
			" }. \n" +
			"} WHERE {" +
			"GRAPH ?c { " +
			"?s ?p ?o. " +
			"}." +
			"}";

	@Override
	public void execute() throws Exception {
		emptyRepository();
		loadResourcesFile( resourcesFile, Vars.getInstance().getHost() );
	}

	protected void emptyRepository() {
		transactionWrapper.runInPlatformContext( () -> sparqlTemplate.executeUpdate( emptyRepositoryQuery, null ) );
	}
}
