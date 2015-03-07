package com.carbonldp.spring;

import java.lang.annotation.*;

@Target( ElementType.METHOD )
@Retention( RetentionPolicy.RUNTIME )
@Inherited
@Documented
public @interface Inject {
	String id() default "[unassigned]";
}
