package com.base22.carbon.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.base22.carbon.constants.Carbon;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ErrorResponse {
	static final String TYPE = Carbon.CONFIGURED_PREFIXES.get("c").concat("ErrorResponse");

	static final String HTTP_STATUS_CODE = Carbon.CONFIGURED_PREFIXES.get("c").concat("httpStatusCode");
	static final Property HTTP_STATUS_CODE_P = ResourceFactory.createProperty(HTTP_STATUS_CODE);

	static final String CARBON_CODE = Carbon.CONFIGURED_PREFIXES.get("c").concat("carbonCode");
	static final Property CARBON_CODE_P = ResourceFactory.createProperty(CARBON_CODE);

	static final String FRIENDLY_MESSAGE = Carbon.CONFIGURED_PREFIXES.get("c").concat("friendlyMessage");
	static final Property FRIENDLY_MESSAGE_P = ResourceFactory.createProperty(FRIENDLY_MESSAGE);

	static final String DEBUG_MESSAGE = Carbon.CONFIGURED_PREFIXES.get("c").concat("debugMessage");
	static final Property DEBUG_MESSAGE_P = ResourceFactory.createProperty(DEBUG_MESSAGE);

	static final String HAS_PARAMETER_ISSUE = Carbon.CONFIGURED_PREFIXES.get("c").concat("hasParameterIssue");
	static final Property HAS_PARAMETER_ISSUE_P = ResourceFactory.createProperty(HAS_PARAMETER_ISSUE);

	static final String HAS_HEADER_ISSUE = Carbon.CONFIGURED_PREFIXES.get("c").concat("hasHeaderIssue");
	static final Property HAS_HEADER_ISSUE_P = ResourceFactory.createProperty(HAS_HEADER_ISSUE);

	static final String HAS_ENTITY_BODY = Carbon.CONFIGURED_PREFIXES.get("c").concat("hasEntityBodyIssue");
	static final Property HAS_ENTITY_BODY_P = ResourceFactory.createProperty(HAS_ENTITY_BODY);

	static final String HAS_STACK_TRACE_ELEMENT = Carbon.CONFIGURED_PREFIXES.get("c").concat("hasStackTraceElement");
	static final Property HAS_STACK_TRACE_ELEMENT_P = ResourceFactory.createProperty(HAS_STACK_TRACE_ELEMENT);

	private HttpStatus httpStatus;
	private String carbonCode;
	private String friendlyMessage;
	private String debugMessage;
	private List<Issue> parameterIssues;
	private List<Issue> headerIssues;
	private Issue entityBodyIssue;

	private List<String> debugStackTrace;

	public ErrorResponse() {
		this.parameterIssues = new ArrayList<Issue>();
		this.headerIssues = new ArrayList<Issue>();
		this.debugStackTrace = new ArrayList<String>();
	}

	public Model generateModel() {
		Model model = ModelFactory.createDefaultModel();

		Resource resource = model.createResource();

		// TODO: Set language
		if ( httpStatus != null ) {
			resource.addLiteral(HTTP_STATUS_CODE_P, httpStatus.value());
		}
		if ( carbonCode != null ) {
			resource.addProperty(CARBON_CODE_P, carbonCode);
		}
		if ( friendlyMessage != null ) {
			resource.addProperty(FRIENDLY_MESSAGE_P, friendlyMessage);
		}
		if ( debugMessage != null ) {
			resource.addProperty(DEBUG_MESSAGE_P, debugMessage);
		}

		// Add parameter Issues (if any)
		int i = 1;
		for (Issue parameterIssue : parameterIssues) {
			String uri = "#parameterIssue-" + String.valueOf(i);
			Resource issue = parameterIssue.addIssueResourceToModel(model, uri);
			resource.addProperty(HAS_PARAMETER_ISSUE_P, issue);
			i++;
		}

		// Add header Issues (if any)
		i = 1;
		for (Issue headerIssue : headerIssues) {
			String uri = "#headerIssue-" + String.valueOf(i);
			Resource issue = headerIssue.addIssueResourceToModel(model, uri);
			resource.addProperty(HAS_HEADER_ISSUE_P, issue);
			i++;
		}

		if ( entityBodyIssue != null ) {
			String uri = "#entityBodyIssue";
			Resource issue = entityBodyIssue.addIssueResourceToModel(model, uri);
			resource.addProperty(HAS_HEADER_ISSUE_P, issue);
		}

		// Add debug stack messages
		i = debugStackTrace.size();
		for (String stackTraceElement : debugStackTrace) {
			StringBuilder elementBuilder = new StringBuilder();
			elementBuilder.append(i);
			elementBuilder.append(" - ");
			elementBuilder.append(stackTraceElement);
			resource.addProperty(HAS_STACK_TRACE_ELEMENT_P, elementBuilder.toString());
			i--;
		}

		return model;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getCarbonCode() {
		return carbonCode;
	}

	public void setCarbonCode(String carbonCode) {
		this.carbonCode = carbonCode;
	}

	public String getFriendlyMessage() {
		return friendlyMessage;
	}

	public void setFriendlyMessage(String friendlyMessage) {
		this.friendlyMessage = friendlyMessage;
	}

	public String getDebugMessage() {
		return debugMessage;
	}

	public void setDebugMessage(String debugMessage) {
		this.debugMessage = debugMessage;
	}

	public void addElementToDebugStackTrace(String step) {
		this.debugStackTrace.add(step);
	}

	public void addHeaderIssue(String key, String code, String description, String value) {
		Issue issue = new Issue(key, code, description, value);
		this.headerIssues.add(issue);
	}

	public void addParameterIssue(String key, String code, String description, String value) {
		Issue issue = new Issue(key, code, description, value);
		this.parameterIssues.add(issue);
	}

	public void setEntityBodyIssue(String code, String description) {
		Issue issue = new Issue(null, code, description, null);
		this.entityBodyIssue = issue;
	}

	public static class ISSUES {
		static final String TYPE_PARAMENTER = Carbon.CONFIGURED_PREFIXES.get("c").concat("ParameterIssue");
		static final String TYPE_HEADER = Carbon.CONFIGURED_PREFIXES.get("c").concat("HeaderIssue");
		static final String TYPE_ENTITY = Carbon.CONFIGURED_PREFIXES.get("c").concat("EntityBodyIssue");

		static final String KEY = Carbon.CONFIGURED_PREFIXES.get("c").concat("key");
		static final Property KEY_P = ResourceFactory.createProperty(KEY);

		static final String CODE = Carbon.CONFIGURED_PREFIXES.get("c").concat("code");
		static final Property CODE_P = ResourceFactory.createProperty(CODE);

		static final String DESCRIPTION = Carbon.CONFIGURED_PREFIXES.get("c").concat("description");
		static final Property DESCRIPTION_P = ResourceFactory.createProperty(DESCRIPTION);

		static final String VALUE = Carbon.CONFIGURED_PREFIXES.get("c").concat("value");
		static final Property VALUE_P = ResourceFactory.createProperty(VALUE);

	}

	public class Issue {
		private String key;
		private String code;
		private String description;
		private String value;

		public Issue(String key, String code, String description, String value) {
			this.key = key;
			this.code = code;
			this.description = description;
			this.value = value;
		}

		public Resource addIssueResourceToModel(Model model, String issueURI) {
			Resource resource = model.createResource(issueURI);

			if ( key != null ) {
				resource.addProperty(ISSUES.KEY_P, key);
			}
			if ( code != null ) {
				resource.addProperty(ISSUES.CODE_P, code);
			}
			if ( description != null ) {
				resource.addProperty(ISSUES.DESCRIPTION_P, description);
			}
			if ( value != null ) {
				resource.addProperty(ISSUES.VALUE_P, value);
			}

			return resource;
		}
	}
}
