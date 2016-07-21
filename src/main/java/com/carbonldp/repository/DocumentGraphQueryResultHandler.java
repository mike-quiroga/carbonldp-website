package com.carbonldp.repository;

import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.ValueUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.Collection;
import java.util.Iterator;

public class DocumentGraphQueryResultHandler extends GraphQueryResultHandler {
	private final Collection<Statement> statements;
	private final Collection<Statement> noContextStatements;
	private final ValueFactory valueFactory;

	public DocumentGraphQueryResultHandler( Collection<Statement> statements ) {
		this.statements = statements;
		this.valueFactory = SimpleValueFactory.getInstance();
		noContextStatements = new LinkedHashModel();
	}

	@Override
	public boolean handleStatement( Statement statement ) {
		Resource contextResource = statement.getContext();
		if ( contextResource != null ) throw new IllegalArgumentException( "Named graphs aren't supported." );
		IRI context;
		Statement documentStatement;

		Resource subjectResource = statement.getSubject();
		if ( ValueUtil.isBNode( subjectResource ) ) {
			context = getContextFromPreviousStatements();
			if ( context == null ) {
				documentStatement = valueFactory.createStatement( statement.getSubject(), statement.getPredicate(), statement.getObject() );
				noContextStatements.add( documentStatement );
			} else {
				documentStatement = valueFactory.createStatement( statement.getSubject(), statement.getPredicate(), statement.getObject(), context );
				statements.add( documentStatement );

			}
		} else {
			IRI subject = ValueUtil.getIRI( subjectResource );
			if ( ! IRIUtil.hasFragment( subject ) ) context = subject;
			else context = SimpleValueFactory.getInstance().createIRI( IRIUtil.getDocumentIRI( subject.stringValue() ) );
			documentStatement = valueFactory.createStatement( subject, statement.getPredicate(), statement.getObject(), context );
			statements.add( documentStatement );
			if ( ! noContextStatements.isEmpty() ) addContextToStatements( context, noContextStatements );
		}

		return true;
	}

	private void addContextToStatements( IRI context, Collection<Statement> noContextStatements ) {
		for ( Statement statement : noContextStatements ) {
			Statement documentStatement = valueFactory.createStatement( statement.getSubject(), statement.getPredicate(), statement.getObject(), context );
			statements.add( documentStatement );
		}
		noContextStatements.clear();
	}

	private IRI getContextFromPreviousStatements() {
		Iterator iterator = statements.iterator();
		if ( iterator.hasNext() ) {
			Statement statement = (Statement) iterator.next();
			return ValueUtil.getIRI( statement.getContext() );
		}
		return null;
	}
}
