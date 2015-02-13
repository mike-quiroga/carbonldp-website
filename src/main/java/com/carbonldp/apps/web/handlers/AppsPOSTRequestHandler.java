package com.carbonldp.apps.web.handlers;

import static com.carbonldp.Consts.EMPTY_STRING;
import static com.carbonldp.Consts.SLASH;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import com.carbonldp.apps.AppFactory;
import com.carbonldp.ldp.web.handlers.AbstractPOSTRequestHandler;
import com.carbonldp.models.Infraction;
import com.carbonldp.models.RDFResource;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.BadRequestException;

@RequestHandler
public class AppsPOSTRequestHandler extends AbstractPOSTRequestHandler {
	public ResponseEntity<Object> handleRequest(AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response) {
		validateRequestModel(requestModel);

		URI requestSubject = getRequestSubject(requestModel);
		RDFResource requestResource = new RDFResource(requestModel, requestSubject, null);

		validateRequestResource(requestResource);

		String targetURI = getTargetURI(request);

		if ( hasGenericRequestURI(requestResource) ) {
			URI forgedURI = forgeUniqueURI(requestResource, request);
			requestResource = renameResource(requestResource, forgedURI);
		} else {
			validateRequestResourceRelativeness(requestResource, targetURI);
			checkRequestResourceAvailability(requestResource);
		}

		// TODO: Forge the new Application's URI
		// TODO: Change the Application's URI
		// TODO: Create repository for the application
		// TODO: Store repositoryID in the Application
		// TODO: Create default resources in the Application's repository
		// -- TODO: Root Container
		// -- TODO: Application Roles Container
		// -- TODO: ACLs
		// TODO: Create application in the platform's Applications container

		// TODO: FT: Return OK
		return new ResponseEntity<Object>(requestModel, HttpStatus.OK);
	}

	private void checkRequestResourceAvailability(RDFResource requestResource) {

	}

	private void validateRequestResourceRelativeness(RDFResource requestResource, String targetURI) {
		String resourceURI = requestResource.getURI().stringValue();
		targetURI = targetURI.endsWith(SLASH) ? targetURI : targetURI.concat(SLASH);
		if ( ! resourceURI.startsWith(targetURI) ) {
			throw new BadRequestException("A request resource's URI doesn't have the request URI as a base.");
		}

		String relativeURI = resourceURI.replace(targetURI, EMPTY_STRING);
		if ( relativeURI.length() == 0 ) {
			throw new BadRequestException("A request resource's URI is the same as the request URI. Remember POST to parent, PUT to me.");
		}

		int slashIndex = relativeURI.indexOf(SLASH);
		if ( slashIndex == - 1 ) {
			if ( configurationRepository.enforceEndingSlash() ) {
				throw new BadRequestException("A request resource's URI doesn't end up in a slash.");
			}
		}

		if ( (slashIndex + 1) < relativeURI.length() ) {
			throw new BadRequestException("A request resource's URI isn't an immediate child of the request URI.");
		}
	}

	private RDFResource renameResource(RDFResource requestResource, URI forgedURI) {
		// TODO Auto-generated method stub
		return null;
	}

	private URI forgeUniqueURI(RDFResource requestResource, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean hasGenericRequestURI(RDFResource resource) {
		return configurationRepository.isGenericRequest(resource.getURI().stringValue());
	}

	private void validateRequestModel(Model requestModel) {
		Set<Resource> subjects = requestModel.subjects();
		if ( subjects.size() != 1 ) {
			throw new BadRequestException("The request contains more than one RDF resource.");
		}

		Resource subject = subjects.iterator().next();

		if ( ValueUtil.isBNode(subject) ) {
			throw new BadRequestException(0);
		}

		URI subjectURI = ValueUtil.getURI(subject);

		if ( URIUtil.hasFragment(subjectURI) ) {
			// TODO: Add error code
			throw new BadRequestException(0);
		}
	}

	private void validateRequestResource(RDFResource requestResource) {
		List<Infraction> infractions = AppFactory.validate(requestResource, false);
		if ( ! infractions.isEmpty() ) {
			throw new BadRequestException(0);
		}
	}

	private URI getRequestSubject(Model requestModel) {
		Assert.notEmpty(requestModel);
		Iterator<Resource> subjectsIterator = requestModel.subjects().iterator();
		while (subjectsIterator.hasNext()) {
			Resource subject = subjectsIterator.next();
			if ( ValueUtil.isURI(subject) ) return ValueUtil.getURI(subject);
		}
		return null;
	}
}
