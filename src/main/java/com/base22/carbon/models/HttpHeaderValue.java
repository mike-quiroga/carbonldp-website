package com.base22.carbon.models;

public class HttpHeaderValue {
	private String mainKey = null;
	private String mainValue;
	private String extendingKey = null;
	private String extendingValue = null;

	public HttpHeaderValue() {
		this.mainValue = "";
	}

	public HttpHeaderValue(String headerValue) {
		String[] headerParts = headerValue.split(";");

		this.setMain(headerParts[0]);

		if ( headerParts.length >= 2 ) {
			this.setExtending(headerParts[1]);
		}
	}

	public String getMain() {
		StringBuilder mainBuilder = new StringBuilder();
		if ( this.mainKey != null ) {
			mainBuilder.append(this.mainKey).append("=");
		}
		if ( this.mainValue != null ) {
			mainBuilder.append(this.mainValue);

			if ( ! (this.mainValue.startsWith("<") && this.mainValue.endsWith(">")) ) {
				boolean specialCharacters = hasSpecialCharacters(this.mainValue);
				if ( specialCharacters ) {
					mainBuilder.insert(0, "\"");
					mainBuilder.append("\"");
				}
			}
		}
		if ( mainBuilder.length() == 0 ) {
			return null;
		}
		return mainBuilder.toString();
	}

	public void setMain(String part) {
		this.mainKey = getKey(part);
		this.mainValue = getValue(part);
	}

	public String getMainKey() {
		return mainKey;
	}

	public void setMainKey(String mainKey) {
		this.mainKey = mainKey;
	}

	public String getMainValue() {
		return mainValue;
	}

	public void setMainValue(String mainValue) {
		this.mainValue = mainValue;
	}

	public String getExtending() {
		StringBuilder extendingBuilder = new StringBuilder();
		if ( this.extendingKey != null ) {
			extendingBuilder.append(this.extendingKey).append("=");
		}
		if ( this.extendingValue != null ) {
			extendingBuilder.append(this.extendingValue);
			if ( ! (this.extendingValue.startsWith("<") && this.extendingValue.endsWith(">")) ) {
				boolean specialCharacters = hasSpecialCharacters(this.extendingValue);
				if ( specialCharacters ) {
					extendingBuilder.insert(0, "\"");
					extendingBuilder.append("\"");
				}
			}
		}
		if ( extendingBuilder.length() == 0 ) {
			return null;
		}
		return extendingBuilder.toString();
	}

	public void setExtending(String part) {
		this.extendingKey = getKey(part);
		this.extendingValue = getValue(part);
	}

	public String getExtendingKey() {
		return extendingKey;
	}

	public void setExtendingKey(String extendingKey) {
		this.extendingKey = extendingKey;
	}

	public String getExtendingValue() {
		return extendingValue;
	}

	public void setExtendingValue(String extendingValue) {
		this.extendingValue = extendingValue;
	}

	private String getKey(String part) {
		String[] divided = part.split("=");
		if ( divided.length == 1 ) {
			return null;
		}
		return cleanString(divided[0]);
	}

	private String getValue(String part) {
		String[] divided = part.split("=");
		if ( divided.length == 1 ) {
			return cleanString(divided[0]);
		}
		return cleanString(divided[1]);
	}

	private String cleanString(String toClean) {
		toClean = toClean.trim();
		toClean = toClean.startsWith("\"") ? toClean.substring(1, toClean.length()) : toClean;
		toClean = toClean.endsWith("\"") ? toClean.substring(0, toClean.length() - 1) : toClean;
		toClean = toClean.startsWith("'") ? toClean.substring(1, toClean.length()) : toClean;
		toClean = toClean.endsWith("'") ? toClean.substring(0, toClean.length() - 1) : toClean;
		return toClean;
	}

	private boolean hasSpecialCharacters(String toCheck) {
		return ! toCheck.matches("^[a-zA-Z0-9]+$");
	}

	@Override
	public String toString() {
		String main = this.getMain();
		String extending = this.getExtending();

		StringBuilder httpValueBuilder = new StringBuilder();
		httpValueBuilder.append(main);

		if ( extending != null ) {
			httpValueBuilder.append("; ").append(extending);
		}
		return httpValueBuilder.toString();
	}
}
