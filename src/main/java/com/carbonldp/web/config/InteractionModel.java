package com.carbonldp.web.config;

import com.carbonldp.descriptions.APIPreferences;

import java.lang.annotation.*;

/**
 * @author MiguelAraCo
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @since 0.10.0-ALPHA
 */
@Target( {ElementType.METHOD, ElementType.TYPE} )
@Retention( RetentionPolicy.RUNTIME )
@Documented
public @interface InteractionModel {
	APIPreferences.InteractionModel[] value();
}
