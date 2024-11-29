package com.ezfx.filesystem;

import com.ezfx.filesystem.utils.MapExpressionHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableMapValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class MapAttributeProperty<K, V, S>
		extends WritableAttributeProperty<ObservableMap<K, V>, Map<K, S>>
		implements ObservableMapValue<K, V> {

	private final MapChangeListener<K, V> mapChangeListener = change -> {
		invalidateProperties();
		invalidated();
		fireValueChangedEvent(change);
	};

	private ObservableValue<? extends ObservableMap<K, V>> observable = null;
	private InvalidationListener listener = null;
	private MapExpressionHelper<K, V> helper = null;

	private SizeProperty size0;
	private EmptyProperty empty0;

	/**
	 * The Constructor of {@code MapPropertyBase}
	 */
	public MapAttributeProperty(FileSystemEntry entry, String name, ObservableMap<K, V> initialValue) {
		super(entry, name, initialValue);
		if (initialValue != null) {
			initialValue.addListener(mapChangeListener);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValue(ObservableMap<K, V> v) {
		set(v);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bindBidirectional(Property<ObservableMap<K, V>> other) {
		Bindings.bindBidirectional(this, other);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unbindBidirectional(Property<ObservableMap<K, V>> other) {
		Bindings.unbindBidirectional(this, other);
	}

	public ReadOnlyIntegerProperty sizeProperty() {
		if (size0 == null) {
			size0 = new SizeProperty();
		}
		return size0;
	}

	private class SizeProperty extends ReadOnlyIntegerPropertyBase {
		@Override
		public int get() {
			return size();
		}

		@Override
		public Object getBean() {
			return MapAttributeProperty.this;
		}

		@Override
		public String getName() {
			return "size";
		}

		@Override
		protected void fireValueChangedEvent() {
			super.fireValueChangedEvent();
		}
	}

	public ReadOnlyBooleanProperty emptyProperty() {
		if (empty0 == null) {
			empty0 = new EmptyProperty();
		}
		return empty0;
	}

	private class EmptyProperty extends ReadOnlyBooleanPropertyBase {

		@Override
		public boolean get() {
			return isEmpty();
		}

		@Override
		public Object getBean() {
			return MapAttributeProperty.this;
		}

		@Override
		public String getName() {
			return "empty";
		}

		@Override
		protected void fireValueChangedEvent() {
			super.fireValueChangedEvent();
		}
	}

	@Override
	public void addListener(InvalidationListener listener) {
		helper = MapExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		helper = MapExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void addListener(ChangeListener<? super ObservableMap<K, V>> listener) {
		helper = MapExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void removeListener(ChangeListener<? super ObservableMap<K, V>> listener) {
		helper = MapExpressionHelper.removeListener(helper, listener);
	}

	@Override
	public void addListener(MapChangeListener<? super K, ? super V> listener) {
		helper = MapExpressionHelper.addListener(helper, this, listener);
	}

	@Override
	public void removeListener(MapChangeListener<? super K, ? super V> listener) {
		helper = MapExpressionHelper.removeListener(helper, listener);
	}

	/**
	 * Sends notifications to all attached
	 * {@link javafx.beans.InvalidationListener InvalidationListeners},
	 * {@link javafx.beans.value.ChangeListener ChangeListeners}, and
	 * {@link javafx.collections.MapChangeListener}.
	 * <p>
	 * This method is called when the value is changed, either manually by
	 * calling {@link #set(javafx.collections.ObservableMap)} or in case of a bound property, if the
	 * binding becomes invalid.
	 */
	protected void fireValueChangedEvent() {
		MapExpressionHelper.fireValueChangedEvent(helper);
	}

	/**
	 * Sends notifications to all attached
	 * {@link javafx.beans.InvalidationListener InvalidationListeners},
	 * {@link javafx.beans.value.ChangeListener ChangeListeners}, and
	 * {@link javafx.collections.MapChangeListener}.
	 * <p>
	 * This method is called when the content of the list changes.
	 *
	 * @param change the change that needs to be propagated
	 */
	protected void fireValueChangedEvent(MapChangeListener.Change<? extends K, ? extends V> change) {
		MapExpressionHelper.fireValueChangedEvent(helper, change);
	}

	private void invalidateProperties() {
		if (size0 != null) {
			size0.fireValueChangedEvent();
		}
		if (empty0 != null) {
			empty0.fireValueChangedEvent();
		}
	}

	private void markInvalid(ObservableMap<K, V> oldValue) {
		if (valid) {
			if (oldValue != null) {
				oldValue.removeListener(mapChangeListener);
			}
			valid = false;
			invalidateProperties();
			invalidated();
			fireValueChangedEvent();
		}
	}

	@Override
	public ObservableMap<K, V> get() {
		if (!valid) {
			value = observable == null ? value : observable.getValue();
			valid = true;
			if (value != null) {
				value.addListener(mapChangeListener);
			}
		}
		return value;
	}

	@Override
	public void set(ObservableMap<K, V> newValue) {
		if (isBound()) {
			throw new RuntimeException((getBean() != null && getName() != null ?
					getBean().getClass().getSimpleName() + "." + getName() + " : " : "") + "A bound value cannot be set.");
		}
		if (value != newValue) {
			final ObservableMap<K, V> oldValue = value;
			value = newValue;
			markInvalid(oldValue);
		}
	}

	@Override
	public boolean isBound() {
		return observable != null;
	}

	@Override
	public void bind(final ObservableValue<? extends ObservableMap<K, V>> newObservable) {
		if (newObservable == null) {
			throw new NullPointerException("Cannot bind to null");
		}
		if (newObservable != observable) {
			unbind();
			observable = newObservable;
			if (listener == null) {
				listener = new Listener<>(this);
			}
			observable.addListener(listener);
			markInvalid(value);
		}
	}

	@Override
	public void unbind() {
		if (observable != null) {
			value = observable.getValue();
			observable.removeListener(listener);
			observable = null;
		}
	}

	private static class Listener<K, V> implements InvalidationListener, WeakListener {

		private final WeakReference<MapAttributeProperty<K, V, ?>> wref;

		public Listener(MapAttributeProperty<K, V, ?> ref) {
			this.wref = new WeakReference<>(ref);
		}

		@Override
		public void invalidated(Observable observable) {
			MapAttributeProperty<K, V, ?> ref = wref.get();
			if (ref == null) {
				observable.removeListener(this);
			} else {
				ref.markInvalid(ref.value);
			}
		}

		@Override
		public boolean wasGarbageCollected() {
			return wref.get() == null;
		}
	}


	@Override
	public int size() {
		return getNonNull().size();
	}

	@Override
	public boolean isEmpty() {
		return getNonNull().isEmpty();
	}

	@Override
	public boolean containsKey(Object obj) {
		return getNonNull().containsKey(obj);
	}

	@Override
	public boolean containsValue(Object obj) {
		return getNonNull().containsValue(obj);
	}

	@Override
	public V put(K key, V value) {
		return getNonNull().put(key, value);
	}

	@Override
	public V remove(Object obj) {
		return getNonNull().remove(obj);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> elements) {
		getNonNull().putAll(elements);
	}

	@Override
	public void clear() {
		getNonNull().clear();
	}

	@Override
	public Set<K> keySet() {
		return getNonNull().keySet();
	}

	@Override
	public Collection<V> values() {
		return getNonNull().values();
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return getNonNull().entrySet();
	}

	@Override
	public V get(Object key) {
		return getNonNull().get(key);
	}

	private ObservableMap<K, V> getNonNull() {
		ObservableMap<K, V> map = get();
		return map == null ? FXCollections.emptyObservableMap() : map;
	}
}
