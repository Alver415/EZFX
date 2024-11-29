package com.ezfx.base.observable;

import javafx.collections.ObservableArrayBase;

import java.lang.reflect.Array;
import java.util.Arrays;

@SuppressWarnings("unchecked")
public class ObservableObjectArrayImpl<T>
		extends ObservableArrayBase<ObservableObjectArray<T>>
		implements ObservableObjectArray<T> {

	private static final Object[] INITIAL = new Object[0];

	private final Class<T> componentType;
	private T[] array;
	private int size = 0;

	public ObservableObjectArrayImpl(Class<T> componentType) {
		this(componentType, (T[]) Array.newInstance(componentType, 0));
	}

	public ObservableObjectArrayImpl(Class<T> componentType, T... elements) {
		this.componentType = componentType;
		this.array = (T[]) Array.newInstance(componentType, elements.length);
		setAll(elements);
	}

	public ObservableObjectArrayImpl(Class<T> componentType, ObservableObjectArray<T> src) {
		this.componentType = componentType;
		this.array = (T[]) Array.newInstance(componentType, src.size());
		setAll(src);
	}

	public Class<T> getComponentType() {
		return componentType;
	}

	@Override
	public void clear() {
		resize(0);
	}

	@Override
	public int size() {
		return size;
	}

	private void addAllInternal(ObservableObjectArray<T> src, int srcIndex, int length) {
		growCapacity(length);
		src.copyTo(srcIndex, array, size, length);
		size += length;
		fireChange(length != 0, size - length, size);
	}

	private void addAllInternal(T[] src, int srcIndex, int length) {
		growCapacity(length);
		System.arraycopy(src, srcIndex, array, size, length);
		size += length;
		fireChange(length != 0, size - length, size);
	}

	@Override
	public void addAll(ObservableObjectArray<T> src) {
		addAllInternal(src, 0, src.size());
	}

	@Override
	public void addAll(T... elements) {
		addAllInternal(elements, 0, elements.length);
	}

	@Override
	public void addAll(ObservableObjectArray<T> src, int srcIndex, int length) {
		rangeCheck(src, srcIndex, length);
		addAllInternal(src, srcIndex, length);
	}

	@Override
	public void addAll(T[] src, int srcIndex, int length) {
		rangeCheck(src, srcIndex, length);
		addAllInternal(src, srcIndex, length);
	}

	private void setAllInternal(ObservableObjectArray<T> src, int srcIndex, int length) {
		boolean sizeChanged = size() != length;
		if (src == this) {
			if (srcIndex == 0) {
				resize(length);
			} else {
				System.arraycopy(array, srcIndex, array, 0, length);
				size = length;
				fireChange(sizeChanged, 0, size);
			}
		} else {
			size = 0;
			ensureCapacity(length);
			src.copyTo(srcIndex, array, 0, length);
			size = length;
			fireChange(sizeChanged, 0, size);
		}
	}

	private void setAllInternal(T[] src, int srcIndex, int length) {
		boolean sizeChanged = size() != length;
		size = 0;
		ensureCapacity(length);
		System.arraycopy(src, srcIndex, array, 0, length);
		size = length;
		fireChange(sizeChanged, 0, size);
	}

	@Override
	public void setAll(ObservableObjectArray<T> src) {
		setAllInternal(src, 0, src.size());
	}

	@Override
	public void setAll(ObservableObjectArray<T> src, int srcIndex, int length) {
		rangeCheck(src, srcIndex, length);
		setAllInternal(src, srcIndex, length);
	}

	@Override
	public void setAll(T[] src, int srcIndex, int length) {
		rangeCheck(src, srcIndex, length);
		setAllInternal(src, srcIndex, length);
	}

	@Override
	public void setAll(T... src) {
		setAllInternal(src, 0, src.length);
	}

	@Override
	public void set(int destIndex, T[] src, int srcIndex, int length) {
		rangeCheck(destIndex + length);
		System.arraycopy(src, srcIndex, array, destIndex, length);
		fireChange(false, destIndex, destIndex + length);
	}

	@Override
	public void set(int destIndex, ObservableObjectArray<T> src, int srcIndex, int length) {
		rangeCheck(destIndex + length);
		src.copyTo(srcIndex, array, destIndex, length);
		fireChange(false, destIndex, destIndex + length);
	}

	@Override
	public T[] toArray(T[] dest) {
		if ((dest == null) || (size() > dest.length)) {
			dest = (T[]) Array.newInstance(componentType, size());
		}
		System.arraycopy(array, 0, dest, 0, size());
		return dest;
	}

	@Override
	public T get(int index) {
		rangeCheck(index + 1);
		return array[index];
	}

	@Override
	public void set(int index, T value) {
		rangeCheck(index + 1);
		array[index] = value;
		fireChange(false, index, index + 1);
	}

	@Override
	public T[] toArray(int index, T[] dest, int length) {
		rangeCheck(index + length);
		if ((dest == null) || (length > dest.length)) {
			dest = (T[]) Array.newInstance(componentType, length);
		}
		System.arraycopy(array, index, dest, 0, length);
		return dest;
	}

	@Override
	public void copyTo(int srcIndex, T[] dest, int destIndex, int length) {
		rangeCheck(srcIndex + length);
		System.arraycopy(array, srcIndex, dest, destIndex, length);
	}

	@Override
	public void copyTo(int srcIndex, ObservableObjectArray<T> dest, int destIndex, int length) {
		rangeCheck(srcIndex + length);
		dest.set(destIndex, array, srcIndex, length);
	}

	@Override
	public void resize(int newSize) {
		if (newSize < 0) {
			throw new NegativeArraySizeException("Can't resize to negative value: " + newSize);
		}
		ensureCapacity(newSize);
		int minSize = Math.min(size, newSize);
		boolean sizeChanged = size != newSize;
		size = newSize;
		Arrays.fill(array, minSize, size, null);
		fireChange(sizeChanged, minSize, newSize);
	}

	/**
	 * The maximum size of array to allocate.
	 * Some VMs reserve some header words in an array.
	 * Attempts to allocate larger arrays may result in
	 * OutOfMemoryError: Requested array size exceeds VM limit
	 */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	private void growCapacity(int length) {
		int minCapacity = size + length;
		int oldCapacity = array.length;
		if (minCapacity > array.length) {
			int newCapacity = oldCapacity + (oldCapacity >> 1);
			if (newCapacity < minCapacity) newCapacity = minCapacity;
			if (newCapacity > MAX_ARRAY_SIZE) newCapacity = hugeCapacity(minCapacity);
			ensureCapacity(newCapacity);
		} else if (length > 0 && minCapacity < 0) {
			throw new OutOfMemoryError(); // overflow
		}
	}

	@Override
	public void ensureCapacity(int capacity) {
		if (array.length < capacity) {
			array = Arrays.copyOf(array, capacity);
		}
	}

	private static int hugeCapacity(int minCapacity) {
		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		return (minCapacity > MAX_ARRAY_SIZE) ?
				Integer.MAX_VALUE :
				MAX_ARRAY_SIZE;
	}

	@Override
	public void trimToSize() {
		if (array.length != size) {
			T[] newArray = (T[]) Array.newInstance(componentType, size);
			System.arraycopy(array, 0, newArray, 0, size);
			array = newArray;
		}
	}

	private void rangeCheck(int size) {
		if (size > this.size) throw new ArrayIndexOutOfBoundsException(this.size);
	}

	private void rangeCheck(ObservableObjectArray<T> src, int srcIndex, int length) {
		if (src == null) throw new NullPointerException();
		if (srcIndex < 0 || srcIndex + length > src.size()) {
			throw new ArrayIndexOutOfBoundsException(src.size());
		}
		if (length < 0) throw new ArrayIndexOutOfBoundsException(-1);
	}

	private void rangeCheck(T[] src, int srcIndex, int length) {
		if (src == null) throw new NullPointerException();
		if (srcIndex < 0 || srcIndex + length > src.length) {
			throw new ArrayIndexOutOfBoundsException(src.length);
		}
		if (length < 0) throw new ArrayIndexOutOfBoundsException(-1);
	}

	@Override
	public String toString() {
		if (array == null)
			return "null";

		int iMax = size() - 1;
		if (iMax == -1)
			return "[]";

		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0; ; i++) {
			b.append(array[i]);
			if (i == iMax)
				return b.append(']').toString();
			b.append(", ");
		}
	}
}
