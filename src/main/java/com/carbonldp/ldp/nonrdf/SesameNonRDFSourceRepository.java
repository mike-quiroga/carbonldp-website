package com.carbonldp.ldp.nonrdf;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.namespaces.C;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.carbonldp.Consts.NEW_LINE;
import static com.carbonldp.Consts.TAB;

/**
 * @author NstorVenegas
 * @since 0.27.5-ALPHA
 */
public class SesameNonRDFSourceRepository extends AbstractSesameLDPRepository implements NonRDFSourceRepository {

	FileRepository fileRepository;

	public SesameNonRDFSourceRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	private static final String gtFileIdentifiersIncludingChildrenQuery;

	static {
		gtFileIdentifiersIncludingChildrenQuery = "" +
			"SELECT ?identifier" + NEW_LINE +
			"WHERE {" + NEW_LINE +
			TAB + "?subject <" + RDF.TYPE + "> <" + C.Classes.RDF_REPRESENTATION + ">;" + NEW_LINE +
			TAB + TAB + "<" + C.Properties.FILE_IDENTIFIER + "> ?identifier." + NEW_LINE +
			"FILTER( STRSTARTS( STR(?subject), STR(?sourceURI) ))" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<String> getFileIdentifiers( URI rdfRepresentationURI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "sourceURI", rdfRepresentationURI );
		return sparqlTemplate.executeTupleQuery( gtFileIdentifiersIncludingChildrenQuery, bindings, queryResult -> {
			Set<String> references = new HashSet<>();
			while ( queryResult.hasNext() ) {
				BindingSet bindingSet = queryResult.next();
				Value member = bindingSet.getValue( "identifier" );
				references.add( ValueUtil.getLiteral( member ).stringValue() );
			}

			return references;
		} );
	}

	@Override
	public void deleteResourceIncludingChildren( URI rdfRepresentationURI ) {
		Set<String> fileIdentifiers = getFileIdentifiers( rdfRepresentationURI );
		for ( String fileIdentifier : fileIdentifiers ) {
			UUID uuid = UUID.fromString( fileIdentifier );
			fileRepository.delete( uuid );
		}
	}

	@Autowired
	public void setFileRepository( FileRepository fileRepository ) {this.fileRepository = fileRepository; }
}
