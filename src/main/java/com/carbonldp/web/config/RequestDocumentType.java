package com.carbonldp.web.config;

import com.carbonldp.descriptions.APIPreferences;

import java.lang.annotation.*;

/**
 * @author MiguelAraCo
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @since _version_
 */
@Target( {ElementType.METHOD, ElementType.TYPE} )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface RequestDocumentType {
	APIPreferences.RequestDocumentType[] value();
}
