package com.base22.carbon.ldp.web;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import com.base22.carbon.CarbonException;
import com.base22.carbon.HttpHeader;
import com.base22.carbon.HttpHeaders;
import com.base22.carbon.HttpUtil;
import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.apps.Application;
import com.base22.carbon.ldp.LDPC;
import com.base22.carbon.ldp.LDPContainer;
import com.base22.carbon.ldp.LDPContainerFactory;
import com.base22.carbon.ldp.LDPContainerQueryOptions;
import com.base22.carbon.ldp.RdfUtil;
import com.base22.carbon.ldp.LDPC.ContainerType;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class POSTRdfRequestHandler extends AbstractCreationRequestHandler {
	private String slugHeader;

	private InteractionModel interactionModel;

	private String jenaDefaultBase;

	private boolean addABase;
	private boolean slugCreated;
	private ContainerType targetContainerType;

	private LDPContainer targetContainer;

	public ResponseEntity<Object> handleRdfPOST(@PathVariable("application") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> entity) throws CarbonException {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleNonMultipartPost()");
		}

		resetGlobalVariables();

		this.request = request;
		this.response = response;
		this.targetURI = HttpUtil.getRequestURL(request);

		Application application = getApplicationFromContext();
		this.dataset = application.getDatasetName();

		try {
			populateLanguage();
			populateSlug();

			populateTargetURIObject();

			populateEntityBody(entity);
			addDefaultPrefixes();
			saveEntityBodyInString();

			// Initial entity body parse to see if it is valid
			parseEntityBody(null);

			populateJenaDefaultBase();

			processEntityBodyModel();
			if ( addABase ) {
				resetEntityBodyInputStream();

				if ( slugCreated ) {
					parseEntityBody(this.requestURI);
				} else {
					String baseURI = this.targetURI.endsWith("/") ? this.targetURI : this.targetURI.concat("/");
					parseEntityBody(baseURI);
				}
			}

			// ModelUtil.removeServerManagedProperties(requestModel);

			checkIfRequestTargetsExistingResource();

			populateInteractionModel();

			populateTargetContainerType();

		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}

		if ( this.targetContainerType == null ) {
			// It was posted to an LDPRSource
			return handlePOSTToLDPRSource();
		} else {
			if ( interactionModel == InteractionModel.RDF_SOURCE ) {
				return handlePOSTToLDPRSource();
			}
			return handlePOSTToContainer();
		}
	}

	private ResponseEntity<Object> handlePOSTToLDPRSource() {
		try {
			populateTargetRDFSource();

			populateRequestURIObject();
			populateRequestRDFSource();
			this.requestRDFSource.setTimestamps();

			checkRequestLDPRSourceIsContainer();

			runLDPContainerChecks();
			populateRequestContainer();

			checkRequestLDPContainerIsAccessPoint();

			if ( requestDocumentExists() ) {
				return handlePOSTExistingToRDFSource();
			} else {
				return handlePOSTNonExistingToRDFSource();
			}

		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}

	private ResponseEntity<Object> handlePOSTExistingToRDFSource() {
		String friendlyMessage = "The body of the request is not valid.";
		String debugMessage = "The request's entity body contains a resource with the same URI as the request URI. Remember POST to parent, PUT to me.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< processEntityBodyModel() > {}", debugMessage);
		}

		ErrorResponseFactory factory = new ErrorResponseFactory();
		ErrorResponse errorObject = factory.create();
		errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);
		errorObject.setEntityBodyIssue(null, debugMessage);

		return HttpUtil.createErrorResponseEntity(errorObject);
	}

	private ResponseEntity<Object> handlePOSTNonExistingToRDFSource() {
		try {
			createAccessPoint();
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}

		response.addHeader(HttpHeaders.LOCATION, requestURI);
		response.addHeader(HttpHeaders.ETAG, HttpUtil.formatWeakETag(this.requestContainer.getETag()));
		for (String type : this.requestContainer.getLinkTypes()) {
			response.addHeader(HttpHeaders.LINK, type);
		}

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handlePOSTExistingToRDFSource() > An accesspoint was created for the resource: '{}'.", requestURI);
		}
		return new ResponseEntity<Object>(HttpStatus.CREATED);
	}

	private ResponseEntity<Object> handlePOSTToContainer() {
		try {
			populateTargetContainer();
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}

		InteractionModel dim = targetContainer.getDefaultInteractionModel();
		if ( dim != null ) {
			if ( dim.equals(InteractionModel.RDF_SOURCE) ) {
				return handlePOSTToLDPRSource();
			}
		}

		try {
			populateRequestURIObject();

			if ( ! requestDocumentExists() ) {
				populateRequestRDFSource();
				this.requestRDFSource.setTimestamps();

				if ( this.targetContainerType == ContainerType.INDIRECT ) {
					checkLDPRSourceForIndirect();
				}

				// Inverse Membership Property
				addIMPToNonExistingIfNeeded();

				if ( getLDPContainerFactory().isContainer(this.requestRDFSource) ) {
					runLDPContainerChecks();
					populateRequestContainer();

					createChildLDPContainer();
				} else {
					createChildLDPRSource();
				}

				addContainmentAndMembershipTriples();
			} else {
				return handlePOSTExistingResourceToContainer();
			}
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}

		response.addHeader(HttpHeaders.LOCATION, requestURI);
		response.addHeader(HttpHeaders.ETAG, HttpUtil.formatWeakETag(this.requestRDFSource.getETag()));
		for (String type : this.requestRDFSource.getLinkTypes()) {
			response.addHeader(HttpHeaders.LINK, type);
		}

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handlePOSTToContainer() > The resource was created and it was inserted into a container.");
		}
		return new ResponseEntity<Object>(HttpStatus.CREATED);
	}

	private ResponseEntity<Object> handlePOSTExistingResourceToContainer() {
		// TODO: Define this process a little bit better
		try {
			checkIfAlreadyMember();

			addMembershipTriples();

			// Inverse MembershipTriples
			addIMPToExistingIfNeeded();
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	protected void resetGlobalVariables() {
		super.resetGlobalVariables();

		this.slugHeader = null;

		this.interactionModel = null;

		this.jenaDefaultBase = null;

		this.addABase = false;
		this.slugCreated = false;

		this.targetContainerType = null;

		this.targetContainer = null;
	}

	private void populateSlug() throws CarbonException {
		this.slugHeader = request.getHeader(HttpHeaders.SLUG);
		if ( this.slugHeader != null ) {
			this.slugHeader = HttpUtil.createSlug(slugHeader);
		}
	}

	private void populateInteractionModel() throws CarbonException {
		Enumeration<String> linkHeaders = request.getHeaders(HttpHeaders.LINK);
		HttpHeader linkHeader = new HttpHeader(linkHeaders);

		this.interactionModel = getInteractionModel(linkHeader);
	}

	// TODO: This could be substituted by a constant when it is in a stable environment
	private void populateJenaDefaultBase() throws CarbonException {
		if ( this.jenaDefaultBase == null ) {
			try {
				this.jenaDefaultBase = RdfUtil.retrieveJenaDefaultBase();
			} catch (IOException e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx populateJenaDefaultBase() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< populateJenaDefaultBase() > There was a problem while trying to get the JenaDefaultBase.");
				}

				String friendlyMessage = "Unexpected Server Error.";
				String debugMessage = "An unexpected problem related with an InputStream rised when parsing the entity body.";

				ErrorResponseFactory factory = new ErrorResponseFactory();
				ErrorResponse errorObject = factory.create();
				errorObject.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
				errorObject.setFriendlyMessage(friendlyMessage);
				errorObject.setDebugMessage(debugMessage);

				throw new CarbonException(errorObject);
			}
		}
	}

	// TODO: Break this
	private void processEntityBodyModel() throws CarbonException {
		String resourceURI = null;
		String baseURI = this.targetURI;
		baseURI = baseURI.endsWith("/") ? baseURI : baseURI.concat("/");

		ResIterator resourceIterator = this.requestModel.listSubjects();
		while (resourceIterator.hasNext()) {
			Resource resource = resourceIterator.next();
			resourceURI = resource.getURI();

			// Check if the URI is relative
			if ( resourceURI.startsWith(jenaDefaultBase) ) {
				// Relative URI
				addABase = true;
				// Remove the defaultBase
				resourceURI = resourceURI.replace(jenaDefaultBase, "");
				// Check if is a document resource (independently resolvable)
				if ( ! resourceURI.matches("#(?:.+)") ) {
					// It is
					// Check if another documentURI was already found
					if ( requestURI != null ) {
						// Another document resource is present
						String friendlyMessage = "The body of the request is not valid.";
						String debugMessage = "The request's entity body contains multiple document resources.";
						String entityBodyMessage = "Multiple document resources.";

						if ( LOG.isDebugEnabled() ) {
							LOG.debug("<< processEntityBodyModel() > {}", debugMessage);
						}

						ErrorResponseFactory factory = new ErrorResponseFactory();
						ErrorResponse errorObject = factory.create();
						errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
						errorObject.setFriendlyMessage(friendlyMessage);
						errorObject.setDebugMessage(debugMessage);
						errorObject.setEntityBodyIssue(null, entityBodyMessage);

						throw new CarbonException(errorObject);
					}
					// Check if it is a null URI
					if ( resourceURI.trim().length() == 0 ) {
						// It is, a Slug needs to be created
						// Check if a Slug was specified in the header
						if ( this.slugHeader == null ) {
							// It wasn't
							// Create a slug
							// TODO: Create a function to give priority and use different properties
							DateTime now = DateTime.now();
							this.slugHeader = String.valueOf(now.getMillis());
						}
						this.slugCreated = true;
					} else {
						// Remove possible / sign
						resourceURI = resourceURI.startsWith("/") ? resourceURI.substring(0, resourceURI.length() - 1) : resourceURI;
						// Check if the resourceURI is only one level below
						if ( resourceURI.matches("(?:.+)/(?:.+)") ) {
							// It isn't
							String friendlyMessage = "The body of the request is not valid.";
							String debugMessage = "The document resource URI must be just a level below the request URI.";
							String entityBodyMessage = MessageFormat.format(
									"The document resource with URI: ''{0}'', isn't a direct child of the request URI.", resourceURI);

							if ( LOG.isDebugEnabled() ) {
								LOG.debug("<< processEntityBodyModel() > {}", debugMessage);
							}

							ErrorResponseFactory factory = new ErrorResponseFactory();
							ErrorResponse errorObject = factory.create();
							errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
							errorObject.setFriendlyMessage(friendlyMessage);
							errorObject.setDebugMessage(debugMessage);
							errorObject.setEntityBodyIssue(null, entityBodyMessage);

							throw new CarbonException(errorObject);
						}
						this.slugHeader = resourceURI;
					}
					// Append the slug to form the new documentURI
					this.requestURI = this.targetURI.endsWith("/") ? this.targetURI.concat(this.slugHeader) : this.targetURI.concat("/").concat(slugHeader);
				} else {
					// TODO: Check secondary resource URI
				}

			} else {
				// Non relative URI
				// Check if it is a valid URL
				if ( ! HttpUtil.isValidURL(resourceURI) ) {
					String friendlyMessage = "The body of the request is not valid.";
					String debugMessage = MessageFormat.format("The resourceURI: ''{0}'', isn't a valid URL.", resourceURI);

					if ( LOG.isDebugEnabled() ) {
						LOG.debug("<< processEntityBodyModel() > {}", debugMessage);
					}

					ErrorResponseFactory factory = new ErrorResponseFactory();
					ErrorResponse errorObject = factory.create();
					errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
					errorObject.setFriendlyMessage(friendlyMessage);
					errorObject.setDebugMessage(debugMessage);
					errorObject.setEntityBodyIssue(null, debugMessage);

					throw new CarbonException(errorObject);
				} else {
					// Check if the URI isn't outside of the domain/dataset

					if ( ! resourceURI.startsWith(baseURI) ) {
						// CASE 4b: The requestURI and the resourceURI share the same server but not the dataset
						// CASE 4c: The requestURI and the resourceURI don't share the same server neither the dataset
						String friendlyMessage = "The body of the request is not valid.";
						String debugMessage = "A resource in the entity body doesn't have for a base the request URI.";
						String entityBodyMessage = MessageFormat.format("The resource with URI: ''{0}'' doesn't have for a base the request URI.", resourceURI);

						if ( LOG.isDebugEnabled() ) {
							LOG.debug("<< processEntityBodyModel() > {}", entityBodyMessage);
						}

						ErrorResponseFactory factory = new ErrorResponseFactory();
						ErrorResponse errorObject = factory.create();
						errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
						errorObject.setFriendlyMessage(friendlyMessage);
						errorObject.setDebugMessage(debugMessage);
						errorObject.setEntityBodyIssue(null, entityBodyMessage);

						throw new CarbonException(errorObject);
					}
					// Check if the resourceURI is only one level below
					if ( resourceURI.replace(baseURI, "").matches("(?:.+)/(?:.+)") ) {
						// It isn't
						String friendlyMessage = "The body of the request is not valid.";
						String debugMessage = "The document resource URI must be just a level below the request URI.";
						String entityBodyMessage = MessageFormat.format("The document resource with URI: ''{0}'', isn't a direct child of the request URI.",
								resourceURI);

						if ( LOG.isDebugEnabled() ) {
							LOG.debug("<< processEntityBodyModel() > {}", debugMessage);
						}

						ErrorResponseFactory factory = new ErrorResponseFactory();
						ErrorResponse errorObject = factory.create();
						errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
						errorObject.setFriendlyMessage(friendlyMessage);
						errorObject.setDebugMessage(debugMessage);
						errorObject.setEntityBodyIssue(null, entityBodyMessage);

						throw new CarbonException(errorObject);
					}
				}
				// Check if it is a document resource (independently resolvable)
				if ( ! resourceURI.matches("(?:.+)#(?:.+)") ) {
					// It is
					if ( this.requestURI != null ) {
						// Another document resource is present
						String friendlyMessage = "The body of the request is not valid.";
						String debugMessage = "The request's entity body contains multiple document resources.";
						String entityBodyMessage = "Multiple document resources.";

						if ( LOG.isDebugEnabled() ) {
							LOG.debug("<< processEntityBodyModel() > {}", debugMessage);
						}

						ErrorResponseFactory factory = new ErrorResponseFactory();
						ErrorResponse errorObject = factory.create();
						errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
						errorObject.setFriendlyMessage(friendlyMessage);
						errorObject.setDebugMessage(debugMessage);
						errorObject.setEntityBodyIssue(null, entityBodyMessage);

						throw new CarbonException(errorObject);
					}
					// Check if the document resourceURI is the same as the requestURI
					if ( resourceURI.equals(targetURI) ) {
						String friendlyMessage = "The body of the request is not valid.";
						String debugMessage = "The request's entity body contains a resource with the same URI as the request URI. Remember POST to parent, PUT to me.";

						if ( LOG.isDebugEnabled() ) {
							LOG.debug("<< processEntityBodyModel() > {}", debugMessage);
						}

						ErrorResponseFactory factory = new ErrorResponseFactory();
						ErrorResponse errorObject = factory.create();
						errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
						errorObject.setFriendlyMessage(friendlyMessage);
						errorObject.setDebugMessage(debugMessage);
						errorObject.setEntityBodyIssue(null, debugMessage);

						throw new CarbonException(errorObject);
					}
					this.requestURI = resourceURI;
				}
			}
		}
		// Check if a document resource was present
		if ( this.requestURI == null ) {
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The request's entity body doesn't contain a document resource.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< processEntityBodyModel() > {}", debugMessage);
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

	private void checkIfRequestTargetsExistingResource() throws CarbonException {
		boolean requestResourceExists = false;

		try {
			requestResourceExists = rdfService.namedModelExists(targetURI, dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		if ( ! requestResourceExists ) {
			String friendlyMessage = "The resource doesn't exist.";
			String debugMessage = "The request URI doesn't point to an existing resource.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< checkIfRequestTargetsExistingResource() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);

			throw new CarbonException(errorObject);
		}
	}

	private void populateTargetContainerType() throws CarbonException {
		try {
			this.targetContainerType = ldpService.getDocumentContainerType(this.targetURIObject, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void populateTargetContainer() throws CarbonException {
		// Build LDPContainerQueryOptions to get the container
		LDPContainerQueryOptions options = new LDPContainerQueryOptions(LDPContainerQueryOptions.METHOD.GET);
		options.setContainerProperties(true);
		options.setContainmentTriples(false);
		options.setMembershipTriples(false);
		options.setContainedResources(false);
		options.setMemberResources(false);

		// Get the LDPContainer
		this.targetContainer = null;
		try {
			this.targetContainer = ldpService.getLDPContainer(this.targetURIObject, dataset, targetContainerType.getURI(), options);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	public void populateRequestURIObject() throws CarbonException {
		try {
			this.requestURIObject = uriObjectDAO.findByURI(this.requestURI);
		} catch (AccessDeniedException e) {
			// TODO: FT - Log it? -
			String friendlyMessage = "The request resource can't be handled with that URI.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< populateURIObject() > The authenticated agent doesn't have DISCOVER access to the resource with URI: {}", this.targetURI);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);

			throw new CarbonException(errorObject);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	public boolean requestDocumentExists() {
		return this.requestURIObject != null;
	}

	private void checkRequestLDPRSourceIsContainer() throws CarbonException {
		if ( ! getLDPContainerFactory().isContainer(this.requestRDFSource) ) {
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The resource posted is not a container and it was posted to an LDPRSource. LDPRSources only support creating Access Points for themselves, not direct childs.";

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
	}

	private void checkRequestLDPContainerIsAccessPoint() throws CarbonException {
		if ( ! (this.requestContainer.isOfType(LDPC.DIRECT) || this.requestContainer.isOfType(LDPC.INDIRECT)) ) {
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The resource posted is not an access point (Direct or Indirect container).";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< checkRequestLDPContainerIsAccessPoint() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, debugMessage);

			throw new CarbonException(errorObject);
		}
		if ( ! this.requestContainer.getMembershipResourceURI().equals(this.targetURI) ) {
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The resource posted doesn't have for a membershipResource the resource with the request URI.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< checkRequestLDPContainerIsAccessPoint() > {}", debugMessage);
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

	private void runLDPContainerChecks() throws CarbonException {
		LDPContainerFactory ldpContainerFactory = new LDPContainerFactory();
		List<String> containerViolations = ldpContainerFactory.validateLDPContainer(this.requestRDFSource);
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

	private void addIMPToNonExistingIfNeeded() {
		String memberOfRelationString = this.targetContainer.getMemberOfRelation();
		if ( memberOfRelationString != null ) {
			Property memberOfRelationProperty = ResourceFactory.createProperty(memberOfRelationString);
			this.requestRDFSource.getResource().addProperty(memberOfRelationProperty, this.targetContainer.getResource());
		}
	}

	private void addIMPToExistingIfNeeded() throws CarbonException {
		if ( this.targetContainer.getMemberOfRelation() != null ) {
			try {
				ldpService.addInverseMembershipTriple(this.targetContainer, this.requestURI, this.dataset);
			} catch (CarbonException e) {
				e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
				throw e;
			}
		}
	}

	private void addContainmentAndMembershipTriples() throws CarbonException {
		// Add containment and membership triples to the container
		try {
			ldpService.addDocumentAsContainment(this.targetContainer, this.requestRDFSource, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void addMembershipTriples() throws CarbonException {
		// Add membership triples
		try {
			ldpService.addDocumentAsMember(this.targetContainer, this.requestRDFSource, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void createAccessPoint() throws CarbonException {
		try {
			this.requestURIObject = ldpService.createAccessPoint(this.requestContainer, this.targetURIObject, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void createChildLDPRSource() throws CarbonException {
		try {
			ldpService.createChildLDPRSource(this.requestRDFSource, this.targetURIObject, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void createChildLDPContainer() throws CarbonException {
		try {
			ldpService.createChildLDPContainer(this.requestContainer, this.targetURIObject, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void checkIfAlreadyMember() throws CarbonException {
		boolean alreadyMember = false;

		try {
			alreadyMember = ldpService
					.resourceIsMemberOfContainer(this.targetURIObject, this.requestURIObject, this.dataset, this.targetContainerType.getURI());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		if ( alreadyMember ) {
			String friendlyMessage = "The resource was already a member.";
			String debugMessage = MessageFormat.format("The resource with URI: ''{}'', is already a member of this container.", this.requestURI);

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< checkIfAlreadyMember() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);

			throw new CarbonException(errorObject);
		}
	}

	private void checkLDPRSourceForIndirect() throws CarbonException {
		Property icrPredicate = ResourceFactory.createProperty(this.targetContainer.getInsertedContentRelation());
		Statement membershipObjectStatement = this.requestRDFSource.getResource().getProperty(icrPredicate);

		if ( membershipObjectStatement == null ) {
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = MessageFormat.format("The resource doesn''t contain the property ''{0}'' that the indirect container demands.",
					this.targetContainer.getInsertedContentRelation());

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< checkLDPRSourceForIndirect() > {}", debugMessage);
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
			String debugMessage = MessageFormat.format("The resource's property ''{0}'' isn't a valid URI node.",
					this.targetContainer.getInsertedContentRelation());

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< checkLDPRSourceForIndirect() > {}", debugMessage);
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
}
