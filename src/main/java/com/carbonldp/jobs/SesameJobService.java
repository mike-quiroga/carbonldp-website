package com.carbonldp.jobs;

import com.carbonldp.Vars;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.containers.DirectContainer;
import com.carbonldp.ldp.containers.DirectContainerFactory;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameJobService extends AbstractSesameLDPService implements JobService {
	private ContainerService containerService;
	private RDFSourceService sourceService;

	public void create( URI targetURI, Job job ) {
		validate( job );

		containerService.createChild( targetURI, job );

		createManualExecutionsContainer( job );
	}

	private void createManualExecutionsContainer( Job job ) {
		URI manualExecutionsURI = new URIImpl( job.getURI().stringValue().concat( Vars.getInstance().getManualExecutions() ) );
		RDFResource manualExecutionsResource = new RDFResource( manualExecutionsURI );

		DirectContainer container = DirectContainerFactory.getInstance().create( manualExecutionsResource, job.getURI(), JobDescription.Property.MANUAL_EXECUTION.getURI() );

		sourceService.createAccessPoint( job.getURI(), container );
	}

	private void validate( Job job ) {
		List<Infraction> infractions = new ArrayList<>();
		JobDescription.Type jobType = JobFactory.getInstance().getJobType( job );
		switch ( jobType ) {
			case BACKUP:
				infractions = BackupJobFactory.getInstance().validate( job );
				break;
			default:
				infractions.add( new Infraction( 0x2001, "rdf.type", "job type" ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Override
	public Job get( URI jobURI ) {
		return new Job( sourceService.get( jobURI ) );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) { this.sourceService = sourceService; }
}
