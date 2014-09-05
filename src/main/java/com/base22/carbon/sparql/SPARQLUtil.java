package com.base22.carbon.sparql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.base22.carbon.Carbon;

public class SPARQLUtil {
	private static final String REGEX_OPTIONAL_SPACE = "\\s*";
	private static final String REGEX_MANDATORY_SPACE = "\\s+";
	private static final String REGEX_URL = "" + "(?:" + "[!#$&-;=?-\\[\\]_a-z~]|%[0-9a-f]{2}" + ")*";
	public static final String REGEX_SPARQL_PREFIX = "" + "PREFIX" + REGEX_MANDATORY_SPACE + "(" + "[a-z][a-z0-9]*" + ")" + ":" + REGEX_MANDATORY_SPACE + "<"
			+ "(" + REGEX_URL + ")" + ">";
	public static final String REGEX_SPARQL_PREFIXES = "" + "(" + "(?:" + "PREFIX" + REGEX_MANDATORY_SPACE + "[a-z][a-z0-9]*" + ":" + REGEX_MANDATORY_SPACE
			+ "<" + REGEX_URL + ">" + REGEX_MANDATORY_SPACE + ")*" + ")";

	public static Pattern getPrefixPattern() {
		Pattern prefixPattern = Pattern.compile(REGEX_SPARQL_PREFIX, Pattern.CASE_INSENSITIVE);
		return prefixPattern;
	}

	public static Pattern getPrefixesPattern() {
		Pattern prefixesPattern = Pattern.compile(REGEX_SPARQL_PREFIXES, Pattern.CASE_INSENSITIVE);
		return prefixesPattern;
	}

	public static String setDefaultNSPrefixes(String query, Boolean inputHasPriority) {
		Map<String, String> highPriority, lowPriority;

		if ( inputHasPriority ) {
			highPriority = getNSPrefixes(query);
			lowPriority = Carbon.CONFIGURED_PREFIXES;
		} else {
			lowPriority = getNSPrefixes(query);
			highPriority = Carbon.CONFIGURED_PREFIXES;
		}

		Iterator<Entry<String, String>> iterator = lowPriority.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> prefix = iterator.next();
			if ( ! highPriority.containsKey(prefix.getKey()) )
				highPriority.put(prefix.getKey(), prefix.getValue());
		}

		query = setNSPrefixes(highPriority, query);

		return query;
	}

	public static Map<String, String> getNSPrefixes(String sparql) {
		Map<String, String> prefixes = new HashMap<String, String>();

		Pattern prefixPattern = getPrefixPattern();
		Matcher prefixMatcher = prefixPattern.matcher(sparql);

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

	public static String setNSPrefixes(Map<String, String> prefixes, String sparql) {
		StringBuilder prefixesStringBuilder = new StringBuilder();

		Iterator<Entry<String, String>> prefixesIterator = prefixes.entrySet().iterator();
		while (prefixesIterator.hasNext()) {
			Entry<String, String> prefix = prefixesIterator.next();
			prefixesStringBuilder.append("PREFIX " + prefix.getKey() + ": <" + prefix.getValue() + "> ");
		}

		String prefixesString = prefixesStringBuilder.toString();

		Pattern prefixesPattern = getPrefixesPattern();
		Matcher prefixesMatcher = prefixesPattern.matcher(sparql);
		sparql = prefixesMatcher.replaceFirst(prefixesString);

		return sparql;
	}
}
