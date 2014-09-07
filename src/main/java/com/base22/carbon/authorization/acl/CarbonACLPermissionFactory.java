package com.base22.carbon.authorization.acl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.acls.model.Permission;

import com.base22.carbon.Carbon;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public final class CarbonACLPermissionFactory implements PermissionFactory {
	private final Map<String, CarbonACLPermission> registeredPermissionsByName;
	private final Map<Integer, CarbonACLPermission> registeredPermissionsByMask;

	public static enum CarbonPermission {
		//@formatter:off
		DISCOVER("cs", "Discover", 1 << 0),
		READ("cs", "Read", 1 << 1),
		UPDATE("cs", "Update", 1 << 2),
		EXTEND("cs", "Extend", 1 << 3),
		DELETE("cs", "Delete", 1 << 4),

		DOWNLOAD("cs", "Download", 1 << 5),

		CREATE_LDPRS("cs", "CreateLDPRS", 1 << 6),
		CREATE_LDPC("cs", "CreateLDPC", 1 << 7),
		CREATE_WFLDPNR("cs", "Upload", 1 << 8),
		CREATE_ACCESS_POINT("cs", "CreateAccessPoints", 1 << 9),

		ADD_MEMBER("cs", "AddMembers", 1 << 10),

		EXECUTE_SPARQL_QUERY("cs", "SparqlQuery", 1 << 11),
		EXECUTE_SPARQL_UPDATE("cs", "SparqlUpdate", 1 << 12),

		ADD_AGENTS("cs", "AddAgents", 1 << 13),
		REMOVE_AGENTS("cs", "RemoveAgents", 1 << 14),
		CREATE_AGENTS("cs", "CreateAgents", 1 << 15),
		EDIT_AGENTS("cs", "EditAgents", 1 << 16),
		DELETE_AGENTS("cs", "DeleteAgents", 1 << 17),

		CREATE_CHILDREN("cs", "CreateChildren", 1 << 18),
		MANAGE_ACLS("cs", "ManageACLs", 1 << 19),

		ADD_GROUPS("cs", "AddGroups", 1 << 20),
		REMOVE_GROUPS("cs", "RemoveGroups", 1 << 21),

		ACCESS_API("cs", "AccessAPI", 1 << 22),
		ACCESS_DEV_GUI("cs", "AccessDevGUI", 1 << 23),
		ACCESS_SPARQL_CLIENT("cs", "AccessSPARQLClient", 1 << 24),
		ACCESS_REST_CLIENT("cs", "AccessRESTClient", 1 << 25),
		ACCESS_LD_EXPLORER("cs", "AccessLDExplorer", 1 << 26);		//formatter:on
		
		private String prefix;
		private String slug;
		private String uri;
		private Resource resource;
		private int mask;
		private CarbonACLPermission permission;

		CarbonPermission(String prefix, String slug, int mask) {
			this.prefix = Carbon.CONFIGURED_PREFIXES.get(prefix);
			this.slug = slug;
			this.uri = this.prefix.concat(slug);
			this.resource = ResourceFactory.createResource(this.uri);
			this.mask = mask;
			this.permission = new CarbonACLPermission(this);
		}

		public String getPrefix() {
			return this.prefix;
		}
		
		public String getSlug() {
			return this.slug;
		}

		public String getURI() {
			return this.uri;
		}

		public Resource getResource() {
			return this.resource;
		}
		
		public int getMask() {
			return this.mask;
		}
		
		public CarbonACLPermission getPermission() {
			return this.permission;
		}

		public static CarbonPermission findByURI(String uri) {
			List<CarbonPermission> permission = findByURIs(Arrays.asList(uri));
			if(permission.size() == 0) {
				return null;
			}
			return permission.get(0);
		}
		
		public static List<CarbonPermission> findByURIs(List<String> uris) {
			List<CarbonPermission> permissions = new ArrayList<CarbonPermission>();
			for (CarbonPermission permission : CarbonPermission.values()) {
				for(String uri : uris) {
					if(permission.getURI().equals(uri)) {
						permissions.add(permission);
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
