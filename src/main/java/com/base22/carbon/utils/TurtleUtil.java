package com.base22.carbon.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base22.carbon.constants.Carbon;
import com.base22.carbon.converters.ConvertInputStream;
import com.base22.carbon.converters.ConvertString;

public abstract class TurtleUtil {
	//@formatter:off
	private static final String REGEX_OPTIONAL_SPACE = "\\s*";
	private static final String REGEX_MANDATORY_SPACE = "\\s+";
	private static final String REGEX_URL = ""
			+ "(?:"
			+ 	"[!#$&-;=?-\\[\\]_a-z~]|%[0-9a-f]{2}"
			+ ")*"
	;
	private static final String REGEX_TURTLE_BASE = ""
			+ 	"@base"
			+ 	REGEX_MANDATORY_SPACE
			+	"<"
			+ "("
			+ 	REGEX_URL
			+ ")"
			+ 	">"
			+ 	"."
			+ 	REGEX_OPTIONAL_SPACE
	;
	private static final String REGEX_TURTLE_PREFIX = ""
			+ 	"@prefix"
			+ 	REGEX_MANDATORY_SPACE
			+ "("
			+ 	"[a-z][a-z0-9]*"
			+ ")"
			+ 	":"
			+ 	REGEX_MANDATORY_SPACE
			+ 	"<"
			+ "("
			+ 		REGEX_URL
			+ ")"
			+ 	">"
			+	"."
	;
	private static final String REGEX_TURTLE_PREFIXES = ""
			+ "("
			+ 	"(?:"
			+ 		"@prefix"
			+ 		REGEX_MANDATORY_SPACE
			+ 		"[a-z][a-z0-9]*"
			+ 		":"
			+ 		REGEX_MANDATORY_SPACE
			+ 		"<"
			+ 			REGEX_URL
			+ 		">"
			+		"."
			+ 		REGEX_MANDATORY_SPACE
			+	")*"
			+ ")"
	;
	//@formatter:on

	public static Pattern getTurtleBasePattern() {
		Pattern prefixPattern = Pattern.compile(REGEX_TURTLE_BASE, Pattern.CASE_INSENSITIVE);
		return prefixPattern;
	}

	public static String getBaseInTurtle(String turtle) {
		String base = null;
		Pattern basePattern = getTurtleBasePattern();
		Matcher baseMatcher = basePattern.matcher(turtle);

		if ( baseMatcher.find() ) {
			base = baseMatcher.group(1);
		}

		return base;
	}

	public static String setBaseInTurtle(String baseURI, String turtle) {
		String turtleBase = "@base <" + baseURI + ">. ";

		Pattern basePattern = getTurtleBasePattern();
		Matcher baseMatcher = basePattern.matcher(turtle);

		if ( baseMatcher.find() ) {
			turtle = baseMatcher.replaceFirst(turtleBase);
		} else {
			turtle = turtleBase.concat(turtle);
		}

		return turtle;
	}

	public static Pattern getTurtlePrefixPattern() {
		Pattern prefixPattern = Pattern.compile(REGEX_TURTLE_PREFIX, Pattern.CASE_INSENSITIVE);
		return prefixPattern;
	}

	public static Pattern getTurtlePrefixesPattern() {
		Pattern prefixesPattern = Pattern.compile(REGEX_TURTLE_PREFIXES, Pattern.CASE_INSENSITIVE);
		return prefixesPattern;
	}

	public static Map<String, String> getNSPrefixesInTurtle(String turtle) {
		Map<String, String> prefixes = new HashMap<String, String>();

		Pattern prefixPattern = getTurtlePrefixPattern();
		Matcher prefixMatcher = prefixPattern.matcher(turtle);

		while (prefixMatcher.find()) {
			String key = prefixMatcher.group(1);
			String value = prefixMatcher.group(2);

			if ( key != null && value != null ) {
				if ( key.length() != 0 && value.length() != 0 ) {
					prefixes.put(key, value);
				}
			}
		}

		return prefixes;
	}

	public static InputStream setDefaultNSPrefixesInTurtle(InputStream turtle, Boolean inputHasPriority) throws IOException {
		Map<String, String> highPriority, lowPriority;

		String turtleString = ConvertInputStream.toString(turtle);

		if ( inputHasPriority ) {
			highPriority = getNSPrefixesInTurtle(turtleString);
			lowPriority = Carbon.CONFIGURED_PREFIXES;
		} else {
			lowPriority = getNSPrefixesInTurtle(turtleString);
			highPriority = Carbon.CONFIGURED_PREFIXES;
		}

		Iterator<Entry<String, String>> iterator = lowPriority.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> prefix = iterator.next();
			if ( ! highPriority.containsKey(prefix.getKey()) )
				highPriority.put(prefix.getKey(), prefix.getValue());
		}

		turtleString = setNSPrefixesInTurtle(highPriority, turtleString);

		turtle = ConvertString.toInputStream(turtleString);

		return turtle;
	}

	public static String setNSPrefixesInTurtle(Map<String, String> prefixes, String turtle) {
		StringBuilder prefixesStringBuilder = new StringBuilder();

		Iterator<Entry<String, String>> prefixesIterator = prefixes.entrySet().iterator();
		while (prefixesIterator.hasNext()) {
			Entry<String, String> prefix = prefixesIterator.next();
			prefixesStringBuilder.append("@prefix " + prefix.getKey() + ": <" + prefix.getValue() + ">. ");
		}

		String prefixesString = prefixesStringBuilder.toString();

		Pattern prefixesPattern = getTurtlePrefixesPattern();
		Matcher prefixesMatcher = prefixesPattern.matcher(turtle);
		turtle = prefixesMatcher.replaceFirst(prefixesString);

		return turtle;
	}
}
