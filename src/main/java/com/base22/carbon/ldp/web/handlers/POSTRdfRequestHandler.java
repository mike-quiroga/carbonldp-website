package com.base22.carbon.ldp.web.handlers;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jena.riot.Lang;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.APIPreferences.RetrieveContainerPreference;
import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.HTTPHeaders;
import com.base22.carbon.apps.Application;
import com.base22.carbon.ldp.ModelUtil;
import com.base22.carbon.ldp.models.Container;
import com.base22.carbon.ldp.models.ContainerClass;
import com.base22.carbon.ldp.models.ContainerClass.ContainerType;
import com.base22.carbon.ldp.models.ContainerFactory;
import com.base22.carbon.ldp.models.NonRDFSourceClass;
import com.base22.carbon.ldp.models.RDFSource;
import com.base22.carbon.ldp.models.RDFSourceFactory;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.models.EmptyResponse;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.models.HttpHeader;
import com.base22.carbon.models.HttpHeaderValue;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class POSTRdfRequestHandler extends AbstractCreationRequestHandler {

	private final static Resource[] invalidTypesForRDFSources;
	static {
		//@formatter:off
		List<Resource> invalidTypes = Arrays.asList(
			ContainerClass.ContainerType.BASIC.getResource(),
			NonRDFSourceClass.Resources.WRAPPER.getResource(),
			NonRDFSourceClass.Resources.LDPNR.getResource()
		);
		//@formatter:on

		invalidTypesForRDFSources = invalidTypes.toArray(new Resource[invalidTypes.size()]);
	}

	private final static Resource[] invalidTypesForContainers;
	static {
		//@formatter:off
		List<Resource> invalidTypes = Arrays.asList(
			ContainerClass.ContainerType.DIRECT.getResource(),
			ContainerClass.ContainerType.INDIRECT.getResource(),
			NonRDFSourceClass.Resources.WRAPPER.getResource(),
			NonRDFSourceClass.Resources.LDPNR.getResource()
		);
		//@formatter:on

		invalidTypesForContainers = invalidTypes.toArray(new Resource[invalidTypes.size()]);
	}

	public ResponseEntity<Object> handleTurtleRDFPost(String appSlug, HttpServletRequest request, HttpServletResponse response, HttpEntity<byte[]> entity)
			throws CarbonException {

		String genericRequestURI = HTTPUtil.createGenericRequestURI();
		InputStream requestBodyInputStream = getBodyInputStream(entity);
		Model requestModel = parseEntityBody(genericRequestURI, requestBodyInputStream, Lang.TURTLE);

		return handleRDFPost(appSlug, requestModel, request, response);
	}

	public ResponseEntity<Object> handleRDFPost(String appSlug, Model requestModel, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleRDFPost()");
		}

		Application app = getApplicationFromContext();

		String targetURI = getTargetURI(request);
		URIObject targetURIObject = getTargetURIObject(targetURI);

		if ( ! targetResourceExists(targetURIObject) ) {
			return handleNonExistentResource(targetURI, requestModel, request, response);
		}

		Resource[] documentResources = getDocumentResources(requestModel);

		if ( documentResources.length == 0 ) {
			return handleRequestWithNoDocumentResources(app, targetURIObject, documentResources, requestModel, request, response);
		}

		// Search for resources that do not have the documentResource's URI as a base
		Resource[] externalResources = getExternalResources(documentResources, requestModel);
		if ( externalResources.length != 0 ) {
			return handleRequestWithExternalResources(externalResources, request, response);
		}

		Map<String, RDFSource> requestRDFSources = null;
		requestRDFSources = getRequestRDFSources(targetURIObject, documentResources, requestModel, request);

		if ( requestRDFSourcesAlreadyExist(requestRDFSources, app) ) {
			return handlePOSTExistentRDFSources(app, request, response);
		}

		HttpHeader linkHeader = new HttpHeader(request.getHeaders(HTTPHeaders.LINK));
		InteractionModel interactionModel = getInteractionModel(linkHeader);

		ContainerType targetContainerType = getTargetContainerType(app, targetURIObject);
		if ( targetResourceIsContainer(targetContainerType) ) {
			InteractionModel dim = getDefaultInteractionModel(targetURIObject, app);
			dim = dim == null ? InteractionModel.CONTAINER : dim;
			interactionModel = interactionModel == null ? dim : interactionModel;

			switch (interactionModel) {
				case CONTAINER:
					return handlePOSTToContainer(app, targetURIObject, requestRDFSources, targetContainerType, request, response);
				case RDF_SOURCE:
					return handlePOSTToRDFSource(app, targetURIObject, requestRDFSources, request, response);
				default:
					return handleInvalidInteractionModel(interactionModel, targetURIObject, request, response);
			}
		} else {
			interactionModel = interactionModel == null ? InteractionModel.RDF_SOURCE : interactionModel;

			switch (interactionModel) {
				case RDF_SOURCE:
					return handlePOSTToRDFSource(app, targetURIObject, requestRDFSources, request, response);
				default:
					return handleInvalidInteractionModel(interactionModel, targetURIObject, request, response);
			}
		}
	}

	private List<String> getRequestURIs(Map<String, RDFSource> requestRDFSources) {
		List<String> uris = new ArrayList<String>();
		for (RDFSource requestRDFSource : requestRDFSources.values()) {
			uris.add(requestRDFSource.getURI());
		}
		return uris;
	}

	private ResponseEntity<Object> handlePOSTExistentRDFSources(Application app, HttpServletRequest request, HttpServletResponse response) {
		String friendlyMessage = "One or more URIs already exists.";
		String debugMessage = "One or more RDFSources have a URI that is already taken.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handlePOSTExistentRDFSources() > {}", debugMessage);
		}

		ErrorResponseFactory factory = new ErrorResponseFactory();
		ErrorResponse errorObject = factory.create();
		errorObject.setHttpStatus(HttpStatus.CONFLICT);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	private boolean requestRDFSourcesAlreadyExist(Map<String, RDFSource> requestRDFSources, Application app) throws CarbonException {
		List<String> uris = getRequestURIs(requestRDFSources);
		try {
			return rdfSourceService.rdfSourcesExist(uris);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private ResponseEntity<Object> handleInvalidInteractionModel(InteractionModel interactionModel2, URIObject targetURIObject, HttpServletRequest request,
			HttpServletResponse response) {
		String friendlyMessage = "The entityBody of the request isn't valid.";
		String debugMessage = "The request specifies an interaction model that isn't valid in this context.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("xx handleInvalidInteractionModel() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);
		errorObject.setEntityBodyIssue(null, debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	private ResponseEntity<Object> handlePOSTToContainer(Application app, URIObject targetURIObject, Map<String, RDFSource> requestRDFSources,
			ContainerType targetContainerType, HttpServletRequest request, HttpServletResponse response) throws CarbonException {

		Container targetContainer = getTargetContainer(targetURIObject, targetContainerType, app);

		List<RDFSource> childRDFSources = new ArrayList<RDFSource>();
		List<Container> childBasicContainers = new ArrayList<Container>();

		validateAndPopulateChildren(childRDFSources, childBasicContainers, requestRDFSources, targetContainerType, targetContainer);

		DateTime etag = createContainerChildren(childRDFSources, childBasicContainers, targetURIObject, targetContainer, targetContainerType, app);

		setLocationHeaders(response, requestRDFSources);
		response.addHeader(HTTPHeaders.ETAG, HTTPUtil.formatWeakETag(etag.toString()));

		return new ResponseEntity<Object>(new EmptyResponse(), HttpStatus.CREATED);
	}

	private DateTime createContainerChildren(List<RDFSource> childRDFSources, List<Container> childBasicContainers, URIObject targetURIObject,
			Container targetContainer, ContainerType targetContainerType, Application app) throws CarbonException {
		try {
			switch (targetContainerType) {
				case BASIC:
					return basicContainerService.createChildren(childRDFSources, childBasicContainers, targetURIObject, targetContainer, app.getDatasetName());
				case DIRECT:
					return directContainerService.createChildren(childRDFSources, childBasicContainers, targetURIObject, targetContainer, app.getDatasetName());
				case INDIRECT:
					return indirectContainerService.createChildren(childRDFSources, childBasicContainers, targetURIObject, targetContainer,
							app.getDatasetName());
				default:
					return null;

			}
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void validateAndPopulateChildren(List<RDFSource> childRDFSources, List<Container> childBasicContainers, Map<String, RDFSource> requestRDFSources,
			ContainerType targetContainerType, Container targetContainer) throws CarbonException {
		ContainerFactory containerFactory = new ContainerFactory();
		Iterator<RDFSource> iterator = requestRDFSources.values().iterator();
		while (iterator.hasNext()) {
			RDFSource requestRDFSource = iterator.next();

			// Check for invalidTypes
			for (Resource invalidType : invalidTypesForContainers) {
				if ( requestRDFSource.isOfType(invalidType) ) {
					String friendlyMessage = "The entityBody of the request isn't valid.";
					String debugMessage = MessageFormat.format("The request targets a Container and Containers can't create resources of type: ''{0}''.",
							invalidType.getURI());

					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx handleInvalidInteractionModel() > {}", debugMessage);
					}

					ErrorResponseFactory errorFactory = new ErrorResponseFactory();
					ErrorResponse errorObject = errorFactory.create();
					errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
					errorObject.setFriendlyMessage(friendlyMessage);
					errorObject.setDebugMessage(debugMessage);
					errorObject.setEntityBodyIssue(null, debugMessage);

					throw new CarbonException(errorObject);
				}
			}

			if ( requestRDFSource.isOfType(ContainerClass.ContainerType.BASIC.getResource()) ) {
				try {
					requestContainer = containerFactory.create(requestRDFSource);
				} catch (CarbonException e) {
					e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
					throw e;
				}
				validateContainer(requestContainer);
				childBasicContainers.add(requestContainer);
			} else {
				// validateRDFSource(rdfSource);
				childRDFSources.add(requestRDFSource);
			}

			if ( targetContainerType == ContainerType.INDIRECT ) {
				validateIndirectContainerChild(targetContainer, requestRDFSource);
			}
		}
	}

	private Container getTargetContainer(URIObject targetURIObject, ContainerType targetContainerType, Application app) throws CarbonException {
		List<RetrieveContainerPreference> preferences = Arrays.asList(RetrieveContainerPreference.CONTAINER_PROPERTIES);

		try {
			switch (targetContainerType) {
				case BASIC:
					return basicContainerService.get(targetURIObject, preferences, app.getDatasetName());
				case DIRECT:
					return directContainerService.get(targetURIObject, preferences, app.getDatasetName());
				case INDIRECT:
					return indirectContainerService.get(targetURIObject, preferences, app.getDatasetName());
				default:
					return null;

			}
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private ResponseEntity<Object> handlePOSTToRDFSource(Application app, URIObject targetURIObject, Map<String, RDFSource> requestRDFSources,
			HttpServletRequest request, HttpServletResponse response) throws CarbonException {

		List<Container> requestAccessPoints = getRequestAccessPoints(requestRDFSources, targetURIObject);

		DateTime etag = null;
		try {
			etag = rdfSourceService.createAccessPoints(requestAccessPoints, targetURIObject, app.getDatasetName());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		setLocationHeaders(response, requestRDFSources);
		response.addHeader(HTTPHeaders.ETAG, HTTPUtil.formatWeakETag(etag.toString()));

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handlePOSTToRDFSource() > The resource was created and it was inserted into a container.");
		}

		return new ResponseEntity<Object>(new EmptyResponse(), HttpStatus.CREATED);
	}

	private List<Container> getRequestAccessPoints(Map<String, RDFSource> requestRDFSources, URIObject targetURIObject) throws CarbonException {
		List<Container> requestAccessPoints = new ArrayList<Container>();
		ContainerFactory containerFactory = new ContainerFactory();
		Iterator<RDFSource> iterator = requestRDFSources.values().iterator();
		while (iterator.hasNext()) {
			RDFSource rdfSource = iterator.next();
			// Check for invalidTypes
			for (Resource invalidType : invalidTypesForRDFSources) {
				if ( rdfSource.isOfType(invalidType) ) {
					String friendlyMessage = "The entityBody of the request isn't valid.";
					String debugMessage = MessageFormat.format("The request targets an RDFSource and RDFSources can't create resources of type: {0}.",
							invalidType.getURI());

					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx handleInvalidInteractionModel() > {}", debugMessage);
					}

					ErrorResponseFactory errorFactory = new ErrorResponseFactory();
					ErrorResponse errorObject = errorFactory.create();
					errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
					errorObject.setFriendlyMessage(friendlyMessage);
					errorObject.setDebugMessage(debugMessage);
					errorObject.setEntityBodyIssue(null, debugMessage);

					throw new CarbonException(errorObject);
				}
			}

			Container requestContainer = null;
			if ( rdfSource.isOfType(ContainerClass.ContainerType.DIRECT.getResource())
					|| rdfSource.isOfType(ContainerClass.ContainerType.INDIRECT.getResource()) ) {
				try {
					requestContainer = containerFactory.create(rdfSource);
				} catch (CarbonException e) {
					e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
					throw e;
				}

				validateContainer(requestContainer);
				validateAccessPoint(targetURIObject, requestContainer);
			} else {
				// The request is of an irrelevant type and thus it is treated as an RDFSource
				String friendlyMessage = "The body of the request is not valid.";
				String debugMessage = "One of the resource posted is not an AccessPoint (Direct/Indirect container) and it was posted to an RDFSource. RDFSources only support creating AccessPoints for themselves, not direct childs.";

				if ( LOG.isDebugEnabled() ) {
					LOG.debug("<< checkRequestLDPRSourceIsContainer() > {}", debugMessage);
				}

				ErrorResponseFactory factory = new ErrorResponseFactory();
				ErrorResponse errorObject = factory.create();
				errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
				errorObject.setFriendlyMessage(friendlyMessage);
				errorObject.setDebugMessage(debugMessage);
				errorObject.setEntityBodyIssue(null, debugMessage);

				throw new CarbonException(errorObject);
			}

			requestAccessPoints.add(requestContainer);
		}

		return requestAccessPoints;
	}

	private ResponseEntity<Object> handleRequestWithNoDocumentResources(Application app, URIObject targetURIObject, Resource[] documentResources,
			Model requestModel, HttpServletRequest request, HttpServletResponse response) throws CarbonException {

		String friendlyMessage = "The entityBody of the request isn't valid.";
		String debugMessage = "The request entityBody doesn't contain a document resource.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("xx handleRequestWithNoDocumentResources() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);
		errorObject.setEntityBodyIssue(null, debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);

	}

	private Map<String, RDFSource> getRequestRDFSources(URIObject targetURIObject, Resource[] documentResources, Model requestModel, HttpServletRequest request)
			throws CarbonException {
		Map<String, RDFSource> requestRDFSources = new HashMap<String, RDFSource>();

		RDFSourceFactory factory = new RDFSourceFactory();

		for (int i = 0; i < documentResources.length; i++) {
			Resource documentResource = documentResources[i];
			String originalURI = documentResource.getURI();

			documentResource = processDocumentResource(targetURIObject, documentResource, requestModel, request);
			Model documentModel = generateDocumentModel(documentResource, requestModel);
			documentResource = documentModel.getResource(documentResource.getURI());

			RDFSource requestRDFSource = null;
			try {
				requestRDFSource = factory.create(documentResource);
			} catch (CarbonException e) {
				e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
				throw e;
			}

			requestRDFSources.put(originalURI, requestRDFSource);
		}

		return requestRDFSources;
	}

	private boolean targetResourceIsContainer(ContainerType targetContainerType) {
		return targetContainerType != null;
	}

	private ResponseEntity<Object> handleRequestWithExternalResources(Resource[] externalResources, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		String friendlyMessage = "The entityBody of the request isn't valid.";
		String debugMessage = "The request entityBody contains inline resources that don't belong to any of the documentResources in the request.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("xx handleRequestWithExternalResources() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);
		errorObject.setEntityBodyIssue(null, debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	private Resource processDocumentResource(URIObject targetURIObject, Resource documentResource, Model requestModel, HttpServletRequest request)
			throws CarbonException {
		if ( HTTPUtil.isGenericRequestURI(documentResource.getURI()) ) {
			return processResourceWithGenericRequestURI(targetURIObject, documentResource, requestModel, request);
		} else {
			return processResourceWithDefinedURI(targetURIObject, documentResource, requestModel, request);
		}
	}

	private Resource processResourceWithGenericRequestURI(URIObject targetURIObject, Resource documentResource, Model requestModel, HttpServletRequest request)
			throws CarbonException {
		Resource[] inlineResources = getInlineResourcesOf(documentResource, requestModel);

		// Forge a URI for the Document Resource and rename it
		String forgedURI = forgeDocumentResourceURI(documentResource, targetURIObject, request);
		documentResource = ModelUtil.renameResource(documentResource, forgedURI, requestModel);

		// Rename the inlineResources
		for (int i = 0; i < inlineResources.length; i++) {
			Resource inlineResource = inlineResources[i];
			String newInlineResourceURI = forgeInlineResourceURI(inlineResource, forgedURI);
			inlineResources[i] = ModelUtil.renameResource(inlineResource, newInlineResourceURI, requestModel);
		}

		return documentResource;
	}

	private Resource processResourceWithDefinedURI(URIObject targetURIObject, Resource documentResource, Model requestModel, HttpServletRequest request)
			throws CarbonException {
		if ( ! HTTPUtil.isImmediateChildURI(documentResource.getURI(), targetURIObject.getURI()) ) {
			String friendlyMessage = "The entityBody of the request isn't valid.";
			String debugMessage = "The request entityBody contains a resource that isn't an immediate child of the target resource.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx processResourceWithDefinedURI() > {}", debugMessage);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, debugMessage);

			throw new CarbonException(errorObject);
		}

		return documentResource;
	}

	private String forgeDocumentResourceURI(Resource documentResource, URIObject targetURIObject, HttpServletRequest request) {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append(targetURIObject.getURI());

		if ( ! targetURIObject.getURI().endsWith("/") ) {
			uriBuilder.append("/");
		}

		uriBuilder.append(forgeSlug(documentResource, targetURIObject, request));

		return uriBuilder.toString();
	}

	private String forgeSlug(Resource documentResource, URIObject targetURIObject, HttpServletRequest request) {
		String slug = request.getHeader(HTTPHeaders.SLUG);
		if ( slug != null ) {
			slug = slug.endsWith("/") ? HTTPUtil.createSlug(slug).concat("/") : HTTPUtil.createSlug(slug);
		}

		DateTime now = DateTime.now();
		return String.valueOf(now.getMillis());
	}

	private String forgeInlineResourceURI(Resource inlineResource, String documentResourceURI) {
		String localSlug = HTTPUtil.getLocalSlug(inlineResource.getURI());
		StringBuilder uriBuilder = new StringBuilder();
		//@formatter:off
		uriBuilder
			.append(documentResourceURI)
			.append(Carbon.EXTENDING_RESOURCE_SIGN)
			.append(localSlug)
		;
		//@formatter:on
		return uriBuilder.toString();
	}

	private Model generateDocumentModel(Resource documentResource, Model requestModel) {
		Model documentModel = ModelFactory.createDefaultModel();

		List<Statement> statements = new ArrayList<Statement>();
		// Add statements of the documentResource
		StmtIterator docResIterator = documentResource.listProperties();
		while (docResIterator.hasNext()) {
			statements.add(docResIterator.next());
		}

		// Add statements of the inlineResources
		Resource[] inlineResources = getInlineResourcesOf(documentResource, requestModel);
		for (Resource inlineResource : inlineResources) {
			StmtIterator inResIterator = inlineResource.listProperties();
			while (inResIterator.hasNext()) {
				statements.add(inResIterator.next());
			}
		}

		documentModel.add(statements);

		return documentModel;
	}

	private ContainerType getTargetContainerType(Application app, URIObject targetURIObject) throws CarbonException {
		try {
			return ldpService.getDocumentContainerType(targetURIObject, app.getDatasetName());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private InteractionModel getDefaultInteractionModel(URIObject targetURIObject, Application app) throws CarbonException {
		try {
			return ldpService.getDefaultInteractionModel(targetURIObject, app.getDatasetName());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void validateContainer(RDFSource rdfSource) throws CarbonException {
		ContainerFactory containerFactory = new ContainerFactory();
		List<String> containerViolations = containerFactory.validateLDPContainer(rdfSource);
		if ( ! containerViolations.isEmpty() ) {
			StringBuilder violationsBuilder = new StringBuilder();
			violationsBuilder.append("The container isn't valid. Violations:");
			for (String violation : containerViolations) {
				violationsBuilder.append("\n\t").append(violation);
			}

			String violations = violationsBuilder.toString();

			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The entity body contains an invalid container.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< runLDPContainerChecks() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, violations);

			throw new CarbonException(errorObject);
		}
	}

	private void validateIndirectContainerChild(Container targetContainer, RDFSource requestRDFSource) throws CarbonException {
		String icrURI = targetContainer.getInsertedContentRelation();

		if ( icrURI == null ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx validateIndirectContainerChild > The container: '{}', doesn't have an ICR.", targetContainer.getURI());
			}
		}

		Property icrPredicate = ResourceFactory.createProperty(icrURI);
		Statement membershipObjectStatement = requestRDFSource.getResource().getProperty(icrPredicate);

		if ( membershipObjectStatement == null ) {
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = MessageFormat.format("The resource doesn''t contain the property ''{0}'' that the indirect container demands.",
					targetContainer.getInsertedContentRelation());

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateIndirectContainerChild() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, debugMessage);

			throw new CarbonException(errorObject);
		}
		if ( ! membershipObjectStatement.getObject().isURIResource() ) {
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = MessageFormat.format("The resource's property ''{0}'' isn't a valid URI node.", targetContainer.getInsertedContentRelation());

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateIndirectContainerChild() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, debugMessage);

			throw new CarbonException(errorObject);
		}
	}

	private void validateAccessPoint(URIObject targetURIObject, Container requestContainer) throws CarbonException {
		String membershipResource = requestContainer.getMembershipResourceURI();
		if ( ! membershipResource.equals(targetURIObject.getURI()) ) {
			String friendlyMessage = "The entityBody of the request isn't valid.";
			String debugMessage = "The request entityBody contains an AccessPoint that doesn't belong to the request URI.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx validateAccessPoint() > {}", debugMessage);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, debugMessage);

			throw new CarbonException(errorObject);
		}
	}

	private void setLocationHeaders(HttpServletResponse response, Map<String, RDFSource> requestRDFSources) {
		HttpHeader locationHeaders = new HttpHeader();
		if ( requestRDFSources.size() == 1 ) {
			RDFSource requestRDFSource = requestRDFSources.values().iterator().next();

			HttpHeaderValue locationHeader = new HttpHeaderValue(false);
			locationHeader.setMainValue(requestRDFSource.getURI());
			locationHeaders.addHeaderValue(locationHeader);
		} else {
			for (String originalURI : requestRDFSources.keySet()) {
				RDFSource requestRDFSource = requestRDFSources.get(originalURI);

				HttpHeaderValue locationHeader = new HttpHeaderValue();
				locationHeader.setMainKey(HTTPUtil.getURISlug(originalURI));
				locationHeader.setMainValue(requestRDFSource.getURI());
				locationHeaders.addHeaderValue(locationHeader);
			}
		}
		response.setHeader(HTTPHeaders.LOCATION, locationHeaders.toString());
	}
}
