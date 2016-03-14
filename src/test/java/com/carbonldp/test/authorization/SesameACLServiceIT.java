package com.carbonldp.test.authorization;

import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.authorization.acl.*;
import com.carbonldp.test.AbstractIT;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.springframework.aop.framework.Advised;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since 0.16.0-ALPHA
 */

public class SesameACLServiceIT extends AbstractIT {

	private ValueFactory valueFactory;

	private URI role1;
	private URI role2;

	private ACEValues aceRAD2T;
	private ACEValues aceRA2F;
	private URI aclUri;
	private URI accessToURI;
	private URI subjectClass = AppRoleDescription.Resource.CLASS.getURI();

	Set<ACEDescription.Permission> permissions2;
	Set<ACEDescription.Permission> permissions1;

	@BeforeMethod
	protected void setUp() {
		valueFactory = new ValueFactoryImpl();
		role1 = valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/roles/blog-admin/" );
		role2 = valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/roles/app-admin/" );

		permissions1 = new LinkedHashSet<>();
		permissions1.add( ACEDescription.Permission.READ );
		permissions1.add( ACEDescription.Permission.ADD_MEMBER );
		permissions1.add( ACEDescription.Permission.DELETE );

		permissions2 = new LinkedHashSet<>();
		permissions2.add( ACEDescription.Permission.READ );
		permissions2.add( ACEDescription.Permission.ADD_MEMBER );

		aceRA2F = new ACEValues( role2, false, permissions2 );
		aceRAD2T = new ACEValues( role2, true, permissions1 );

		aclUri = valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/~acl/" );
		accessToURI = valueFactory.createURI( "https://local.carbonldp.com/apps/test-blog/" );

	}

	private void addACEToACL( ACL acl, ACEValues aceValues, SesameACLService.InheritanceType type ) {
		ACE ace = ACEFactory.getInstance().create( acl, ACEDescription.SubjectType.APP_ROLE, aceValues.role, aceValues.permissions, aceValues.granting );
		if ( type == SesameACLService.InheritanceType.DIRECT ) {
			acl.addACEntry( ace.getSubject() );
		} else {
			acl.addInheritableEntry( ace.getSubject() );
		}
	}

	@Test
	public void addACESubjectsTest() {

		ACL acl = ACLFactory.create( aclUri, accessToURI );
		addACEToACL( acl, aceRA2F, SesameACLService.InheritanceType.DIRECT );
		addACEToACL( acl, aceRAD2T, SesameACLService.InheritanceType.INHERITABLE );
		Set<ACE> aces = ACEFactory.getInstance().get( acl.getBaseModel(), acl.getACEntries(), acl.getURI() );
		Set<ACE> inheritableAces = ACEFactory.getInstance().get( acl.getBaseModel(), acl.getInheritableEntries(), acl.getURI() );

		Map<SesameACLService.Subject, SesameACLService.SubjectPermissions> aclSubjects = new HashMap<>();

		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "addACEsSubjects", Set.class, SesameACLService.InheritanceType.class, Map.class );
			privateMethod.setAccessible( true );
			privateMethod.invoke( ((Advised)aclService).getTargetSource().getTarget(), aces, SesameACLService.InheritanceType.DIRECT, aclSubjects );
			privateMethod.invoke( ((Advised)aclService).getTargetSource().getTarget(), inheritableAces, SesameACLService.InheritanceType.INHERITABLE, aclSubjects );
			Assert.assertFalse( aclSubjects.isEmpty() );

		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't find the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "Exception inside the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't access the method", e );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		Assert.assertTrue( aclSubjects.get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.DENYING ).containsAll( permissions2 ) );
		Assert.assertTrue( aclSubjects.get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).containsAll( permissions2 ) );

	}

	@Test
	public void getACLSubjectsIT() {
		ACL acl = ACLFactory.create( aclUri, accessToURI );
		addACEToACL( acl, aceRA2F, SesameACLService.InheritanceType.DIRECT );
		addACEToACL( acl, aceRAD2T, SesameACLService.InheritanceType.INHERITABLE );

		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "getACLSubjects", ACL.class );
			privateMethod.setAccessible( true );
			Map<SesameACLService.Subject, SesameACLService.SubjectPermissions> aclSubjects = (Map<SesameACLService.Subject, SesameACLService.SubjectPermissions>) privateMethod.invoke( ((Advised)aclService).getTargetSource().getTarget(), acl );

			Assert.assertEquals( aclSubjects.size(), 1 );
			Assert.assertTrue( aclSubjects.get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.DENYING ).containsAll( permissions2 ) );
			Assert.assertTrue( aclSubjects.get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).containsAll( permissions2 ) );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't find the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "Exception inside the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't access the method", e );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

	}

	@Test
	public void getSubjectPermissionsToModify_sameSubjectTest() {

		SesameACLService.SubjectPermissions oldACLSubjectPermissions = new SesameACLService.SubjectPermissions();
		oldACLSubjectPermissions.get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.DENYING ).addAll( permissions1 );
		oldACLSubjectPermissions.get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).addAll( permissions2 );

		Map<SesameACLService.Subject, SesameACLService.SubjectPermissions> oldACLSubjects = new HashMap<>();
		oldACLSubjects.put( new SesameACLService.Subject( role2, subjectClass ), oldACLSubjectPermissions );

		SesameACLService.SubjectPermissions newACLSubjectPermissions = new SesameACLService.SubjectPermissions();
		newACLSubjectPermissions.get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.DENYING ).addAll( permissions2 );
		newACLSubjectPermissions.get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).addAll( permissions1 );

		Map<SesameACLService.Subject, SesameACLService.SubjectPermissions> newACLSubjects = new HashMap<>();
		newACLSubjects.put( new SesameACLService.Subject( role2, subjectClass ), newACLSubjectPermissions );

		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "getSubjectPermissionsToModify", Map.class, Map.class );
			privateMethod.setAccessible( true );
			Map<SesameACLService.ModifyType, Map<SesameACLService.Subject, SesameACLService.SubjectPermissions>> aclSubjects =
				(Map<SesameACLService.ModifyType, Map<SesameACLService.Subject, SesameACLService.SubjectPermissions>>) privateMethod.invoke( ((Advised)aclService).getTargetSource().getTarget(), oldACLSubjects, newACLSubjects );
			Assert.assertEquals( aclSubjects.size(), 2 );
			Assert.assertTrue( aclSubjects.get( SesameACLService.ModifyType.REMOVE ).get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.DENYING ).contains( ACEDescription.Permission.DELETE ) );
			Assert.assertTrue( aclSubjects.get( SesameACLService.ModifyType.ADD ).get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).contains( ACEDescription.Permission.DELETE ) );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't find the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "Exception inside the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't access the method", e );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	@Test
	public void getSubjectPermissionsToModify_differentSubjectTest() {

		SesameACLService.SubjectPermissions oldACLSubjectPermissions = new SesameACLService.SubjectPermissions();
		oldACLSubjectPermissions.get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.DENYING ).addAll( permissions1 );
		oldACLSubjectPermissions.get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).addAll( permissions2 );

		Map<SesameACLService.Subject, SesameACLService.SubjectPermissions> oldACLSubjects = new HashMap<>();
		oldACLSubjects.put( new SesameACLService.Subject( role2, subjectClass ), oldACLSubjectPermissions );

		SesameACLService.SubjectPermissions newACLSubjectPermissions = new SesameACLService.SubjectPermissions();
		newACLSubjectPermissions.get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).addAll( permissions1 );

		Map<SesameACLService.Subject, SesameACLService.SubjectPermissions> newACLSubjects = new HashMap<>();
		newACLSubjects.put( new SesameACLService.Subject( role1, subjectClass ), newACLSubjectPermissions );

		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "getSubjectPermissionsToModify", Map.class, Map.class );
			privateMethod.setAccessible( true );
			Map<SesameACLService.ModifyType, Map<SesameACLService.Subject, SesameACLService.SubjectPermissions>> aclSubjects =
				(Map<SesameACLService.ModifyType, Map<SesameACLService.Subject, SesameACLService.SubjectPermissions>>) privateMethod.invoke( ((Advised)aclService).getTargetSource().getTarget(), oldACLSubjects, newACLSubjects );
			Assert.assertEquals( aclSubjects.size(), 2 );
			Assert.assertTrue( aclSubjects.get( SesameACLService.ModifyType.REMOVE ).get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.DENYING ).containsAll( permissions1 ) );
			Assert.assertTrue( aclSubjects.get( SesameACLService.ModifyType.REMOVE ).get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).containsAll( permissions2 ) );
			Assert.assertTrue( aclSubjects.get( SesameACLService.ModifyType.ADD ).get( new SesameACLService.Subject( role1, subjectClass ) ).get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).containsAll( permissions1 ) );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't find the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "Exception inside the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't access the method", e );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	@Test
	public void getAffectedSubjectsTest() {

		Map<SesameACLService.ModifyType, Map<SesameACLService.Subject, SesameACLService.SubjectPermissions>> subjectPermissionsToModify = new HashMap<>();
		subjectPermissionsToModify.put( SesameACLService.ModifyType.ADD, new HashMap<>() );
		subjectPermissionsToModify.get( SesameACLService.ModifyType.ADD ).put( new SesameACLService.Subject( role2, subjectClass ), new SesameACLService.SubjectPermissions() );
		subjectPermissionsToModify.get( SesameACLService.ModifyType.ADD ).get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.GRANTING ).add( ACEDescription.Permission.DELETE );

		subjectPermissionsToModify.put( SesameACLService.ModifyType.REMOVE, new HashMap<>() );
		subjectPermissionsToModify.get( SesameACLService.ModifyType.REMOVE ).put( new SesameACLService.Subject( role1, subjectClass ), new SesameACLService.SubjectPermissions() );
		subjectPermissionsToModify.get( SesameACLService.ModifyType.REMOVE ).get( new SesameACLService.Subject( role1, subjectClass ) ).get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.DENYING ).add( ACEDescription.Permission.READ );

		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "getAffectedSubjects", Map.class );
			privateMethod.setAccessible( true );
			Set<SesameACLService.Subject> aclSubjects = (Set<SesameACLService.Subject>) privateMethod.invoke( ((Advised)aclService).getTargetSource().getTarget(), subjectPermissionsToModify );

			Assert.assertTrue( aclSubjects.contains( new SesameACLService.Subject( role1, subjectClass ) ) );
			Assert.assertTrue( aclSubjects.contains( new SesameACLService.Subject( role2, subjectClass ) ) );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't find the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "Exception inside the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't access the method", e );
		} catch ( Exception e ) {
			e.printStackTrace();
		}

	}

	@Test
	public void getAffectedPermissionsTest() {

		Map<SesameACLService.ModifyType, Map<SesameACLService.Subject, SesameACLService.SubjectPermissions>> subjectPermissionsToModify = new HashMap<>();
		subjectPermissionsToModify.put( SesameACLService.ModifyType.ADD, new HashMap<>() );
		subjectPermissionsToModify.get( SesameACLService.ModifyType.ADD ).put( new SesameACLService.Subject( role2, subjectClass ), new SesameACLService.SubjectPermissions() );
		subjectPermissionsToModify.get( SesameACLService.ModifyType.ADD ).get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.GRANTING ).add( ACEDescription.Permission.DELETE );

		subjectPermissionsToModify.put( SesameACLService.ModifyType.REMOVE, new HashMap<>() );
		subjectPermissionsToModify.get( SesameACLService.ModifyType.REMOVE ).put( new SesameACLService.Subject( role1, subjectClass ), new SesameACLService.SubjectPermissions() );
		subjectPermissionsToModify.get( SesameACLService.ModifyType.REMOVE ).get( new SesameACLService.Subject( role1, subjectClass ) ).get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.DENYING ).add( ACEDescription.Permission.READ );

		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "getAffectedPermissions", Map.class );
			privateMethod.setAccessible( true );
			Set<ACEDescription.Permission> aclPermissions = (Set<ACEDescription.Permission>) privateMethod.invoke( ((Advised)aclService).getTargetSource().getTarget(), subjectPermissionsToModify );

			Assert.assertTrue( aclPermissions.contains( ACEDescription.Permission.DELETE ) );
			Assert.assertTrue( aclPermissions.contains( ACEDescription.Permission.READ ) );

		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't find the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "Exception inside the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't access the method", e );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	@Test
	public void generateACLTest() {
		Map<SesameACLService.Subject, SesameACLService.SubjectPermissions> subjectPermissionsToModify = new HashMap<>();
		SesameACLService.SubjectPermissions role1Permissions = new SesameACLService.SubjectPermissions();
		role1Permissions.get( SesameACLService.InheritanceType.DIRECT ).put( SesameACLService.PermissionType.GRANTING, permissions1 );
		subjectPermissionsToModify.put( new SesameACLService.Subject( role1, subjectClass ), role1Permissions );

		try {
			Method privateMethod = SesameACLService.class.getDeclaredMethod( "generateACL", URI.class, URI.class, Map.class );
			privateMethod.setAccessible( true );
			ACL acl = (ACL) privateMethod.invoke( ((Advised)aclService).getTargetSource().getTarget(), aclUri, accessToURI, subjectPermissionsToModify );

			Assert.assertTrue( acl.getACEntries().size() == 1 );
			Assert.assertTrue( acl.getInheritableEntries().size() == 0 );
			ACE ace = ACEFactory.getInstance().get( acl.getBaseModel(), acl.getACEntries() ).iterator().next();
			Assert.assertEquals( ace.getPermissions(), permissions1 );
			Assert.assertTrue( ace.isGranting() );

		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't find the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "Exception inside the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't access the method", e );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	private class ACEValues {
		public URI role;
		public boolean granting;
		public Set<ACEDescription.Permission> permissions;

		public ACEValues( URI role, boolean granting, Set<ACEDescription.Permission> permissions ) {
			this.role = role;
			this.granting = granting;
			this.permissions = permissions;

		}
	}
}
