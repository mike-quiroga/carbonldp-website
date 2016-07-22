package com.carbonldp.web.converters;

import com.carbonldp.config.ConfigurationRepository;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.*;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
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
		MediaType requestMediaType = inputMessage.getHeaders().getContentType();

		RDFFormat formatToUse = getFormatToUse( requestMediaType );

		InputStream bodyInputStream = inputMessage.getBody();
		if ( bodyInputStream == null ) return new LinkedHashModel();

		RDFParser parser = Rio.createParser( formatToUse );
		AbstractModel model = new LinkedHashModel();
		String baseIRI = configurationRepository.forgeGenericRequestURL();

		parser.setRDFHandler( new StatementCollector( model ) );
		try {
			parser.parse( bodyInputStream, baseIRI );
		} catch ( RDFParseException | RDFHandlerException e ) {
			throw new HttpMessageNotReadableException( "The message couldn't be parsed into an RDF Model.", e );
		}

		return model;
	}
}
