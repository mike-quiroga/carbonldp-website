package com.base22.carbon.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.base22.carbon.constants.Carbon;
import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.HttpHeader;

public abstract class HttpUtil {

	/**
	 * Given a valid HTTP status code, this method will return the code and its short description. For example, given
	 * the code 200, this method will return &quot;200 OK&quot;.
	 * 
	 * @see http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
	 * @param httpStatusCode
	 * @return
	 */
	public static String getStatusCodeText(int httpStatusCode) {

		String status = null;
		switch (httpStatusCode) {
			case 100:
				status = "100 Continue";
				break;
			case 101:
				status = "101 Switching Protocols";
				break;
			case 200:
				status = "200 OK";
				break;
			case 201:
				status = "201 Created";
				break;
			case 202:
				status = "202 Accepted";
				break;
			case 203:
				status = "203 Non-Authoritative Information";
				break;
			case 204:
				status = "204 No Content";
				break;
			case 205:
				status = "205 Reset Content";
				break;
			case 206:
				status = "206 Partial Content";
				break;
			case 300:
				status = "300 Multiple Choices";
				break;
			case 301:
				status = "301 Moved Permanently";
				break;
			case 302:
				status = "302 Found";
				break;
			case 303:
				status = "303 See Other";
				break;
			case 304:
				status = "304 Not Modified";
				break;
			case 305:
				status = "305 Use Proxy";
				break;
			case 306:
				status = "306 (Unused)";
				break;
			case 307:
				status = "307 Temporary Redirect";
				break;
			case 400:
				status = "400 Bad Request";
				break;
			case 401:
				status = "401 Unauthorized";
				break;
			case 402:
				status = "402 Payment Required";
				break;
			case 403:
				status = "403 Forbidden";
				break;
			case 404:
				status = "404 Not Found";
				break;
			case 405:
				status = "405 Method Not Allowed";
				break;
			case 406:
				status = "406 Not Acceptable";
				break;
			case 407:
				status = "407 Proxy Authentication Required";
				break;
			case 408:
				status = "408 Request Timeout";
				break;
			case 409:
				status = "409 Conflict";
				break;
			case 410:
				status = "410 Gone";
				break;
			case 411:
				status = "411 Length Required";
				break;
			case 412:
				status = "412 Precondition Failed";
				break;
			case 413:
				status = "413 Request Entity Too Large";
				break;
			case 414:
				status = "414 Request-URI Too Long";
				break;
			case 415:
				status = "415 Unsupported Media Type";
				break;
			case 416:
				status = "416 Requested Range Not Satisfiable";
				break;
			case 417:
				status = "417 Expectation Failed";
				break;
			case 500:
				status = "500 Internal Server Error";
				break;
			case 501:
				status = "501 Not Implemented";
				break;
			case 502:
				status = "502 Bad Gateway";
				break;
			case 503:
				status = "503 Service Unavailable";
				break;
			case 504:
				status = "504 Gateway Timeout";
				break;
			case 505:
				status = "505 HTTP Version Not Supported";
				break;
			default:
				status = httpStatusCode + " Unknown HTTP Status Code";
				break;
		}

		return status;

	}

	//@formatter:off
	public static String printRequestInfo(HttpServletRequest request) {

		StringBuilder requestInfo = new StringBuilder();
		requestInfo
			.append("\n\tRequest URL: ")
				.append(request.getRequestURL())
			.append("\n\tRequest URI: ")
				.append(request.getRequestURI())
			.append("\n\tRequest Method: ")
				.append(request.getMethod())
		;
		
		requestInfo
			.append("\n\tRequest Headers:")
		;
		Enumeration<String> requestHeaderNames = request.getHeaderNames();
		if( ! requestHeaderNames.hasMoreElements() ) {
			requestInfo
				.append(" None")
			;
		}
		while(requestHeaderNames.hasMoreElements()) {
			String requestHeaderName = requestHeaderNames.nextElement();
			String requestHeader = request.getHeader(requestHeaderName);
			requestInfo.append("\n\t\t")
				.append(requestHeaderName)
				.append(": ")
				.append(requestHeader)
			;
		}
		requestInfo
			.append("\n\tRequest Parameters:")
		;
		Iterator<Entry<String, String[]>> requestParametersIterator = request.getParameterMap().entrySet().iterator();
		if( ! requestParametersIterator.hasNext() ) {
			requestInfo
				.append(" None")
			;
		}
		while(requestParametersIterator.hasNext()) {
			Entry<String, String[]> requestParameter = requestParametersIterator.next();
			requestInfo
				.append("\n\t\t")
				.append(requestParameter.getKey())
			;
			for( String requestParameterValue : requestParameter.getValue() ) {
				if( requestParameter.getValue().length == 1 ) {
					requestInfo
						.append(": ")
						.append(requestParameterValue)
				;
				} else {
					requestInfo
						.append("\n\t\t\t")
						.append(requestParameterValue)
					;
				}
			}
		}

		return requestInfo.toString();

	}
	//@formatter:on

	public static String printResponseInfo(ResponseEntity<?> responseEntity) {

		StringBuilder responseInfo = new StringBuilder();

		//@formatter:off
		responseInfo
			.append("\n\tResponse Status Code: ")
				.append(responseEntity.getStatusCode().value())
			.append("\n\tResponse Has Body: ")
				.append(responseEntity.hasBody())
			.append("\n\tResponse Headers: ")
		;
		//@formatter:on

		HttpHeaders headers = responseEntity.getHeaders();

		if ( headers.isEmpty() ) {
			responseInfo.append(" None");
		} else {

			for (Entry<String, List<String>> entry : headers.entrySet()) {
				responseInfo.append("\n\t\t").append(entry.getKey()).append(": ");

				if ( entry.getValue().size() > 1 ) {
					for (String s : entry.getValue()) {
						responseInfo.append("\n").append(s);
					}
				} else {
					responseInfo.append(entry.getValue());
				}

			}

		}

		return responseInfo.toString();

	}

	public static String printResponseInfo(HttpServletResponse response) {
		StringBuilder responseInfo = new StringBuilder();

		//@formatter:off
		responseInfo
			.append("\n\tResponse Status Code: ")
				.append(getStatusCodeText(response.getStatus()))
			.append("\n\tResponse Headers: ")
		;
		//@formatter:on

		Collection<String> headerNames = response.getHeaderNames();

		if ( headerNames.isEmpty() ) {
			responseInfo.append("None");
		}

		// This list is needed so the headers don't repeat
		List<String> printedNames = new ArrayList<String>();

		for (String headerName : headerNames) {
			if ( ! printedNames.contains(headerName) ) {
				Collection<String> headerValues = response.getHeaders(headerName);
				if ( ! headerValues.isEmpty() ) {
					HttpHeader header = new HttpHeader(headerValues);
					//@formatter:off
					responseInfo
						.append("\n\t\t")
						.append(headerName)
						.append(": ")
						.append(header.toString())
					;
					//@formatter:on
				}
				printedNames.add(headerName);
			}
		}

		return responseInfo.toString();
	}

	public static String formatWeakETag(String eTagValue) {
		StringBuilder sb = new StringBuilder();
		sb.append("W/\"");
		sb.append(eTagValue);
		sb.append("\"");
		return sb.toString();
	}

	public static String createSlug(String origin) {
		String delimiter = "-";
		String slug = HttpUtil.changeCharacterSet(origin);
		slug = slug.replaceAll("[^a-zA-Z0-9\\/_|+ -]", "");
		slug = slug.trim();
		slug = slug.toLowerCase();
		slug = slug.replaceAll("[\\/_|+ -]+", delimiter);
		return slug;
	}

	private static String changeCharacterSet(String origin) {
		/* Decompose original "accented" string to basic characters. */
		origin = Normalizer.normalize(origin, Normalizer.Form.NFKD);
		/* Build a new String with only ASCII characters. */
		StringBuilder buf = new StringBuilder();
		for (int idx = 0; idx < origin.length(); ++idx) {
			char ch = origin.charAt(idx);
			if ( ch < 128 )
				buf.append(ch);
		}
		origin = buf.toString();
		return origin;
	}

	public static Boolean isValidURL(String url) {

		return url.matches("^"
				+
				// protocol identifier
				"(?:(?:https?|ftp)://)"
				+
				// user:pass authentication
				"(?:\\S+(?::\\S*)?@)?"
				+ "(?:"
				+
				// IP address exclusion
				// private & local networks
				"(?!10(?:\\.\\d{1,3}){3})"
				+ "(?!127(?:\\.\\d{1,3}){3})"
				+ "(?!169\\.254(?:\\.\\d{1,3}){2})"
				+ "(?!192\\.168(?:\\.\\d{1,3}){2})"
				+ "(?!172\\.(?:1[6-9]|2\\d|3[0-1])(?:\\.\\d{1,3}){2})"
				+
				// IP address dotted notation octets
				// excludes loopback network 0.0.0.0
				// excludes reserved space >= 224.0.0.0
				// excludes network & broacast addresses
				// (first & last IP address of each class)
				"(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])" + "(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}" + "(?:\\.(?:[1-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))"
				+ "|" +
				// host name
				"(?:(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)" +
				// domain name
				"(?:\\.(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)*" +
				// TLD identifier
				"(?:\\.(?:[a-z\\u00a1-\\uffff]{2,}))" + ")" +
				// port number
				"(?::\\d{2,5})?" +
				// resource path
				"(?:/[^\\s]*)?" + "$");
	}

	public static boolean isDocumentResourceURI(String uri) {
		return ! uri.matches(".+" + Carbon.EXTENDING_RESOURCE_SIGN + ".*");
	}

	public static boolean isSecondaryResourceURI(String uri) {
		return uri.matches(".+" + Carbon.EXTENDING_RESOURCE_SIGN + ".+");
	}

	public static boolean isSystemResourceURI(String uri) {
		return uri.matches(".+" + Carbon.SYSTEM_RESOURCE_REGEX + ".+");
	}

	public static String getRequestURL(HttpServletRequest request) {

		String requestURL = request.getRequestURL().toString();

		if ( request.getRequestURL().indexOf("localhost") > 0 || request.getRequestURL().indexOf("127.0.0.1") > 0 ) {

			StringBuilder builder = new StringBuilder();
			builder.append(request.getScheme());
			builder.append("://");
			builder.append(request.getLocalName());
			builder.append(request.getRequestURI());
			requestURL = builder.toString();
		}

		return requestURL;
	}

	public static ResponseEntity<Object> createErrorResponseEntity(CarbonException e) {
		return HttpUtil.createErrorResponseEntity(e.getErrorObject());
	}

	public static ResponseEntity<Object> createErrorResponseEntity(CarbonException e, HttpStatus httpStatus) {
		e.getErrorObject().setHttpStatus(httpStatus);
		return HttpUtil.createErrorResponseEntity(e.getErrorObject());
	}

	public static ResponseEntity<Object> createErrorResponseEntity(ErrorResponse errorResponse, HttpStatus httpStatus) {
		errorResponse.setHttpStatus(httpStatus);
		return HttpUtil.createErrorResponseEntity(errorResponse);
	}

	public static ResponseEntity<Object> createErrorResponseEntity(ErrorResponse errorResponse) {
		HttpStatus errorStatus = errorResponse.getHttpStatus();
		if ( errorStatus == null ) {
			errorStatus = HttpStatus.I_AM_A_TEAPOT;
			errorResponse.setHttpStatus(errorStatus);
		}
		return new ResponseEntity<Object>(errorResponse.generateModel(), errorStatus);
	}
}
