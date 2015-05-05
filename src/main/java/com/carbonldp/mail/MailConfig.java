package com.carbonldp.mail;

import com.carbonldp.config.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {
	@Autowired
	private ConfigurationRepository configurationRepository;

	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

		MailSettings settings = configurationRepository.getMailSettings();

		mailSender.setHost( settings.getHost() );
		mailSender.setProtocol( settings.getProtocol() );
		mailSender.setPort( settings.getPort() );
		mailSender.setUsername( settings.getUsername() );
		mailSender.setPassword( settings.getPassword() );

		mailSender.setJavaMailProperties( settings.getJavaMailProperties() );

		return mailSender;
	}
}
