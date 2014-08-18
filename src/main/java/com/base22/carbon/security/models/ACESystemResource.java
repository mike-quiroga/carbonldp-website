package com.base22.carbon.security.models;

import java.util.List;
import java.util.UUID;

import com.base22.carbon.models.LDPSystemResource;
import com.base22.carbon.security.constants.AceSR.SubjectType;
import com.base22.carbon.security.models.CarbonACLPermissionFactory.CarbonPermission;

public interface ACESystemResource extends LDPSystemResource {
	public UUID getSubjectUUID();

	public void setSubjectUUID(UUID subjectUUID);

	public SubjectType getSubjectType();

	public void setSubjectType(SubjectType subjectType);

	public List<CarbonPermission> getPermissions();

	public List<CarbonACLPermission> getACLPermissions();

	public PermissionImpl getCombinedACLPermissionMask();

	public void addPermission(CarbonPermission mode);

	public void removePermission(CarbonPermission mode);

	public void setPermissions(List<CarbonPermission> modes);

	public Boolean isGranting();

	public void setGranting(boolean granting);

}
