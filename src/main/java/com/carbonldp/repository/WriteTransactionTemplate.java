package com.carbonldp.repository;

public interface WriteTransactionTemplate {
	public void addCallback(WriteTransactionCallback callback);

	public void execute();

	public void execute(WriteTransactionCallback callback);
}
