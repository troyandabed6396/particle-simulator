import java.util.Arrays;

class HeapImpl<T extends Comparable<? super T>> implements Heap<T> {
	private static final int INITIAL_CAPACITY = 128;
	private T[] _storage;
	private int _numElements;

	@SuppressWarnings("unchecked")
	public HeapImpl () {
		_storage = (T[]) new Comparable[INITIAL_CAPACITY];
		_numElements = 0;
	}

	@SuppressWarnings("unchecked")
	public void add (T data) {
		if (_numElements == _storage.length - 1) {
			_storage = Arrays.copyOf(_storage, _storage.length * 2);
		}

		_storage[++_numElements] = data;
		swim(_numElements);
	}

	public T removeFirst () {
		T max = _storage[1];
		swap(1, _numElements--);
		_storage[_numElements+1] = null;
		sink(1);

		return max;
	}

	public int size () {
		return _numElements;
	}

	private void swim (int k) {
		while (k > 1 && _storage[k/2].compareTo(_storage[k]) < 0) {
			swap(k, k/2);
			k = k/2;
		}
	}

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

	private void swap(int i, int j) {
		T temp = _storage[i];
		_storage[i] = _storage[j];
		_storage[j] = temp;
	}
}
