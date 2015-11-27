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
	ACE ace6;
	ACE ace7;
	ACE ace8;
	ACE ace9;

	Set<ACE> aces1;
	Set<ACE> aces2;
	Set<ACE> directAcesTrue;
	Set<ACE> inheritableAcesTrue;
	Set<ACE> directAcesFalse;
	Set<ACE> inheritableAcesFalse;

	Set<ACE> newPermissions1;
	Set<ACE> newPermissions2;
	Set<ACE> newPermissions3;

	URI aclUri;
	URI accessToURI;

	ACL acl;

	Map<Boolean, Map<Boolean, Set<ACE>>> permissions1;
	Map<Boolean, Map<Boolean, Set<ACE>>> permissions2;
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

		ace6 = createAce( valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-6" ), role2, true, permissions2 );

		ace7 = createAce( valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-7" ), role2, false, permissions2 );

		ace8 = createAce( valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-8" ), role1, false, permissions1 );

		ace9 = createAce( valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/#ace-9" ), role1, true, permissions1 );

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

		newPermissions1 = new LinkedHashSet<>();
		newPermissions1.add( ace6 );
		newPermissions1.add( ace9 );

		newPermissions2 = new LinkedHashSet<>();
		newPermissions2.add( ace7 );

		newPermissions3 = new LinkedHashSet<>();
		newPermissions3.add( ace8 );

		aclUri = valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/" );
		accessToURI = valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/" );

		acl = ACLFactory.create( aclUri, accessToURI );

		createMapsPermissions();
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

	private void createMapsPermissions() {
		permissions1 = new LinkedHashMap<>();
		permissions2 = new LinkedHashMap<>();
		directPermissions = new LinkedHashMap<>();
		inheritablePermissions = new LinkedHashMap<>();

		directPermissions.put( true, directAcesTrue );
		directPermissions.put( false, directAcesFalse );

		inheritablePermissions.put( true, inheritableAcesTrue );
		inheritablePermissions.put( false, inheritableAcesFalse );

		permissions1.put( true, directPermissions );
		permissions1.put( false, inheritablePermissions );

		directPermissions = new LinkedHashMap<>();
		inheritablePermissions = new LinkedHashMap<>();

		directPermissions.put( true, newPermissions1 );
		directPermissions.put( false, newPermissions2 );

		inheritablePermissions.put( true, new LinkedHashSet<>() );
		inheritablePermissions.put( false, newPermissions3 );

		permissions2.put( true, directPermissions );
		permissions2.put( false, inheritablePermissions );
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
			ACL newAcl = (ACL) privateMethod.invoke( aclService, permissions1, acl );
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
	public void compareAcesToAdd() {
		compareAcesTest( inheritableAcesTrue, aces1 );
	}

	@Test
	public void compareAcesToRemove() {
		compareAcesTest( aces1, inheritableAcesTrue );
	}

	private void compareAcesTest( Set<ACE> set1, Set<ACE> set2 ) {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "compareAces", Set.class, Set.class );
			privateMethod.setAccessible( true );
			Set<ACE> aces = (Set<ACE>) privateMethod.invoke( aclService, set1, set2 );
			Assert.assertEquals( aces.size(), 2 );
			Iterator<ACE> iterator = aces.iterator();
			ACE differencesAce1 = iterator.next();
			ACE differencesAce2 = iterator.next();
			Set<ACEDescription.Permission> changedPermissions1 = differencesAce1.getPermissions();
			Set<ACEDescription.Permission> changedPermissions2 = differencesAce2.getPermissions();
			if ( changedPermissions1.size() == 3 ) {
				Assert.assertEquals( changedPermissions2.size(), 1 );
				Assert.assertTrue( changedPermissions1.contains( ACEDescription.Permission.READ ) );
				Assert.assertTrue( changedPermissions1.contains( ACEDescription.Permission.ADD_MEMBER ) );
				Assert.assertTrue( changedPermissions1.contains( ACEDescription.Permission.DELETE ) );
				Assert.assertTrue( changedPermissions2.contains( ACEDescription.Permission.DELETE ) );
				Assert.assertTrue( differencesAce1.getSubjects().iterator().next().equals( role1 ) );
				Assert.assertTrue( differencesAce2.getSubjects().iterator().next().equals( role2 ) );
			} else {
				Assert.assertEquals( changedPermissions2.size(), 3 );
				Assert.assertEquals( changedPermissions1.size(), 1 );
				Assert.assertTrue( changedPermissions2.contains( ACEDescription.Permission.READ ) );
				Assert.assertTrue( changedPermissions2.contains( ACEDescription.Permission.ADD_MEMBER ) );
				Assert.assertTrue( changedPermissions2.contains( ACEDescription.Permission.DELETE ) );
				Assert.assertTrue( changedPermissions1.contains( ACEDescription.Permission.DELETE ) );
				Assert.assertTrue( differencesAce2.getSubjects().iterator().next().equals( role1 ) );
				Assert.assertTrue( differencesAce1.getSubjects().iterator().next().equals( role2 ) );
			}

		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void comparePermissionsTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "comparePermissions", Map.class, Map.class );
			privateMethod.setAccessible( true );
			permissions1.get( true ).get( true ).remove( ace3 );
			Set<ACE> aces = (Set<ACE>) privateMethod.invoke( aclService, permissions1, permissions2 );
			Assert.assertEquals( aces.size(), 2 );
			Iterator<ACE> iterator = aces.iterator();
			ACE differencesAce1 = iterator.next();
			ACE differencesAce2 = iterator.next();
			Set<ACEDescription.Permission> changedPermissions1 = differencesAce1.getPermissions();
			Set<ACEDescription.Permission> changedPermissions2 = differencesAce2.getPermissions();
			if ( changedPermissions1.size() == 2 ) {
				Assert.assertEquals( changedPermissions2.size(), 1 );
				Assert.assertTrue( changedPermissions1.contains( ACEDescription.Permission.READ ) );
				Assert.assertTrue( changedPermissions1.contains( ACEDescription.Permission.ADD_MEMBER ) );
				Assert.assertTrue( changedPermissions2.contains( ACEDescription.Permission.DELETE ) );
				Assert.assertTrue( differencesAce1.getSubjects().iterator().next().equals( role2 ) );
				Assert.assertTrue( differencesAce2.getSubjects().iterator().next().equals( role2 ) );
			} else {
				Assert.assertEquals( changedPermissions2.size(), 2 );
				Assert.assertEquals( changedPermissions1.size(), 1 );
				Assert.assertTrue( changedPermissions2.contains( ACEDescription.Permission.READ ) );
				Assert.assertTrue( changedPermissions2.contains( ACEDescription.Permission.ADD_MEMBER ) );
				Assert.assertTrue( changedPermissions1.contains( ACEDescription.Permission.DELETE ) );
				Assert.assertTrue( differencesAce2.getSubjects().iterator().next().equals( role2 ) );
				Assert.assertTrue( differencesAce1.getSubjects().iterator().next().equals( role2 ) );
			}

		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void getACESubjectsIT() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "getACESubjects", Set.class );
			privateMethod.setAccessible( true );
			Map<URI, ACE> aceSubjects = (Map<URI, ACE>) privateMethod.invoke( aclService, aces1 );
			Assert.assertEquals( aceSubjects.size(), 2 );
			for ( URI subject : aceSubjects.keySet() ) {
				Assert.assertTrue( aceSubjects.get( subject ).getSubjects().iterator().next().equals( subject ) );
			}

		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void updateAcesAddPermissionTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "updateAces", Set.class, ACE.class, boolean.class );
			privateMethod.setAccessible( true );
			aces1.remove( ace2 );
			aces1.add( ace3 );
			Set<ACE> acesUpdated = (Set<ACE>) privateMethod.invoke( aclService, aces1, ace2, true );
			Assert.assertEquals( acesUpdated.size(), 2 );
			Iterator<ACE> iterator = acesUpdated.iterator();
			ACE aceUpdated1 = iterator.next();
			ACE aceUpdated2 = iterator.next();
			Set<ACEDescription.Permission> updatedPermissions1 = aceUpdated1.getPermissions();
			Set<ACEDescription.Permission> updatedPermissions2 = aceUpdated2.getPermissions();
			Assert.assertEquals( updatedPermissions1, updatedPermissions2 );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void updateAcesAddAceTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "updateAces", Set.class, ACE.class, boolean.class );
			privateMethod.setAccessible( true );
			aces1.remove( ace2 );
			Set<ACE> acesUpdated = (Set<ACE>) privateMethod.invoke( aclService, aces1, ace2, true );
			Assert.assertEquals( acesUpdated.size(), 2 );
			Iterator<ACE> iterator = acesUpdated.iterator();
			ACE aceUpdated1 = iterator.next();
			ACE aceUpdated2 = iterator.next();
			Set<ACEDescription.Permission> updatedPermissions1 = aceUpdated1.getPermissions();
			Set<ACEDescription.Permission> updatedPermissions2 = aceUpdated2.getPermissions();
			Assert.assertEquals( updatedPermissions1, updatedPermissions2 );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void updateAcesWrongGrantingTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "updateAces", Set.class, ACE.class, boolean.class );
			privateMethod.setAccessible( true );
			aces1.remove( ace2 );
			Set<ACE> acesUpdated = (Set<ACE>) privateMethod.invoke( aclService, aces1, ace2, false );
			Assert.assertEquals( aces1, acesUpdated );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void updateAcesNoNewPermissionTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "updateAces", Set.class, ACE.class, boolean.class );
			privateMethod.setAccessible( true );
			Set<ACE> acesUpdated = (Set<ACE>) privateMethod.invoke( aclService, aces1, ace3, false );
			Assert.assertEquals( aces1, acesUpdated );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void updateACEListTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "updateACEList", Set.class );
			privateMethod.setAccessible( true );
			Set<ACE> directAces = new LinkedHashSet<>();
			directAces.addAll( aces1 );
			directAces.add( ace4 );
			Map<Boolean, Set<ACE>> dividedAces = (Map<Boolean, Set<ACE>>) privateMethod.invoke( aclService, directAces );
			Set<ACE> grantingAces = dividedAces.get( true );
			Set<ACE> denyingAces = dividedAces.get( false );
			Assert.assertEquals( grantingAces.size(), 2 );
			Assert.assertEquals( denyingAces.size(), 1 );
			for ( ACE ace : grantingAces ) {
				Assert.assertTrue( ace.isGranting() );
			}
			for ( ACE ace : denyingAces ) {
				Assert.assertFalse( ace.isGranting() );
			}
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	@Test
	public void getPermissionsTest() {
		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "getPermissions", ACL.class );
			privateMethod.setAccessible( true );

			acl.addACEntry( ace2.getSubject() );
			acl.addACEntry( ace3.getSubject() );
			acl.addACEntry( ace4.getSubject() );
			acl.addInheritableEntry( ace1.getSubject() );
			acl.addInheritableEntry( ace5.getSubject() );

			acl.getBaseModel().addAll( ace1 );
			acl.getBaseModel().addAll( ace2 );
			acl.getBaseModel().addAll( ace3 );
			acl.getBaseModel().addAll( ace4 );
			acl.getBaseModel().addAll( ace5 );

			Map<Boolean, Map<Boolean, Set<ACE>>> aclPermissions = (Map<Boolean, Map<Boolean, Set<ACE>>>) privateMethod.invoke( aclService, acl );

			Assert.assertEquals( aclPermissions.get( true ).get( true ).size(), 1 );
			Assert.assertEquals( aclPermissions.get( true ).get( false ).size(), 1 );
			Assert.assertEquals( aclPermissions.get( false ).get( true ).size(), 1 );
			Assert.assertEquals( aclPermissions.get( false ).get( false ).size(), 1 );

			Assert.assertEquals( aclPermissions.get( true ).get( true ).iterator().next().getPermissions().size(), 3 );
			Assert.assertEquals( aclPermissions.get( true ).get( false ).iterator().next().getPermissions().size(), 2 );
			Assert.assertEquals( aclPermissions.get( false ).get( true ).iterator().next().getPermissions().size(), 3 );
			Assert.assertEquals( aclPermissions.get( false ).get( false ).iterator().next().getPermissions().size(), 3 );

		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}
}
