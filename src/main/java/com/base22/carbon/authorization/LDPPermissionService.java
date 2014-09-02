package com.base22.carbon.authorization;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.agents.Agent;
import com.base22.carbon.agents.AgentDAO;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.apps.roles.ApplicationRoleDAO;
import com.base22.carbon.authentication.AgentAuthenticationToken;
import com.base22.carbon.authentication.ApplicationContextToken;
import com.base22.carbon.authentication.AuthenticationUtil;
import com.base22.carbon.authorization.acl.ACESystemResource;
import com.base22.carbon.authorization.acl.ACESystemResourceFactory;
import com.base22.carbon.authorization.acl.ACLSystemResource;
import com.base22.carbon.authorization.acl.ACLSystemResourceFactory;
import com.base22.carbon.authorization.acl.CarbonACLPermission;
import com.base22.carbon.authorization.acl.CarbonACLPermissionFactory;
import com.base22.carbon.authorization.acl.AceSR.SubjectType;
import com.base22.carbon.groups.Group;
import com.base22.carbon.groups.GroupDAO;
import com.base22.carbon.ldp.models.LDPResource;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.repository.services.LDPService;

@Service("ldpPermissionService")
public class LDPPermissionService {
	// TODO: Decide. Should we use the platform variants, or the protected ones?
	@Autowired
	@Qualifier("platformAgentDetailsDAO")
	protected AgentDAO agentLoginDetailsDAO;
	@Autowired
	@Qualifier("platformGroupDAO")
	protected GroupDAO groupDAO;
	@Autowired
	@Qualifier("platformApplicationRoleDAO")
	protected ApplicationRoleDAO applicationRoleDAO;

	@Autowired
	private CarbonACLPermissionFactory permissionFactory;
	@Autowired
	private PermissionService permissionService;
	@Autowired
	private LDPService ldpService;

	static final Logger LOG = LoggerFactory.getLogger(LDPPermissionService.class);

	public void injectACLToLDPResource(Object identityObject, LDPResource ldpResource) throws CarbonException {
		Acl acl = null;
		try {
			acl = permissionService.getACL(identityObject);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}

		// Create the ACL
		ACLSystemResourceFactory aclSRFactory = new ACLSystemResourceFactory();
		ACLSystemResource aclSR = aclSRFactory.create(ldpResource);

		// Create the ACEs
		ACESystemResourceFactory aceSRFactory = new ACESystemResourceFactory();
		for (AccessControlEntry entry : acl.getEntries()) {
			UUID subjectUUID = getSubjectUUIDFromSid(entry.getSid());
			if ( subjectUUID == null ) {
				// TODO: FT
				throw new CarbonException("The sid doesn't have the proper format.");
			}
			SubjectType subjectType = getSubjectTypeFromSid(entry.getSid());
			if ( subjectType == null ) {
				// TODO: FT
				throw new CarbonException("The sid doesn't have the proper format.");
			}

			ACESystemResource aceSR = aceSRFactory.create(aclSR, subjectUUID, subjectType, entry.isGranting());

			// Get the modes
			Permission entryPermission = entry.getPermission();
			List<CarbonACLPermission> permissions = permissionFactory.getPermissionsFromMask(entryPermission.getMask());
			for (CarbonACLPermission permission : permissions) {
				aceSR.addPermission(permission.getCarbonPermission());
			}
		}
	}

	public void replaceLDPResourceACL(Object object, ACLSystemResource aclSR) throws CarbonException {
		AgentAuthenticationToken authenticationToken = getAgentAuthenticationToken();
		Application application = getApplicationFromContext(authenticationToken);

		if ( application == null ) {
			// TODO: FT
			throw new CarbonException("The application context hasn't been set.");
		}

		Map<Sid, Map<Boolean, ACESystemResource>> aclModifications = validateACLModification(object, aclSR, application, authenticationToken);

		applyACLModifications(object, aclModifications);
	}

	private AgentAuthenticationToken getAgentAuthenticationToken() throws CarbonException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( authentication == null ) {
			// TODO: FT
			throw new CarbonException("The context doesn't hold an authentication object.");
		}
		if ( ! (authentication instanceof AgentAuthenticationToken) ) {
			// TODO: FT
			throw new CarbonException("The context's authentication token is not an AgentAuthenticationToken.");
		}
		return (AgentAuthenticationToken) authentication;
	}

	private Application getApplicationFromContext(ApplicationContextToken applicationToken) throws CarbonException {
		return applicationToken.getCurrentApplicationContext();
	}

	// TODO: Take into account if the modification is actually modifying something
	private Map<Sid, Map<Boolean, ACESystemResource>> validateACLModification(Object object, ACLSystemResource aclSR, Application application,
			AgentAuthenticationToken authenticationToken) throws CarbonException {

		Map<Sid, Map<Boolean, ACESystemResource>> aclModifications = new HashMap<Sid, Map<Boolean, ACESystemResource>>();

		Acl acl = permissionService.getACL(object);
		Map<Sid, Map<Boolean, AccessControlEntry>> aclEntries = getACLEntriesMap(acl);

		Map<Sid, SubjectType> affectedSids = new HashMap<Sid, SubjectType>();
		Map<Sid, Object> affectedSidObjects = new HashMap<Sid, Object>();

		int combinedPermissionChanges = 0;

		for (ACESystemResource entry : aclSR.getACEntries()) {
			SubjectType subjectType = entry.getSubjectType();

			Object sidObject = null;
			Sid sid = null;
			switch (subjectType) {
				case AGENT:
					sidObject = getAgent(entry.getSubjectUUID());
					sid = new PrincipalSid(new AgentAuthenticationToken((Agent) sidObject, new ArrayList<Group>()));
					break;
				case APP_ROLE:
					sidObject = getApplicationRole(entry.getSubjectUUID());
					sid = new GrantedAuthoritySid((ApplicationRole) sidObject);
					break;
				case GROUP:
					sidObject = getGroup(entry.getSubjectUUID());
					sid = new GrantedAuthoritySid((Group) sidObject);
					break;
				default:
					String entityBodyIssue = MessageFormat.format("The ACEntry: ''{0}'', doesn't target an Application Role.", entry.getURI());

					if ( LOG.isDebugEnabled() ) {
						LOG.debug("<< validateACLModification() > {}", entityBodyIssue);
					}

					ErrorResponseFactory errorFactory = new ErrorResponseFactory();
					ErrorResponse errorObject = errorFactory.create();
					errorObject.setHttpStatus(HttpStatus.NOT_IMPLEMENTED);
					errorObject.setFriendlyMessage("The request can't be completed.");
					errorObject.setDebugMessage("Assignation of permissions to something else than an Application Role hasn't been implemented.");
					throw new CarbonException(errorObject);
			}

			if ( aclEntries.containsKey(sid) ) {
				// The AceSR is a modification of an already existing ACL
				Map<Boolean, AccessControlEntry> mapEntry = aclEntries.get(sid);
				if ( mapEntry.containsKey(entry.isGranting()) ) {
					// There is an ACE that shares Sid and Granting, compare them
					AccessControlEntry ace = mapEntry.get(entry.isGranting());
					int entryDifferences = ace.getPermission().getMask() ^ entry.getCombinedACLPermissionMask().getMask();
					if ( entryDifferences == 0 ) {
						// There were no changes in this entry
					} else {
						// There are differences, add them to the registered changes
						combinedPermissionChanges = combinedPermissionChanges | entryDifferences;
						affectedSids.put(sid, subjectType);
						affectedSidObjects.put(sid, sidObject);
					}

					// Remove the ace from the map
					mapEntry.remove(entry.isGranting());
					if ( mapEntry.isEmpty() ) {
						// Remove the map<boolean, ace> from the sid map
						aclEntries.remove(sid);
					}
				} else {
					// There is not an ACE that shares both the Sid and the Granting, add the entry as a change
					affectedSids.put(sid, subjectType);
					affectedSidObjects.put(sid, sidObject);
					combinedPermissionChanges = combinedPermissionChanges | entry.getCombinedACLPermissionMask().getMask();
				}
			} else {
				// The AceSR is a new ACL, add them to the registered changes
				affectedSids.put(sid, subjectType);
				affectedSidObjects.put(sid, sidObject);
				combinedPermissionChanges = combinedPermissionChanges | entry.getCombinedACLPermissionMask().getMask();
			}

			Map<Boolean, ACESystemResource> aceModification = null;
			if ( aclModifications.containsKey(sid) ) {
				aceModification = aclModifications.get(sid);
			} else {
				aceModification = new HashMap<Boolean, ACESystemResource>();
			}

			aceModification.put(entry.isGranting(), entry);
			aclModifications.put(sid, aceModification);
		}

		// Add the entries that were removed to the changes registry
		for (Map<Boolean, AccessControlEntry> mapEntry : aclEntries.values()) {
			for (AccessControlEntry ace : mapEntry.values()) {

				affectedSids.put(ace.getSid(), getSubjectTypeFromSid(ace.getSid()));
				affectedSidObjects.put(ace.getSid(), null);
				combinedPermissionChanges = combinedPermissionChanges | ace.getPermission().getMask();
			}
		}

		validateAffectedSids(affectedSids, affectedSidObjects, application, authenticationToken);

		if ( ! permissionService.hasPermission(new PermissionImpl(combinedPermissionChanges), object) ) {
			String entityBodyIssue = "The request changes permissions that the agent doesn't have.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateACLModification() > {}", entityBodyIssue);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.UNAUTHORIZED);
			errorObject.setFriendlyMessage("The request can't be completed.");
			errorObject.setDebugMessage(entityBodyIssue);
			errorObject.setEntityBodyIssue(null, entityBodyIssue);
			throw new CarbonException(errorObject);
		}

		return aclModifications;
	}

	private void validateAffectedSids(Map<Sid, SubjectType> affectedSids, Map<Sid, Object> affectedSidObjects, Application application,
			AgentAuthenticationToken authenticationToken) throws CarbonException {
		Set<ApplicationRole> agentApplicationRoles = null;

		for (Entry<Sid, SubjectType> sidEntry : affectedSids.entrySet()) {
			Object sidObject = affectedSidObjects.get(sidEntry.getKey());
			switch (sidEntry.getValue()) {
				case AGENT:

					Agent agent = null;
					if ( sidObject == null ) {
						agent = getAgent(getSubjectUUIDFromSid(sidEntry.getKey()));
					} else if ( sidObject instanceof Agent ) {
						agent = (Agent) sidObject;
					} else {
						// TODO: Fail
					}
					validateAffectedAgent(agent);

					break;
				case APP_ROLE:

					ApplicationRole applicationRole = null;
					if ( sidObject == null ) {
						applicationRole = getApplicationRole(getSubjectUUIDFromSid(sidEntry.getKey()));
					} else if ( sidObject instanceof ApplicationRole ) {
						applicationRole = (ApplicationRole) sidObject;
					} else {
						// TODO: Fail
					}

					// Lazy initialization of the agentApplicationRoles
					if ( agentApplicationRoles == null ) {
						agentApplicationRoles = getAgentApplicationRoles(authenticationToken, application);
					}
					validateAffectedApplicationRole(applicationRole, agentApplicationRoles);

					break;
				case GROUP:
					Group group = null;
					if ( sidObject == null ) {
						group = getGroup(getSubjectUUIDFromSid(sidEntry.getKey()));
					} else if ( sidObject instanceof Group ) {
						group = (Group) sidObject;
					} else {
						// TODO: Fail
					}
					validateAffectedGroup(group);
					break;
				default:
					break;

			}
		}
	}

	private void validateAffectedApplicationRole(ApplicationRole applicationRole, Set<ApplicationRole> agentApplicationRoles) throws CarbonException {
		List<ApplicationRole> parentRoles = null;
		try {
			parentRoles = applicationRoleDAO.getAllParentsOfApplicationRole(applicationRole.getUuid());
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}

		boolean agentHasParentRole = false;
		for (ApplicationRole parentRole : parentRoles) {
			if ( agentApplicationRoles.contains(parentRole) ) {
				agentHasParentRole = true;
				break;
			}
		}

		if ( ! agentHasParentRole ) {
			String entityBodyIssue = "The request targets an ApplicationRole higher than the ones the Agent has.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateAffectedApplicationRole() > {}", entityBodyIssue);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.UNAUTHORIZED);
			errorObject.setFriendlyMessage("The request can't be completed.");
			errorObject.setDebugMessage(entityBodyIssue);
			errorObject.setEntityBodyIssue(null, entityBodyIssue);
			throw new CarbonException(errorObject);
		}
	}

	private void validateAffectedAgent(Agent agent) {
		// TODO: When agent ACL assignation is implemented
	}

	private void validateAffectedGroup(Group group) {
		// TODO: When group ACL assignation is implemented
	}

	private Set<ApplicationRole> getAgentApplicationRoles(AgentAuthenticationToken authenticationToken, Application application) {
		return authenticationToken.getPrincipal().getApplicationRoles(application);
	}

	private ApplicationRole getApplicationRole(UUID applicationRoleUUID) throws CarbonException {

		ApplicationRole applicationRole = null;
		try {
			applicationRole = applicationRoleDAO.findByUUID(applicationRoleUUID);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}

		if ( applicationRole == null ) {
			String entityBodyIssue = "The request doesn't target an existing ApplicationRole.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateACLModification() > {}", entityBodyIssue);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.UNAUTHORIZED);
			errorObject.setFriendlyMessage("The request can't be completed.");
			errorObject.setDebugMessage(entityBodyIssue);
			errorObject.setEntityBodyIssue(null, entityBodyIssue);
			throw new CarbonException(errorObject);
		}

		return applicationRole;
	}

	private Agent getAgent(UUID agentUUID) throws CarbonException {
		String entityBodyIssue = "The request doesn't target an Application Role.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< validateACLModification() > {}", entityBodyIssue);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.NOT_IMPLEMENTED);
		errorObject.setFriendlyMessage("The request can't be completed.");
		errorObject.setDebugMessage("Assignation of permissions to something else than an Application Role hasn't been implemented.");
		throw new CarbonException(errorObject);
	}

	private Group getGroup(UUID groupUUID) throws CarbonException {
		String entityBodyIssue = "The request doesn't target an Application Role.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< validateACLModification() > {}", entityBodyIssue);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.NOT_IMPLEMENTED);
		errorObject.setFriendlyMessage("The request can't be completed.");
		errorObject.setDebugMessage("Assignation of permissions to something else than an Application Role hasn't been implemented.");
		throw new CarbonException(errorObject);
	}

	private void applyACLModifications(Object object, Map<Sid, Map<Boolean, ACESystemResource>> aclModifications) throws CarbonException {
		try {
			permissionService.emptyACL(object);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}

		for (Entry<Sid, Map<Boolean, ACESystemResource>> modifications : aclModifications.entrySet()) {
			Sid sid = modifications.getKey();
			Map<Boolean, ACESystemResource> modification = modifications.getValue();

			for (Entry<Boolean, ACESystemResource> entry : modification.entrySet()) {
				try {
					if ( entry.getKey() ) {
						permissionService.grantPermissions(sid, object, entry.getValue().getACLPermissions());
					} else {
						permissionService.denyPermissions(sid, object, entry.getValue().getACLPermissions());
					}
				} catch (CarbonException e) {
					// TODO: FT
					throw e;
				}
			}
		}
	}

	private Map<Sid, Map<Boolean, AccessControlEntry>> getACLEntriesMap(Acl acl) {
		Map<Sid, Map<Boolean, AccessControlEntry>> aclEntries = new HashMap<Sid, Map<Boolean, AccessControlEntry>>();

		for (AccessControlEntry entry : acl.getEntries()) {
			Sid sid = entry.getSid();
			Boolean isGranting = entry.isGranting();

			Map<Boolean, AccessControlEntry> mapEntry = null;
			if ( aclEntries.containsKey(sid) ) {
				mapEntry = aclEntries.get(sid);
			} else {
				mapEntry = new HashMap<Boolean, AccessControlEntry>();
			}

			mapEntry.put(isGranting, entry);
			aclEntries.put(sid, mapEntry);
		}

		return aclEntries;
	}

	// Expects the Sid to be like: "Agent: 2c39d946-10fe-4db1-9d71-df6e01dc94ff"
	private UUID getSubjectUUIDFromSid(Sid sid) {
		String sidString = sid.toString();
		if ( sid instanceof PrincipalSid ) {
			sidString = ((PrincipalSid) sid).getPrincipal();
		} else if ( sid instanceof GrantedAuthoritySid ) {
			sidString = ((GrantedAuthoritySid) sid).getGrantedAuthority();
		} else {
			return null;
		}

		String[] sidParts = sidString.split(": ");
		if ( sidParts.length < 2 ) {
			return null;
		}
		if ( ! AuthenticationUtil.isUUIDString(sidParts[1]) ) {
			return null;
		}
		return AuthenticationUtil.restoreUUID(sidParts[1]);
	}

	// Expects the Sid to be like: "Agent: 2c39d946-10fe-4db1-9d71-df6e01dc94ff"
	private SubjectType getSubjectTypeFromSid(Sid sid) {
		String sidString = sid.toString();
		if ( sid instanceof PrincipalSid ) {
			sidString = ((PrincipalSid) sid).getPrincipal();
		} else if ( sid instanceof GrantedAuthoritySid ) {
			sidString = ((GrantedAuthoritySid) sid).getGrantedAuthority();
		} else {
			return null;
		}

		String[] sidParts = sidString.split(": ");
		if ( sidParts.length < 2 ) {
			return null;
		}
		return SubjectType.findByName(sidParts[0]);
	}
}
