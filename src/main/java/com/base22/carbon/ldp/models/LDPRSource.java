package com.base22.carbon.ldp.models;

import java.util.List;

import org.joda.time.DateTime;

import com.base22.carbon.CarbonException;
import com.hp.hpl.jena.rdf.model.Resource;

public interface LDPRSource extends LDPResource {

	public DateTime getCreated();

	public DateTime getModified();

	/**
	 * Returns the current value of the entity tag for this instance/variant.
	 * 
	 * <p>
	 * The entity tag value is a DateTime timestamp in ISO8601 format. For example:
	 * <code>2014-07-09T12:03:01.936-05:00</code>. This method does not return a quoted string or a string formatted in
	 * any other format than ISO8601. It is often necessary, therefore, for developers to format the return value as a
	 * weak, quoted ETag value (e.g. <code>W/&quot;&quot;</code>) before using it as an HTTP response header. For that
	 * purpose, developers can pass the return value into the method formatWeakETag(), which is in the
	 * AbstractBaseRdfAPIController class that all RdfAPI Controllers extend; otherwise developers can simply use the
	 * raw return value as-is for lower-level comparisons.
	 * </p>
	 * 
	 * @return the current value of the entity tag
	 */
	public String getETag();

	public boolean hasExtendingResource(String resourceSlug);

	public Resource getExtendingResource(String resourceSlug);

	public List<Resource> getExtendingResources();

	public boolean hasSystemResource(String resourceSlug);

	public Resource getSystemResource(String resourceSlug);

	public List<Resource> getSystemResources();

	public Resource createSystemResource(String resourceSlug) throws CarbonException;

	public void setTimestamps();

	public void setTimestamps(DateTime createdDate);

	public void setTimestamps(DateTime createdDate, DateTime modifiedDate);

	public void touch();
}
