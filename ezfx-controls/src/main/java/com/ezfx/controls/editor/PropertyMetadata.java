package com.ezfx.controls.editor;

import java.lang.annotation.*;


@Inherited
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertyMetadata {

	String DEFAULT_DISPLAY_NAME = "";

	String displayName() default DEFAULT_DISPLAY_NAME;

	String DEFAULT_CATEGORY_TITLE = "";

	String categoryTitle() default DEFAULT_CATEGORY_TITLE;

	int DEFAULT_CATEGORY_ORDER = Integer.MAX_VALUE / 2;

	int categoryOrder() default DEFAULT_CATEGORY_ORDER;

	int DEFAULT_ORDER = Integer.MAX_VALUE / 2;

	int order() default DEFAULT_ORDER;

	boolean DEFAULT_IGNORE = false;

	boolean ignore() default DEFAULT_IGNORE;
}
