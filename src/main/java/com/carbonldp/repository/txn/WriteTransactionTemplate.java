package com.carbonldp.repository.txn;

public interface WriteTransactionTemplate {
	public void addCallback(WriteTransactionCallback callback);

	public void execute();

	public void execute(WriteTransactionCallback callback);
}
