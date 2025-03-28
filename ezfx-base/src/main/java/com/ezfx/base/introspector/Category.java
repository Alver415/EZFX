package com.ezfx.base.introspector;

import java.util.Comparator;

public interface Category extends Comparable<Category> {

	int order();

	String title();

	Comparator<Category> COMPARATOR = Comparator.comparingInt(Category::order);

	@Override
	default int compareTo(Category other) {
		return COMPARATOR.thenComparing(Category::title).compare(this, other);
	}

	record Simple(String title, int order) implements Category {

	}

	static Category of(String title) {
		return new Simple(title, PropertyMetadata.DEFAULT_CATEGORY_ORDER);
	}
	static Category of(String title, int order) {
		return new Simple(title, order);
	}

}
