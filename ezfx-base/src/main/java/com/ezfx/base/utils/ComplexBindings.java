package com.ezfx.base.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Subscription;

import java.lang.ref.WeakReference;
import java.util.function.Function;

public interface ComplexBindings {

	static <T, R> Subscription bindContentBidirectional(
			ObservableList<T> firstList, ObservableList<R> secondList,
			Function<T, R> to, Function<R, T> from) {
		return bindContentBidirectional(firstList, secondList, Converter.of(to, from));
	}

	static <T, R> Subscription bindContentBidirectional(
			ObservableList<T> firstList, ObservableList<R> secondList,
			Converter<T, R> converter) {

		Converter<R, T> inverseConverter = converter.inverted();

		BiMap<T, R> biMap = HashBiMap.create();
		BiMap<R, T> inverseBiMap = biMap.inverse();

		secondList.clear();
		for (T element : firstList) {
			R transformed = biMap.computeIfAbsent(element, _ -> converter.to(element));
			secondList.add(transformed);
		}

		final boolean[] lock = {false};
		ListChangeListener<? super T> listenerA = getListChangeListener(secondList, converter, lock, biMap);

		ListChangeListener<? super R> listenerB = change -> {
			if (lock[0]) return;
			lock[0] = true;
			while (change.next()) {
				for (R element : change.getRemoved()) {
					T transformed = biMap.inverse().computeIfAbsent(element, _ -> converter.from(element));
					firstList.remove(transformed);
				}
				for (R element : change.getAddedSubList()) {
					T transformed = biMap.inverse().computeIfAbsent(element, _ -> converter.from(element));
					firstList.add(transformed);
				}
			}
			lock[0] = false;
		};

		firstList.addListener(listenerA);
		secondList.addListener(listenerB);
		return () -> {
			firstList.removeListener(listenerA);
			secondList.removeListener(listenerB);
		};
	}

	private static <T, R> ListChangeListener<? super T> getListChangeListener(ObservableList<R> secondList, Converter<T, R> converter, boolean[] lock, BiMap<T, R> biMap) {
		ListChangeListener<? super T> listenerA = change -> {
			if (lock[0]) return;
			lock[0] = true;
			while (change.next()) {
				for (T element : change.getRemoved()) {
					R transformed = biMap.computeIfAbsent(element, _ -> converter.to(element));
					secondList.remove(transformed);
				}
				for (T element : change.getAddedSubList()) {
					R transformed = biMap.computeIfAbsent(element, _ -> converter.to(element));
					secondList.add(transformed);
				}
			}
			lock[0] = false;
		};
		return listenerA;
	}

	class ComplexListBinding<A, B> {
		private final WeakReference<ObservableList<A>> first;
		private final WeakReference<ObservableList<B>> second;

		private final Converter<A, B> converter;
		private final HashBiMap<A, B> bimap;

		private boolean locked;

		public ComplexListBinding(ObservableList<A> first, ObservableList<B> second, Converter<A, B> converter) {
			this.first = new WeakReference<>(first);
			this.second = new WeakReference<>(second);

			this.locked = false;
			this.converter = converter;
			this.bimap = HashBiMap.create();

			first.addListener(buildListener(second, converter, bimap));
			second.addListener(buildListener(first, converter.inverted(), bimap.inverse()));
		}

		private <A, B> ListChangeListener<? super A> buildListener(
				ObservableList<B> otherList, Converter<A, B> converter, BiMap<A, B> biMap) {
			return change -> {
				if (locked) return;
				locked = true;
				while (change.next()) {
					for (A element : change.getRemoved()) {
						B transformed = biMap.computeIfAbsent(element, _ -> converter.to(element));
						otherList.remove(transformed);
					}
					for (A element : change.getAddedSubList()) {
						B transformed = biMap.computeIfAbsent(element, _ -> converter.to(element));
						otherList.add(transformed);
					}
				}
				locked = false;
			};
		}
	}
}
