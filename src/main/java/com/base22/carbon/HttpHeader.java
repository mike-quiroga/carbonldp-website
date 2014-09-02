package com.base22.carbon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class HttpHeader {

	private List<HttpHeaderValue> values;

	public HttpHeader() {
		this.values = new ArrayList<HttpHeaderValue>();
	}

	public HttpHeader(String header) {
		this.values = new ArrayList<HttpHeaderValue>();
		this.parseHeader(header);
	}

	public HttpHeader(Collection<String> headers) {
		this.values = new ArrayList<HttpHeaderValue>();
		Iterator<String> headersIterator = headers.iterator();
		while (headersIterator.hasNext()) {
			String header = headersIterator.next();
			this.parseHeader(header);
		}
	}

	public HttpHeader(Enumeration<String> headers) {
		this.values = new ArrayList<HttpHeaderValue>();
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			this.parseHeader(header);
		}
	}

	private void parseHeader(String header) {
		String[] headerParts = header.split(",");
		for (String headerPart : headerParts) {
			this.addValue(headerPart);
		}
	}

	private void addValue(String value) {
		HttpHeaderValue headerValue = new HttpHeaderValue(value);
		this.values.add(headerValue);
	}

	public void addHeaderValue(String value) {
		this.parseHeader(value);
	}

	public void addHeaderValue(HttpHeaderValue value) {
		this.values.add(value);
	}

	public List<HttpHeaderValue> getHeaderValues() {
		return this.values;
	}

	@Override
	public String toString() {
		StringBuilder headerBuilder = new StringBuilder();
		Iterator<HttpHeaderValue> valuesIterator = this.values.iterator();
		while (valuesIterator.hasNext()) {
			HttpHeaderValue value = valuesIterator.next();
			headerBuilder.append(value.toString());

			if ( valuesIterator.hasNext() ) {
				headerBuilder.append(", ");
			}
		}
		return headerBuilder.toString();
	}

	public static List<HttpHeaderValue> filterHeaderValues(HttpHeader header, String mainKey, String mainValue, String extendingKey, String extendingValue) {
		List<HttpHeaderValue> sortedValues = new ArrayList<HttpHeaderValue>();
		for (HttpHeaderValue headerValue : header.getHeaderValues()) {
			boolean mainKeyMatches = false;
			boolean mainValueMatches = false;
			boolean extendingKeyMatches = false;
			boolean extendingValueMatches = false;

			String headerMainKey = headerValue.getMainKey();
			if ( headerMainKey != null && mainKey != null ) {
				if ( headerMainKey.equals(mainKey) ) {
					mainKeyMatches = true;
				}
			} else if ( mainKey == null ) {
				mainKeyMatches = true;
			}

			String headerMainValue = headerValue.getMainValue();
			if ( headerMainValue != null && mainValue != null ) {
				if ( headerMainValue.equals(mainValue) ) {
					mainValueMatches = true;
				}
			} else if ( mainValue == null ) {
				mainValueMatches = true;
			}

			String headerExtendingKey = headerValue.getExtendingKey();
			if ( headerExtendingKey != null && extendingKey != null ) {
				if ( headerExtendingKey.equals(extendingKey) ) {
					extendingKeyMatches = true;
				}
			} else if ( extendingKey == null ) {
				extendingKeyMatches = true;
			}

			String headerExtendingValue = headerValue.getExtendingValue();
			if ( headerExtendingValue != null && extendingValue != null ) {
				if ( headerExtendingValue.equals(extendingValue) ) {
					extendingValueMatches = true;
				}
			} else if ( extendingValue == null ) {
				extendingValueMatches = true;
			}

			if ( mainKeyMatches && mainValueMatches && extendingKeyMatches && extendingValueMatches ) {
				sortedValues.add(headerValue);
			}
		}
		return sortedValues;
	}
}
