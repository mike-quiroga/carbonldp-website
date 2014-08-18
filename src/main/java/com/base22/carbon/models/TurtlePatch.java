package com.base22.carbon.models;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Add support for localization in literals

public final class TurtlePatch {
	private String baseURI;

	private String basePart;
	private String prefixPart;
	private String deletePart;
	private String insertPart;

	//@formatter:off
	private static final String REGEX_SOF = "\\A";
	private static final String REGEX_EOF = "\\Z";
	private static final String REGEX_OPTIONAL_SPACE = "\\s*";
	private static final String REGEX_MANDATORY_SPACE = "\\s+";
	private static final String REGEX_STRING = ""
			+ "\""
			+ 	"[^\"]+"
			+ "\"";
	
	
	private static final String REGEX_URL = ""
			+ "(?:"
			+ 	"[!#$&-;=?-\\[\\]_a-z~]|%[0-9a-f]{2}"
			+ ")*"
	;
	private static final String REGEX_URI = ""
			+ "(?:"
			+ 	"<"
			+    	REGEX_URL
			+ 	">"
			+ ")"
	;
	private static final String REGEX_EXTENDING_RESOURCE_URI = "(?:.*)#(?:.+)";
	private static final String REGEX_NAMESPACE = ""
			+ 	"(?:"
			+ 		"(?:"
			+ 			"[a-z][a-z0-9]*"
			+		")?"
			+ 		":*"
			+ 		"[a-z][a-z0-9]*"
			+ 	")"
	;
	private static final String REGEX_LITERAL = ""
			+ "(?:"
			+ 	REGEX_STRING
			+ 	"(?:"
			+ 		REGEX_OPTIONAL_SPACE
			+ 		"\\^\\^"
			+ 		REGEX_OPTIONAL_SPACE
			+    	REGEX_NAMESPACE
			+ 		"|"
			+    	REGEX_URI
			+ 	")?"
			+ ")"
	;
	
	private static final String REGEX_C_RESOURCE = ""
			+	"<"
			+ "("
			+ 		REGEX_URL
			+ ")"
			+ 	">"
			+ 	"(?:"
			+ 		"(?:"
			+ 			REGEX_MANDATORY_SPACE
			+ 			"(?:"
			+       		REGEX_NAMESPACE
			+ 				"|"
			+      			REGEX_URI
			+ 			")"
			+ 			REGEX_MANDATORY_SPACE
			+			"(?:"
			+ 				"(?:"
			+   	   			REGEX_NAMESPACE
			+ 					"|"
			+   	  			REGEX_URI
			+ 					"|"
			+   	    		REGEX_LITERAL
			+ 				")"
			+				","
			+				REGEX_MANDATORY_SPACE
			+			")*"
			+ 			"(?:"
			+      			REGEX_NAMESPACE
			+ 				"|"
			+     			REGEX_URI
			+ 				"|"
			+       		REGEX_LITERAL
			+ 			")"
			+ 			";"
			+ 		")*"
			+ 	")?"
			+ 	"(?:"
			+ 		REGEX_MANDATORY_SPACE
			+ 		"(?:"
			+      		REGEX_NAMESPACE
			+ 			"|"
			+      		REGEX_URI
			+ 		")"
			+ 		REGEX_MANDATORY_SPACE
			+		"(?:"
			+ 			"(?:"
			+   	  			REGEX_NAMESPACE
			+ 				"|"
			+   	 			REGEX_URI
			+ 				"|"
			+   	   		REGEX_LITERAL
			+ 			")"
			+			","
			+			REGEX_MANDATORY_SPACE
			+		")*"
			+ 		"(?:"
			+      		REGEX_NAMESPACE
			+ 			"|"
			+      		REGEX_URI
			+ 			"|"
			+      		REGEX_LITERAL
			+ 		")"
			+ 		"\\."
			+ 	")"
			+ 	REGEX_OPTIONAL_SPACE
	;
	private static final String REGEX_NC_RESOURCE = ""
			+	"<"
			+ 		REGEX_URL
			+ 	">"
			+ 	"(?:"
			+ 		"(?:"
			+ 			REGEX_MANDATORY_SPACE
			+ 			"(?:"
			+       		REGEX_NAMESPACE
			+ 				"|"
			+      			REGEX_URI
			+ 			")"
			+ 			REGEX_MANDATORY_SPACE
			+			"(?:"
			+ 				"(?:"
			+   	   			REGEX_NAMESPACE
			+ 					"|"
			+   	  			REGEX_URI
			+ 					"|"
			+   	    		REGEX_LITERAL
			+ 				")"
			+				","
			+				REGEX_MANDATORY_SPACE
			+			")*"
			+ 			"(?:"
			+      			REGEX_NAMESPACE
			+ 				"|"
			+     			REGEX_URI
			+ 				"|"
			+       		REGEX_LITERAL
			+ 			")"
			+ 			";"
			+ 		")*"
			+ 	")?"
			+ 	"(?:"
			+ 		REGEX_MANDATORY_SPACE
			+ 		"(?:"
			+      		REGEX_NAMESPACE
			+ 			"|"
			+      		REGEX_URI
			+ 		")"
			+ 		REGEX_MANDATORY_SPACE
			+		"(?:"
			+ 			"(?:"
			+   	  			REGEX_NAMESPACE
			+ 				"|"
			+   	 			REGEX_URI
			+ 				"|"
			+   	   		REGEX_LITERAL
			+ 			")"
			+			","
			+			REGEX_MANDATORY_SPACE
			+		")*"
			+ 		"(?:"
			+      		REGEX_NAMESPACE
			+ 			"|"
			+      		REGEX_URI
			+ 			"|"
			+      		REGEX_LITERAL
			+ 		")"
			+ 		"\\."
			+ 	")"
			+ 	REGEX_OPTIONAL_SPACE
	;
	
	private static final String REGEX_C_BASE = ""
			+ "("
			+ 	"BASE"
			+ 	REGEX_MANDATORY_SPACE
			+	"<"
			+ ")"
			+ "("
			+ 		REGEX_URL
			+ ")"
			+ "("
			+ 	">"
			+ 	REGEX_OPTIONAL_SPACE
			+ ")"
	;
	private static final String REGEX_NC_BASE = ""
			+ 	"BASE"
			+ 	REGEX_MANDATORY_SPACE
			+	"<"
			+ 		REGEX_URL
			+ 	">"
	;
	
	private static final String REGEX_C_PREFIX = ""
			+ 	"(?:"
			+ 		REGEX_OPTIONAL_SPACE
			+ 		"PREFIX"
			+ 		REGEX_MANDATORY_SPACE
			+ "("										// Prefix key
			+ 		"[a-z]+"
			+ ")"
			+ 		":"
			+ 		REGEX_MANDATORY_SPACE
			+ 		"<"
			+ "("										// Prefix value
			+ 			REGEX_URL
			+ ")"
			+ 		">"
			+ 		REGEX_OPTIONAL_SPACE
			+ 	")"
	;
	
	private static final String REGEX_PREFIXES_PART = ""
			+ 	"(?:"
			+ 		REGEX_OPTIONAL_SPACE
			+ 		"PREFIX"
			+ 		REGEX_MANDATORY_SPACE
			+ 		"[a-z]+"
			+ 		":"
			+ 		REGEX_MANDATORY_SPACE
			+ 		"<"
			+ 			REGEX_URL
			+ 		">"
			+ 	")+"
	;
	
	private static final String REGEX_INSERT_PART = ""
			+ 	"INSERT"
			+ 	REGEX_MANDATORY_SPACE
			+ 	"DATA"
			+ 	REGEX_MANDATORY_SPACE
			+ 	"\\{"
			+ 		REGEX_MANDATORY_SPACE
			+		"(?:"
			+ 			REGEX_NC_RESOURCE
			+		")+"
			+ 	"\\}"
	;
	
	private static final String REGEX_DELETE_PART = ""
			+ 	"DELETE"
			+ 	REGEX_MANDATORY_SPACE
			+ 	"DATA"
			+ 	REGEX_MANDATORY_SPACE
			+ 	"\\{"
			+ 		REGEX_MANDATORY_SPACE
			+		"(?:"
			+ 			REGEX_NC_RESOURCE
			+		")+"
			+ 	"\\}"
	;
	
	private static final String REGEX_TURTLEPATCH = ""
			+ 	REGEX_SOF
			+ 	REGEX_OPTIONAL_SPACE
			+ "("
			+	REGEX_NC_BASE
			+ ")?"
			+ 	REGEX_OPTIONAL_SPACE
			+ "("
			+ 	REGEX_PREFIXES_PART
			+ ")?"
			+ 	REGEX_OPTIONAL_SPACE
			+ "("
			+ 	REGEX_DELETE_PART
			+ ")?"
			+ 	REGEX_OPTIONAL_SPACE
			+ "("
			+	REGEX_INSERT_PART
			+ ")?"
			+ 	REGEX_OPTIONAL_SPACE
			+	REGEX_EOF
	;
	//@formatter:on

	public TurtlePatch(CharSequence toParse, String documentResourceURIToMatch) throws InvalidParameterException {
		Pattern pattern = getTurtlePatchPattern();
		Matcher matcher = pattern.matcher(toParse);

		if ( ! matcher.matches() ) {
			// TODO: Retrieve information regarding the exact error (start(), end())
			throw new InvalidParameterException("The content provided couldn't be parsed with the TurtlePatch format.");
		}

		basePart = matcher.group(1);
		prefixPart = matcher.group(2);
		deletePart = matcher.group(3);
		insertPart = matcher.group(4);

		// Check if it has at least a delete or a insert part
		if ( deletePart == null && insertPart == null ) {
			throw new InvalidParameterException(
					"The content provided couldn't be parsed with the TurtlePatch format. Neither a Delete or an Insert part were provided.");
		}

		Pattern resourcePattern = Pattern.compile(REGEX_C_RESOURCE, Pattern.CASE_INSENSITIVE);
		String documentResourceURI = null;

		if ( deletePart != null ) {
			// Loop through the resources being targeted by the delete part to check that maximum one document resource
			// is targeted and matches the one provided
			Matcher deleteResourceMatcher = resourcePattern.matcher(deletePart);

			while (deleteResourceMatcher.find()) {

				String resourceURI = deleteResourceMatcher.group(1);

				if ( ! resourceURI.matches(REGEX_EXTENDING_RESOURCE_URI) ) {
					// It is a document resource

					if ( this.getBaseURI() != null ) {
						if ( ! this.getBaseURI().concat(resourceURI).startsWith(documentResourceURIToMatch) ) {
							// The document resources URIs don't match
							throw new InvalidParameterException(
									"The content provided couldn't be parsed with the TurtlePatch format: The document resource of the TurtlePatch doesn't match.");
						}
						resourceURI = documentResourceURIToMatch;
					}
					else {
						if ( resourceURI.length() == 0 ) {
							// The document resource was declared as null, set base_part
							this.setBaseURI(documentResourceURIToMatch);
							resourceURI = documentResourceURIToMatch;
						}
						else {
							// The document resource wasn't declared as null, it needs to match the one provided
							if ( ! resourceURI.equals(documentResourceURIToMatch) ) {
								// The document resources URIs don't match
								throw new InvalidParameterException(
										"The content provided couldn't be parsed with the TurtlePatch format: The document resource of the TurtlePatch doesn't match.");
							}
						}
					}

					if ( documentResourceURI != null ) {
						// Another document resource was provided
						if ( ! documentResourceURI.equals(resourceURI) ) {
							// It isn't the same
							throw new InvalidParameterException(
									"The content provided couldn't be parsed with the TurtlePatch format: The delete part contains more than one document resource.");
						}
					}

					documentResourceURI = resourceURI;
				}
				else {
					if ( this.getBaseURI() != null ) {
						if ( ! this.getBaseURI().concat(resourceURI).startsWith(documentResourceURIToMatch) ) {
							// The document resources URIs don't match
							throw new InvalidParameterException(
									"The content provided couldn't be parsed with the TurtlePatch format: A extending resource doesn't have the same base as the document resource.");
						}
					}
					else {
						if ( ! resourceURI.startsWith(documentResourceURIToMatch) ) {
							// The extending resource doesn't start with the document resource URI. Maybe it is relative
							// Set the baseURI provided and check again.
							this.setBaseURI(documentResourceURIToMatch);
							if ( ! this.getBaseURI().concat(resourceURI).startsWith(documentResourceURIToMatch) ) {
								// The document resources URIs don't match
								throw new InvalidParameterException(
										"The content provided couldn't be parsed with the TurtlePatch format: A extending resource doesn't have the same base as the document resource.");
							}
						}
					}
				}

			}

		}

		if ( insertPart != null ) {
			// Loop through the resources being targeted by the delete part to check that maximum one document resource
			// is targeted and matches the one provided
			Matcher insertResourceMatcher = resourcePattern.matcher(insertPart);

			while (insertResourceMatcher.find()) {

				String resourceURI = insertResourceMatcher.group(1);

				if ( ! resourceURI.matches(REGEX_EXTENDING_RESOURCE_URI) ) {
					// It is a document resource

					if ( this.getBaseURI() != null ) {
						if ( ! this.getBaseURI().concat(resourceURI).startsWith(documentResourceURIToMatch) ) {
							// The document resources URIs don't match
							throw new InvalidParameterException(
									"The content provided couldn't be parsed with the TurtlePatch format: The document resource of the TurtlePatch doesn't match.");
						}
						resourceURI = documentResourceURIToMatch;
					}
					else {
						if ( resourceURI.length() == 0 ) {
							// The document resource was declared as null, set base_part
							this.setBaseURI(documentResourceURIToMatch);
							resourceURI = documentResourceURIToMatch;
						}
						else {
							// The document resource wasn't declared as null, it needs to match the one provided
							if ( ! resourceURI.equals(documentResourceURIToMatch) ) {
								// The document resources URIs don't match
								throw new InvalidParameterException(
										"The content provided couldn't be parsed with the TurtlePatch format: The document resource of the TurtlePatch doesn't match.");
							}
						}
					}

					if ( documentResourceURI != null ) {
						// Another document resource was provided
						if ( ! documentResourceURI.equals(resourceURI) ) {
							// It isn't the same
							throw new InvalidParameterException(
									"The content provided couldn't be parsed with the TurtlePatch format: The delete part contains more than one document resource.");
						}
					}

					documentResourceURI = resourceURI;
				}
				else {
					if ( this.getBaseURI() != null ) {
						if ( ! this.getBaseURI().concat(resourceURI).startsWith(documentResourceURIToMatch) ) {
							// The document resources URIs don't match
							throw new InvalidParameterException(
									"The content provided couldn't be parsed with the TurtlePatch format: A extending resource doesn't have the same base as the document resource.");
						}
					}
					else {
						if ( ! resourceURI.startsWith(documentResourceURIToMatch) ) {
							// The extending resource doesn't start with the document resource URI. Maybe it is relative
							// Set the baseURI provided and check again.
							this.setBaseURI(documentResourceURIToMatch);
							if ( ! this.getBaseURI().concat(resourceURI).startsWith(documentResourceURIToMatch) ) {
								// The document resources URIs don't match
								throw new InvalidParameterException(
										"The content provided couldn't be parsed with the TurtlePatch format: A extending resource doesn't have the same base as the document resource.");
							}
						}
					}
				}

			}
		}

	}

	public String getBaseURI() {
		if ( this.baseURI != null )
			return baseURI;
		if ( this.basePart == null )
			return null;

		Pattern basePattern = Pattern.compile(REGEX_C_BASE, Pattern.CASE_INSENSITIVE);
		Matcher baseMatcher = basePattern.matcher(basePart);

		baseMatcher.matches();

		this.baseURI = baseMatcher.group(2);

		return this.baseURI;
	}

	public void setBaseURI(String baseURI) {
		this.basePart = "BASE <" + baseURI + ">";
		this.baseURI = baseURI;
	}

	public void setDefaultPrefixes(Map<String, String> defaultPrefixes) {
		// Will contain all of the final prefixes
		Map<String, String> prefixes = new HashMap<String, String>();

		// Match the prefix_part to retrieve the prefixes declarated there
		Pattern prefixPattern = getPrefixPattern();

		if ( prefixPart != null ) {
			Matcher prefixMatcher = prefixPattern.matcher(prefixPart);
			// Iterate through the matches
			while (prefixMatcher.find()) {
				// The first group gets the prefix key
				String key = prefixMatcher.group(1);
				// The second group gets the prefix value
				String value = prefixMatcher.group(2);
				// If they are not null, add them to the prefixes map
				if ( key != null && value != null ) {
					prefixes.put(key, value);
				}
			}
		}

		// Iterate through the defaultPrefixes
		Iterator<Entry<String, String>> defaultIterator = defaultPrefixes.entrySet().iterator();
		while (defaultIterator.hasNext()) {
			Entry<String, String> prefix = (Entry<String, String>) defaultIterator.next();
			// If the prefix isn't already in the prefixes map, add it (give priority to the ones declared in the entity
			// body)
			if ( ! prefixes.containsKey(prefix.getKey()) )
				prefixes.put(prefix.getKey(), prefix.getValue());
		}

		// Construct the prefix_part again

		// Iterate through the prefixes
		prefixPart = "";
		Iterator<Entry<String, String>> iterator = prefixes.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> prefix = (Entry<String, String>) iterator.next();
			prefixPart = prefixPart.concat("PREFIX " + prefix.getKey() + ": <" + prefix.getValue() + "> ");
		}

	}

	public String getDeleteQuery() {
		if ( deletePart == null )
			return null;

		String deleteQuery = deletePart;

		if ( prefixPart != null )
			deleteQuery = prefixPart.concat(deleteQuery);
		if ( basePart != null )
			deleteQuery = basePart.concat(deleteQuery);

		return deleteQuery;
	}

	public String getInsertQuery() {
		if ( insertPart == null )
			return null;

		String insertQuery = insertPart;

		if ( prefixPart != null )
			insertQuery = prefixPart.concat(insertQuery);
		if ( basePart != null )
			insertQuery = basePart.concat(insertQuery);

		return insertQuery;
	}

	private Pattern getPrefixPattern() {
		Pattern prefixPattern = Pattern.compile(REGEX_C_PREFIX, Pattern.CASE_INSENSITIVE);
		return prefixPattern;
	}

	private Pattern getTurtlePatchPattern() {
		Pattern turtlePatchPattern = Pattern.compile(REGEX_TURTLEPATCH, Pattern.CASE_INSENSITIVE);
		return turtlePatchPattern;
	}
}
