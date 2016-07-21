package com.carbonldp.sparql;

import com.google.common.collect.ImmutableList;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InMemoryTupleQueryResult implements TupleQueryResult {
	List<BindingSet> bindingSets;
	Iterator<BindingSet> bindingSetIterator;
	List<String> bindingNames;

	private InMemoryTupleQueryResult() {
		bindingSets = new ArrayList<>();
		bindingNames = new ArrayList<>();
	}

	public static InMemoryTupleQueryResult from( TupleQueryResult queryResult ) throws QueryEvaluationException {
		InMemoryTupleQueryResult inMemoryResult = new InMemoryTupleQueryResult();

		inMemoryResult.setBindingNames( queryResult.getBindingNames() );
		while ( queryResult.hasNext() ) {
			inMemoryResult.addBindingSet( queryResult.next() );
		}

		return inMemoryResult;
	}

	private void addBindingSet( BindingSet bindingSet ) {
		this.bindingSets.add( bindingSet );
	}

	@Override
	public List<String> getBindingNames() throws QueryEvaluationException {
		return ImmutableList.copyOf( this.bindingNames );
	}

	private void setBindingNames( List<String> bindingNames ) {
		this.bindingNames = bindingNames;
	}

	@Override
	public void close() throws QueryEvaluationException {
		if ( bindingSetIterator != null ) bindingSetIterator = null;
	}

	@Override
	public boolean hasNext() throws QueryEvaluationException {
		if ( bindingSetIterator == null ) this.bindingSetIterator = bindingSets.iterator();
		return bindingSetIterator.hasNext();
	}

	@Override
	public BindingSet next() throws QueryEvaluationException {
		if ( bindingSetIterator == null ) this.bindingSetIterator = bindingSets.iterator();
		return bindingSetIterator.next();
	}

	@Override
	public void remove() throws QueryEvaluationException {
		throw new UnsupportedOperationException();
	}
}
