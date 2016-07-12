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

		disableDefaultCORSProcessor( handlerMapping );

		return handlerMapping;
	}

	/**
	 * This method disables the default spring CORSProcessor that works with @{@link org.springframework.web.bind.annotation.CrossOrigin} annotations.
	 * The platform is using a filter based system to provide CORS support and spring native system interferes with it.
	 *
	 * @param handlerMapping
	 * @see <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/html/cors.html">CORS Support</a>
	 * @see com.carbonldp.web.cors.CORSContextFilter
	 */
	private void disableDefaultCORSProcessor( CustomRequestMappingHandlerMapping handlerMapping ) {
		handlerMapping.setCorsProcessor( ( configuration, request, response ) -> true );
	}
}
