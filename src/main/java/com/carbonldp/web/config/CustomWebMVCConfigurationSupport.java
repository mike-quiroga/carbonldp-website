package com.carbonldp.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author MiguelAraCo
 * @since 0.10.0-ALPHA
 */
@Configuration
public class CustomWebMVCConfigurationSupport extends DelegatingWebMvcConfiguration {

	@Override
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		return super.requestMappingHandlerAdapter();
	}

	@Override
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		CustomRequestMappingHandlerMapping handlerMapping = new CustomRequestMappingHandlerMapping();
		handlerMapping.setOrder( 0 );
		handlerMapping.setInterceptors( this.getInterceptors() );
		handlerMapping.setContentNegotiationManager( this.mvcContentNegotiationManager() );

		PathMatchConfigurer configurer = this.getPathMatchConfigurer();
		if ( configurer.isUseSuffixPatternMatch() != null ) {
			handlerMapping.setUseSuffixPatternMatch( configurer.isUseSuffixPatternMatch() );
		}

		if ( configurer.isUseRegisteredSuffixPatternMatch() != null ) {
			handlerMapping.setUseRegisteredSuffixPatternMatch( configurer.isUseRegisteredSuffixPatternMatch() );
		}

		if ( configurer.isUseTrailingSlashMatch() != null ) {
			handlerMapping.setUseTrailingSlashMatch( configurer.isUseTrailingSlashMatch() );
		}

		if ( configurer.getPathMatcher() != null ) {
			handlerMapping.setPathMatcher( configurer.getPathMatcher() );
		}

		if ( configurer.getUrlPathHelper() != null ) {
			handlerMapping.setUrlPathHelper( configurer.getUrlPathHelper() );
		}

		return handlerMapping;
	}
}
