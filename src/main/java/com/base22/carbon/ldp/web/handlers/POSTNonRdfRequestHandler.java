package com.base22.carbon.ldp.web.handlers;

import java.nio.charset.UnsupportedCharsetException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.ParseException;
import org.apache.http.entity.ContentType;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.base22.carbon.CarbonException;
import com.base22.carbon.HttpHeaders;
import com.base22.carbon.ldp.LDPC.ContainerType;
import com.base22.carbon.ldp.models.LDPContainer;
import com.base22.carbon.ldp.models.LDPContainerQueryOptions;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.ldp.models.WrapperForLDPNR;
import com.base22.carbon.ldp.models.WrapperForLDPNRFactory;
import com.base22.carbon.models.BASE64DecodedMultipartFile;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.repository.FileUtil;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class POSTNonRdfRequestHandler extends AbstractLDPRequestHandler {

	public ResponseEntity<Object> handleNonMultipartPOST(String applicationIdentifier, String contentTypeHeader, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> requestEntity) throws CarbonException {

		byte[] fileBody = requestEntity.getBody();
		String fileName = generateFileName(request);
		ContentType contentType = getContentTypeFromHeader(contentTypeHeader);

		// TODO: Care about the charset
		MultipartFile file = new BASE64DecodedMultipartFile(fileName, fileBody, contentType);

		return handleMultipartPost(applicationIdentifier, null, file, request, response, requestEntity);
	}

	private ContentType getContentTypeFromHeader(String contentTypeHeader) throws CarbonException {
		ContentType contentType = null;
		try {
			contentType = ContentType.parse(contentTypeHeader);
		} catch (ParseException e) {
			String friendlyMessage = "The format of the entity body couldn't be recognized.";
			String debugMessage = "The Content-Type couldn't be parsed.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< getContentTypeFromHeader() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.addHeaderIssue("Content-Type", null, "invalid", contentTypeHeader);

			throw new CarbonException(errorObject);
		} catch (UnsupportedCharsetException e) {
			String friendlyMessage = "The format of the entity body isn't supported.";
			String debugMessage = "The specified Content-Type's charset isn't supported.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< getContentTypeFromHeader() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.addHeaderIssue("Content-Type", null, "not supported", contentTypeHeader);

			throw new CarbonException(errorObject);
		}
		return contentType;
	}

	public String generateFileName(HttpServletRequest request) {
		String fileName = null;
		String slugHeader = request.getHeader(HttpHeaders.SLUG);
		if ( slugHeader == null ) {
			// TODO: Enhance this
			DateTime now = DateTime.now();
			fileName = String.valueOf(now.getMillis());
		} else {
			fileName = HTTPUtil.createSlug(slugHeader);
		}
		return fileName;
	}

	public ResponseEntity<Object> handleMultipartPost(String applicationIdentifier, String fileName, MultipartFile file, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> requestEntity) throws CarbonException {

		resetGlobalVariables();
		setGlobalVariables(applicationIdentifier, request, response, requestEntity);

		String targetURI = null;
		URIObject targetURIObject = null;

		targetURI = HTTPUtil.getRequestURL(request);

		validateFileParameter(file);

		targetURIObject = getTargetURIObject(targetURI);

		if ( targetResourceExists(targetURIObject) ) {
			return handleExistingTargetResource(fileName, file, targetURIObject);
		} else {
			return handleNonExistingTargetResource(fileName, file, targetURI);
		}
	}

	public ResponseEntity<Object> handleNonExistingTargetResource(String fileName, MultipartFile file, String targetURI) {
		String friendlyMessage = "The resource doesn't exist.";
		String debugMessage = "The request URI doesn't point to an existing resource.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleNonExistingTargetResource() > {}", debugMessage);
		}

		ErrorResponseFactory factory = new ErrorResponseFactory();
		ErrorResponse errorObject = factory.create();
		errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	public ResponseEntity<Object> handleExistingTargetResource(String fileName, MultipartFile file, URIObject targetURIObject) throws CarbonException {

		ContainerType targetContainerType = null;

		targetContainerType = getTargetContainerType(targetURIObject);

		if ( targetResourceIsContainer(targetContainerType) ) {
			return handlePOSTToContainer(fileName, file, targetURIObject, targetContainerType);
		} else {
			return handlePOSTToNonContainer(fileName, file, targetURIObject, targetContainerType);
		}
	}

	private ResponseEntity<Object> handlePOSTToNonContainer(String fileName, MultipartFile file, URIObject targetURIObject, ContainerType targetContainerType) {
		String friendlyMessage = "The resource doesn't exist.";
		String debugMessage = "The request URI doesn't point to an existing resource.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handlePOSTToNonContainer() > {}", debugMessage);
		}

		ErrorResponseFactory factory = new ErrorResponseFactory();
		ErrorResponse errorObject = factory.create();
		errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	private ResponseEntity<Object> handlePOSTToContainer(String fileName, MultipartFile file, URIObject targetURIObject, ContainerType targetContainerType)
			throws CarbonException {

		LDPContainer targetContainer = null;
		String requestURI = null;
		URIObject requestURIObject = null;

		targetContainer = getTargetContainer(targetURIObject, targetContainerType);

		requestURI = constructRequestURI(targetURIObject, file, fileName);

		requestURIObject = getRequestURIObject(requestURI);

		if ( requestDocumentExists(requestURIObject) ) {
			return handleExistingRequestResource(fileName, file, targetURIObject, targetContainer, requestURIObject);
		} else {
			return handleNonExistingRequestResource(fileName, file, targetURIObject, targetContainer, requestURI);
		}
	}

	private ResponseEntity<Object> handleExistingRequestResource(String fileName, MultipartFile file, URIObject targetURIObject, LDPContainer targetContainer,
			URIObject requestURIObject) throws CarbonException {
		// TODO: NF. Version Control Support

		String friendlyMessage = "A resource with the file URI already exists.";
		String debugMessage = MessageFormat.format("A resource with the URI: ''{0}'', alredy exists.", requestURIObject.getURI());

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleExistingRequestResource() > {}", debugMessage);
		}

		ErrorResponseFactory factory = new ErrorResponseFactory();
		ErrorResponse errorObject = factory.create();
		errorObject.setHttpStatus(HttpStatus.CONFLICT);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	private ResponseEntity<Object> handleNonExistingRequestResource(String fileName, MultipartFile file, URIObject targetURIObject,
			LDPContainer targetContainer, String requestURI) throws CarbonException {

		WrapperForLDPNR requestWrapper = null;
		URIObject requestURIObject = null;

		requestWrapper = createRequestWrapper(fileName, file, requestURI);

		addIfNeededIMP(targetURIObject, targetContainer, requestWrapper);

		requestURIObject = createChildLDPNR(targetURIObject, requestWrapper);

		addContainmentAndMembershipTriples(targetContainer, requestWrapper);

		saveLDPNR(requestURIObject, file, requestWrapper);

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleNonExistingRequestResource() > The resource was created and it was inserted into a container.");
		}
		return new ResponseEntity<Object>(requestWrapper, HttpStatus.CREATED);
	}

	protected void resetGlobalVariables() {
		super.resetGlobalVariables();
	}

	private void validateFileParameter(MultipartFile file) throws CarbonException {
		if ( file == null ) {
			String friendlyMessage = "The request didn't contained the required 'file' parameter.";
			String debugMessage = friendlyMessage;

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateFileParameter() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.addParameterIssue("file", null, "required", null);

			throw new CarbonException(errorObject);
		}

		if ( file.isEmpty() ) {
			String friendlyMessage = "The file received is empty.";
			String debugMessage = friendlyMessage;

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateFileParameter() > {}", debugMessage);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.addParameterIssue("file", null, "Cannot be empty", "empty");

			throw new CarbonException(errorObject);
		}
	}

	protected URIObject getTargetURIObject(String targetURI) throws CarbonException {
		URIObject targetURIObject = null;
		try {
			targetURIObject = uriObjectDAO.findByURI(targetURI);
		} catch (AccessDeniedException e) {
			String friendlyMessage = "A resource with the request URI wasn't found.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< getTargetURIObject() > The authenticated agent doesn't have DISCOVER access to the resource with URI: {}", targetURI);
			}

			ErrorResponseFactory factory = new ErrorResponseFactory();
			ErrorResponse errorObject = factory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setHttpStatus(HttpStatus.NOT_FOUND);

			throw new CarbonException(errorObject);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetURIObject;
	}

	protected boolean targetResourceExists(URIObject targetURIObject) {
		return targetURIObject != null;
	}

	protected ContainerType getTargetContainerType(URIObject targetURIObject) throws CarbonException {
		ContainerType containerType = null;
		try {
			containerType = ldpService.getDocumentContainerType(targetURIObject, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return containerType;
	}

	protected boolean targetResourceIsContainer(ContainerType targetContainerType) {
		return targetContainerType != null;
	}

	protected LDPContainer getTargetContainer(URIObject targetURIObject, ContainerType targetContainerType) throws CarbonException {
		LDPContainer targetContainer = null;
		LDPContainerQueryOptions options = new LDPContainerQueryOptions(LDPContainerQueryOptions.METHOD.GET);
		options.setContainerProperties(true);
		options.setContainmentTriples(false);
		options.setMembershipTriples(false);
		options.setContainedResources(false);
		options.setMemberResources(false);
		// Get the LDPContainer
		try {
			targetContainer = ldpService.getLDPContainer(targetURIObject, dataset, targetContainerType.getURI(), options);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetContainer;
	}

	protected String constructRequestURI(URIObject targetURIObject, MultipartFile file, String fileName) throws CarbonException {
		String requestURI = null;
		String baseURI = targetURIObject.getURI().endsWith("/") ? targetURIObject.getURI() : targetURIObject.getURI().concat("/");

		StringBuilder requestURIStringBuilder = new StringBuilder();
		//@formatter:off
		requestURIStringBuilder
			.append(baseURI)
		;
		
		if (fileName != null) {
			requestURIStringBuilder
			.append(HTTPUtil.createSlug(fileName))
			;
		} else {
			requestURIStringBuilder
			.append(HTTPUtil.createSlug(FileUtil.getFileName(file)))
			;
		}
		
		String fileExtension = FileUtil.getFileExtension(file);
		if(fileExtension != null) {
			if(fileExtension.trim().length() != 0) {
				requestURIStringBuilder
					.append(".")
					.append(HTTPUtil.createSlug(fileExtension))
				;
			}
		}
		//@formatter:on

		requestURI = requestURIStringBuilder.toString();
		return requestURI;
	}

	protected URIObject getRequestURIObject(String requestURI) throws CarbonException {
		URIObject requestURIObject = null;
		try {
			requestURIObject = uriObjectDAO.findByURI(requestURI);
		} catch (AccessDeniedException e) {
			String friendlyMessage = "The request resource can't be handled with that URI.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< getRequestURIObject() > The authenticated agent doesn't have DISCOVER access to the resource with URI: {}", requestURI);
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
		return requestURIObject;
	}

	protected boolean requestDocumentExists(URIObject requestURIObject) {
		return requestURIObject != null;
	}

	protected WrapperForLDPNR createRequestWrapper(String fileName, MultipartFile file, String requestURI) throws CarbonException {
		WrapperForLDPNR requestWrapper = null;

		WrapperForLDPNRFactory factory = new WrapperForLDPNRFactory();
		try {
			requestWrapper = factory.create(requestURI, file);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return requestWrapper;
	}

	// Inverse Membership Properties
	protected void addIfNeededIMP(URIObject targetURIObject, LDPContainer targetContainer, WrapperForLDPNR requestWrapper) throws CarbonException {
		String memberOfRelationString = targetContainer.getMemberOfRelation();
		if ( memberOfRelationString != null ) {
			Property memberOfRelationProperty = ResourceFactory.createProperty(memberOfRelationString);
			requestWrapper.getResource().addProperty(memberOfRelationProperty, targetContainer.getResource());
		}
	}

	protected URIObject createChildLDPNR(URIObject targetURIObject, WrapperForLDPNR requestWrapper) throws CarbonException {
		URIObject requestURIObject = null;
		try {
			requestURIObject = ldpService.createChildLDPNR(requestWrapper, targetURIObject, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return requestURIObject;
	}

	protected void addContainmentAndMembershipTriples(LDPContainer targetContainer, WrapperForLDPNR requestWrapper) throws CarbonException {
		try {
			ldpService.addDocumentAsContainment(targetContainer, requestWrapper, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	protected void saveLDPNR(URIObject requestURIObject, MultipartFile file, WrapperForLDPNR requestWrapper) throws CarbonException {
		try {
			fileService.saveFile(requestURIObject, this.application, file, requestWrapper);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}
}
