package com.carbonldp.repository;

import com.carbonldp.repository.txn.ReadTransactionTemplate;
import com.carbonldp.repository.txn.WriteTransactionTemplate;

public interface RepositoryService {
	public void createRepository( String repositoryID );

	public boolean repositoryExists( String repositoryID );

	public <T> ReadTransactionTemplate<T> getReadTransactionTemplate( String repositoryID );

	public WriteTransactionTemplate getWriteTransactionTemplate( String repositoryID );

	//@PreAuthorize( "hasPermission(#repositoryURI, 'DELETE')" )
	public void deleteRepository( String repositoryID );
}
