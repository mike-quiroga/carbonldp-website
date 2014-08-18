package com.base22.carbon.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Test;

public class HttpUtilTest {

	int httpStatusCode;
	String url;
	String slug;
	String requestInfo;

	@Test
	public void testCreateSlugValid() {
		slug = "Exámple-+=[*EXAMPLE-=+ExAmPLe";

		assertEquals("example-example-example", HttpUtil.createSlug(slug));
	}

	@Test
	public void testCreateSlugNull() {
		slug = "";

		assertEquals("", HttpUtil.createSlug(slug));
	}

	@Test
	public void testGetStatusCodeText200() {
		httpStatusCode = 200;

		assertEquals("200 OK", HttpUtil.getStatusCodeText(httpStatusCode));
	}

	@Test
	public void testGetStatusCodeTextUnknown() {
		httpStatusCode = 20;

		assertEquals("20 Unknown HTTP Status Code", HttpUtil.getStatusCodeText(httpStatusCode));
	}

	@Test
	public void testIsValidUrlValid() {
		url = "http://base22.com/carbon/ldp/main/people/example";

		assertTrue(HttpUtil.isValidURL(url));
	}

	@Test
	public void testIsValidUrlNonStandardPort() {
		url = "http://base22.com/carbon/ldp/main/people/example:88008800";

		assertTrue(HttpUtil.isValidURL(url));
	}

	@Test
	public void testIsValidUrlDeepPath() {
		url = "http://base22.com/carbon/ldp/a/b/c/d/e/f/g/h/i/j/k/l/m/n/o/main/people/example";

		assertTrue(HttpUtil.isValidURL(url));
	}

	@Test
	public void testIsValidUrlFile() {
		url = "http://base22.com/carbon/ldp/main/people/example.jpg";

		assertTrue(HttpUtil.isValidURL(url));
	}

	@Test
	public void testIsValidUrlAnchorTag() {
		url = "http://base22.com/carbon/ldp/main/people/example#final";

		assertTrue(HttpUtil.isValidURL(url));
	}

	@Test
	public void testIsValidUrlParameters() {
		url = "http://base22.com/carbon/ldp/main/people/example&test=123";

		assertTrue(HttpUtil.isValidURL(url));
	}

	@Test
	public void testIsValidUrlWhiteSpaces() {
		url = "http://base22.com/carbon/ldp/ main/people/example";

		assertFalse(HttpUtil.isValidURL(url));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

}
