package com.carbonldp.authorization.acl.web;

import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLService;
import com.carbonldp.ldp.sources.AbstractPUTRequestHandler;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author NestorVenegas
 * @since _version_
 */
@RequestHandler
public class AclPUTRequestHandler extends AbstractPUTRequestHandler<ACL> {
	ACLService aclService;

	@Override
	protected ACL getDocumentResourceView( RDFResource requestDocumentResource ) {
		return new ACL( requestDocumentResource, requestDocumentResource.getDocumentURI() );
	}

	@Override
	protected void replaceResource( URI targetURI, ACL documentResourceView ) {
		aclService.replace( documentResourceView );
	}

	@Autowired
	public void setAclService( ACLService aclService ) {this.aclService = aclService;}
}
