package com.carbonldp.test.authorization;

import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.authorization.acl.*;
import com.carbonldp.test.AbstractIT;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
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

	private ACEValues aceRAD1T;
	private ACEValues aceRAD2T;
	private ACEValues aceRA2T;
	private ACEValues aceRA2F;
	private ACEValues aceRAD2F;
	private ACEValues aceRAD1F;
	private ACEValues aceRA1F;

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

		aceRA2T = new ACEValues( role2, true, permissions2 );
		aceRA2F = new ACEValues( role2, false, permissions2 );
		aceRAD2F = new ACEValues( role2, false, permissions1 );
		aceRAD2T = new ACEValues( role2, true, permissions1 );
		aceRAD1F = new ACEValues( role1, false, permissions1 );
		aceRA1F = new ACEValues( role1, false, permissions2 );
		aceRAD1T = new ACEValues( role1, true, permissions1 );

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

	private boolean hasSameParmissions( ACE ace1, ACE ace2 ) {
		Set<ACEDescription.Permission> ace1Permissions = ace1.getPermissions();
		Set<ACEDescription.Permission> ace2Permissions = ace2.getPermissions();
		if ( ace1Permissions.size() != ace2Permissions.size() ) return false;
		for ( ACEDescription.Permission permission : ace2Permissions )
			if ( ! ace1Permissions.contains( permission ) ) return false;
		return true;
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
			privateMethod.invoke( aclService, aces, SesameACLService.InheritanceType.DIRECT, aclSubjects );
			privateMethod.invoke( aclService, inheritableAces, SesameACLService.InheritanceType.INHERITABLE, aclSubjects );
			Assert.assertFalse( aclSubjects.isEmpty() );

		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't find the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't access the method", e );
		}

		SesameACLService sesameACLService = (SesameACLService) aclService;
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
			Map<SesameACLService.Subject, SesameACLService.SubjectPermissions> aclSubjects = (Map<SesameACLService.Subject, SesameACLService.SubjectPermissions>) privateMethod.invoke( aclService, acl );

			SesameACLService sesameACLService = (SesameACLService) aclService;
			Assert.assertEquals( aclSubjects.size(), 1 );
			Assert.assertTrue( aclSubjects.get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.DENYING ).containsAll( permissions2 ) );
			Assert.assertTrue( aclSubjects.get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).containsAll( permissions2 ) );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}

	}

	@Test
	public void getSubjectPermissionsToModifyTest() {
		SesameACLService sesameACLService = (SesameACLService) aclService;

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
				(Map<SesameACLService.ModifyType, Map<SesameACLService.Subject, SesameACLService.SubjectPermissions>>) privateMethod.invoke( aclService, oldACLSubjects, newACLSubjects );
			Assert.assertEquals( aclSubjects.size(), 2 );
			Assert.assertTrue( aclSubjects.get( SesameACLService.ModifyType.REMOVE ).get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.DIRECT ).get( SesameACLService.PermissionType.DENYING ).contains( ACEDescription.Permission.DELETE ) );
			Assert.assertTrue( aclSubjects.get( SesameACLService.ModifyType.ADD ).get( new SesameACLService.Subject( role2, subjectClass ) ).get( SesameACLService.InheritanceType.INHERITABLE ).get( SesameACLService.PermissionType.GRANTING ).contains( ACEDescription.Permission.DELETE ) );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}
	}

	public void getAffectedSubjectsTest() {
		SesameACLService sesameACLService = (SesameACLService) aclService;

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
			Set<SesameACLService.Subject> aclSubjects = (Set<SesameACLService.Subject>) privateMethod.invoke( aclService, subjectPermissionsToModify );

			Assert.assertTrue( aclSubjects.contains( new SesameACLService.Subject( role1, subjectClass ) ) );
			Assert.assertTrue( aclSubjects.contains( new SesameACLService.Subject( role2, subjectClass ) ) );
		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method", e );
		}

	}

	public void getAffectedPermissionsTest() {
		SesameACLService sesameACLService = (SesameACLService) aclService;

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
			Set<ACEDescription.Permission> aclPermissions = (Set<ACEDescription.Permission>) privateMethod.invoke( aclService, subjectPermissionsToModify );

			Assert.assertTrue( aclPermissions.contains( ACEDescription.Permission.DELETE ) );
			Assert.assertTrue( aclPermissions.contains( ACEDescription.Permission.READ ) );

		} catch ( NoSuchMethodException e ) {
			throw new SkipException( "can't use the method 1", e );
		} catch ( InvocationTargetException e ) {
			throw new SkipException( "can't use the method 2", e );
		} catch ( IllegalAccessException e ) {
			throw new SkipException( "can't use the method 3", e );
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
