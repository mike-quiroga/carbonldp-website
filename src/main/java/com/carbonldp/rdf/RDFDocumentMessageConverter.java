package com.carbonldp.rdf;

import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.converters.ModelMessageConverter;
import com.carbonldp.web.exceptions.BadRequestException;
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
import java.util.HashSet;
import java.util.Set;

/**
 * @author MiguelAraCo
 * @since 0.10.0-ALPHA
 */
public class RDFDocumentMessageConverter extends ModelMessageConverter<RDFDocument> {

	private ConfigurationRepository configurationRepository;

	public RDFDocumentMessageConverter( ConfigurationRepository configurationRepository ) {
		super( true, false );

		Assert.notNull( configurationRepository );
		this.configurationRepository = configurationRepository;
	}

	@Override
	protected boolean supports( Class<?> clazz ) {
		return RDFDocument.class.isAssignableFrom( clazz );
	}

	@Override
	public RDFDocument read( Class<? extends RDFDocument> clazz, HttpInputMessage inputMessage ) throws IOException, HttpMessageNotReadableException {
		MediaType requestMediaType = inputMessage.getHeaders().getContentType();

		RDFFormat formatToUse = getFormatToUse( requestMediaType );

		InputStream bodyInputStream = inputMessage.getBody();

		RDFParser parser = Rio.createParser( formatToUse );

		String baseURI = configurationRepository.forgeGenericRequestURL();

		DocumentRDFHandler documentRDFHandler = new DocumentRDFHandler();
		documentRDFHandler.setDefaultContext( new URIImpl( baseURI ) );
		parser.setRDFHandler( documentRDFHandler );

		try {
			parser.parse( bodyInputStream, baseURI );
		} catch ( RDFParseException e ) {
			throw new HttpMessageNotReadableException( "The attempt of parsing the request body as: '" + formatToUse.getName() + "', failed." );
		} catch ( RDFHandlerException e ) {
			throw new BadRequestException( new Infraction( 0x6001, "formatToUse", formatToUse.getName() ) );
		}

		return documentRDFHandler.getDocument();
	}

	public class DocumentRDFHandler extends RDFHandlerBase {
		private RDFDocument document;
		private final AbstractModel documentModel;
		private final Set<Statement> contextLessStatements;

		private Boolean explicit = null;

		private final ValueFactory valueFactory;

		private URI defaultContext;
		private URI context;

		public DocumentRDFHandler() {
			this.documentModel = new LinkedHashModel();
			this.contextLessStatements = new HashSet<>();
			this.valueFactory = ValueFactoryImpl.getInstance();
		}

		@Override
		public void handleStatement( Statement statement ) throws RDFHandlerException {
			Resource context = statement.getContext();
			if ( context != null ) {
				if ( ( explicit != null && ! explicit ) || ( this.context != null && ! this.context.equals( context ) ) ) throw new RDFHandlerException( "Two (or more) different contexts were found." );
				if ( ! ValueUtil.isURI( context ) ) throw new RDFHandlerException( "BNodes are not valid contexts." );
				this.context = (URI) context;
				this.explicit = true;
			} else {
				if ( explicit != null && explicit ) throw new RDFHandlerException( "Two (or more) different contexts were found." );
				this.explicit = false;

				Resource subjectResource = statement.getSubject();
				if ( ValueUtil.isURI( subjectResource ) ) {
					URI subject = (URI) subjectResource;
					URI documentResource = new URIImpl( URIUtil.getDocumentURI( subject.stringValue() ) );
					if ( this.context != null && ! this.context.equals( documentResource ) ) throw new RDFHandlerException( "Two (or more) different contexts were found." );
					this.context = documentResource;
				} else {
					if ( this.context == null ) {
						this.contextLessStatements.add( statement );
						return;
					}
				}
			}

			statement = valueFactory.createStatement( statement.getSubject(), statement.getPredicate(), statement.getObject(), this.context );
			this.documentModel.add( statement );
		}

		@Override
		public void endRDF() throws RDFHandlerException {
			if ( this.context == null ) {
				if ( this.defaultContext == null ) throw new RDFHandlerException( "The model doesn't define (directly or indirectly) any context the RDFDocument can be based on." );

				this.context = this.defaultContext;
			}

			for ( Statement contextLessStatement : this.contextLessStatements ) {
				Statement statement = valueFactory.createStatement( contextLessStatement.getSubject(), contextLessStatement.getPredicate(), contextLessStatement.getObject(), this.context );
				this.documentModel.add( statement );
			}

			this.document = new RDFDocument( this.documentModel, this.context );
		}

		public RDFDocument getDocument() {
			return this.document;
		}

		public void setDefaultContext( URI context ) {
			this.defaultContext = context;
		}
	}
}
