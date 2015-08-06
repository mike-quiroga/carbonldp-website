package com.carbonldp.sparql;

import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.resultio.QueryResultIO;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SPARQLTupleMessageConverter extends SPARQLMessageConverter<SPARQLTupleResult, TupleQueryResultFormat> {
	public SPARQLTupleMessageConverter() {
		List<TupleQueryResultFormat> list = new ArrayList<>();
		list.addAll( TupleQueryResultFormat.values() );
		setSupportedFormats( list );
	}

	@Override
	protected boolean supports( Class<?> clazz ) {
		return SPARQLTupleResult.class.isAssignableFrom( clazz );
	}

	protected void writeSparqlResult( SPARQLResult sparqlResult, MediaType contentType, HttpOutputMessage outputMessage ) {
		TupleQueryResult result = (TupleQueryResult) sparqlResult.getResult();
		OutputStream outputStream;
		try {
			outputStream = outputMessage.getBody();
			QueryResultIO.write( result, mediaTypeFormats.get( contentType ), outputStream );
			outputStream.flush();
		} catch ( IOException e ) {
			throw new RuntimeException( "unable to write response body, nested Exception: ", e );
		} catch ( QueryResultHandlerException e ) {
			throw new RuntimeException( "unable to write response body, nested Exception: ", e );
		} catch ( QueryEvaluationException e ) {
			throw new RuntimeException( "unable to evaluate query result, nested Exception: ", e );
		}
	}

}