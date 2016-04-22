package com.carbonldp.ldp.sources;

import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.AccessPoint;
import com.carbonldp.ldp.containers.AccessPointFactory;
import com.carbonldp.ldp.containers.ContainerFactory;
import com.carbonldp.ldp.nonrdf.NonRDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.*;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.LiteralUtil;
import com.carbonldp.utils.ValueUtil;
import org.joda.time.DateTime;
import org.openrdf.model.*;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.query.algebra.Datatype;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;
import java.util.stream.Collectors;

public class SesameRDFSourceService extends AbstractSesameLDPService implements RDFSourceService {

	private NonRDFSourceRepository nonRDFSourceRepository;

	@Override
	public boolean exists( IRI sourceIRI ) {
		return sourceRepository.exists( sourceIRI );
	}

	@Override
	public RDFSource get( IRI sourceIRI ) {
		if ( ! exists( sourceIRI ) ) throw new ResourceDoesntExistException();
		return sourceRepository.get( sourceIRI );
	}

	@Override
	public String getETag( IRI sourceIRI ) {
		if ( ! exists( sourceIRI ) ) throw new ResourceDoesntExistException();
		return sourceRepository.getETag( sourceIRI );
	}

	@Override
	public DateTime getModified( IRI sourceIRI ) {

		if ( ! exists( sourceIRI ) ) throw new ResourceDoesntExistException();
		return sourceRepository.getModified( sourceIRI );
	}

	@Override
	public IRI getDefaultInteractionModel( IRI sourceIRI ) {
		if ( ! exists( sourceIRI ) ) throw new ResourceDoesntExistException();
		return sourceRepository.getDefaultInteractionModel( sourceIRI );
	}

	@Override
	public DateTime createAccessPoint( IRI parentIRI, AccessPoint accessPoint ) {
		if ( ! exists( parentIRI ) ) throw new ResourceDoesntExistException();
		DateTime creationTime = DateTime.now();
		accessPoint.setTimestamps( creationTime );
		validate( accessPoint, parentIRI );
		sourceRepository.createAccessPoint( parentIRI, accessPoint );
		sourceRepository.touch( parentIRI, creationTime );
		aclRepository.createACL( accessPoint.getIRI() );
		return creationTime;
	}

	private void validate( AccessPoint accessPoint, IRI parentIRI ) {
		List<Infraction> infractions = AccessPointFactory.getInstance().validate( accessPoint, parentIRI );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Override
	public void touch( IRI sourceIRI, DateTime now ) {
		if ( ! exists( sourceIRI ) ) throw new ResourceDoesntExistException();
		sourceRepository.touch( sourceIRI, now );
	}

	@Override
	public void add( IRI sourceIRI, RDFDocument document ) {
		if ( ! exists( sourceIRI ) ) throw new ResourceDoesntExistException();

		containsImmutableProperties( document );
		validateBNodesUniqueIdentifier( document );

		document = handleBlankNodesIdentifiers( document );

		documentRepository.add( sourceIRI, document );

		sourceRepository.touch( sourceIRI );
	}

	@Override
	public void set( IRI sourceIRI, RDFDocument document ) {
		if ( ! exists( sourceIRI ) ) throw new ResourceDoesntExistException();
		containsImmutableProperties( document );

		documentRepository.set( sourceIRI, document );

		sourceRepository.touch( sourceIRI );
	}

	@Override
	public DateTime replace( RDFSource source ) {
		DateTime modifiedTime = DateTime.now();

		RDFSource originalSource = get( source.getIRI() );
		RDFDocument originalDocument = originalSource.getDocument();
		RDFDocument newDocument = mapBNodeSubjects( originalDocument, source.getDocument() );

		AbstractModel toAdd = newDocument.stream().filter( statement -> ! isStatementInModel( statement, originalDocument ) ).collect( Collectors.toCollection( LinkedHashModel::new ) );
		RDFDocument documentToAdd = new RDFDocument( toAdd, source.getIRI() );

		AbstractModel toDelete = originalDocument.stream().filter( statement -> ! isStatementInModel( statement, newDocument ) ).collect( Collectors.toCollection( LinkedHashModel::new ) );
		RDFDocument documentToDelete = new RDFDocument( toDelete, source.getIRI() );

		subtract( originalSource.getIRI(), documentToDelete );
		add( originalSource.getIRI(), documentToAdd );

		sourceRepository.touch( source.getIRI(), modifiedTime );

		return modifiedTime;
	}

	@Override
	public void subtract( IRI sourceIRI, RDFDocument document ) {
		if ( ! exists( sourceIRI ) ) throw new ResourceDoesntExistException();
		RDFSource originalSource = get( document.getDocumentResource().getIRI() );
		RDFDocument originalDocument = originalSource.getDocument();
		document = mapBNodeSubjects( originalDocument, document );

		containsImmutableProperties( originalDocument, document );
		document = addIdentifierToRemoveIfBNodeIsEmpty( originalDocument, document );

		documentRepository.subtract( sourceIRI, document );

		sourceRepository.touch( sourceIRI );
	}

	private RDFDocument addIdentifierToRemoveIfBNodeIsEmpty( RDFDocument originalDocument, RDFDocument document ) {
		IRI resourceIRI = originalDocument.getDocumentResource().getIRI();

		Set<Resource> newSubjects = document.subjects();

		for ( Resource subject : newSubjects ) {
			if ( ! ValueUtil.isBNode( subject ) ) continue;
			BNode subjectBNode = ValueUtil.getBNode( subject );
			RDFBlankNode newBlankNode = new RDFBlankNode( document.getBaseModel(), subjectBNode, resourceIRI );
			RDFBlankNode originalBlankNode = new RDFBlankNode( originalDocument.getBaseModel(), subjectBNode, resourceIRI );
			if ( originalBlankNode.size() - newBlankNode.size() != 1 ) continue;
			newBlankNode.setIdentifier( originalBlankNode.getIdentifier() );
		}

		return document;
	}

	@Override
	public void delete( IRI sourceIRI ) {
		if ( ! exists( sourceIRI ) ) throw new ResourceDoesntExistException();
		nonRDFSourceRepository.delete( sourceIRI );
		sourceRepository.delete( sourceIRI, true );
	}

	private boolean isStatementInModel( Statement statement, Model model ) {
		if ( ValueUtil.isResource( statement.getObject() ) ) return model.contains( statement );
		Literal objectLiteral = ValueUtil.getLiteral( statement.getObject() );
		if ( LiteralUtil.isString( objectLiteral ) || LiteralUtil.isBoolean( objectLiteral ) ) return model.contains( statement );

		Model filteredModel = model.filter( statement.getSubject(), statement.getPredicate(), null );
		if ( filteredModel.isEmpty() ) return false;

		boolean isDate = LiteralUtil.isDate( objectLiteral );
		Set<Value> objects = filteredModel.objects();
		for ( Value modelObject : objects ) {
			if ( ! ValueUtil.isLiteral( modelObject ) ) continue;
			Literal modelLiteral = ValueUtil.getLiteral( modelObject );
			if ( isDate ) {
				if ( ! LiteralUtil.isDate( modelLiteral ) ) continue;
				XMLGregorianCalendar modelDate = modelLiteral.calendarValue().normalize();
				XMLGregorianCalendar statementDate = objectLiteral.calendarValue().normalize();
				if ( modelDate.equals( statementDate ) ) return true;

			} else {
				if ( ! LiteralUtil.isDecimal( modelLiteral ) ) continue;
				if ( ! modelLiteral.getDatatype().equals( objectLiteral.getDatatype() ) ) continue;
				double modelDecimal = modelLiteral.doubleValue();
				double statementDecimal = objectLiteral.doubleValue();
				if ( modelDecimal == statementDecimal ) return true;
			}
		}
		return false;
	}

	private void validateResourcesBelongToSource( IRI sourceIRI, Collection<RDFResource> resourceViews ) {
		List<IRI> resourceIRIs = resourceViews.stream().map( RDFResource::getIRI ).collect( Collectors.toList() );
		resourceIRIs.add( sourceIRI );
		IRI[] uris = resourceIRIs.toArray( new IRI[resourceIRIs.size()] );
		if ( ! IRIUtil.belongsToSameDocument( uris ) ) throw new IllegalArgumentException( "The resourceViews don't belong to the source's document." );
	}

	private RDFDocument handleBlankNodesIdentifiers( RDFDocument document ) {
		RDFSource originalSource = get( document.getDocumentResource().getIRI() );
		RDFDocument originalDocument = originalSource.getDocument();
		document = mapBNodeSubjects( originalDocument, document );

		Set<Resource> originalSubjects = originalDocument.subjects();
		Set<Resource> newSubjects = document.subjects();

		for ( Resource subject : newSubjects ) {
			if ( ! ValueUtil.isBNode( subject ) ) continue;
			if ( originalSubjects.contains( subject ) ) continue;
			BNode subjectBNode = ValueUtil.getBNode( subject );
			RDFBlankNode blankNode = new RDFBlankNode( document.getBaseModel(), subjectBNode, originalSource.getIRI() );
			if ( blankNode.getIdentifier() != null ) {
				if ( blankNode.size() == 1 ) document.removeAll( blankNode );
				continue;
			}
			RDFBlankNodeFactory.setIdentifier( blankNode );
		}

		return document;
	}

	private void containsImmutableProperties( RDFDocument document ) {
		List<Infraction> infractions = validateDocumentContainsImmutableProperties( document );

		infractions.addAll( RDFDocumentFactory.getInstance().validateBlankNodes( document ) );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private void containsImmutableProperties( RDFDocument originalDocument, RDFDocument document ) {
		List<Infraction> infractions = validateDocumentContainsImmutableProperties( document );

		Set<Resource> newSubjects = document.subjects();

		for ( Resource subject : newSubjects ) {
			if ( ! ValueUtil.isBNode( subject ) ) continue;
			BNode subjectBNode = ValueUtil.getBNode( subject );
			RDFBlankNode originalBlankBode = new RDFBlankNode( originalDocument.getBaseModel(), subjectBNode, originalDocument.getDocumentResource().getIRI() );
			RDFBlankNode newBlankNode = new RDFBlankNode( document.getBaseModel(), subjectBNode, document.getDocumentResource().getIRI() );
			if ( originalBlankBode.size() == newBlankNode.size() ) continue;
			infractions.addAll( RDFDocumentFactory.getInstance().validateBlankNodes( document ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private List<Infraction> validateDocumentContainsImmutableProperties( RDFDocument document ) {
		List<Infraction> infractions = new ArrayList<>();
		infractions.addAll( ContainerFactory.getInstance().validateImmutableProperties( document.getDocumentResource() ) );
		infractions.addAll( ContainerFactory.getInstance().validateSystemManagedProperties( document.getDocumentResource() ) );
		return infractions;
	}

	private RDFDocument mapBNodeSubjects( RDFDocument originalDocument, RDFDocument newDocument ) {
		IRI documentUri = newDocument.getDocumentResource().getIRI();

		Set<String> originalDocumentBlankNodesIdentifier = originalDocument.getBlankNodesIdentifier();
		Set<String> newDocumentBlankNodesIdentifiers = newDocument.getBlankNodesIdentifier();
		for ( String newDocumentBlankNodeIdentifier : newDocumentBlankNodesIdentifiers ) {
			if ( ! originalDocumentBlankNodesIdentifier.contains( newDocumentBlankNodeIdentifier ) ) continue;

			RDFBlankNode newBlankNode = newDocument.getBlankNode( newDocumentBlankNodeIdentifier );
			BNode originalBNode = originalDocument.getBlankNode( newDocumentBlankNodeIdentifier ).getSubject();

			if ( newBlankNode.getSubject().equals( originalBNode ) ) continue;

			Map<IRI, Set<Value>> propertiesMap = newBlankNode.getPropertiesMap();
			Set<IRI> properties = propertiesMap.keySet();

			for ( IRI property : properties ) {
				Set<Value> objects = propertiesMap.get( property );
				for ( Value object : objects ) {
					newDocument.getBaseModel().add( originalBNode, property, object, documentUri );
				}
			}
			newDocument.subjects().remove( newBlankNode.getSubject() );

		}
		return newDocument;
	}

	private void validateBNodesUniqueIdentifier( RDFDocument document ) {
		Set<RDFBlankNode> blankNodes = document.getBlankNodes();
		List<Infraction> infractions = new ArrayList<>();
		IRI documentIRI = document.getDocumentResource().getDocumentIRI();
		for ( RDFBlankNode blankNode : blankNodes ) {
			if ( blankNode.getIdentifier() == null ) continue;
			if ( blankNodeRepository.hasProperty( blankNode.getSubject(), RDFBlankNodeDescription.Property.BNODE_IDENTIFIER, documentIRI ) )
				infractions.add( new Infraction( 0x2004, "property", RDFBlankNodeDescription.Property.BNODE_IDENTIFIER.getIRI().stringValue() ) );
		}
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Autowired
	public void setNonRDFSourceService( NonRDFSourceRepository nonRDFSourceRepository ) { this.nonRDFSourceRepository = nonRDFSourceRepository; }
}
