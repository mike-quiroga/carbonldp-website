package com.carbonldp.ldp.nonrdf.backup;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.ContainerDescription;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.nonrdf.RDFRepresentationRepository;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import org.joda.time.DateTime;
import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class SesameBackupRepository extends AbstractSesameLDPRepository implements BackupRepository {

	private RDFRepresentationRepository rdfRepresentationRepository;
	private ContainerRepository containerRepository;
	private RDFSourceRepository sourceRepository;
	private static ValueFactory valueFactory = SimpleValueFactory.getInstance();

	public SesameBackupRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );
	}

	@Override
	public void createAppBackup( IRI appIRI, IRI backupIRI, File zipFile ) {
		DateTime creationTime = DateTime.now();
		IRI containerIRI = valueFactory.createIRI( appIRI.stringValue() + Vars.getInstance().getBackupsContainer() );

		Backup backup = BackupFactory.getInstance().create( backupIRI );
		backup.add( RDFSourceDescription.Property.DEFAULT_INTERACTION_MODEL.getIRI(), RDFSourceDescription.Resource.CLASS.getIRI() );
		backup.setTimestamps( creationTime );
		rdfRepresentationRepository.create( backup, zipFile, Consts.ZIP );

		addContainedResource( containerIRI, backupIRI );

		containerRepository.addMember( containerIRI, backupIRI );
		sourceRepository.touch( containerIRI, creationTime );
	}

	private void addContainedResource( IRI containerIRI, IRI resourceIRI ) {
		connectionTemplate.write( ( connection ) -> connection.add( containerIRI, ContainerDescription.Property.CONTAINS.getIRI(), resourceIRI, containerIRI ) );
	}

	@Autowired
	public void setRdfRepresentationRepository( RDFRepresentationRepository rdfRepresentationRepository ) {
		this.rdfRepresentationRepository = rdfRepresentationRepository;
	}

	@Autowired
	public void setContainerRepository( ContainerRepository containerRepository ) {
		this.containerRepository = containerRepository;
	}

	@Autowired
	public void setSourceRepository( RDFSourceRepository sourceRepository ) {
		this.sourceRepository = sourceRepository;
	}
}
