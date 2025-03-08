package com.ezfx.base.utils;

import java.util.function.Function;

public interface Converter<A, B> {

	static <T> Converter<T, T> identity() {
		return Converter.of(Function.identity(), Function.identity());
	}

	B to(A a);

	A from(B b);

	default Converter<B, A> inverted() {
		return new Converter<>() {
			@Override
			public A to(B b) {
				return Converter.this.from(b);
			}

			@Override
			public B from(A a) {
				return Converter.this.to(a);
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
}
