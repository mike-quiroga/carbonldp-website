package com.carbonldp.ldp.sources;

import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.rdf.RDFNodeEnum;
import org.joda.time.DateTime;
import org.openrdf.model.IRI;

import java.util.Set;

public interface RDFSourceRepository {
	public boolean exists( IRI sourceIRI );

	public String getETag( IRI sourceIRI );

	public RDFSource get( IRI sourceIRI );

	public Set<RDFSource> get( Set<IRI> sourceIRIs );

	public IRI getDefaultInteractionModel( IRI targetIRI );

	public DateTime getModified( IRI sourceIRI );

	public DateTime touch( IRI sourceIRI );

	public DateTime touch( IRI sourceIRI, DateTime modified );

	public void createAccessPoint( IRI sourceIRI, AccessPoint accessPoint );

	public void update( RDFSource source );

	public void delete( IRI sourceIRI, boolean deleteOcurrences );

	public void replace( RDFSource source );

	boolean is( IRI resourceIRI, RDFNodeEnum type );
}
