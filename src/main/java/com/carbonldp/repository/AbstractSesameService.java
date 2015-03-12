package com.carbonldp.repository;

import com.carbonldp.AbstractComponent;
import com.carbonldp.spring.TransactionWrapper;
import org.springframework.util.Assert;

public abstract class AbstractSesameService extends AbstractComponent {
	protected TransactionWrapper transactionWrapper;

	public AbstractSesameService( TransactionWrapper transactionWrapper ) {
		Assert.notNull( transactionWrapper );
		this.transactionWrapper = transactionWrapper;
	}
}
