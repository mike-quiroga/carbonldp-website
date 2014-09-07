package com.base22.carbon.repository.services;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.base22.carbon.repository.RepositoryServiceException;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.layout2.hash.FmtLayout2HashOracle;
import com.hp.hpl.jena.sdb.sql.JDBC;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;

public class OracleSDBRepositoryService implements RepositoryService {

	protected SDBConnection connection = null;

	protected String jdbcBaseURL;
	protected String oracleUser;
	protected String oraclePassword;

	protected Map<String, Store> connectionStores;
	protected Map<Store, Map<String, Dataset>> storeDatasets;
	protected Map<Store, Map<String, Model>> storeModels;

	static final LayoutType LAYOUT_TYPE = LayoutType.LayoutTripleNodesHash;
	static final DatabaseType DATABASE_TYPE = DatabaseType.Oracle;

	static final Logger LOG = LoggerFactory.getLogger(OracleSDBRepositoryService.class);
	static final Marker FATAL = MarkerFactory.getMarker("FATAL");

	protected Map<String, Dataset> datasetRegistry;

	private StoreDesc getStoreDesc() {
		StoreDesc storeDesc = new StoreDesc(LAYOUT_TYPE, DATABASE_TYPE);
		return storeDesc;
	}

	private SDBConnection getConnection(String datasetName) throws RepositoryServiceException {
		if ( connection != null ) {
			LOG.debug("A connection already existed, is it open?");
			try {
				if ( ! connection.getSqlConnection().isClosed() ) {
					LOG.debug("It is open, returning it");
					return connection;
				}
			} catch (SQLException exception) {
				LOG.debug("The connection throwed an exeption, creating a new one...");
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
			}
		} else {
			LOG.debug("A connection didn't exist, creating one...");
		}

		String jdbcURL = jdbcBaseURL.concat(datasetName);
		try {
			JDBC.loadDriverOracle();
			connection = new SDBConnection(jdbcURL, oracleUser, oraclePassword);
		} catch (Exception exception) {
			LOG.error(FATAL, "The connection couldn't be stablished. JDBC URL: {}, User: {}, Password: ********.", jdbcURL, oracleUser);
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("The connection couldn't be stablished.");
		}

		return connection;
	}

	private Store getStore(String datasetName) throws RepositoryServiceException {
		Store store = null;

		SDBConnection connection = getConnection(datasetName);

		if ( connectionStores == null ) {
			LOG.debug("The stores registry wasn't initialized, doing it now.");
			connectionStores = new HashMap<String, Store>();
		} else {
			if ( connectionStores.containsKey(datasetName) ) {
				return connectionStores.get(datasetName);
			}
			LOG.debug("The store wasn't in the registry, creating it.");
		}
		try {
			StoreDesc storeDesc = getStoreDesc();
			store = SDBFactory.connectStore(connection, storeDesc);
			connectionStores.put(datasetName, store);
		} catch (Exception exception) {
			LOG.error("The RDF store couldn't be retrieved. Dataset: {}", datasetName);
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("The RDF store couldn't be retrieved.");
		}

		return store;
	}

	@Override
	public void release() throws RepositoryServiceException {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug(">> release() > Closing the possible connection...");
		}
		try {
			if ( connection == null ) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("<< release() > There wasn't any connection opened.");
				}
				return;
			}
			if ( connection.getSqlConnection().isClosed() ) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("<< release() > The connection was already closed.");
				}
				return;
			}

			if ( connectionStores != null ) {
				Iterator<Entry<String, Store>> storesIterator = connectionStores.entrySet().iterator();
				while (storesIterator.hasNext()) {
					Store store = storesIterator.next().getValue();

					if ( storeModels != null ) {
						if ( storeModels.containsKey(store) ) {
							Map<String, Model> modelsMap = storeModels.get(store);
							if ( LOG.isDebugEnabled() ) {
								LOG.debug("<< release() > There are {} models to close.", modelsMap.size());
							}
							Iterator<Entry<String, Model>> modelsIterator = modelsMap.entrySet().iterator();
							while (modelsIterator.hasNext()) {
								Model datasetModel = (Model) ((Map.Entry<String, Model>) modelsIterator.next()).getValue();
								if ( ! datasetModel.isClosed() ) {
									datasetModel.close();
								}
							}
							storeModels.remove(store);
						}
					}

					if ( storeDatasets != null ) {
						if ( storeDatasets.containsKey(store) ) {
							Map<String, Dataset> datasetsMap = storeDatasets.get(store);
							if ( LOG.isDebugEnabled() ) {
								LOG.debug("<< release() > There are {} datasets to close.", datasetsMap.size());
							}
							Iterator<Entry<String, Dataset>> datasetsIterator = datasetsMap.entrySet().iterator();
							while (datasetsIterator.hasNext()) {
								Dataset dataset = datasetsIterator.next().getValue();
								dataset.close();
							}
							storeDatasets.remove(store);
						}
					}

				}
				connectionStores.clear();
			}
			connection.getSqlConnection().commit();
			connection.close();
			connection = null;
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< release() > The connection and it's datasets were closed.");
			}
		} catch (SQLException exception) {
			String message = "Connection could not be closed.";
			LOG.error(FATAL, message);
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException(message);
		}
	}

	@Override
	public Model getNamedModel(String name, String dataset) throws RepositoryServiceException {
		Model namedModel = null;
		Map<String, Model> namedModelsMap = null;

		Store store = this.getStore(dataset);

		if ( storeModels == null ) {
			storeModels = new HashMap<Store, Map<String, Model>>();
		}
		if ( storeModels.containsKey(store) ) {
			namedModelsMap = storeModels.get(store);
			if ( namedModelsMap.containsKey(name) ) {
				namedModel = namedModelsMap.get(name);
				if ( namedModel.isClosed() ) {
					namedModelsMap.remove(name);
				} else {
					return namedModel;
				}
			}

		} else {
			namedModelsMap = new HashMap<String, Model>();
			storeModels.put(store, namedModelsMap);
		}
		try {
			namedModel = SDBFactory.connectNamedModel(store, name);
			namedModelsMap.put(name, namedModel);
		} catch (Exception exception) {
			LOG.error("The named model couldn't be retrieved");
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("The named model couldn't be retrieved.");
		}

		return namedModel;
	}

	@Override
	public Dataset getDataset(String datasetName) throws RepositoryServiceException {
		Dataset dataset = null;
		Map<String, Dataset> datasetsMap = null;

		Store store = this.getStore(datasetName);

		if ( storeDatasets == null ) {
			storeDatasets = new HashMap<Store, Map<String, Dataset>>();
		}
		if ( storeDatasets.containsKey(store) ) {
			datasetsMap = storeDatasets.get(store);
			if ( datasetsMap.containsKey(dataset) ) {
				dataset = datasetsMap.get(datasetName);
				return dataset;
			}

		} else {
			datasetsMap = new HashMap<String, Dataset>();
			storeDatasets.put(store, datasetsMap);
		}
		try {
			dataset = SDBFactory.connectDataset(store);
			datasetsMap.put(datasetName, dataset);
		} catch (Exception exception) {
			LOG.error("The dataset couldn't be retrieved");
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("The dataset couldn't be retrieved.");
		}

		return dataset;
		// new StoreTriplesNodesHashOracle(connection, storeDesc).getTableFormatter().format();

	}

	@Override
	public List<String> getDatasets() throws RepositoryServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void formatDatabase(String datasetName) throws RepositoryServiceException {
		FmtLayout2HashOracle layout = new FmtLayout2HashOracle(this.getConnection(datasetName));
		try {
			layout.format();
		} catch (Exception exception) {
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("Error creating the tables in the database.");
		}
		try {
			layout.addIndexes();
		} catch (Exception exception) {
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("Error creating the indexes in the database.");
		}
	}

	public void setJdbcBaseURL(String jdbcBaseURL) {
		this.jdbcBaseURL = jdbcBaseURL;
	}

	public void setOracleUser(String oracleUser) {
		this.oracleUser = oracleUser;
	}

	public void setOraclePassword(String oraclePassword) {
		this.oraclePassword = oraclePassword;
	}

	@Override
	public boolean datasetExists(String dataset) throws RepositoryServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createDataset(String datasetName) throws RepositoryServiceException {
		// TODO Auto-generated method stub

	}

}
