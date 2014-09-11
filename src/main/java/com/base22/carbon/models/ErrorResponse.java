package com.base22.carbon.models;

import org.springframework.http.HttpStatus;

import com.base22.carbon.ldp.models.RDFResource;

public interface ErrorResponse extends RDFResource {

	public HttpStatus getHttpStatus();

	public void setHttpStatus(HttpStatus httpStatus);

	public String getCarbonCode();

	public void setCarbonCode(String carbonCode);

	public String getFriendlyMessage();

	public void setFriendlyMessage(String friendlyMessage);

	public String getDebugMessage();

	public void setDebugMessage(String debugMessage);

	public void addElementToDebugStackTrace(String step);

	public void addHeaderIssue(String key, String code, String description, String value);

	public void addParameterIssue(String key, String code, String description, String value);

	public void setEntityBodyIssue(String code, String description);
}
