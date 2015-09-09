package com.carbonldp.web.config;

import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;

/**
 * @author MiguelAraCo
 * @since 0.10.0-ALPHA
 */
public class CustomRequestConditionsHolder implements RequestCondition<CustomRequestConditionsHolder> {
	private final InteractionModelRequestCondition interactionModelsCondition;
	private final RequestDocumentTypeRequestCondition documentTypeCondition;

	public CustomRequestConditionsHolder( InteractionModelRequestCondition interactionModelsCondition, RequestDocumentTypeRequestCondition documentTypeCondition ) {
		this.interactionModelsCondition = interactionModelsCondition != null ? interactionModelsCondition : new InteractionModelRequestCondition();
		this.documentTypeCondition = documentTypeCondition != null ? documentTypeCondition : new RequestDocumentTypeRequestCondition();
	}

	@Override
	public CustomRequestConditionsHolder combine( CustomRequestConditionsHolder other ) {
		InteractionModelRequestCondition interactionModelCondition = this.getInteractionModelsCondition().combine( other.getInteractionModelsCondition() );
		RequestDocumentTypeRequestCondition documentTypeCondition = this.getDocumentTypeCondition().combine( other.getDocumentTypeCondition() );

		return new CustomRequestConditionsHolder( interactionModelCondition, documentTypeCondition );
	}

	@Override
	public CustomRequestConditionsHolder getMatchingCondition( HttpServletRequest request ) {
		InteractionModelRequestCondition interactionModel = this.getInteractionModelsCondition().getMatchingCondition( request );
		RequestDocumentTypeRequestCondition documentTypeCondition = this.getDocumentTypeCondition().getMatchingCondition( request );

		if ( interactionModel == null || documentTypeCondition == null ) return null;

		return new CustomRequestConditionsHolder( interactionModel, documentTypeCondition );
	}

	@Override
	public int compareTo( CustomRequestConditionsHolder other, HttpServletRequest request ) {
		int result = this.getInteractionModelsCondition().compareTo( other.getInteractionModelsCondition(), request );
		if ( result != 0 ) return result;

		result = this.getDocumentTypeCondition().compareTo( other.getDocumentTypeCondition(), request );
		if ( result != 0 ) return result;

		return 0;
	}

	public InteractionModelRequestCondition getInteractionModelsCondition() {
		return interactionModelsCondition;
	}

	public RequestDocumentTypeRequestCondition getDocumentTypeCondition() {
		return documentTypeCondition;
	}
}
