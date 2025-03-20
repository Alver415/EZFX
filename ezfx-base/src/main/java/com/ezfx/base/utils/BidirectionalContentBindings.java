package com.ezfx.base.utils;

import javafx.beans.WeakListener;
import javafx.collections.*;
import javafx.util.Subscription;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

public class BidirectionalContentBindings {

	private static void checkParameters(Object objectA, Object objectB) {
		if ((objectA == null) || (objectB == null)) {
			throw new NullPointerException("Both parameters must be specified.");
		}
		if (objectA == objectB) {
			throw new IllegalArgumentException("Cannot bind object to itself");
		}
	}

	public static <T> ListContentBinding<T, T> bind(ObservableList<T> listA, ObservableList<T> listB) {
		return bind(listA, listB, Converter.identity());
	}

	public static <A, B> ListContentBinding<A, B> bind(ObservableList<A> listA, ObservableList<B> listB, Converter<A, B> converter) {
		checkParameters(listA, listB);
		final ListContentBinding<A, B> binding = new ListContentBinding<>(listA, listB, converter);
		listA.setAll(listB.stream().map(converter::from).toList());
		listA.addListener(binding.listenerA);
		listB.addListener(binding.listenerB);
		return binding;
	}

	public static <T> SetContentBinding<T, T> bind(ObservableSet<T> setA, ObservableSet<T> setB) {
		return bind(setA, setB, Converter.identity());
	}

	public static <A, B> SetContentBinding<A, B> bind(ObservableSet<A> setA, ObservableSet<B> setB, Converter<A, B> converter) {
		checkParameters(setA, setB);
		final SetContentBinding<A, B> binding = new SetContentBinding<>(setA, setB, converter);
		setA.clear();
		setA.addAll(setB.stream().map(converter::from).collect(Collectors.toSet()));
		setA.addListener(binding.listenerA);
		setB.addListener(binding.listenerB);
		return binding;
	}

	public static <K, V> Object bind(ObservableMap<K, V> mapA, ObservableMap<K, V> mapB) {
		return bind(mapA, mapB, Converter.identity(), Converter.identity());
	}

	public static <AK, AV, BK, BV> Object bind(ObservableMap<AK, AV> mapA, ObservableMap<BK, BV> mapB, Converter<AK, BK> keyConverter, Converter<AV, BV> valueConverter) {
		checkParameters(mapA, mapB);
		final MapContentBinding<AK, AV, BK, BV> binding = new MapContentBinding<>(mapA, mapB, keyConverter, valueConverter);
		mapA.clear();
		mapA.putAll(mapB.entrySet().stream().collect(Collectors.toMap(
				entry -> keyConverter.from(entry.getKey()),
				entry -> valueConverter.from(entry.getValue()))));
		mapA.addListener(binding.listenerA);
		mapB.addListener(binding.listenerB);
		return binding;
	}

	public static class ListContentBinding<A, B> implements Subscription, WeakListener {

		private final WeakReference<ObservableList<A>> referenceA;
		private final WeakReference<ObservableList<B>> referenceB;

		private final ListChangeListener<A> listenerA;
		private final ListChangeListener<B> listenerB;

		private final Converter<A, B> converter;

		private boolean updating = false;

		public ListContentBinding(ObservableList<A> listA, ObservableList<B> listB, Converter<A, B> converter) {
			this.converter = converter;

			this.referenceA = new WeakReference<>(listA);
			this.referenceB = new WeakReference<>(listB);

			this.listenerA = this::listenerA;
			this.listenerB = this::listenerB;
		}

		private void listenerA(ListChangeListener.Change<? extends A> change) {
			if (!updating) {
				final ObservableList<A> listA = referenceA.get();
				final ObservableList<B> listB = referenceB.get();
				if ((listA == null) || (listB == null)) {
					if (listA != null) {
						listA.removeListener(listenerA);
					}
					if (listB != null) {
						listB.removeListener(listenerB);
					}
				} else {
					try {
						updating = true;
						while (change.next()) {
							if (change.wasPermutated()) {
								List<? extends A> aSubList = change.getList().subList(change.getFrom(), change.getTo());
								List<? extends B> bsubList = aSubList.stream().map(converter::to).toList();

								listB.remove(change.getFrom(), change.getTo());
								listB.addAll(change.getFrom(), bsubList);
							} else {
								if (change.wasRemoved()) {
									listB.remove(change.getFrom(), change.getFrom() + change.getRemovedSize());
								}
								if (change.wasAdded()) {
									List<? extends A> aSubList = change.getAddedSubList();
									List<? extends B> bsubList = aSubList.stream().map(converter::to).toList();

									listB.addAll(change.getFrom(), bsubList);
								}
							}
						}
					} finally {
						updating = false;
					}
				}
			}
		}

		private void listenerB(ListChangeListener.Change<? extends B> change) {
			if (!updating) {
				final ObservableList<A> listA = referenceA.get();
				final ObservableList<B> listB = referenceB.get();
				if ((listA == null) || (listB == null)) {
					if (listA != null) {
						listA.removeListener(listenerA);
					}
					if (listB != null) {
						listB.removeListener(listenerB);
					}
				} else {
					try {
						updating = true;
						while (change.next()) {
							if (change.wasPermutated()) {
								List<? extends B> aSubList = change.getList().subList(change.getFrom(), change.getTo());
								List<? extends A> bsubList = aSubList.stream().map(converter::from).toList();

								listA.remove(change.getFrom(), change.getTo());
								listA.addAll(change.getFrom(), bsubList);
							} else {
								if (change.wasRemoved()) {
									listA.remove(change.getFrom(), change.getFrom() + change.getRemovedSize());
								}
								if (change.wasAdded()) {
									List<? extends B> aSubList = change.getAddedSubList();
									List<? extends A> bsubList = aSubList.stream().map(converter::from).toList();

									listA.addAll(change.getFrom(), bsubList);
								}
							}
						}
					} finally {
						updating = false;
					}
				}
			}
		}

		@Override
		public boolean wasGarbageCollected() {
			return (referenceA.get() == null) || (referenceB.get() == null);
		}

		@Override
		public int hashCode() {
			final ObservableList<A> listA = referenceA.get();
			final ObservableList<B> listB = referenceB.get();
			final int hcA = (listA == null) ? 0 : listA.hashCode();
			final int hcB = (listB == null) ? 0 : listB.hashCode();
			return hcA * hcB;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			final Object objectA = referenceA.get();
			final Object objectB = referenceB.get();
			if ((objectA == null) || (objectB == null)) {
				return false;
			}

			if (obj instanceof ListContentBinding<?, ?> otherBinding) {
				final Object otherA = otherBinding.referenceA.get();
				final Object otherB = otherBinding.referenceB.get();
				if ((otherA == null) || (otherB == null)) {
					return false;
				}

				if ((objectA == otherA) && (objectB == otherB)) {
					return true;
				}
				if ((objectA == otherB) && (objectB == otherA)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void unsubscribe() {
			ObservableList<A> listA = referenceA.get();
			if (listA != null) {
				listA.removeListener(listenerA);
			}
			ObservableList<B> listB = referenceB.get();
			if (listB != null) {
				listB.removeListener(listenerB);
			}
		}
	}

	public static class SetContentBinding<A, B> implements Subscription, WeakListener {

		private final WeakReference<ObservableSet<A>> referenceA;
		private final WeakReference<ObservableSet<B>> referenceB;

		private final SetChangeListener<A> listenerA;
		private final SetChangeListener<B> listenerB;

		private final Converter<A, B> converter;

		private boolean updating = false;


		public SetContentBinding(ObservableSet<A> setA, ObservableSet<B> setB, Converter<A, B> converter) {
			this.converter = converter;

			this.referenceA = new WeakReference<>(setA);
			this.referenceB = new WeakReference<>(setB);

			this.listenerA = this::listenerA;
			this.listenerB = this::listenerB;
		}

		private void listenerA(SetChangeListener.Change<? extends A> change) {
			if (!updating) {
				final ObservableSet<A> setA = referenceA.get();
				final ObservableSet<B> setB = referenceB.get();
				if ((setA == null) || (setB == null)) {
					if (setA != null) {
						setA.removeListener(listenerA);
					}
					if (setB != null) {
						setB.removeListener(listenerB);
					}
				} else {
					try {
						updating = true;
						if (change.wasRemoved()) {
							A removedA = change.getElementRemoved();
							B removedB = converter.to(removedA);
							setB.remove(removedB);
						} else {
							A addedA = change.getElementAdded();
							B addedB = converter.to(addedA);
							setB.add(addedB);
						}
					} finally {
						updating = false;
					}
				}
			}
		}

		private void listenerB(SetChangeListener.Change<? extends B> change) {
			if (!updating) {
				final ObservableSet<A> setA = referenceA.get();
				final ObservableSet<B> setB = referenceB.get();
				if ((setA == null) || (setB == null)) {
					if (setA != null) {
						setA.removeListener(listenerA);
					}
					if (setB != null) {
						setB.removeListener(listenerB);
					}
				} else {
					try {
						updating = true;
						if (change.wasRemoved()) {
							B removedB = change.getElementRemoved();
							A removedA = converter.from(removedB);
							setA.remove(removedA);
						} else {
							B addedB = change.getElementAdded();
							A addedA = converter.from(addedB);
							setA.add(addedA);
						}
					} finally {
						updating = false;
					}
				}
			}
		}

		@Override
		public boolean wasGarbageCollected() {
			return (referenceA.get() == null) || (referenceB.get() == null);
		}

		@Override
		public int hashCode() {
			final ObservableSet<A> setA = referenceA.get();
			final ObservableSet<B> setB = referenceB.get();
			final int hcA = (setA == null) ? 0 : setA.hashCode();
			final int hcB = (setB == null) ? 0 : setB.hashCode();
			return hcA * hcB;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			final Object objectA = referenceA.get();
			final Object objectB = referenceB.get();
			if ((objectA == null) || (objectB == null)) {
				return false;
			}

			if (obj instanceof SetContentBinding<?, ?> otherBinding) {
				final Object otherA = otherBinding.referenceA.get();
				final Object otherB = otherBinding.referenceB.get();
				if ((otherA == null) || (otherB == null)) {
					return false;
				}

				if ((objectA == otherA) && (objectB == otherB)) {
					return true;
				}
				if ((objectA == otherB) && (objectB == otherA)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void unsubscribe() {
			ObservableSet<A> listA = referenceA.get();
			if (listA != null) {
				listA.removeListener(listenerA);
			}
			ObservableSet<B> listB = referenceB.get();
			if (listB != null) {
				listB.removeListener(listenerB);
			}
		}
	}

	private static class MapContentBinding<AK, AV, BK, BV> implements Subscription, WeakListener {

		private final WeakReference<ObservableMap<AK, AV>> referenceA;
		private final WeakReference<ObservableMap<BK, BV>> referenceB;

		private final MapChangeListener<AK, AV> listenerA;
		private final MapChangeListener<BK, BV> listenerB;

		private final Converter<AK, BK> keyConverter;
		private final Converter<AV, BV> valueConverter;

		private boolean updating = false;

		public MapContentBinding(ObservableMap<AK, AV> mapA, ObservableMap<BK, BV> mapB, Converter<AK, BK> keyConverter, Converter<AV, BV> valueConverter) {
			this.keyConverter = keyConverter;
			this.valueConverter = valueConverter;

			this.referenceA = new WeakReference<>(mapA);
			this.referenceB = new WeakReference<>(mapB);

			this.listenerA = this::listenerA;
			this.listenerB = this::listenerB;
		}

		private void listenerA(MapChangeListener.Change<? extends AK, ? extends AV> change) {
			if (!updating) {
				final ObservableMap<AK, AV> mapA = referenceA.get();
				final ObservableMap<BK, BV> mapB = referenceB.get();
				if ((mapA == null) || (mapB == null)) {
					if (mapA != null) {
						mapA.removeListener(listenerA);
					}
					if (mapB != null) {
						mapB.removeListener(listenerB);
					}
				} else {
					try {
						updating = true;
						if (change.wasRemoved()) {
							AK keyA = change.getKey();
							BK keyB = keyConverter.to(keyA);
							mapB.remove(keyB);
						}
						if (change.wasAdded()) {
							AK keyA = change.getKey();
							AV valueA = change.getValueAdded();
							BK keyB = keyConverter.to(keyA);
							BV valueB = valueConverter.to(valueA);
							mapB.put(keyB, valueB);
						}
					} finally {
						updating = false;
					}
				}
			}
		}

		private void listenerB(MapChangeListener.Change<? extends BK, ? extends BV> change) {
			if (!updating) {
				final ObservableMap<AK, AV> mapA = referenceA.get();
				final ObservableMap<BK, BV> mapB = referenceB.get();
				if ((mapA == null) || (mapB == null)) {
					if (mapA != null) {
						mapA.removeListener(listenerA);
					}
					if (mapB != null) {
						mapB.removeListener(listenerB);
					}
				} else {
					try {
						updating = true;
						if (change.wasRemoved()) {
							BK keyB = change.getKey();
							AK keyA = keyConverter.from(keyB);
							mapA.remove(keyA);
						}
						if (change.wasAdded()) {
							BK keyB = change.getKey();
							BV valueB = change.getValueAdded();
							AK keyA = keyConverter.from(keyB);
							AV valueA = valueConverter.from(valueB);
							mapA.put(keyA, valueA);
						}
					} finally {
						updating = false;
					}
				}
			}
		}

		@Override
		public boolean wasGarbageCollected() {
			return (referenceA.get() == null) || (referenceB.get() == null);
		}

		@Override
		public int hashCode() {
			final ObservableMap<AK, AV> mapA = referenceA.get();
			final ObservableMap<BK, BV> mapB = referenceB.get();
			final int hcA = (mapA == null) ? 0 : mapA.hashCode();
			final int hcB = (mapB == null) ? 0 : mapB.hashCode();
			return hcA * hcB;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}

			final Object objectA = referenceA.get();
			final Object objectB = referenceB.get();
			if ((objectA == null) || (objectB == null)) {
				return false;
			}

			if (obj instanceof MapContentBinding<?, ?, ?, ?> otherBinding) {
				final Object otherA = otherBinding.referenceA.get();
				final Object otherB = otherBinding.referenceB.get();
				if ((otherA == null) || (otherB == null)) {
					return false;
				}

				if ((objectA == otherA) && (objectB == otherB)) {
					return true;
				}
				if ((objectA == otherB) && (objectB == otherA)) {
					return true;
				}
			}
			return false;
		}


		@Override
		public void unsubscribe() {
			ObservableMap<AK, AV> mapA = referenceA.get();
			if (mapA != null) {
				mapA.removeListener(listenerA);
			}
			ObservableMap<BK, BV> mapB = referenceB.get();
			if (mapB != null) {
				mapB.removeListener(listenerB);
			}
		}
	}

}

