package com.carbonldp.ldp.nonrdf;

import com.carbonldp.Consts;
import org.openrdf.model.URI;
import org.openrdf.rio.turtle.TurtleUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author JorgeEspinsa
 * @since _version_
 */
public class TriGWriter extends org.openrdf.rio.trig.TriGWriter {

	private String base;

	public TriGWriter( OutputStream out ) {
		super( out );
		setNamespaceTable();
	}

	@Override
	protected void writeURI( URI uri )
		throws IOException {
		String uriString = uri.toString();

		String prefix = null;

		int splitIdx = TurtleUtil.findURISplitIndex( uriString );
		if ( splitIdx > 0 ) {
			String namespace = uriString.substring( 0, splitIdx );
			prefix = namespaceTable.get( namespace );

		}

		if ( prefix != null ) {
			writer.write( prefix );
			writer.write( ":" );
			writer.write( uriString.substring( splitIdx ) );
		} else {
			writer.write( "<" );
			if ( base == null ) {
				writer.write( TurtleUtil.encodeURIString( uriString ) );
			} else {
				writer.write( TurtleUtil.encodeURIString( uriString.replace( base, "" ) ) );
			}
			writer.write( ">" );
		}
	}

	protected void setNamespaceTable() {
		for ( String ns : Consts.COMMON_PREFIXES.keySet() ) {
			namespaceTable.put( Consts.COMMON_PREFIXES.get( ns ), ns );
		}
	}

	public void setBase( String base ) { this.base = base; }

	public String getBase() {return this.base; }
}
