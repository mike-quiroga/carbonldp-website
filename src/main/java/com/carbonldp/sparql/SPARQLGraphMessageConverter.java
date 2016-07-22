package com.carbonldp.sparql;

import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.resultio.QueryResultIO;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class SPARQLGraphMessageConverter extends SPARQLMessageConverter<SPARQLGraphResult, RDFFormat> {

	public SPARQLGraphMessageConverter() {
		setSupportedFormats(
			Arrays.asList(
				RDFFormat.TURTLE,
				RDFFormat.JSONLD,
				RDFFormat.RDFJSON,
				RDFFormat.RDFXML,
				RDFFormat.TRIG,
				RDFFormat.NTRIPLES,
				RDFFormat.N3,
				RDFFormat.TRIX,
				RDFFormat.BINARY,
				RDFFormat.NQUADS
			)
		);
	}

	@Override
	protected boolean supports( Class<?> clazz ) {
		return SPARQLGraphResult.class.isAssignableFrom( clazz );
	}

	protected void writeSparqlResult( SPARQLResult sparqlResult, MediaType contentType, HttpOutputMessage outputMessage ) {
		GraphQueryResult result = (GraphQueryResult) sparqlResult.getResult();
		OutputStream outputStream;
		try {
			outputStream = outputMessage.getBody();
			QueryResultIO.writeGraph( result, mediaTypeFormats.get( contentType ), outputStream );
			outputStream.flush();
		} catch ( IOException e ) {
			throw new RuntimeException( "unable to write response body, nested Exception: ", e );
		} catch ( QueryEvaluationException e ) {
			throw new RuntimeException( "unable to evaluate query result, nested Exception: ", e );
		} catch ( RDFHandlerException e ) {
			throw new RuntimeException( "unable to write response body, nested Exception: ", e );
		}
	}
}
