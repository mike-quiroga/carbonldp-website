package com.carbonldp.ldp.nonrdf;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.namespaces.C;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.carbonldp.Consts.*;

/**
 * @author NestorVenegas
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
			"FILTER( STRSTARTS( STR(?subject), STR(?sourceIRI) ))" + NEW_LINE +
			"}"
		;
	}

	@Override
	public Set<String> getFileIdentifiers( IRI rdfRepresentationIRI ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "sourceIRI", rdfRepresentationIRI );
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
	public void delete( IRI rdfRepresentationIRI ) {
		Set<String> fileIdentifiers = getFileIdentifiers( rdfRepresentationIRI );
		for ( String fileIdentifier : fileIdentifiers ) {
			UUID uuid = UUID.fromString( fileIdentifier );
			fileRepository.delete( uuid );
		}
	}

	@Autowired
	public void setFileRepository( FileRepository fileRepository ) {this.fileRepository = fileRepository; }
}
