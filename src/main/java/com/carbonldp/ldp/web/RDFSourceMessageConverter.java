package com.carbonldp.ldp.web;

import static com.carbonldp.Consts.LESS_THAN;
import static com.carbonldp.Consts.MORE_THAN;
import static com.carbonldp.Consts.REL;
import static com.carbonldp.Consts.TYPE;

import org.openrdf.model.Model;
import org.springframework.http.HttpHeaders;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.RDFNodeEnum;
import com.carbonldp.descriptions.RDFResourceDescription;
import com.carbonldp.descriptions.RDFSourceDescription;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.models.RDFSource;
import com.carbonldp.utils.HTTPUtil;
import com.carbonldp.web.ModelMessageConverter;

public class RDFSourceMessageConverter extends ModelMessageConverter<RDFSource> {

	public RDFSourceMessageConverter() {
		super(false, true);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return RDFSource.class.isAssignableFrom(clazz);
	}

	@Override
	protected Model getModelToWrite(RDFSource model) {
		return model.getDocument();
	}

	@Override
	protected void setAdditionalHeaders(RDFSource model, HttpHeaders headers) {
		setLinkHeader(headers);
		setETagHeader(model, headers);
	}

	private void setLinkHeader(HttpHeaders headers) {
		headers.set(HTTPHeaders.LINK, getLinkHeader(RDFResourceDescription.Resource.CLASS));
		headers.set(HTTPHeaders.LINK, getLinkHeader(RDFSourceDescription.Resource.CLASS));
	}

	private String getLinkHeader(RDFNodeEnum classNodeEnum) {
		HTTPHeaderValue headerValue = new HTTPHeaderValue();

		StringBuilder mainBuilder = new StringBuilder();
		mainBuilder.append(LESS_THAN).append(classNodeEnum.getURI().stringValue()).append(MORE_THAN);

		headerValue.setMain(mainBuilder.toString());
		headerValue.setExtendingKey(REL);
		headerValue.setExtendingValue(TYPE);

		return headerValue.toString();
	}

	private void setETagHeader(RDFSource model, HttpHeaders headers) {
		headers.set(HTTPHeaders.ETAG, HTTPUtil.formatWeakETag(model.getModified().toString()));
	}
}
