package com.carbonldp.ldp.web;

import static com.carbonldp.Consts.SLASH;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.URIImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.descriptions.BasicContainerDescription;
import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.descriptions.DirectContainerDescription;
import com.carbonldp.descriptions.IndirectContainerDescription;
import com.carbonldp.descriptions.RDFNodeEnum;
import com.carbonldp.models.AccessPoint;
import com.carbonldp.models.AccessPointFactory;
import com.carbonldp.models.BasicContainer;
import com.carbonldp.models.BasicContainerFactory;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.models.RDFResource;
import com.carbonldp.models.RDFSource;
import com.carbonldp.utils.HTTPUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.AbstractRequestHandler;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;

public abstract class AbstractPOSTRequestHandler extends AbstractRequestHandler {

	private final static RDFNodeEnum[] invalidTypesForRDFSources;
	static {
		//@formatter:off
		List<? extends RDFNodeEnum> invalidTypes = Arrays.asList(
			BasicContainerDescription.Resource.CLASS
			// TODO: Add LDPNR, NRWRAPPER
		);
		//@formatter:on

		invalidTypesForRDFSources = invalidTypes.toArray(new RDFNodeEnum[invalidTypes.size()]);
	}

	private final static RDFNodeEnum[] invalidTypesForContainers;
	static {
		//@formatter:off
		List<? extends RDFNodeEnum> invalidTypes = Arrays.asList(
			DirectContainerDescription.Resource.CLASS,
			IndirectContainerDescription.Resource.CLASS
			// TODO: Add LDPNR, NRWRAPPER
		);
		//@formatter:on

		invalidTypesForContainers = invalidTypes.toArray(new RDFNodeEnum[invalidTypes.size()]);
	}

	protected void setUp(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.appliedPreferences = new ArrayList<HTTPHeaderValue>();
	}

	@Transactional
	public ResponseEntity<Object> handleRequest(AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response) {
		setUp(request, response);

		URI targetURI = getTargetURI(request);
		if ( ! targetResourceExists(targetURI) ) {
			throw new NotFoundException("The target resource wasn't found.");
		}

		validateRequestModel(requestModel);

		Set<RDFResource> requestDocumentResources = getRequestDocumentResources(requestModel);
		seekForOrphanFragments(requestModel, requestDocumentResources);

		InteractionModel interactionModel = getInteractionModel(targetURI);

		switch (interactionModel) {
			case RDF_SOURCE:
				return handlePOSTToRDFSource(targetURI, requestDocumentResources);
			case CONTAINER:
				return handlePOSTToContainer(targetURI, requestDocumentResources);
			default:
				throw new IllegalStateException();
		}

	}

	private ResponseEntity<Object> handlePOSTToRDFSource(final URI targetURI, Set<RDFResource> requestDocumentResources) {
		Set<AccessPoint> requestAccessPoints = processDocumentResources(requestDocumentResources, new ResourceProcessor<AccessPoint>() {
			@Override
			public AccessPoint processResource(RDFResource resource) {
				for (RDFNodeEnum invalidType : invalidTypesForRDFSources) {
					if ( resource.hasType(invalidType) ) throw new BadRequestException("One of the resources sent in the request contains an invalid type.");
				}
				if ( ! AccessPointFactory.isAccessPoint(resource) ) throw new BadRequestException("RDFSource interaction model can only create AccessPoints.");
				if ( ! AccessPointFactory.isValid(resource, targetURI) ) throw new BadRequestException("An AccessPoint sent isn't valid.");
				// TODO: Check for system managed properties
				return AccessPointFactory.getAccessPoint(resource);
			}
		});

		// TODO: Check URIs
		// TODO: Rename URIs if needed

		// TODO: Implement
		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
	}

	private ResponseEntity<Object> handlePOSTToContainer(URI targetURI, Set<RDFResource> requestDocumentResources) {
		// TODO: Get containerType
		Type targetContainerType;
		Set<BasicContainer> requestBasicContainers = processDocumentResources(requestDocumentResources, new ResourceProcessor<BasicContainer>() {
			@Override
			public BasicContainer processResource(RDFResource resource) {
				for (RDFNodeEnum invalidType : invalidTypesForContainers) {
					if ( resource.hasType(invalidType) ) throw new BadRequestException("One of the resources sent in the request contains an invalid type.");
				}
				if ( BasicContainerFactory.isBasicContainer(resource) ) {
					if ( ! BasicContainerFactory.isValid(resource) ) throw new BadRequestException("A BasicContainer sent isn't valid.");
					// TODO: Check for system managed properties
					return new BasicContainer(resource);
				} else {
					BasicContainer basicContainer = BasicContainerFactory.create(resource);
					basicContainer.setDefaultInteractionModel(InteractionModel.RDF_SOURCE);
					return basicContainer;
				}
			}
		});
		// TODO: Validate requestDocumentResources
		// TODO: Check URIs
		// TODO: Rename URIs if needed
		// TODO: Implement
		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
	}

	protected void validateRequestModel(AbstractModel requestModel) {
		Set<Resource> subjects = requestModel.subjects();
		validateRequestResourcesNumber(subjects.size());

		for (Resource subject : subjects)
			validateRequestResource(subject);
	}

	protected void validateRequestResourcesNumber(int number) {
		if ( number == 0 ) throw new BadRequestException("The request doesn't contain rdf resources.");
	}

	protected void validateRequestResource(Resource subject) {
		if ( ValueUtil.isBNode(subject) ) throw new BadRequestException("Blank nodes are not supported.");
	}

	protected Set<RDFResource> getRequestDocumentResources(AbstractModel requestModel) {
		Set<RDFResource> documentResources = new HashSet<RDFResource>();
		for (Resource subject : requestModel.subjects()) {
			if ( ! ValueUtil.isURI(subject) ) continue;
			URI subjectURI = ValueUtil.getURI(subject);
			if ( URIUtil.hasFragment(subjectURI) ) continue;
			RDFResource documentResource = new RDFResource(requestModel, subjectURI, null);
			documentResources.add(documentResource);
		}
		return documentResources;
	}

	protected <E extends RDFSource> Set<E> processDocumentResources(Set<RDFResource> requestDocumentResources, ResourceProcessor<E> resourceProcessor) {
		validateRequestDocumentResourcesNumber(requestDocumentResources.size());

		Set<E> processedResources = new HashSet<E>();
		for (RDFResource documentResource : requestDocumentResources) {
			processedResources.add(resourceProcessor.processResource(documentResource));
		}
		return processedResources;
	}

	protected void validateRequestDocumentResourcesNumber(int number) {
		if ( number == 0 ) throw new BadRequestException("The request doesn't contain ");
	}

	// TODO: Optimize this
	protected void seekForOrphanFragments(AbstractModel requestModel, Set<RDFResource> requestDocumentResources) {
		for (Resource subject : requestModel.subjects()) {
			if ( ! ValueUtil.isURI(subject) ) continue;
			URI subjectURI = ValueUtil.getURI(subject);
			if ( ! URIUtil.hasFragment(subjectURI) ) continue;
			URI documentURI = new URIImpl(URIUtil.getDocumentURI(subjectURI.stringValue()));
			RDFResource fragmentResource = new RDFResource(requestModel, documentURI);
			if ( requestDocumentResources.contains(fragmentResource) ) {
				throw new BadRequestException("All fragment resources must be accompanied by their document resource");
			}
		}
	}

	protected URI forgeUniqueURI(RDFResource requestResource, String parentURI, HttpServletRequest request) {
		// TODO: Check that the resourceURI is unique and if not forge another one
		return forgeDocumentResourceURI(requestResource, parentURI, request);
	}

	protected URI forgeDocumentResourceURI(RDFResource documentResource, String parentURI, HttpServletRequest request) {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append(parentURI);

		if ( ! parentURI.endsWith(SLASH) ) uriBuilder.append(SLASH);

		uriBuilder.append(forgeSlug(documentResource, parentURI, request));

		return new URIImpl(uriBuilder.toString());
	}

	private String forgeSlug(RDFResource documentResource, String parentURI, HttpServletRequest request) {
		String uriSlug = configurationRepository.getGenericRequestSlug(documentResource.getURI().stringValue());
		String slug = uriSlug != null ? uriSlug : request.getHeader(HTTPHeaders.SLUG);

		if ( slug != null ) {
			if ( slug.endsWith(SLASH) ) {
				slug = slug.substring(0, slug.length() - 1);
				slug = HTTPUtil.createSlug(slug).concat(SLASH);
			} else slug = HTTPUtil.createSlug(slug);
		} else {
			Random random = new Random();
			slug = String.valueOf(Math.abs(random.nextLong()));
		}

		if ( configurationRepository.enforceEndingSlash() && ! slug.endsWith(SLASH) ) slug = slug.concat(SLASH);

		return slug;
	}

	protected interface ResourceProcessor<E> {
		public E processResource(RDFResource resource);
	}

}