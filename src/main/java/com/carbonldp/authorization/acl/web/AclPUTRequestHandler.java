package com.carbonldp.authorization.acl.web;

import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLService;
import com.carbonldp.ldp.sources.AbstractPUTRequestHandler;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author NestorVenegas
 * @since 0.16.0-ALPHA
 */
@RequestHandler
public class AclPUTRequestHandler extends AbstractPUTRequestHandler<ACL> {
	ACLService aclService;

	@Override
	protected ACL getDocumentResourceView( RDFResource requestDocumentResource ) {
		return new ACL( requestDocumentResource, requestDocumentResource.getDocumentIRI() );
	}

	@Override
	protected void replaceResource( IRI targetIRI, ACL documentResourceView ) {
		aclService.replace( documentResourceView );
	}

	@Override
	protected void checkPrecondition( IRI targetIRI, String requestETag ) {
		// TODO: delete this method when ETag stops being time stamp based
	}

	@Autowired
	public void setAclService( ACLService aclService ) {this.aclService = aclService;}
}
