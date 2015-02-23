package com.carbonldp.apps.web.handlers;

import static com.carbonldp.Consts.EMPTY_STRING;
import static com.carbonldp.Consts.SLASH;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppFactory;
import com.carbonldp.apps.AppService;
import com.carbonldp.ldp.web.AbstractPOSTRequestHandler;
import com.carbonldp.models.Infraction;
import com.carbonldp.models.RDFResource;
import com.carbonldp.utils.ModelUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.ConflictException;

@RequestHandler
public class AppsPOSTHandler extends AbstractPOSTRequestHandler {

	private final AppService appService;

	@Autowired
	public AppsPOSTHandler(AppService appService) {
		this.appService = appService;
	}

	public ResponseEntity<Object> handleRequest(AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response) {
		validateRequestModel(requestModel);

		URI requestSubject = getRequestSubject(requestModel);
		RDFResource requestResource = new RDFResource(requestModel, requestSubject);

		validateRequestResource(requestResource);

		String targetURI = getTargetURL(request);

		if ( hasGenericRequestURI(requestResource) ) {
			URI forgedURI = forgeUniqueURI(requestResource, targetURI, request);
			requestResource = renameResource(requestResource, forgedURI);
		} else {
			validateRequestResourceRelativeness(requestResource, targetURI);
		}

		// TODO: After ensuring uniqueness, move this back into the "else" right above
		checkRequestResourceAvailability(requestResource);

		URI resourceContext = requestResource.getURI();
		requestResource = addMissingContext(requestResource, resourceContext);

		App app = new App(requestResource.getBaseModel(), requestResource.getURI());

		app = appService.create(app);
		appService.initialize(app);

		return new ResponseEntity<Object>(requestResource, HttpStatus.OK);
	}

	@Override
	protected void validateRequestResourcesNumber(int size) {
		super.validateRequestResourcesNumber(size);
		if ( size > 1 ) throw new BadRequestException("The request cannot contain more than one rdf resource.");
	}

	@Override
	protected void validateRequestResource(Resource subject) {
		super.validateRequestResource(subject);
		if ( URIUtil.hasFragment(ValueUtil.getURI(subject)) ) {
			throw new BadRequestException("The request resource cannot have a fragment in its URI.");
		}
	}

	private RDFResource addMissingContext(RDFResource requestResource, URI resourceContext) {
		AbstractModel modifiedModel = ModelUtil.replaceContext(requestResource.getBaseModel(), null, resourceContext);
		return new RDFResource(modifiedModel, requestResource.getURI());
	}

	private void checkRequestResourceAvailability(RDFResource requestResource) {
		if ( sourceWithURIExists(requestResource.getURI()) ) {
			throw new ConflictException("The URI is already in use.");
		}
	}

	private boolean sourceWithURIExists(URI sourceURI) {
		return sourceService.exists(sourceURI);
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
		AbstractModel renamedModel = ModelUtil.replaceSubject(requestResource.getBaseModel(), requestResource.getURI(), forgedURI);
		return new RDFResource(renamedModel, forgedURI);
	}

	private boolean hasGenericRequestURI(RDFResource resource) {
		return configurationRepository.isGenericRequest(resource.getURI().stringValue());
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
