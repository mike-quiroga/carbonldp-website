package com.carbonldp.web.config;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.models.HTTPHeader;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.utils.RDFNodeUtil;
import com.google.common.collect.ImmutableSet;
import org.springframework.web.servlet.mvc.condition.AbstractRequestCondition;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class RequestDocumentTypeRequestCondition extends AbstractRequestCondition<RequestDocumentTypeRequestCondition> {
	private final Set<APIPreferences.RequestDocumentType> requiredTypes;

	public RequestDocumentTypeRequestCondition( APIPreferences.RequestDocumentType... types ) {
		this.requiredTypes = new HashSet<>( Arrays.asList( types ) );
	}

	@Override
	protected Set<APIPreferences.RequestDocumentType> getContent() {
		return this.getRequiredDocumentTypes();
	}

	@Override
	protected String getToStringInfix() {
		return " && ";
	}

	@Override
	public RequestDocumentTypeRequestCondition combine( RequestDocumentTypeRequestCondition other ) {
		return ! other.getRequiredDocumentTypes().isEmpty() ? other : this;
	}

	@Override
	public RequestDocumentTypeRequestCondition getMatchingCondition( HttpServletRequest request ) {
		if ( this.getRequiredDocumentTypes().size() == 0 ) return this;

		Set<APIPreferences.RequestDocumentType> requestDocumentTypes = getRequestDocumentTypes( request );
		if ( requestDocumentTypes.isEmpty() ) return null;

		for ( APIPreferences.RequestDocumentType requiredDocumentType : this.getRequiredDocumentTypes() ) {
			if ( ! requestDocumentTypes.contains( requiredDocumentType ) ) return null;
		}

		return this;
	}

	private Set<APIPreferences.RequestDocumentType> getRequestDocumentTypes( HttpServletRequest request ) {
		Set<APIPreferences.RequestDocumentType> documentTypes = new HashSet<>();

		HTTPHeader preferHeader = new HTTPHeader( request.getHeaders( HTTPHeaders.LINK ) );
		List<HTTPHeaderValue> filteredValues = HTTPHeader.filterHeaderValues( preferHeader, null, null, "rel", "type" );

		for ( HTTPHeaderValue filteredValue : filteredValues ) {
			APIPreferences.RequestDocumentType documentType = RDFNodeUtil.findByURI( filteredValue.getMainValue(), APIPreferences.RequestDocumentType.class );
			if ( documentType != null ) documentTypes.add( documentType );
		}

		return documentTypes;
	}

	@Override
	public int compareTo( RequestDocumentTypeRequestCondition other, HttpServletRequest httpServletRequest ) {
		// TODO: Corroborate that this is the correct implementation
		return other.getRequiredDocumentTypes().size() - this.getRequiredDocumentTypes().size();
	}

	public Set<APIPreferences.RequestDocumentType> getRequiredDocumentTypes() {
		return ImmutableSet.copyOf( this.requiredTypes );
	}
}
