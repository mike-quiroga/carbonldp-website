package com.carbonldp.repository;

import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.ModelUtil;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;

import java.util.ArrayList;
import java.util.List;

public class ETagHandler extends GraphQueryResultHandler {
	private final List<Resource> contexts;
	private final ValueFactory valueFactory;
	private final List<Integer> noContextValues;
	private int eTagValue;

	public ETagHandler() {
		this.contexts = new ArrayList<>();
		this.noContextValues = new ArrayList<>();
		this.valueFactory = SimpleValueFactory.getInstance();
		this.eTagValue = 0;
	}

	public int getETagValue() {
		return this.eTagValue;
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
				noContextValues.add( ModelUtil.calculateStatementETag( documentStatement ) );
			} else {
				documentStatement = valueFactory.createStatement( statement.getSubject(), statement.getPredicate(), statement.getObject(), context );
				int currentValue = ModelUtil.calculateStatementETag( documentStatement );
				eTagValue = ( eTagValue == 0 ) ? currentValue : eTagValue ^ currentValue;
			}
		} else {
			IRI subject = ValueUtil.getIRI( subjectResource );
			if ( ! IRIUtil.hasFragment( subject ) ) context = subject;
			else context = SimpleValueFactory.getInstance().createIRI( IRIUtil.getDocumentIRI( subject.stringValue() ) );
			if ( ! contexts.contains( context ) ) contexts.add( context );
			documentStatement = valueFactory.createStatement( subject, statement.getPredicate(), statement.getObject(), context );
			int currentValue = ModelUtil.calculateStatementETag( documentStatement );
			eTagValue = ( eTagValue == 0 ) ? currentValue : eTagValue ^ currentValue;
			if ( ! noContextValues.isEmpty() ) addContextValue( context, noContextValues );
		}
		return true;
	}

	private void addContextValue( IRI context, List<Integer> noContextValues ) {
		for ( Integer currentValue : noContextValues ) {
			currentValue = currentValue + 23 * context.hashCode();
			eTagValue = ( eTagValue == 0 ) ? currentValue : eTagValue ^ currentValue;
		}
		noContextValues.clear();
	}

	private IRI getContextFromPreviousStatements() {
		if ( contexts.isEmpty() ) return null;
		Resource context = contexts.get( contexts.size() - 1 );
		return ValueUtil.getIRI( context );
	}
}
