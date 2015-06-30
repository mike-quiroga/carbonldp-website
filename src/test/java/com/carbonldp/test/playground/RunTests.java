package com.carbonldp.test.playground;

import com.carbonldp.test.AbstractIT;
import com.carbonldp.test.agents.AgentUT;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

public class RunTests extends AbstractIT {

	public static void main( String args[] ) {
		TestListenerAdapter tla = new TestListenerAdapter();
		TestNG testng = new TestNG();
		testng.setGroups( "unit-test" );
		testng.setTestClasses( new Class[]{AgentUT.class} );
		testng.setSourcePath( "com.carbonldp.test.resources.testng.xml" );
		testng.addListener( tla );
		testng.run();

	}

}
