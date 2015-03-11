package com.carbonldp.web;

import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.ldp.web.RDFSourceMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
@EnableWebMvc
//@formatter:off
@ComponentScan(
		useDefaultFilters = false,
		basePackages = {"com.carbonldp"},
		includeFilters = {
				@ComponentScan.Filter( type = FilterType.ANNOTATION, value = ControllerAdvice.class ),
				@ComponentScan.Filter( type = FilterType.ANNOTATION, value = Controller.class ),
				@ComponentScan.Filter( type = FilterType.ANNOTATION, value = RequestHandler.class )
		}
)
//@formatter:on
public class WebConfig extends WebMvcConfigurerAdapter {

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add( rdfSourceMessageConverter() );
		converters.add( modelMessageConverter() );
		converters.add( emptyResponseMessageConverter() );
		converters.add( new ByteArrayHttpMessageConverter() );
		converters.add( new StringHttpMessageConverter() );
		converters.add( new FormHttpMessageConverter() );
	}

	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		super.configureContentNegotiation( configurer );
		configurer.favorPathExtension( false );
	}

	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}

	@Bean
	public AbstractModelMessageConverter modelMessageConverter() {
		return new AbstractModelMessageConverter( configurationRepository );
	}

	@Bean
	public RDFSourceMessageConverter rdfSourceMessageConverter() {
		return new RDFSourceMessageConverter();
	}

	@Bean
	public EmptyResponseMessageConverter emptyResponseMessageConverter() {
		return new EmptyResponseMessageConverter();
	}
}
