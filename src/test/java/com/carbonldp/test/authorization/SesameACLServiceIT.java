package com.carbonldp.test.authorization;

import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.authorization.acl.ACE;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.SesameACLService;
import com.carbonldp.test.AbstractIT;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class SesameACLServiceIT extends AbstractIT {

	ValueFactory valueFactory;

	String role1 = "https://local.carbonldp.com/apps/test-blog/roles/blog-admin/";
	String role2 = "https://local.carbonldp.com/apps/test-blog/roles/app-admin/";

	ACE ace1;
	ACE ace2;
	ACE ace3;
	Set<ACE> aces1;
	Set<ACE> aces2;
	Set<ACE> aces3;

	@BeforeClass
	protected void setUp() {
		valueFactory = new ValueFactoryImpl();
		ace1 = new ACE( new LinkedHashModel(), valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-1" ) );
		ace1.addType( ACEDescription.Resource.CLASS.getURI() );
		ace1.setSubjectClass( AppRoleDescription.Resource.CLASS.getURI() );
		ace1.addSubject( valueFactory.createURI( role1 ) );
		ace1.setGranting( true );
		ace1.addPermission( ACEDescription.Permission.READ );
		ace1.addPermission( ACEDescription.Permission.ADD_MEMBER );
		ace1.addPermission( ACEDescription.Permission.DELETE );

		ace2 = new ACE( new LinkedHashModel(), valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-2" ) );
		ace2.addType( ACEDescription.Resource.CLASS.getURI() );
		ace2.setSubjectClass( AppRoleDescription.Resource.CLASS.getURI() );
		ace2.addSubject( valueFactory.createURI( role2 ) );
		ace2.setGranting( true );
		ace2.addPermission( ACEDescription.Permission.READ );
		ace2.addPermission( ACEDescription.Permission.ADD_MEMBER );
		ace2.addPermission( ACEDescription.Permission.DELETE );

		ace3 = new ACE( new LinkedHashModel(), valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-3" ) );
		ace3.addType( ACEDescription.Resource.CLASS.getURI() );
		ace3.setSubjectClass( AppRoleDescription.Resource.CLASS.getURI() );
		ace3.addSubject( valueFactory.createURI( role2 ) );
		ace3.setGranting( true );
		ace3.addPermission( ACEDescription.Permission.READ );
		ace3.addPermission( ACEDescription.Permission.ADD_MEMBER );

		aces1 = new LinkedHashSet<>();
		aces1.add( ace1 );
		aces1.add( ace2 );

		aces2 = new LinkedHashSet<>();
		aces2.add( ace2 );
		aces2.add( ace3 );

		aces3 = new LinkedHashSet<>();
		aces3.add( ace1 );
		aces3.add( ace2 );
		aces3.add( ace3 );
	}

	@Test
	public void hasSamePermissionsTrueTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "hasSamePermissions", ACE.class, ACE.class );
			privateMethod.setAccessible( true );
			Boolean equals = (Boolean) privateMethod.invoke( aclService, ace1, ace2 );
			Assert.assertTrue( equals );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void hasSamePermissionsFalseTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "hasSamePermissions", ACE.class, ACE.class );
			privateMethod.setAccessible( true );
			Boolean equals = (Boolean) privateMethod.invoke( aclService, ace1, ace3 );
			Assert.assertFalse( equals );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void getRepeatedAcesTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "getRepeatedAces", Set.class, Set.class );
			privateMethod.setAccessible( true );
			Set<ACE> repeatedAces = (Set<ACE>) privateMethod.invoke( aclService, aces1, aces2 );
			Assert.assertTrue( aces1.size() == 1 && aces1.contains( ace1 ) );
			Assert.assertTrue( aces2.size() == 1 && aces2.contains( ace3 ) );
			Assert.assertTrue( repeatedAces.size() == 1 && repeatedAces.contains( ace2 ) );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test( priority = 1 )
	public void fuseQuadrantTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "fuseQuadrant", Set.class );
			privateMethod.setAccessible( true );
			Set<ACE> fusedQuadrant = (Set<ACE>) privateMethod.invoke( aclService, aces3 );
			Iterator<ACE> iterator = fusedQuadrant.iterator();
			ACE fusedAce1 = iterator.next();
			ACE fusedAce2 = iterator.next();
			Set<URI> subjects1 = fusedAce1.getSubjects();
			Set<URI> subjects2 = fusedAce2.getSubjects();
			boolean containsRole1;
			boolean containsRole2;
			boolean containsRole;
			if ( fusedAce1.getSubjects().size() == 2 ) {
				containsRole1 = subjects1.contains( valueFactory.createURI( role1 ) );
				containsRole2 = subjects1.contains( valueFactory.createURI( role2 ) );
				containsRole = subjects2.contains( valueFactory.createURI( role2 ) );
			} else {
				containsRole = subjects1.contains( valueFactory.createURI( role2 ) );
				containsRole1 = subjects2.contains( valueFactory.createURI( role1 ) );
				containsRole2 = subjects2.contains( valueFactory.createURI( role2 ) );
			}
			Assert.assertTrue( containsRole1 && containsRole2 && containsRole );
			Assert.assertTrue( fusedQuadrant.size() == 2 );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}
}
