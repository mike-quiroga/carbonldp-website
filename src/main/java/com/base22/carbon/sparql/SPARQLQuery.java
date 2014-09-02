package com.base22.carbon.sparql;

public class SPARQLQuery {
	private TYPE type;
	private String dataset;
	private String query;
	private String format;
	
	public static enum TYPE {
		QUERY, UPDATE;
		@Override 
		public String toString() {
			String s = super.toString();
			return s.substring(0, 1) + s.substring(1).toLowerCase();
		}
	}

	public SPARQLQuery() { }
	public SPARQLQuery(TYPE type, String dataset, String query) {
		this.type = type;
		this.dataset = dataset;
		this.query = query;
	}
	public SPARQLQuery(TYPE type, String dataset, String query, String format) {
		this.type = type;
		this.dataset = dataset;
		this.query = query;
		this.format = format;
	}

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
}
