package com.carbonldp.test;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.repository.ConnectionRWTemplate;
import com.carbonldp.repository.ConnectionRWTemplate.ReadCallback;
import com.carbonldp.repository.ConnectionRWTemplate.WriteCallback;

@Transactional
public class TransactionActionTemplate {
	protected final SesameConnectionFactory connectionFactory;
	protected final ConnectionRWTemplate connectionTemplate;

	public TransactionActionTemplate(SesameConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
		this.connectionTemplate = new ConnectionRWTemplate(connectionFactory);
	}

	public <E> E readInTransaction(ReadCallback<E> callback) {
		return connectionTemplate.read(callback);
	}

	public void writeInTransaction(WriteCallback callback) {
		connectionTemplate.write(callback);
	}

	public void runInTransaction(ActionWithConnection action) {
		action.run(connectionFactory.getConnection());
	}

	public interface ActionWithConnection {
		public void run(RepositoryConnection repositoryConnection);
	}
}
