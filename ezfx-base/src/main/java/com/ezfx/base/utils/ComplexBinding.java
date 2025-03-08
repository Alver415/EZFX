package com.ezfx.base.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.property.Property;
import javafx.util.Subscription;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComplexBinding<A, B> implements InvalidationListener, WeakListener {

	private final int cachedHashCode;

	private final Converter<A, B> converter;
	private final WeakReference<Property<A>> propertyRefA;
	private final WeakReference<Property<B>> propertyRefB;
	private A oldValueA;
	private B oldValueB;

	private boolean updating = false;

	private ComplexBinding(Property<A> propertyA, Property<B> propertyB, Converter<A, B> converter) {
		this.cachedHashCode = propertyA.hashCode() * propertyB.hashCode();
		this.oldValueA = propertyA.getValue();
		this.oldValueB = propertyB.getValue();
		this.propertyRefA = new WeakReference<>(propertyA);
		this.propertyRefB = new WeakReference<>(propertyB);
		this.converter = converter;
	}

	@Override
	public int hashCode() {
		return cachedHashCode;
	}

	@Override
	public boolean wasGarbageCollected() {
		return (getPropertyA() == null) || (getPropertyB() == null);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		final Object propertyA1 = getPropertyA();
		final Object propertyA2 = getPropertyB();
		if ((propertyA1 == null) || (propertyA2 == null)) {
			return false;
		}

		if (obj instanceof ComplexBinding<?, ?> otherBinding) {
			final Object propertyB1 = otherBinding.getPropertyA();
			final Object propertyB2 = otherBinding.getPropertyB();
			if ((propertyB1 == null) || (propertyB2 == null)) {
				return false;
			}

			if (propertyA1 == propertyB1 && propertyA2 == propertyB2) {
				return true;
			}
			if (propertyA1 == propertyB2 && propertyA2 == propertyB1) {
				return true;
			}
		}
		return false;
	}

	public static <T> Subscription bindBidirectional(Property<T> propA, Property<T> propB) {
		return bindBidirectional(propA, propB, Converter.identity());
	}
	public static <A, B> Subscription bindBidirectional(Property<A> propA, Property<B> propB, Converter<A, B> converter) {
		checkParameters(propA, propB);
		final ComplexBinding<A, B> binding = new ComplexBinding<>(propA, propB, converter);
		propA.setValue(converter.from(propB.getValue()));
		propA.getValue();
		propA.addListener(binding);
		propB.addListener(binding);

		return () -> {
			propA.removeListener(binding);
			propB.removeListener(binding);
		};
	}


	private static void checkParameters(Object propertyA, Object propertyB) {
		Objects.requireNonNull(propertyA, "Both properties must be specified.");
		Objects.requireNonNull(propertyB, "Both properties must be specified.");
		if (propertyA == propertyB) {
			throw new IllegalArgumentException("Cannot bind property to itself");
		}
	}

	protected Property<A> getPropertyA() {
		return propertyRefA.get();
	}

	protected Property<B> getPropertyB() {
		return propertyRefB.get();
	}

	@Override
	public void invalidated(Observable sourceProperty) {
		if (!updating) {
			final Property<A> propertyA = propertyRefA.get();
			final Property<B> propertyB = propertyRefB.get();
			if ((propertyA == null) || (propertyB == null)) {
				if (propertyA != null) {
					propertyA.removeListener(this);
				}
				if (propertyB != null) {
					propertyB.removeListener(this);
				}
			} else {
				try {
					updating = true;
					if (propertyA == sourceProperty) {
						A newValue = propertyA.getValue();
						propertyB.setValue(converter.to(newValue));
						propertyB.getValue();
						oldValueA = newValue;
					} else {
						B newValue = propertyB.getValue();
						propertyA.setValue(converter.from(newValue));
						propertyA.getValue();
						oldValueB = newValue;
					}
				} catch (RuntimeException e) {
					try {
						if (propertyA == sourceProperty) {
							propertyA.setValue(oldValueA);
							propertyA.getValue();
						} else {
							propertyB.setValue(oldValueB);
							propertyB.getValue();
						}
					} catch (Exception e2) {
						e2.addSuppressed(e);
						unbind(propertyA, propertyB);
						throw new RuntimeException(
								"Complex binding failed together with an attempt"
										+ " to restore the source property to the previous value."
										+ " Removing the bidirectional binding from properties " +
										propertyA + " and " + propertyB, e2);
					}
					throw new RuntimeException(
							"Complex binding failed, setting to the previous value", e);
				} finally {
					updating = false;
				}
			}
		}
	}

	public static <A, B> void unbind(Property<A> propertyA, Property<B> propertyB) {
		checkParameters(propertyA, propertyB);
		final ComplexBinding<A, B> binding = new ComplexBinding<>(propertyA, propertyB, null);
		propertyA.removeListener(binding);
		propertyB.removeListener(binding);
	}
}
