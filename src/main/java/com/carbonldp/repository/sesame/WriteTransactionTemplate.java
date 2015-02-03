package com.carbonldp.repository.sesame;

public interface WriteTransactionTemplate {
	public void addCallback(WriteTransactionCallback callback);

	public void execute();

	public void execute(WriteTransactionCallback callback);
}
