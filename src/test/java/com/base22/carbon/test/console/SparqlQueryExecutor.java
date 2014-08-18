package com.base22.carbon.test.console;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.ibm.rdf.store.Store;
import com.ibm.rdf.store.StoreManager;
import com.ibm.rdf.store.jena.RdfStoreFactory;
import com.ibm.rdf.store.jena.RdfStoreQueryExecutionFactory;
import com.ibm.rdf.store.jena.RdfStoreQueryFactory;

public class SparqlQueryExecutor {
	
	public static void main(String[] args) throws SQLException
	{
		Connection conn = null;
		Store store = null;
		String storeName = "staffing";
		String schema = "db2admin";

		try {

			Class.forName("com.ibm.db2.jcc.DB2Driver");
			conn = DriverManager.getConnection(
					"jdbc:db2://localhost:50000/RDFSAMPL", "db2admin", "db2admin");
			conn.setAutoCommit(false);

		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		// Connect to the store
		store = StoreManager.connectStore(conn, schema, storeName);

		/*
		 * Generally keep the 'Store' object hanging around. Otherwise there is
		 * always an unnecessary query to know which set of tables we need to
		 * work with. - The Store object does not keep a reference to the
		 * connection passed to the StoreManager methods. Thats why in the API u
		 * need to pass a connection again in RDFStoreFactory's methods. - It is
		 * OK to use all other objects (Dataset/Graph/Model) as lightweight i.e.
		 * Create afresh for each request.
		 */
		
		printAllEmployeeFriends(store, conn);
	}
	
	private static void printAllEmployeeFriends(Store store, Connection conn) {
		
		
		String query = "select ?member ?friendname where { " +
					"<http://xyz.com/project/robotX> <http://xyz.com/project/member> ?memberId . " +
					"?memberId <http://xmlns.com/foaf/0.1/name> ?member . " +
					"?memberId <http://xmlns.com/foaf/0.1/knows> ?friendId . " +
					" ?friendId      <http://xmlns.com/foaf/0.1/name>  ?friendname " +
				"}";
		
		Dataset ds = RdfStoreFactory.connectDataset(store, conn);
		Query q = RdfStoreQueryFactory.create(query);
		QueryExecution qe = RdfStoreQueryExecutionFactory.create(q, ds);

		Model m = null;

		if (q.isSelectType()) {
			
			ResultSet rs = qe.execSelect();
			while (rs.hasNext()) {
			    QuerySolution qs = rs.next();
			    System.out.println(qs);
			    System.out.println();
			}
			
		} else if (q.isDescribeType()) {
			m = qe.execDescribe();
			m.write(System.out, "N-TRIPLE");
		} else if (q.isAskType()) {
			System.out.println(qe.execAsk());

		} else if (q.isConstructType()) {
			m = qe.execConstruct();
			m.write(System.out, "N-TRIPLE");
		}

		qe.close();
		if (m != null) {
			System.out.println("Number of Rows  : " + m.size());
			m.close();
		} 
	}

}
