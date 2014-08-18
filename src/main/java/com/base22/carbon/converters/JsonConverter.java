package com.base22.carbon.converters;

import java.io.IOException;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {

	static final Logger LOG = LoggerFactory.getLogger(JsonConverter.class);

	/**
	 * Serializes the given object to JSON in a compact format.
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJsonCompact(final Object obj) {

		String jsonResult = null;

		try {
			final ObjectMapper mapper = new ObjectMapper();
			jsonResult = mapper.writeValueAsString(obj);
		} catch (JsonGenerationException e) {
			LOG.error("-- toJsonCompact() caught JsonGenerationException: " + e.getMessage());
		} catch (JsonMappingException e) {
			LOG.error("-- toJsonCompact() caught JsonMappingException: " + e.getMessage());
		} catch (IOException e) {
			LOG.error("-- toJsonCompact() caught IOException: " + e.getMessage());
		}

		return jsonResult;

	}

	/**
	 * Serializes the given object to JSON in a more legible, but less compact format.
	 * 
	 * @param obj
	 * @return
	 */
	public static String toJsonPretty(final Object obj) {

		StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();

		try {
			final JsonGenerator jsonGenerator = mapper.getFactory().createGenerator(writer);
			jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
			mapper.writeValue(jsonGenerator, obj);
		} catch (JsonGenerationException e) {
			LOG.error("-- toJsonCompact() caught JsonGenerationException: " + e.getMessage());
		} catch (JsonMappingException e) {
			LOG.error("-- toJsonCompact() caught JsonMappingException: " + e.getMessage());
		} catch (IOException e) {
			LOG.error("-- toJsonCompact() caught IOException: " + e.getMessage());
		}

		return writer.toString();

	}
}
