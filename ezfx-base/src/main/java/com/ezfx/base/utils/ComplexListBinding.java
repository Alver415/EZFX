package com.ezfx.base.utils;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ComplexListBinding<A, B> implements InvalidationListener, WeakListener {

	private final int cachedHashCode;

	private final Converter<A, B> converter;
	private final WeakReference<ListProperty<A>> propertyRefA;
	private final WeakReference<ListProperty<B>> propertyRefB;
	private ObservableList<A> oldValueA;
	private ObservableList<B> oldValueB;

	private boolean updating = false;

	private ComplexListBinding(ListProperty<A> propertyA, ListProperty<B> propertyB, Converter<A, B> converter) {
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

		if (obj instanceof ComplexListBinding<?, ?> otherBinding) {
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

	public static <A, B> void bindBidirectional(ListProperty<A> propA, ListProperty<B> propB, Converter<A, B> converter) {
		checkParameters(propA, propB);
		final ComplexListBinding<A, B> binding = new ComplexListBinding<>(propA, propB, converter);
		setTo(propA, propB, converter::from);

		propA.getValue();
		propA.addListener(binding);
		propB.addListener(binding);
	}

	private static <A, B> void setTo(ListProperty<A> propA, ListProperty<B> propB, Function<B, A> converter) {
		propA.clear();
		for (B b : propB) {
			A a = converter.apply(b);
			if (!propA.contains(a)) {
				propA.add(a);
			}
		}
	}

	private static void checkParameters(Object propertyA, Object propertyB) {
		Objects.requireNonNull(propertyA, "Both properties must be specified.");
		Objects.requireNonNull(propertyB, "Both properties must be specified.");
		if (propertyA == propertyB) {
			throw new IllegalArgumentException("Cannot bind property to itself");
		}
	}

	protected ListProperty<A> getPropertyA() {
		return propertyRefA.get();
	}

	protected ListProperty<B> getPropertyB() {
		return propertyRefB.get();
	}

	@Override
	public void invalidated(Observable sourceProperty) {
		if (!updating) {
			final ListProperty<A> propertyA = propertyRefA.get();
			final ListProperty<B> propertyB = propertyRefB.get();
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
						ObservableList<A> newValue = propertyA.getValue();
						setTo(propertyA, propertyB, converter::from);
						propertyB.getValue();
						oldValueA = newValue;
					} else {
						ObservableList<B> newValue = propertyB.getValue();
						setTo(propertyB, propertyA, converter::to);
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

	public static <A, B> void unbind(ListProperty<A> propertyA, ListProperty<B> propertyB) {
		checkParameters(propertyA, propertyB);
		final ComplexListBinding<A, B> binding = new ComplexListBinding<>(propertyA, propertyB, null);
		propertyA.removeListener(binding);
		propertyB.removeListener(binding);
	}
}
