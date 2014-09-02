package com.base22.carbon.sparql;

public class SPARQLClientMessage {
	private SPARQLQuery sparqlQuery;
	private boolean valid;
	private String action;
	private String message;
	
	public SPARQLClientMessage() { }
	public SPARQLClientMessage(SPARQLQuery sparqlQuery, boolean valid, String action, String message) {
		this.sparqlQuery = sparqlQuery;
		this.valid = valid;
		this.action = action;
		this.message = message;
	}
	
	public SPARQLQuery getSparqlQuery() {
		return sparqlQuery;
	}
	public void setSparqlQuery(SPARQLQuery sparqlQuery) {
		this.sparqlQuery = sparqlQuery;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
