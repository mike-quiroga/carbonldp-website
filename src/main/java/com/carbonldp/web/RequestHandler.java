package com.carbonldp.web;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target( ElementType.TYPE )
@Component
@Scope( proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request" )
public @interface RequestHandler {

}
