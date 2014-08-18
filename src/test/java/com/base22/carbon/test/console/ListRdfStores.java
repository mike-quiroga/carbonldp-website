package com.base22.carbon.test.console;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ListRdfStores {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ListRdfStores console = new ListRdfStores();
		console.execute();

	}
	
	public void execute() { 

		    String url = "jdbc:db2://localhost:50000/CARBON";
		    String user = "db2admin";
		    String password = "db2admin";

		    Connection con;
		    Statement stmt;
		    ResultSet rs;
		    
		    try 
		    {                                                                        
		      // Load the driver
		      Class.forName("com.ibm.db2.jcc.DB2Driver");
		      //System.out.println("**** Loaded the JDBC driver");

		      // Create the connection using the IBM Data Server Driver for JDBC and SQLJ
		      con = DriverManager.getConnection (url, user, password);
		      
		      // Commit changes manually
		      con.setAutoCommit(false);
		      //System.out.println("**** Created a JDBC connection to the data source");

		      // Create the Statement
		      stmt = con.createStatement();
		      //System.out.println("**** Created JDBC Statement object");

		      // Execute a query and generate a ResultSet instance
		      rs = stmt.executeQuery("SELECT storeName, schemaName FROM SYSTOOLS.RDFSTORES");
		      //System.out.println("**** Created JDBC ResultSet object");

		      // Print all of the employee numbers to standard output device
		      while (rs.next()) {
		    	System.out.println("RDF store:");
		        String storeName = rs.getString("storeName");
		        String schemaName = rs.getString("schemaName");
		        System.out.println("\t storeName: " + storeName);
		        System.out.println("\t schemaName: " + schemaName);
		      }
		      
		      //System.out.println("**** Fetched all rows from JDBC ResultSet");
		      // Close the ResultSet
		      rs.close();
		      //System.out.println("**** Closed JDBC ResultSet");
		      
		      // Close the Statement
		      stmt.close();
		      //System.out.println("**** Closed JDBC Statement");

		      // Connection must be on a unit-of-work boundary to allow close
		      con.commit();
		      //System.out.println ( "**** Transaction committed" );
		      
		      // Close the connection
		      con.close();
		      //System.out.println("**** Disconnected from data source");

		      //System.out.println("**** JDBC Exit from class ListRdfStores - no errors");

		    }
		    
		    catch (ClassNotFoundException e)
		    {
		      System.err.println("Could not load JDBC driver");
		      System.out.println("Exception: " + e);
		      e.printStackTrace();
		    }

		    catch(SQLException ex)
		    {
		      System.err.println("SQLException information");
		      while(ex!=null) {
		        System.err.println ("Error msg: " + ex.getMessage());
		        System.err.println ("SQLSTATE: " + ex.getSQLState());
		        System.err.println ("Error code: " + ex.getErrorCode());
		        ex.printStackTrace();
		        ex = ex.getNextException(); // For drivers that support chained exceptions
		      }
		    }

	}

}





