package com.base22.carbon.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.StoreManager;
import com.ibm.rdf.store.jena.RdfStoreFactory;

public class DB2RepositoryService implements RepositoryService {

	protected String dbUrl;
	protected String dbUsername;
	protected String dbPassword;
	protected String schema;

	static final Logger LOG = LoggerFactory.getLogger(DB2RepositoryService.class);
	static final Marker FATAL = MarkerFactory.getMarker("FATAL");

	/*
	 * The connection and the retrieved datasets need to stay open through all the request so the information can be
	 * manipulated (formatted, etc.)
	 */

	// The connection will be held through all the request
	protected Connection connection = null;
	// The accessed stores will be saved to reuse them in the request
	protected Map<String, Store> connectionStores;
	// The datasetModels opened will also be saved to reuse them as much as possible, and close them at the end of the
	// request
	protected Map<Store, Map<String, Dataset>> storeDatasets;
	// The models opened will also be saved to reuse them as much as possible, and close them at the end of the request
	protected Map<Store, Map<String, Model>> storeModels;

	/**
	 * Get the connection. If it doesn't exist or it is closed, create a new one and save it.
	 * 
	 * @return an open connection
	 * @throws RepositoryServiceException
	 */
	private Connection getConnection() throws RepositoryServiceException {
		if ( connection != null ) {
			LOG.debug("A connection already existed, is it open?");
			try {
				if ( ! connection.isClosed() ) {
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
		try {
			Class.forName("com.ibm.db2.jcc.DB2Driver");
			// connection = DriverManager.getConnection(defaultDbUrl,
			// defaultDbUserName, defaultDbUserPassword);
			Properties properties = new Properties();
			properties.setProperty("user", dbUsername);
			properties.setProperty("password", dbPassword);
			/*
			 * Property that solves the Closed connection exception in DELETE related methods
			 * https://www.ibm.com/developerworks/community/forums /html/topic?id=77777777-0000-0000-0000-000014867894
			 */
			properties.setProperty("enableExtendedIndicators", "2");
			connection = DriverManager.getConnection(dbUrl, properties);
			connection.setAutoCommit(false);
		} catch (Exception exception) {
			LOG.error(FATAL, "The connection couldn't be stablished. User: {}, Password: ********, Host: {}.", dbUsername, dbUrl);
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("The connection couldn't be stablished.");
		}

		return connection;
	}

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
			namedModel = RdfStoreFactory.connectNamedModel(store, connection, name);
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

	/**
	 * DB2 specific.
	 * 
	 * @param schema
	 * @param datasetName
	 * @return a Store
	 */
	private Store getStore(String datasetName) throws RepositoryServiceException {
		Store store = null;
		String storeName = "Schema: " + schema.toLowerCase() + " Dataset: " + datasetName.toLowerCase();

		Connection connection = getConnection();

		if ( connectionStores == null ) {
			LOG.debug("The stores registry wasn't initialized, doing it now.");
			connectionStores = new HashMap<String, Store>();
		} else {
			if ( connectionStores.containsKey(storeName) ) {
				return connectionStores.get(storeName);
			}
			LOG.debug("The store wasn't in the registry, creating it.");
		}
		try {
			store = StoreManager.connectStore(connection, schema, datasetName);
			connectionStores.put(storeName, store);
		} catch (Exception exception) {
			LOG.error("The RDF store couldn't be retrieved. Schema: {}, Dataset: {}", schema, datasetName);
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("DB2: The RDF store couldn't be retrieved.");
		}

		return store;
	}

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
			dataset = RdfStoreFactory.connectDataset(store, connection);
			datasetsMap.put(datasetName, dataset);
		} catch (Exception exception) {
			LOG.error("The dataset couldn't be retrieved");
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("The dataset couldn't be retrieved.");
		}

		return dataset;
	}

	public List<String> getDatasets() throws RepositoryServiceException {
		Connection connection = null;
		Statement statement = null;
		ResultSet results = null;

		List<String> rdfStoreList = new ArrayList<String>();

		connection = getConnection();

		try {
			statement = connection.createStatement();
			LOG.debug("Executing DB2 specific query to get the datasets...");
			results = statement.executeQuery("SELECT storeName, schemaName FROM SYSTOOLS.RDFSTORES");
			while (results.next()) {
				rdfStoreList.add(results.getString("storeName"));
			}
		} catch (SQLException exception) {
			LOG.error("The query couldn't be executed properly.");
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new RepositoryServiceException("There was an error with the database.");
		} finally {
			try {
				results.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			try {
				statement.close();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			release();
		}

		return rdfStoreList;

	}

	/**
	 * Used to close the connection and all the datasets retrieved from it. Needs to be called just before returning the
	 * response to the client.
	 */
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
			if ( connection.isClosed() ) {
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
			connection.commit();
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

	public boolean connectionIsOpen() throws SQLException {
		if ( connection == null )
			return false;
		return ! connection.isClosed();
	}

	public void setDbUrl(String url) {
		this.dbUrl = url;
	}

	public void setDbUsername(String username) {
		this.dbUsername = username;
	}

	public void setDbPassword(String password) {
		this.dbPassword = password;
	}

	public void setSchema(String schema) {
		this.schema = schema;
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
