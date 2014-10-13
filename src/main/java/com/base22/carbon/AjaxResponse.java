package com.base22.carbon;

import java.util.HashMap;
import java.util.Map;

public class AjaxResponse {
	private String message;
	private Object data;
	private Map<String, Object> parameters = null;
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	public void addParameter(String name, Object value) {
		if(parameters == null) parameters = new HashMap<String, Object>();
		parameters.put(name, value);
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
}
