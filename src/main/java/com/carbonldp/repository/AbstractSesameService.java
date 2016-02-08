package com.carbonldp.repository;

import com.carbonldp.AbstractComponent;
import com.carbonldp.spring.TransactionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class AbstractSesameService extends AbstractComponent {
	protected TransactionWrapper transactionWrapper;

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) { this.transactionWrapper = transactionWrapper; }
}
