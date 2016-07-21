package com.carbonldp.repository;

import org.eclipse.rdf4j.model.Statement;

public interface StatementsHandler {
	public void start();

	public boolean handleStatement( Statement statement );

	public void end();
}
