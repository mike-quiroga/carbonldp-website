package com.carbonldp.rdf;

import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.ModelUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.converters.ModelMessageConverter;
import com.carbonldp.web.exceptions.BadRequestException;
import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.AbstractRDFHandler;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

		String baseIRI = configurationRepository.forgeGenericRequestURL();

		DocumentRDFHandler documentRDFHandler = new DocumentRDFHandler();
		documentRDFHandler.setDefaultContext( SimpleValueFactory.getInstance().createIRI( baseIRI ) );
		parser.setRDFHandler( documentRDFHandler );

		try {
			parser.parse( bodyInputStream, baseIRI );
		} catch ( RDFParseException | RDFHandlerException | IOException e ) {
			throw new BadRequestException( new Infraction( 0x6001, "formatToUse", formatToUse.getName() ) );
		}

		// TODO: Fix the root cause instead of monkey patching it
		return setGenericBNodes( documentRDFHandler.getDocument() );
	}

	private RDFDocument setGenericBNodes( RDFDocument document ) {
		ValueFactory valueFactory = SimpleValueFactory.getInstance();
		Set<Resource> subjects = document.subjects();
		Map<Resource, Resource> toChange = new HashMap<>();
		for ( Resource subject : subjects ) {
			if ( ! ValueUtil.isBNode( subject ) ) continue;
			String randomUUID = UUID.randomUUID().toString();
			toChange.put( subject, valueFactory.createBNode( randomUUID ) );
		}
		ModelUtil.replace( document.getBaseModel(), toChange );

		return document;
	}

	public class DocumentRDFHandler extends AbstractRDFHandler {
		private RDFDocument document;
		private final AbstractModel documentModel;
		private final Set<Statement> contextLessStatements;

		private Boolean explicit = null;

		private final ValueFactory valueFactory;

		private IRI defaultContext;
		private IRI context;

		public DocumentRDFHandler() {
			this.documentModel = new LinkedHashModel();
			this.contextLessStatements = new HashSet<>();
			this.valueFactory = SimpleValueFactory.getInstance();
		}

		@Override
		public void handleStatement( Statement statement ) throws RDFHandlerException {
			Resource context = statement.getContext();
			if ( context != null ) {
				if ( ( explicit != null && ! explicit ) || ( this.context != null && ! this.context.equals( context ) ) ) throw new RDFHandlerException( "Two (or more) different contexts were found." );
				if ( ! ValueUtil.isIRI( context ) ) throw new RDFHandlerException( "BNodes are not valid contexts." );
				this.context = (IRI) context;
				this.explicit = true;
			} else {
				if ( explicit != null && explicit ) throw new RDFHandlerException( "Two (or more) different contexts were found." );
				this.explicit = false;

				Resource subjectResource = statement.getSubject();
				if ( ValueUtil.isIRI( subjectResource ) ) {
					IRI subject = (IRI) subjectResource;
					IRI documentResource = SimpleValueFactory.getInstance().createIRI( IRIUtil.getDocumentIRI( subject.stringValue() ) );
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

		public void setDefaultContext( IRI context ) {
			this.defaultContext = context;
		}
	}
}
