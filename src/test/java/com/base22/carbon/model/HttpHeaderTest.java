package com.base22.carbon.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.base22.carbon.models.HttpHeader;
import com.base22.carbon.models.HttpHeaderValue;

public class HttpHeaderTest {
	@Test
	public void testParsing() {
		HttpHeader httpHeader = new HttpHeader(
				"return=representation; include=\"http://example.com/something\", return=representation; omit=\"http://example.com/something\"");
		assertTrue(httpHeader.getHeaderValues().size() == 2);
	}

	@Test
	public void testAssembling() {
		HttpHeader httpHeader = new HttpHeader();
		httpHeader.addHeaderValue(new HttpHeaderValue("return=representation; include=\"http://example.com/something\""));

		HttpHeaderValue httpHeaderValue = new HttpHeaderValue();
		httpHeaderValue.setMain("return=representation");
		httpHeaderValue.setExtending("omit=http://example.com/something");

		httpHeader.addHeaderValue(httpHeaderValue);

		String resultString = "return=representation; include=\"http://example.com/something\", return=representation; omit=\"http://example.com/something\"";

		String header = httpHeader.toString();
		assertTrue(header.equals(resultString));
	}
}
