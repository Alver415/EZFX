package com.ezfx.base.utils;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.function.Function;

public interface Converter<A, B> {

	B to(A a);

	A from(B b);

	static <T> Converter<T, T> identity() {
		return Converter.of(Function.identity(), Function.identity());
	}

	default Converter<B, A> inverted() {
		return inverted(this);
	}

	static <A, B> Converter<B, A> inverted(Converter<A, B> converter) {
		return new Converter<>() {
			@Override
			public A to(B b) {
				return converter.from(b);
			}

			@Override
			public B from(A a) {
				return converter.to(a);
			}
		};
	}

	default <C> Converter<A, C> compound(Converter<B, C> second) {
		return compound(this, second);
	}

	static <A, B, C> Converter<A, C> compound(Converter<A, B> first, Converter<B, C> second) {
		return new Converter<>() {
			@Override
			public C to(A a) {
				B intermediate = first.to(a);
				return second.to(intermediate);
			}

			@Override
			public A from(C c) {
				B intermediate = second.from(c);
				return first.from(intermediate);
			}
		};
	}

	static <A, B> Converter<A, B> of(Function<A, B> to, Function<B, A> from) {
		return new Simple<>(to, from);
	}

	static <A, B> Converter<A, B> passingNull(Function<A, B> to, Function<B, A> from) {
		return of(passingNull(to), passingNull(from));
	}

	static <T, R> Function<T, R> passingNull(Function<T, R> function) {
		return t -> t == null ? null : function.apply(t);
	}

	class Simple<A, B> implements Converter<A, B> {
		private final Function<A, B> to;
		private final Function<B, A> from;

		private Simple(Function<A, B> to, Function<B, A> from) {
			this.to = to;
			this.from = from;
		}

		@Override
		public B to(A a) {
			return to.apply(a);
		}

		@Override
		public A from(B b) {
			return from.apply(b);
		}
	}

	static <A, B> Converter<A, B> cached(Converter<A, B> converter) {
		return cached(converter::to, converter::from);
	}

	static <A, B> Converter<A, B> cached(Function<A, B> to, Function<B, A> from) {
		return new Cached<>(to, from);
	}

	class Cached<A, B> implements Converter<A, B> {
		private final BiMap<A, B> biMap = HashBiMap.create();
		private final Function<A, B> to;
		private final Function<B, A> from;

		private Cached(Function<A, B> to, Function<B, A> from) {
			this.to = to;
			this.from = from;
		}

		@Override
		public B to(A a) {
			return biMap.computeIfAbsent(a, to);
		}

		@Override
		public A from(B b) {
			return biMap.inverse().computeIfAbsent(b, from);
		}
	}
}
