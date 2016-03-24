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

		createExecutionsContainer( job );
	}

	private void createExecutionsContainer( Job job ) {
		URI executionsURI = new URIImpl( job.getURI().stringValue().concat( Vars.getInstance().getExecutions() ) );
		RDFResource executionsResource = new RDFResource( executionsURI );

		DirectContainer container = DirectContainerFactory.getInstance().create( executionsResource, job.getURI(), JobDescription.Property.EXECUTION.getURI(), ExecutionDescription.Property.JOB.getURI() );

		sourceService.createAccessPoint( job.getURI(), container );
	}

	private void validate( Job job ) {
		List<Infraction> infractions = new ArrayList<>();
		JobDescription.Type jobType = JobFactory.getInstance().getJobType( job );
		switch ( jobType ) {
			case EXPORT_BACKUP_JOB:
				infractions = ExportBackupJobFactory.getInstance().validate( job );
				break;
			default:
				infractions.add( new Infraction( 0x2001, "rdf.type", "job type" ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Override
	public Job get( URI targetURI ) {
		return new Job( sourceService.get( targetURI ) );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) { this.sourceService = sourceService; }
}
