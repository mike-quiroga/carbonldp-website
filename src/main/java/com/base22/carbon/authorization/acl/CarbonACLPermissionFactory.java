package com.base22.carbon.authorization.acl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.Permission;

import com.base22.carbon.models.PrefixedURI;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public final class CarbonACLPermissionFactory implements PermissionFactory {
	private final Map<String, CarbonACLPermission> registeredPermissionsByName;
	private final Map<Integer, CarbonACLPermission> registeredPermissionsByMask;

	public static enum CarbonPermission {
		//@formatter:off
		DISCOVER(
			1 << 0,
			new PrefixedURI("cs", "Discover")
		),
		READ(
			1 << 1,
			new PrefixedURI("cs", "Read")
		),
		UPDATE(
			1 << 2,
			new PrefixedURI("cs", "Update")
		),
		EXTEND(
			1 << 3,
			new PrefixedURI("cs", "Extend")
		),
		DELETE(
			1 << 4,
			new PrefixedURI("cs", "Delete")
		),

		DOWNLOAD(
			1 << 5,
			new PrefixedURI("cs", "Download")
		),

		CREATE_LDPRS(
			1 << 6,
			new PrefixedURI("cs", "CreateLDPRS")
		),
		CREATE_LDPC(
			1 << 7,
			new PrefixedURI("cs", "CreateLDPC")
		),
		CREATE_WFLDPNR(
			1 << 8,
			new PrefixedURI("cs", "Upload")
		),
		CREATE_ACCESS_POINT(
			1 << 9,
			new PrefixedURI("cs", "CreateAccessPoints")
		),

		ADD_MEMBER(
			1 << 10,
			new PrefixedURI("cs", "AddMembers")
		),

		EXECUTE_SPARQL_QUERY(
			1 << 11,
			new PrefixedURI("cs", "SparqlQuery")
		),
		EXECUTE_SPARQL_UPDATE(
			1 << 12,
			new PrefixedURI("cs", "SparqlUpdate")
		),

		ADD_AGENTS(
			1 << 13,
			new PrefixedURI("cs", "AddAgents")
		),
		REMOVE_AGENTS(
			1 << 14,
			new PrefixedURI("cs", "RemoveAgents")
		),
		CREATE_AGENTS(
			1 << 15,
			new PrefixedURI("cs", "CreateAgents")
		),
		EDIT_AGENTS(
			1 << 16,
			new PrefixedURI("cs", "EditAgents")
		),
		DELETE_AGENTS(
			1 << 17,
			new PrefixedURI("cs", "DeleteAgents")
		),

		CREATE_CHILDREN(
			1 << 18,
			new PrefixedURI("cs", "CreateChildren")
		),
		MANAGE_ACLS(
			1 << 19,
			new PrefixedURI("cs", "ManageACLs")
		),

		ADD_GROUPS(
			1 << 20,
			new PrefixedURI("cs", "AddGroups")
		),
		REMOVE_GROUPS(
			1 << 21,
			new PrefixedURI("cs", "RemoveGroups")
		),

		ACCESS_API(
			1 << 22,
			new PrefixedURI("cs", "AccessAPI")
		),
		ACCESS_DEV_GUI(
			1 << 23,
			new PrefixedURI("cs", "AccessDevGUI")
		),
		ACCESS_SPARQL_CLIENT(
			1 << 24,
			new PrefixedURI("cs", "AccessSPARQLClient")
		),
		ACCESS_REST_CLIENT(
			1 << 25,
			new PrefixedURI("cs", "AccessRESTClient")
		),
		ACCESS_LD_EXPLORER(
			1 << 26,
			new PrefixedURI("cs", "AccessLDExplorer")
		);		
		//formatter:on

		private final PrefixedURI prefixedURI;
		private final PrefixedURI[] prefixedURIs;
		private final Resource resource;
		
		private int mask;
		private CarbonACLPermission permission;

		CarbonPermission(int mask, PrefixedURI... uris) {
			this.prefixedURI = uris[0];
			this.prefixedURIs = uris;

			this.resource = ResourceFactory.createResource(this.prefixedURI.getURI());
			
			this.mask = mask;
			this.permission = new CarbonACLPermission(this);
		}

		public PrefixedURI getPrefixedURI() {
			return prefixedURI;
		}

		public PrefixedURI[] getPrefixedURIs() {
			return this.prefixedURIs;
		}

		public Resource getResource() {
			return resource;
		}
		
		public int getMask() {
			return this.mask;
		}
		
		public CarbonACLPermission getPermission() {
			return this.permission;
		}

		public static CarbonPermission findByURI(String uri) {
			for (CarbonPermission permission : CarbonPermission.values()) {
				for (PrefixedURI permissionURI : permission.getPrefixedURIs()) {
					if ( permissionURI.getURI().equals(uri) || permissionURI.getShortVersion().equals(uri) || permissionURI.getResourceURI().equals(uri) ) {
						return permission;
					}
				}
			}
			return null;
		}
		
		public static List<CarbonPermission> findByURIs(List<String> uris) {
			List<CarbonPermission> permissions = new ArrayList<CarbonPermission>();
			for (CarbonPermission permission : CarbonPermission.values()) {
				for(String uri : uris) {
					for (PrefixedURI permissionURI : permission.getPrefixedURIs()) {
						if ( permissionURI.getURI().equals(uri) || permissionURI.getShortVersion().equals(uri) || permissionURI.getResourceURI().equals(uri) ) {
							permissions.add(permission);
						}
					}
				}
			}
			return permissions;
		}
		
		public static CarbonPermission findByMask(int mask) {
			for (CarbonPermission permission : CarbonPermission.values()) {
				if(permission.getMask() == mask) {
					return permission;
				}
			}
			return null;
		}
		
		public static List<CarbonACLPermission> getACLPermissionList(List<CarbonPermission> carbonPermissions) {
			List<CarbonACLPermission> permissions = new ArrayList<CarbonACLPermission>();
			for (CarbonPermission carbonPermission : carbonPermissions) {
				permissions.add(carbonPermission.getPermission());
			}
			return permissions;
		}
	}
	
	public CarbonACLPermissionFactory() {
		registeredPermissionsByName = new HashMap<String, CarbonACLPermission>();
		registeredPermissionsByMask = new HashMap<Integer, CarbonACLPermission>();

		registerCarbonPermissions();
	}
	
	private void registerCarbonPermissions() {
		for(CarbonPermission carbonPermission : CarbonPermission.values()) {			
			registeredPermissionsByName.put(carbonPermission.name(), carbonPermission.getPermission()); 
			registeredPermissionsByMask.put(carbonPermission.getMask(), carbonPermission.getPermission()); 
		}
	}
	
	@Override
	public Permission buildFromMask(int mask) {
		if ( registeredPermissionsByMask.containsKey(Integer.valueOf(mask)) ) {
			return registeredPermissionsByMask.get(Integer.valueOf(mask));
		}

		CumulativePermission cumulativePermission = new CumulativePermission();

		for (int i = 0; i < 32; i++) {
			int permissionToCheck = 1 << i;

			if ( (mask & permissionToCheck) == permissionToCheck ) {
				Permission permission = registeredPermissionsByMask.get(Integer.valueOf(permissionToCheck));

				if ( permission == null ) {
					// TODO: FT
					throw new IllegalStateException("Mask '" + permissionToCheck + "' does not have a corresponding static Permission");
				}
				cumulativePermission.set(permission);
			}
		}

		return cumulativePermission;
	}

	@Override
	public Permission buildFromName(String name) {
		CarbonACLPermission permission = registeredPermissionsByName.get(name);

		if ( permission == null ) {
			// TODO: FT
			throw new IllegalArgumentException("Unknown permission '" + name + "'");
		}

		return permission;
	}

	@Override
	public List<Permission> buildFromNames(List<String> names) {
		if ( (names == null) || (names.size() == 0) ) {
			return Collections.emptyList();
		}

		List<Permission> permissions = new ArrayList<Permission>(names.size());

		for (String name : names) {
			permissions.add(buildFromName(name));
		}

		return permissions;
	}

	public List<CarbonACLPermission> getPermissionsFromMask(int mask) {
		List<CarbonACLPermission> matchedPermissions = new ArrayList<CarbonACLPermission>();

		for (int bit = 0; bit <= 31; bit++) {
			Integer currentMask = 1 << bit;
			if ( (currentMask & mask) == currentMask ) {
				if ( registeredPermissionsByMask.containsKey(currentMask) ) {
					matchedPermissions.add(registeredPermissionsByMask.get(currentMask));
				}
			}
		}

		return matchedPermissions;
	}

}
