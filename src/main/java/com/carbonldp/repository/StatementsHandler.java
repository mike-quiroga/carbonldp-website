package com.carbonldp.repository;

import org.openrdf.model.Statement;

public interface StatementsHandler {
	public void start();

	public boolean handleStatement(Statement statement);

	public void end();
}
