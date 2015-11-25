package com.carbonldp.test.authorization;

import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.authorization.acl.*;
import com.carbonldp.test.AbstractIT;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class SesameACLServiceIT extends AbstractIT {

	ValueFactory valueFactory;

	URI role1;
	URI role2;

	ACE ace1;
	ACE ace2;
	ACE ace3;
	ACE ace4;
	ACE ace5;

	Set<ACE> aces1;
	Set<ACE> aces2;
	Set<ACE> directAcesTrue;
	Set<ACE> inheritableAcesTrue;
	Set<ACE> directAcesFalse;
	Set<ACE> inheritableAcesFalse;

	URI aclUri;
	URI accessToURI;

	ACL acl;

	Map<Boolean, Map<Boolean, Set<ACE>>> permissions;
	Map<Boolean, Set<ACE>> directPermissions;
	Map<Boolean, Set<ACE>> inheritablePermissions;

	@BeforeMethod
	protected void setUp() {
		valueFactory = new ValueFactoryImpl();
		role1 = valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/roles/blog-admin/" );
		role2 = valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/roles/app-admin/" );

		Set<ACEDescription.Permission> permissions1 = new LinkedHashSet<>();
		permissions1.add( ACEDescription.Permission.READ );
		permissions1.add( ACEDescription.Permission.ADD_MEMBER );
		permissions1.add( ACEDescription.Permission.DELETE );

		ace1 = createAce( valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-1" ), role1, true, permissions1 );
		ace2 = createAce( valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-2" ), role2, true, permissions1 );

		Set<ACEDescription.Permission> permissions2 = new LinkedHashSet<>();
		permissions2.add( ACEDescription.Permission.READ );
		permissions2.add( ACEDescription.Permission.ADD_MEMBER );

		ace3 = createAce( valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-3" ), role2, true, permissions2 );

		ace4 = createAce( valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-4" ), role2, false, permissions2 );

		ace5 = createAce( valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-5" ), role1, false, permissions1 );

		aces1 = new LinkedHashSet<>();
		aces1.add( ace1 );
		aces1.add( ace2 );

		aces2 = new LinkedHashSet<>();
		aces2.add( ace2 );
		aces2.add( ace3 );

		directAcesTrue = new LinkedHashSet<>();
		directAcesTrue.add( ace1 );
		directAcesTrue.add( ace2 );
		directAcesTrue.add( ace3 );

		inheritableAcesTrue = new LinkedHashSet<>();
		inheritableAcesTrue.add( ace3 );

		directAcesFalse = new LinkedHashSet<>();
		directAcesFalse.add( ace4 );

		inheritableAcesFalse = new LinkedHashSet<>();
		inheritableAcesFalse.add( ace5 );

		aclUri = valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/" );
		accessToURI = valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/" );

		acl = ACLFactory.create( aclUri, accessToURI );

		createMapPermissions();

	}

	private ACE createAce( Resource aclResource, URI role, boolean granting, Set<ACEDescription.Permission> permissions ) {
		ACE ace = new ACE( new LinkedHashModel(), aclResource );
		ace.addType( ACEDescription.Resource.CLASS.getURI() );
		ace.setSubjectClass( AppRoleDescription.Resource.CLASS.getURI() );
		ace.addSubject( role );
		ace.setGranting( granting );
		permissions.forEach( ace::addPermission );
		return ace;
	}

	private void createMapPermissions() {
		permissions = new LinkedHashMap<>();
		directPermissions = new LinkedHashMap<>();
		inheritablePermissions = new LinkedHashMap<>();

		directPermissions.put( true, directAcesTrue );
		directPermissions.put( false, directAcesFalse );

		inheritablePermissions.put( true, inheritableAcesTrue );
		inheritablePermissions.put( false, inheritableAcesFalse );

		permissions.put( true, directPermissions );
		permissions.put( false, inheritablePermissions );
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

	@Test( dependsOnMethods = {"hasSamePermissionsTrueTest", "hasSamePermissionsFalseTest"} )
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

	@Test( dependsOnMethods = {"hasSamePermissionsTrueTest", "hasSamePermissionsFalseTest"} )
	public void fuseQuadrantTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "fuseQuadrant", Set.class );
			privateMethod.setAccessible( true );
			Set<ACE> fusedQuadrant = (Set<ACE>) privateMethod.invoke( aclService, directAcesTrue );
			Iterator<ACE> iterator = fusedQuadrant.iterator();
			ACE fusedAce1 = iterator.next();
			ACE fusedAce2 = iterator.next();
			Set<URI> subjects1 = fusedAce1.getSubjects();
			Set<URI> subjects2 = fusedAce2.getSubjects();
			boolean containsRole1;
			boolean containsRole2;
			boolean containsRole;
			if ( fusedAce1.getSubjects().size() == 2 ) {
				containsRole1 = subjects1.contains( role1 );
				containsRole2 = subjects1.contains( role2 );
				containsRole = subjects2.contains( role2 );
			} else {
				containsRole = subjects1.contains( role2 );
				containsRole1 = subjects2.contains( role1 );
				containsRole2 = subjects2.contains( role2 );
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

	@Test
	public void addPermissionsTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "addPermissions", Map.class, ACL.class );
			privateMethod.setAccessible( true );
			ACL newAcl = (ACL) privateMethod.invoke( aclService, permissions, acl );
			Assert.assertEquals( newAcl.getAccessTo(), acl.getAccessTo() );
			Set<ACE> directACEs = ACEFactory.getInstance().get( newAcl.getBaseModel(), newAcl.getACEntries() );
			Set<ACE> inheritableACEs = ACEFactory.getInstance().get( newAcl.getBaseModel(), newAcl.getInheritableEntries() );
			Assert.assertEquals( directACEs.size(), 3 );
			Assert.assertEquals( inheritableACEs.size(), 2 );
			Assert.assertTrue( directACEs.contains( ace3 ) );
			Assert.assertTrue( directACEs.contains( ace4 ) );
			Assert.assertTrue( inheritableACEs.contains( ace3 ) );
			Assert.assertTrue( inheritableACEs.contains( ace5 ) );
			directACEs.remove( ace3 );
			directACEs.remove( ace4 );
			Assert.assertEquals( directACEs.iterator().next().getSubjects().size(), 2 );
			Assert.assertTrue( directACEs.iterator().next().getSubjects().contains( role1 ) );
			Assert.assertTrue( directACEs.iterator().next().getSubjects().contains( role2 ) );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void compareAcesTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "compareAces", Set.class, Set.class );
			privateMethod.setAccessible( true );
			Set<ACE> aces = (Set<ACE>) privateMethod.invoke( aclService, aces1, aces2 );
			Assert.assertFalse( aces.isEmpty());
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}
}
