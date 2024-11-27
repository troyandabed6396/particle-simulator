import java.util.Arrays;

class HeapImpl<T extends Comparable<? super T>> implements Heap<T> {
	private static final int INITIAL_CAPACITY = 128;
	private T[] _storage;
	private int _numElements;

	/**
	 * Default constructor for the HeapImpl class.
	 */
	@SuppressWarnings("unchecked")
	public HeapImpl () {
		_storage = (T[]) new Comparable[INITIAL_CAPACITY];
		_numElements = 0;
	}

	/**
	 * Adds the specified data to the heap.
	 * @param data the data to add to the heap
	 */
	@SuppressWarnings("unchecked")
	public void add (T data) {
		if (_numElements == _storage.length - 1) {
			_storage = Arrays.copyOf(_storage, _storage.length * 2);
		}

		_storage[++_numElements] = data;
		swim(_numElements);
	}

	/**
	 * Removes and returns the first element in the heap.
	 * @return the first element in the heap
	 */
	public T removeFirst () {
		T max = _storage[1];
		swap(1, _numElements--);
		_storage[_numElements+1] = null;
		sink(1);

		return max;
	}

	/**
	 * Returns the number of elements in the heap.
	 * @return the number of elements in the heap
	 */
	public int size () {
		return _numElements;
	}


	/**
	 * Moves an element up the heap to maintain the max-heap property.
	 * @param k the index of the element to be moved up in the heap
	 */
	private void swim (int k) {
		while (k > 1 && _storage[k/2].compareTo(_storage[k]) < 0) {
			swap(k, k/2);
			k = k/2;
		}
	}

	/**
	 * Moves an element down the heap to maintain the max-heap property.
	 * @param k the index of the element to be moved down in the heap
	 */
	private void sink (int k) {
		while (2*k <= _numElements) {
			int j = 2*k;
			if (j < _numElements && _storage[j].compareTo(_storage[j+1]) < 0) {
				j++;
			}
			if (_storage[k].compareTo(_storage[j]) >= 0) {
				break;
			}
			swap(k, j);
			k = j;
		}
	}

	/**
	 * Swaps the elements at the specified indices in the heap.
	 * @param i the index of the first element to swap
	 * @param j the index of the second element to swap
	 */
	private void swap(int i, int j) {
		T temp = _storage[i];
		_storage[i] = _storage[j];
		_storage[j] = temp;
	}
}
