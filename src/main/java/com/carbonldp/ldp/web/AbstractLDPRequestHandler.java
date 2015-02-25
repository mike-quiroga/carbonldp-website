package com.carbonldp.ldp.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;

import com.carbonldp.ConfigurationRepository;
import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.ldp.services.ContainerService;
import com.carbonldp.ldp.services.RDFSourceService;
import com.carbonldp.models.HTTPHeader;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.web.AbstractRequestHandler;
import com.carbonldp.web.exceptions.BadRequestException;

public abstract class AbstractLDPRequestHandler extends AbstractRequestHandler {
	public static final HTTPHeaderValue interactionModelApplied;
	static {
		interactionModelApplied = new HTTPHeaderValue();
		interactionModelApplied.setMainKey("rel");
		interactionModelApplied.setMainValue("interaction-model");
	}

	protected ConfigurationRepository configurationRepository;

	protected RDFSourceService sourceService;
	protected ContainerService containerService;

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected List<HTTPHeaderValue> appliedPreferences;

	private Set<InteractionModel> supportedInteractionModels;
	private InteractionModel defaultInteractionModel;

	protected void setUp(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.appliedPreferences = new ArrayList<HTTPHeaderValue>();
	}

	protected boolean targetResourceExists(URI targetURI) {
		return sourceService.exists(targetURI);
	}

	protected String getTargetURL(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		String platformDomain = configurationRepository.getPlatformURL();
		StringBuilder targetURIBuilder = new StringBuilder();
		targetURIBuilder.append(platformDomain.substring(0, platformDomain.length() - 1)).append(requestURI);
		return targetURIBuilder.toString();
	}

	protected URI getTargetURI(HttpServletRequest request) {
		String url = getTargetURL(request);
		return new URIImpl(url);
	}

	protected InteractionModel getInteractionModel(URI targetURI) {
		InteractionModel requestInteractionModel = getRequestInteractionModel(request);
		if ( requestInteractionModel != null ) {
			checkInteractionModelSupport(requestInteractionModel);
			appliedPreferences.add(interactionModelApplied);
			return requestInteractionModel;
		}
		return getDefaultInteractionModel(targetURI);
	}

	private InteractionModel getRequestInteractionModel(HttpServletRequest request) {
		HTTPHeader preferHeader = new HTTPHeader(request.getHeaders(HTTPHeaders.PREFER));
		// TODO: Move this to a constants file
		List<HTTPHeaderValue> filteredValues = HTTPHeader.filterHeaderValues(preferHeader, null, null, "rel", "interaction-model");
		int size = filteredValues.size();
		if ( size == 0 ) return null;
		if ( size > 1 ) throw new BadRequestException("The request defines more than 1 interaction model to apply.");

		String interactionModelURI = filteredValues.get(0).getMainValue();
		InteractionModel interactionModel = RDFNodeUtil.findByURI(interactionModelURI, InteractionModel.class);
		if ( interactionModel == null ) throw new BadRequestException("The defined interaction-model cannot be recognized.");
		return interactionModel;
	}

	private void checkInteractionModelSupport(InteractionModel requestInteractionModel) {
		if ( ! getSupportedInteractionModels().contains(requestInteractionModel) ) {
			throw new BadRequestException("The interaction-model defined is not supported in this entrypoint.");
		}
	}

	private InteractionModel getDefaultInteractionModel(URI targetURI) {
		URI dimURI = sourceService.getDefaultInteractionModel(targetURI);
		if ( dimURI == null ) return getDefaultInteractionModel();

		InteractionModel sourceDIM = RDFNodeUtil.findByURI(dimURI, InteractionModel.class);
		if ( sourceDIM == null ) return getDefaultInteractionModel();

		if ( ! getSupportedInteractionModels().contains(sourceDIM) ) return getDefaultInteractionModel();
		return sourceDIM;
	}

	protected void setAppliedPreferenceHeaders() {
		for (HTTPHeaderValue appliedPreference : appliedPreferences) {
			response.addHeader(HTTPHeaders.PREFERENCE_APPLIED, appliedPreference.toString());
		}
	}

	protected Set<InteractionModel> getSupportedInteractionModels() {
		return supportedInteractionModels;
	}

	protected void setSupportedInteractionModels(Set<InteractionModel> supportedInteractionModels) {
		this.supportedInteractionModels = supportedInteractionModels;
	}

	protected InteractionModel getDefaultInteractionModel() {
		return defaultInteractionModel;
	}

	protected void setDefaultInteractionModel(InteractionModel defaultInteractionModel) {
		this.defaultInteractionModel = defaultInteractionModel;
	}

	@Autowired
	public void setConfigurationRepository(ConfigurationRepository configurationRepository) {
		this.configurationRepository = configurationRepository;
	}

	@Autowired
	public void setRDFSourceService(RDFSourceService sourceService) {
		this.sourceService = sourceService;
	}

	@Autowired
	public void setContainerService(ContainerService containerService) {
		this.containerService = containerService;
	}

}
