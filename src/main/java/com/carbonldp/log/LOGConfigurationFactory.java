package com.carbonldp.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.net.URI;

/**
 * @author MiguelAraCo
 * @since 0.32.1
 */
@Plugin( name = "LOGConfigurationFactory", category = ConfigurationFactory.CATEGORY )
@Order( 50 )
public class LOGConfigurationFactory extends ConfigurationFactory {
	static Configuration createConfiguration( String name, ConfigurationBuilder<BuiltConfiguration> configurationBuilder ) {
		configurationBuilder.setConfigurationName( name );

		AppenderComponentBuilder syslogBuilder = LOGConfigurationFactory.createSyslogAppender( configurationBuilder );
		configurationBuilder.add( syslogBuilder );

		AppenderComponentBuilder consoleAppender = LOGConfigurationFactory.createSTDOUTAppender( configurationBuilder );
		configurationBuilder.add( consoleAppender );

		LOGConfigurationFactory.configurePackageSpecificLevels( configurationBuilder );

		RootLoggerComponentBuilder rootLogger = configurationBuilder.newRootLogger( Level.DEBUG );
		rootLogger.add( configurationBuilder.newAppenderRef( "STDOUT" ) );
		rootLogger.add( configurationBuilder.newAppenderRef( "SYSLOG" ) );
		configurationBuilder.add( rootLogger );

		return configurationBuilder.build();
	}

	private static AppenderComponentBuilder createSyslogAppender( ConfigurationBuilder<BuiltConfiguration> configurationBuilder ) {
		AppenderComponentBuilder syslogBuilder = configurationBuilder.newAppender( "SYSLOG", "SYSLOG" );
		syslogBuilder.addAttribute( "format", "RFC5424" ); // Non-customizable
		syslogBuilder.addAttribute( "host", "logger" ); // TODO: Point to the network alias for the syslog-ng service
		syslogBuilder.addAttribute( "port", "601" );
		syslogBuilder.addAttribute( "protocol", "TCP" );
		syslogBuilder.addAttribute( "appName", "carbon-platform" ); // TODO: Customize this value
		syslogBuilder.addAttribute( "includeMDC", "true" ); // Include ContextMap? (non-customizable)
		syslogBuilder.addAttribute( "facility", "USER" ); // From where the
		syslogBuilder.addAttribute( "enterpriseNumber", "18060" );
		syslogBuilder.addAttribute( "newLine", "true" );
		syslogBuilder.addAttribute( "newLineEscape", "#012#011" );
		syslogBuilder.addAttribute( "messageId", "Audit" ); // Type of message being sent. E.g. "TCPIN", "TCPOUT"
		syslogBuilder.addAttribute( "mdcId", "mdc" ); // ContextMap SD id. E.g. [mdc@18060 key="value"]
		syslogBuilder.addAttribute( "id", "App" ); // Default ID to use when StructuredData has no ID
		syslogBuilder.addAttribute( "connectTimeoutMillis", "1000" );
		syslogBuilder.addAttribute( "reconnectionDelayMillis", "5000" );

		syslogBuilder.addComponent(
			configurationBuilder.newComponent( "LoggerFields" )
			                    .addComponent( configurationBuilder.newComponent( "KeyValuePair" ).addAttribute( "key", "thread" ).addAttribute( "value", "%t" ) )
			                    .addComponent( configurationBuilder.newComponent( "KeyValuePair" ).addAttribute( "key", "priority" ).addAttribute( "value", "%p" ) )
			                    .addComponent( configurationBuilder.newComponent( "KeyValuePair" ).addAttribute( "key", "class" ).addAttribute( "value", "%c" ) )
			                    .addComponent( configurationBuilder.newComponent( "KeyValuePair" ).addAttribute( "key", "exception" ).addAttribute( "value", "%ex" ) )
		);

		return syslogBuilder;
	}

	private static AppenderComponentBuilder createSTDOUTAppender( ConfigurationBuilder<BuiltConfiguration> configurationBuilder ) {
		AppenderComponentBuilder consoleAppender = configurationBuilder.newAppender( "STDOUT", "CONSOLE" ).addAttribute( "target", ConsoleAppender.Target.SYSTEM_OUT );
		consoleAppender.add( configurationBuilder.newLayout( "PatternLayout" ).addAttribute( "pattern", "%-5p [%40c{1.}]%notEmpty{[%mdc{shortRequestID}]} -- %m%n" ) );
		return consoleAppender;
	}

	private static void configurePackageSpecificLevels( ConfigurationBuilder<BuiltConfiguration> configurationBuilder ) {
		configurationBuilder.add( configurationBuilder.newLogger( "org.eclipse.jetty", Level.WARN ) );
		configurationBuilder.add( configurationBuilder.newLogger( "jndi", Level.WARN ) );
		// TODO: Find out what class is logging using "/" as a name
		configurationBuilder.add( configurationBuilder.newLogger( "/", Level.WARN ) );
		configurationBuilder.add( configurationBuilder.newLogger( "org.springframework", Level.WARN ) );
		configurationBuilder.add( configurationBuilder.newLogger( "org.openrdf", Level.WARN ) );
		configurationBuilder.add( configurationBuilder.newLogger( "com.carbonldp", Level.TRACE ) );
	}

	@Override
	public Configuration getConfiguration( ConfigurationSource source ) {
		return this.getConfiguration( source.toString(), null );
	}

	@Override
	public Configuration getConfiguration( String name, URI configLocation ) {
		ConfigurationBuilder<BuiltConfiguration> configurationBuilder = LOGConfigurationFactory.newConfigurationBuilder();

		return LOGConfigurationFactory.createConfiguration( name, configurationBuilder );
	}

	@Override
	public Configuration getConfiguration( String name, URI configLocation, ClassLoader loader ) {
		throw new RuntimeException( "Method not supported" );
	}

	@Override
	protected String[] getSupportedTypes() {
		return new String[]{"*"};
	}
}
