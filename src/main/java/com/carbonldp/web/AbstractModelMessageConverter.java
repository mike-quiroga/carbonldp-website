package com.carbonldp.web;

import com.carbonldp.ConfigurationRepository;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.RDFHandlerBase;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class AbstractModelMessageConverter extends ModelMessageConverter<AbstractModel> {

	private ConfigurationRepository configurationRepository;

	public AbstractModelMessageConverter(ConfigurationRepository configurationRepository) {
		super( true, true );
		Assert.notNull( configurationRepository );
		this.configurationRepository = configurationRepository;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return AbstractModel.class.isAssignableFrom( clazz );
	}

	@Override
	public AbstractModel read(Class<? extends AbstractModel> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
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

	private class DocumentRDFHandler extends RDFHandlerBase {
		private final Collection<Statement> statements;
		private final ValueFactory valueFactory;

		public DocumentRDFHandler(Collection<Statement> statements) {
			this.statements = statements;
			this.valueFactory = ValueFactoryImpl.getInstance();
		}

		@Override
		public void handleStatement(Statement statement) throws RDFHandlerException {
			Resource contextResource = statement.getContext();
			if ( contextResource != null ) throw new RDFHandlerException( "Named graphs aren't supported." );

			Resource subjectResource = statement.getSubject();
			if ( ValueUtil.isBNode( subjectResource ) ) throw new RDFHandlerException( "BNodes aren't supported." );

			URI subject = ValueUtil.getURI( subjectResource );
			URI context;
			if ( !URIUtil.hasFragment( subject ) ) context = subject;
			else context = new URIImpl( URIUtil.getDocumentURI( subject.stringValue() ) );

			Statement documentStatement = valueFactory.createStatement( subject, statement.getPredicate(), statement.getObject(), context );
			statements.add( documentStatement );
		}
	}
}
