package com.carbonldp.rdf;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.utils.ValueUtil;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.eclipse.rdf4j.rio.helpers.NTriplesWriterSettings;
import org.eclipse.rdf4j.rio.nquads.NQuadsWriter;
import org.eclipse.rdf4j.rio.ntriples.NTriplesUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * While writing the NQuads file, this writer seeks and replaces configuration specific details like
 * the platform's domain and the application slug, with placeholders that can be replaced with the new
 * configuration later on
 *
 * @author JorgeEspinosa
 * @since _version_
 */
public class RelativeNQuadsWriter extends NQuadsWriter {
	private final String host;
	private final String hostPlaceholder;

	private final String app;
	private final String appPlaceholder;

	private final String appRootContainer;
	private final String appRootContainerPlaceholder;

	public RelativeNQuadsWriter( OutputStream outputStream, String domainPlaceholder ) {
		this( outputStream, domainPlaceholder, null, null );
	}

	public RelativeNQuadsWriter( OutputStream outputStream, String domainPlaceholder, String app, String appPlaceholder ) {
		super( outputStream );

		Vars vars = Vars.getInstance();

		String protocol = vars.getProtocol();
		String domain = vars.getDomain();

		this.host = protocol + "://" + domain + "/";
		this.hostPlaceholder = protocol + "://" + domainPlaceholder + "/";

		if ( app != null && appPlaceholder != null ) {
			app = app.endsWith( Consts.SLASH ) ? app : app + Consts.SLASH;
			appPlaceholder = appPlaceholder.endsWith( Consts.SLASH ) ? appPlaceholder : appPlaceholder + Consts.SLASH;

			String appsEntryPointURL = vars.getAppsEntryPointURL();
			if ( ! appsEntryPointURL.startsWith( this.host ) ) throw new RuntimeException( "The apps entry point doesn't start with the configured platform host" );
			String appsEntryPointURLPlaceholder = this.hostPlaceholder + appsEntryPointURL.substring( this.host.length() );

			this.appRootContainer = appsEntryPointURL + app;
			this.appRootContainerPlaceholder = appsEntryPointURLPlaceholder + appPlaceholder;

			String appsContainer = vars.getAppsContainerURL();
			if ( ! appsContainer.startsWith( this.host ) ) throw new RuntimeException( "The apps container URL doesn't start with the configured platform host" );
			String appsContainerPlaceholder = this.hostPlaceholder + appsContainer.substring( this.host.length() );

			this.app = appsContainer + app;
			this.appPlaceholder = appsContainerPlaceholder + appPlaceholder;
		} else {
			this.app = null;
			this.appPlaceholder = null;
			this.appRootContainer = null;
			this.appRootContainerPlaceholder = null;
		}
	}

	@Override
	public void handleStatement( Statement statement ) throws RDFHandlerException {
		if ( ! writingStarted ) throw new RuntimeException( "Document writing has not yet been started" );

		try {
			// Subject
			NTriplesUtil.append( relativize( statement.getSubject() ), writer );
			writer.write( Consts.SPACE );

			// Predicate
			NTriplesUtil.append( relativize( statement.getPredicate() ), writer );
			writer.write( Consts.SPACE );

			// Object
			NTriplesUtil.append(
				relativize( statement.getObject() ),
				writer,
				getWriterConfig().get( BasicWriterSettings.XSD_STRING_TO_PLAIN_LITERAL ),
				getWriterConfig().get( NTriplesWriterSettings.ESCAPE_UNICODE )
			);

			// Context
			if ( statement.getContext() != null ) {
				writer.write( Consts.SPACE );
				NTriplesUtil.append( relativize( statement.getContext() ), writer );
			}

			writer.write( " .\n" );
		} catch ( IOException e ) {
			throw new RDFHandlerException( e );
		}
	}

	private Value relativize( Value value ) {
		if ( ! ValueUtil.isIRI( value ) ) return value;

		String stringValue = value.stringValue();

		if ( this.app != null && stringValue.startsWith( this.app ) ) {
			stringValue = this.appPlaceholder + stringValue.substring( this.app.length() );
		} else if ( this.app != null && stringValue.startsWith( this.appRootContainer ) ) {
			stringValue = this.appRootContainerPlaceholder + stringValue.substring( this.appRootContainer.length() );
		} else if ( stringValue.startsWith( this.host ) ) {
			stringValue = this.hostPlaceholder + stringValue.substring( this.host.length() );
		}

		return SimpleValueFactory.getInstance().createIRI( stringValue );
	}
}
