package com.base22.carbon.authorization.acl;

import java.util.List;
import java.util.UUID;

import com.base22.carbon.authorization.PermissionImpl;
import com.base22.carbon.authorization.acl.AceSR.SubjectType;
import com.base22.carbon.authorization.acl.CarbonACLPermissionFactory.CarbonPermission;
import com.base22.carbon.ldp.models.SystemRDFResource;

public interface ACESystemResource extends SystemRDFResource {
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
