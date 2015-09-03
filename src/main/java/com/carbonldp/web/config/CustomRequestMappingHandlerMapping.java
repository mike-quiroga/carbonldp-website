package com.carbonldp.web.config;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
	@Override
	protected RequestCondition<CustomRequestConditionsHolder> getCustomTypeCondition( Class<?> handlerType ) {
		InteractionModelRequestCondition interactionModel = getInteractionModelRequestCondition( handlerType );
		RequestDocumentTypeRequestCondition documentType = getRequestDocumentTypeRequestCondition( handlerType );

		return new CustomRequestConditionsHolder( interactionModel, documentType );
	}

	@Override
	protected RequestCondition<CustomRequestConditionsHolder> getCustomMethodCondition( Method method ) {
		InteractionModelRequestCondition interactionModel = getInteractionModelRequestCondition( method );
		RequestDocumentTypeRequestCondition documentType = getRequestDocumentTypeRequestCondition( method );

		return new CustomRequestConditionsHolder( interactionModel, documentType );
	}

	private InteractionModelRequestCondition getInteractionModelRequestCondition( Class<?> handlerType ) {
		InteractionModel interactionModel = AnnotationUtils.findAnnotation( handlerType, InteractionModel.class );
		if ( interactionModel == null ) return new InteractionModelRequestCondition();

		return new InteractionModelRequestCondition( interactionModel.value() );
	}

	private InteractionModelRequestCondition getInteractionModelRequestCondition( Method method ) {
		InteractionModel interactionModel = AnnotationUtils.findAnnotation( method, InteractionModel.class );
		if ( interactionModel == null ) return new InteractionModelRequestCondition();

		return new InteractionModelRequestCondition( interactionModel.value() );
	}

	private RequestDocumentTypeRequestCondition getRequestDocumentTypeRequestCondition( Class<?> handlerType ) {
		RequestDocumentType requestDocumentType = AnnotationUtils.findAnnotation( handlerType, RequestDocumentType.class );

		if ( requestDocumentType == null ) return new RequestDocumentTypeRequestCondition();

		return new RequestDocumentTypeRequestCondition( requestDocumentType.value() );
	}

	private RequestDocumentTypeRequestCondition getRequestDocumentTypeRequestCondition( Method method ) {
		RequestDocumentType requestDocumentType = AnnotationUtils.findAnnotation( method, RequestDocumentType.class );

		if ( requestDocumentType == null ) return new RequestDocumentTypeRequestCondition();

		return new RequestDocumentTypeRequestCondition( requestDocumentType.value() );
	}
}