package com.carbonldp.sparql;

import com.carbonldp.Consts;
import com.carbonldp.repository.ConnectionRWTemplate;
import org.openrdf.model.Value;
import org.openrdf.query.*;
import org.openrdf.spring.SesameConnectionFactory;

import java.util.Map;

public class SPARQLTemplate {
	private final ConnectionRWTemplate connectionTemplate;

	public SPARQLTemplate( SesameConnectionFactory connectionFactory ) {
		this.connectionTemplate = new ConnectionRWTemplate( connectionFactory );
	}

	public boolean executeBooleanQuery( String queryString ) {
		return executeBooleanQuery( queryString, null );
	}

	public boolean executeBooleanQuery( String queryString, Map<String, Value> bindings ) {
		return connectionTemplate.read( connection -> {
			BooleanQuery query = connection.prepareBooleanQuery( QueryLanguage.SPARQL, queryString );

			if ( bindings != null ) setBindings( query, bindings );

			return query.evaluate();
		} );
	}

	public <E> E executeTupleQuery( String queryString, TupleQueryResultHandler<E> resultHandler ) {
		return executeTupleQuery( queryString, null, resultHandler );
	}

	public <E> E executeTupleQuery( String queryString, Map<String, Value> bindings, TupleQueryResultHandler<E> resultHandler ) {
		return connectionTemplate.read( connection -> {
			TupleQuery query = connection.prepareTupleQuery( QueryLanguage.SPARQL, queryString );

			if ( bindings != null ) setBindings( query, bindings );

			TupleQueryResult queryResult = query.evaluate();
			E result;
			try {
				result = resultHandler.handle( queryResult );
			} finally {
				queryResult.close();
			}
			return result;
		} );
	}

	public <E> E executeGraphQuery( String queryString, GraphQueryResultHandler<E> resultHandler ) {
		return executeGraphQuery( queryString, null, resultHandler );
	}

	public <E> E executeGraphQuery( String queryString, Map<String, Value> bindings, GraphQueryResultHandler<E> resultHandler ) {
		return connectionTemplate.read( connection -> {
			GraphQuery query = connection.prepareGraphQuery( QueryLanguage.SPARQL, queryString );
			if ( bindings != null ) setBindings( query, bindings );

			GraphQueryResult queryResult = query.evaluate();
			E result;
			try {
				result = resultHandler.handle( queryResult );
			} finally {
				queryResult.close();
			}
			return result;
		} );
	}

	public void executeUpdate( String updateString, Map<String, Value> bindings ) {
		connectionTemplate.write( connection -> {
			Update update = connection.prepareUpdate( QueryLanguage.SPARQL, updateString );
			if ( bindings != null ) setBindings( update, bindings );

			update.execute();
		} );
	}

	private void setBindings( Operation operation, Map<String, Value> bindings ) {
		for ( String bindingName : bindings.keySet() ) {
			bindingName = getBindingName( bindingName );
			Value bindingValue = bindings.get( bindingName );
			operation.setBinding( bindingName, bindingValue );
		}
	}

	private String getBindingName( String name ) {
		if ( ! name.startsWith( Consts.SPARQL_VAR_PREFIX ) ) return name;
		else return name.substring( 1, name.length() );
	}

	@FunctionalInterface
	public interface TupleQueryResultHandler<E> {
		public E handle( TupleQueryResult result ) throws Exception;
	}

	@FunctionalInterface
	public interface GraphQueryResultHandler<E> {
		public E handle( GraphQueryResult result ) throws Exception;
	}
}
