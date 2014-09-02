package com.base22.carbon;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
//@ComponentScan(basePackages = {"com.base22.carbon"})
public class SpringWebMvcConfig extends WebMvcConfigurerAdapter {

	@Override  
	 public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// registry.addResourceHandler("/static/**").addResourceLocations("/static/").setCachePeriod(31556926);
		registry.addResourceHandler("/static/**").addResourceLocations("/static/");
	}

	/*
	 * This method allows for mapping the DispatcherServlet to "/" (thus overriding
	 * the mapping of the container's default Servlet), while still allowing
	 * static resource requests to be handled by the container's default
	 * Servlet. It configures a DefaultServletHttpRequestHandler with a URL
	 * mapping of "/**" and the lowest priority relative to other URL mappings.
	 * 
	 * This handler will forward all requests to the default Servlet. Therefore
	 * it is important that it remains last in the order of all other URL
	 * HandlerMappings. That will be the case if you use <mvc:annotation-driven>
	 * or alternatively if you are setting up your own customized HandlerMapping
	 * instance be sure to set its order property to a value lower than that of
	 * the DefaultServletHttpRequestHandler, which is Integer.MAX_VALUE.
	 * 
	 * The caveat to overriding the "/" Servlet mapping is that
	 * the RequestDispatcher for the default Servlet must be retrieved by name
	 * rather than by path. The DefaultServletHttpRequestHandler will attempt to
	 * auto-detect the default Servlet for the container at startup time, using
	 * a list of known names for most of the major Servlet containers (including
	 * Tomcat, Jetty, GlassFish, JBoss, Resin, WebLogic, and WebSphere). If the
	 * default Servlet has been custom configured with a different name, or if a
	 * different Servlet container is being used where the default Servlet name
	 * is unknown, then the default Servlet's name must be explicitly provided
	 * as in the following example:
	 * 
	 * configurer.enable("myCustomDefaultServlet");
	 * 
	 * (non-Javadoc)
	 * @see
	 * org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
	 * #configureDefaultServletHandling(org.springframework.web.servlet.config.
	 * annotation.DefaultServletHandlerConfigurer)
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

}