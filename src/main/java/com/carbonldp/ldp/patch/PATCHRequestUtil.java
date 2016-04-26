package com.carbonldp.ldp.patch;

import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.Statement;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PATCHRequestUtil {

	public Set<DeleteAction> getDeleteActions( PATCHRequest patchRequest ) {
		return patchRequest.getDeleteActions()
						   .stream()
						   .map( uri -> new RDFResource( patchRequest.getBaseModel(), uri ) )
						   .map( DeleteAction::new )
						   .collect( Collectors.toSet() )
			;
	}

	private void executeDeleteActions( IRI sourceIRI, Set<DeleteAction> actions ) {
		for ( DeleteAction action : actions ) {
			RDFResource resourceToDelete = new RDFResource( action.getSubjectIRI() );
			for ( Statement actionStatement : action ) {
				DeleteActionDescription.Property actionSpecialProperty = getDeleteActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeDeleteActionSpecialProperty( sourceIRI, action, actionSpecialProperty );
				else resourceToDelete.add( actionStatement.getPredicate(), actionStatement.getObject() );
			}
		}

		// TODO: Delete statements
		throw new RuntimeException( "Not Implemented" );
	}

	private DeleteActionDescription.Property getDeleteActionSpecialProperty( Statement actionStatement ) {
		IRI predicate = actionStatement.getPredicate();
		return RDFNodeUtil.findByIRI( predicate, DeleteActionDescription.Property.class );
	}

	private void executeDeleteActionSpecialProperty( IRI sourceIRI, DeleteAction action, DeleteActionDescription.Property actionSpecialProperty ) {
		switch ( actionSpecialProperty ) {
			default:
				throw new RuntimeException( "Not Implemented" );
		}
	}

	private Set<SetAction> getSetActions( PATCHRequest patchRequest ) {
		return patchRequest.getSetActions()
						   .stream()
						   .map( uri -> new RDFResource( patchRequest.getBaseModel(), uri ) )
						   .map( SetAction::new )
						   .collect( Collectors.toSet() )
			;
	}

	private void executeSetAction( IRI sourceIRI, Set<SetAction> actions ) {
		Set<RDFResource> resourcesToSet = new HashSet<>();

		for ( SetAction action : actions ) {
			RDFResource resourceToSet = new RDFResource( action.getSubjectIRI() );
			for ( Statement actionStatement : action ) {
				SetActionDescription.Property actionSpecialProperty = getSetActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeSetActionSpecialProperty( sourceIRI, action, actionSpecialProperty );
				else resourceToSet.add( actionStatement.getPredicate(), actionStatement.getObject() );
			}
		}

		// TODO: Delete properties
		// TODO: Add Statements

		throw new RuntimeException( "Not Implemented" );
	}

	private SetActionDescription.Property getSetActionSpecialProperty( Statement actionStatement ) {
		IRI predicate = actionStatement.getPredicate();
		return RDFNodeUtil.findByIRI( predicate, SetActionDescription.Property.class );
	}

	private void executeSetActionSpecialProperty( IRI sourceIRI, SetAction action, SetActionDescription.Property actionSpecialProperty ) {
		switch ( actionSpecialProperty ) {
			default:
				throw new RuntimeException( "Not Implemented" );
		}
	}

	private Set<AddAction> getAddActions( PATCHRequest patchRequest ) {
		return patchRequest.getAddActions()
						   .stream()
						   .map( uri -> new RDFResource( patchRequest.getBaseModel(), uri ) )
						   .map( AddAction::new )
						   .collect( Collectors.toSet() )
			;
	}

	private void executeAddActions( IRI sourceIRI, Collection<AddAction> actions ) {
		Set<RDFResource> resourcesToAdd = new HashSet<>();
		for ( AddAction action : actions ) {
			RDFResource resourceToAdd = new RDFResource( action.getSubjectIRI() );
			for ( Statement actionStatement : action ) {
				AddActionDescription.Property actionSpecialProperty = getAddActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeAddActionSpecialProperty( sourceIRI, action, actionSpecialProperty );
				else resourceToAdd.add( actionStatement.getPredicate(), actionStatement.getObject() );
			}
			resourcesToAdd.add( resourceToAdd );
		}

		// TODO: Add statements
		throw new RuntimeException( "Not Implemented" );
	}

	private AddActionDescription.Property getAddActionSpecialProperty( Statement actionStatement ) {
		IRI predicate = actionStatement.getPredicate();
		return RDFNodeUtil.findByIRI( predicate, AddActionDescription.Property.class );
	}

	private void executeAddActionSpecialProperty( IRI sourceIRI, AddAction action, AddActionDescription.Property actionSpecialProperty ) {
		switch ( actionSpecialProperty ) {
			default:
				throw new RuntimeException( "Not Implemented" );
		}
	}
}
