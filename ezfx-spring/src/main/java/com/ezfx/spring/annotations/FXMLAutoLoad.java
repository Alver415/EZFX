package com.ezfx.spring.annotations;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FXMLAutoLoad {

	Mode mode() default Mode.EAGER;

	String location() default "";

	String resourceBundle() default "";

	enum Mode {
		EAGER, LAZY
	}
}