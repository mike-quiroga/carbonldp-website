package com.carbonldp.repository;

import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

import java.util.Collection;
import java.util.Iterator;

public class DocumentGraphQueryResultHandler extends GraphQueryResultHandler {
	private final Collection<Statement> statements;
	private final Collection<Statement> noContextStatements;
	private final ValueFactory valueFactory;

	public DocumentGraphQueryResultHandler( Collection<Statement> statements ) {
		this.statements = statements;
		this.valueFactory = ValueFactoryImpl.getInstance();
		noContextStatements = new LinkedHashModel();
	}

	@Override
	public boolean handleStatement( Statement statement ) {
		Resource contextResource = statement.getContext();
		if ( contextResource != null ) throw new IllegalArgumentException( "Named graphs aren't supported." );
		URI context;
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
			URI subject = ValueUtil.getIRI( subjectResource );
			if ( ! IRIUtil.hasFragment( subject ) ) context = subject;
			else context = new URIImpl( IRIUtil.getDocumentIRI( subject.stringValue() ) );
			documentStatement = valueFactory.createStatement( subject, statement.getPredicate(), statement.getObject(), context );
			statements.add( documentStatement );
			if ( ! noContextStatements.isEmpty() ) addContextToStatements( context, noContextStatements );
		}

		return true;
	}

	private void addContextToStatements( URI context, Collection<Statement> noContextStatements ) {
		for ( Statement statement : noContextStatements ) {
			Statement documentStatement = valueFactory.createStatement( statement.getSubject(), statement.getPredicate(), statement.getObject(), context );
			statements.add( documentStatement );
		}
		noContextStatements.clear();
	}

	private URI getContextFromPreviousStatements() {
		Iterator iterator = statements.iterator();
		if ( iterator.hasNext() ) {
			Statement statement = (Statement) iterator.next();
			return ValueUtil.getIRI( statement.getContext() );
		}
		return null;
	}
}
