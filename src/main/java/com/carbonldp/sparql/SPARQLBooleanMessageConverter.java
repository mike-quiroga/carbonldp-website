package com.carbonldp.sparql;

import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.resultio.BooleanQueryResultFormat;
import org.openrdf.query.resultio.QueryResultIO;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SPARQLBooleanMessageConverter extends SPARQLMessageConverter<SPARQLBooleanResult, BooleanQueryResultFormat> {

	public SPARQLBooleanMessageConverter() {
		List<BooleanQueryResultFormat> list = new ArrayList<>();
		list.addAll( BooleanQueryResultFormat.values() );
		setSupportedFormats( list );
	}

	@Override
	protected boolean supports( Class<?> clazz ) {
		return SPARQLBooleanResult.class.isAssignableFrom( clazz );
	}

	protected void writeSparqlResult( SPARQLResult sparqlResult, MediaType contentType, HttpOutputMessage outputMessage ) {
		boolean result = (boolean) sparqlResult.getResult();
		OutputStream outputStream;
		try {
			outputStream = outputMessage.getBody();
			QueryResultIO.writeBoolean( result, this.mediaTypeFormats.get( contentType ), outputStream );
			outputStream.flush();
		} catch ( IOException e ) {
			throw new RuntimeException( "unable to write response body, nested Exception: ", e );
		} catch ( QueryResultHandlerException e ) {
			throw new RuntimeException( "unable to write response body, nested Exception: ", e );
		}

	}
}
