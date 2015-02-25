package com.carbonldp.repository;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;

public class DocumentGraphQueryResultHandler extends GraphQueryResultHandler {
	private final Collection<Statement> statements;
	private final ValueFactory valueFactory;

	public DocumentGraphQueryResultHandler(Collection<Statement> statements) {
		this.statements = statements;
		this.valueFactory = ValueFactoryImpl.getInstance();
	}

	@Override
	public boolean handleStatement(Statement statement) {
		Resource contextResource = statement.getContext();
		if ( contextResource != null ) throw new IllegalArgumentException("Named graphs aren't supported.");

		Resource subjectResource = statement.getSubject();
		if ( ValueUtil.isBNode(subjectResource) ) throw new IllegalArgumentException("BNodes aren't supported.");

		URI subject = ValueUtil.getURI(subjectResource);
		URI context;
		if ( ! URIUtil.hasFragment(subject) ) context = subject;
		else context = new URIImpl(URIUtil.getDocumentURI(subject.stringValue()));

		Statement documentStatement = valueFactory.createStatement(subject, statement.getPredicate(), statement.getObject(), context);
		statements.add(documentStatement);

		return true;
	}
}
