package com.carbonldp.web;

import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.rdf.DocumentRDFHandler;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.*;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;

public class AbstractModelMessageConverter extends ModelMessageConverter<AbstractModel> {

	private ConfigurationRepository configurationRepository;

	public AbstractModelMessageConverter( ConfigurationRepository configurationRepository ) {
		super( true, true );
		Assert.notNull( configurationRepository );
		this.configurationRepository = configurationRepository;
	}

	@Override
	protected boolean supports( Class<?> clazz ) {
		return AbstractModel.class.isAssignableFrom( clazz );
	}

	@Override
	public AbstractModel read( Class<? extends AbstractModel> clazz, HttpInputMessage inputMessage ) throws IOException, HttpMessageNotReadableException {
		MediaType mediaType = inputMessage.getHeaders().getContentType();

		RDFFormat formatToUse;
		if ( this.mediaTypeFormats.containsKey( mediaType ) ) formatToUse = this.mediaTypeFormats.get( mediaType );
		else formatToUse = this.getDefaultFormat();

		InputStream bodyInputStream = inputMessage.getBody();

		RDFParser parser = Rio.createParser( formatToUse );
		AbstractModel model = new LinkedHashModel();
		String baseURI = configurationRepository.forgeGenericRequestURL();

		parser.setRDFHandler( new DocumentRDFHandler( model ) );

		try {
			parser.parse( bodyInputStream, baseURI );
		} catch ( RDFParseException | RDFHandlerException e ) {
			throw new HttpMessageNotReadableException( "The message couldn't be parsed into an RDF Model.", e );
		}

		return model;
	}
}
