package queue;

import java.util.Iterator;
import java.util.NoSuchElementException;
 
/**
 * 
 * 描述：索引数组pq保存的是数据集keys中的下标，遍历索引数组可以刻画出二叉堆<br/>
 * 其中keys指的是IndexMaxPQ.java里的一个成员变量<br/>
 * keys={it,was,the,best,of,times,it,was,the,worst,null,}<br/>
 * 则索引数组pq为{0,9,1,5,8,7,2,6,3,4,0,}<br/>
 * 那么遍历pq,则pq[1]=9,keys[9]=worst,pq[2]=1,keys[1]=was,pq[3]=5,keys[5]= times
 * 
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID   DATE             PERSON          REASON
 *  1    2016-10-10        蒙奇·D·许                     Create
 * ****************************************************************************
 * </pre>
 * 
 * @author 蒙奇·D·许
 * @since 1.0
 */
public class IndexMaxPQ<Key extends Comparable<Key>> implements
		Iterable<Integer> {
	private int n; // number of elements on PQ
	private int[] pq; // binary heap using 1-based indexing
	private int[] qp; // inverse of pq - qp[pq[i]] = pq[qp[i]] = i
	private Key[] keys; // keys[i] = priority of i
 
	/**
	 * Initializes an empty indexed priority queue with indices between
	 * {@code 0} and {@code maxN - 1}.
	 *
	 * @param maxN
	 *            the keys on this priority queue are index from {@code 0} to
	 *            {@code maxN - 1}
	 * @throws IllegalArgumentException
	 *             if {@code maxN < 0}
	 */
	@SuppressWarnings("unchecked")
	public IndexMaxPQ(int maxN) {
		if (maxN < 0)
			throw new IllegalArgumentException();
		n = 0;
		keys = (Key[]) new Comparable[maxN + 1]; // make this of length maxN??
		pq = new int[maxN + 1];
		qp = new int[maxN + 1]; // make this of length maxN??
		for (int i = 0; i <= maxN; i++)
			qp[i] = -1;
	}
 
	/**
	 * Returns true if this priority queue is empty.
	 *
	 * @return {@code true} if this priority queue is empty; {@code false}
	 *         otherwise
	 */
	public boolean isEmpty() {
		return n == 0;
	}
 
	/**
	 * Is {@code i} an index on this priority queue?
	 *
	 * @param i
	 *            an index
	 * @return {@code true} if {@code i} is an index on this priority queue;
	 *         {@code false} otherwise
	 * @throws IndexOutOfBoundsException
	 *             unless {@code 0 <= i < maxN}
	 */
	public boolean contains(int i) {
		return qp[i] != -1;
	}
 
	/**
	 * Returns the number of keys on this priority queue.
	 *
	 * @return the number of keys on this priority queue
	 */
	public int size() {
		return n;
	}
 
	/**
	 * Associate key with index i.
	 *
	 * @param i
	 *            an index
	 * @param key
	 *            the key to associate with index {@code i}
	 * @throws IndexOutOfBoundsException
	 *             unless {@code 0 <= i < maxN}
	 * @throws IllegalArgumentException
	 *             if there already is an item associated with index {@code i}
	 */
	public void insert(int i, Key key) {
		if (contains(i))
			throw new IllegalArgumentException(
					"index is already in the priority queue");
		n++;
		qp[i] = n;
		pq[n] = i;
		keys[i] = key;
		swim(n);
	}
 
	/**
	 * Returns an index associated with a maximum key.
	 *
	 * @return an index associated with a maximum key
	 * @throws NoSuchElementException
	 *             if this priority queue is empty
	 */
	public int maxIndex() {
		if (n == 0)
			throw new NoSuchElementException("Priority queue underflow");
		return pq[1];
	}
 
	/**
	 * Returns a maximum key.
	 *
	 * @return a maximum key
	 * @throws NoSuchElementException
	 *             if this priority queue is empty
	 */
	public Key maxKey() {
		if (n == 0)
			throw new NoSuchElementException("Priority queue underflow");
		return keys[pq[1]];
	}
 
	/**
	 * Removes a maximum key and returns its associated index.
	 *
	 * @return an index associated with a maximum key
	 * @throws NoSuchElementException
	 *             if this priority queue is empty
	 */
	public int delMax() {
		if (n == 0)
			throw new NoSuchElementException("Priority queue underflow");
		int min = pq[1];
		exch(1, n--);
		sink(1);
 
		assert pq[n + 1] == min;
		qp[min] = -1; // delete
		keys[min] = null; // to help with garbage collection
		pq[n + 1] = -1; // not needed
		return min;
	}
 
	/**
	 * Returns the key associated with index {@code i}.
	 *
	 * @param i
	 *            the index of the key to return
	 * @return the key associated with index {@code i}
	 * @throws IndexOutOfBoundsException
	 *             unless {@code 0 <= i < maxN}
	 * @throws NoSuchElementException
	 *             no key is associated with index {@code i}
	 */
	public Key keyOf(int i) {
		if (!contains(i))
			throw new NoSuchElementException(
					"index is not in the priority queue");
		else
			return keys[i];
	}
 
	/**
	 * Change the key associated with index {@code i} to the specified value.
	 *
	 * @param i
	 *            the index of the key to change
	 * @param key
	 *            change the key associated with index {@code i} to this key
	 * @throws IndexOutOfBoundsException
	 *             unless {@code 0 <= i < maxN}
	 */
	public void changeKey(int i, Key key) {
		if (!contains(i))
			throw new NoSuchElementException(
					"index is not in the priority queue");
		keys[i] = key;
		swim(qp[i]);
		sink(qp[i]);
	}
 
	/**
	 * Change the key associated with index {@code i} to the specified value.
	 *
	 * @param i
	 *            the index of the key to change
	 * @param key
	 *            change the key associated with index {@code i} to this key
	 * @throws IndexOutOfBoundsException
	 *             unless {@code 0 <= i < maxN}
	 * @deprecated Replaced by {@code changeKey(int, Key)}.
	 */
	@Deprecated
	public void change(int i, Key key) {
		changeKey(i, key);
	}
 
	/**
	 * Increase the key associated with index {@code i} to the specified value.
	 *
	 * @param i
	 *            the index of the key to increase
	 * @param key
	 *            increase the key associated with index {@code i} to this key
	 * @throws IndexOutOfBoundsException
	 *             unless {@code 0 <= i < maxN}
	 * @throws IllegalArgumentException
	 *             if {@code key <= keyOf(i)}
	 * @throws NoSuchElementException
	 *             no key is associated with index {@code i}
	 */
	public void increaseKey(int i, Key key) {
		if (!contains(i))
			throw new NoSuchElementException(
					"index is not in the priority queue");
		if (keys[i].compareTo(key) >= 0)
			throw new IllegalArgumentException(
					"Calling increaseKey() with given argument would not strictly increase the key");
 
		keys[i] = key;
		swim(qp[i]);
	}
 
	/**
	 * Decrease the key associated with index {@code i} to the specified value.
	 *
	 * @param i
	 *            the index of the key to decrease
	 * @param key
	 *            decrease the key associated with index {@code i} to this key
	 * @throws IndexOutOfBoundsException
	 *             unless {@code 0 <= i < maxN}
	 * @throws IllegalArgumentException
	 *             if {@code key >= keyOf(i)}
	 * @throws NoSuchElementException
	 *             no key is associated with index {@code i}
	 */
	public void decreaseKey(int i, Key key) {
		if (!contains(i))
			throw new NoSuchElementException(
					"index is not in the priority queue");
		if (keys[i].compareTo(key) <= 0)
			throw new IllegalArgumentException(
					"Calling decreaseKey() with given argument would not strictly decrease the key");
 
		keys[i] = key;
		sink(qp[i]);
	}
 
	/**
	 * Remove the key on the priority queue associated with index {@code i}.
	 *
	 * @param i
	 *            the index of the key to remove
	 * @throws IndexOutOfBoundsException
	 *             unless {@code 0 <= i < maxN}
	 * @throws NoSuchElementException
	 *             no key is associated with index {@code i}
	 */
	public void delete(int i) {
		if (!contains(i))
			throw new NoSuchElementException(
					"index is not in the priority queue");
		int index = qp[i];
		exch(index, n--);
		swim(index);
		sink(index);
		keys[i] = null;
		qp[i] = -1;
	}
 
	/***************************************************************************
	 * General helper functions.
	 ***************************************************************************/
	private boolean less(int i, int j) {
		return keys[pq[i]].compareTo(keys[pq[j]]) < 0;
	}
 
	private void exch(int i, int j) {
		int swap = pq[i];
		pq[i] = pq[j];
		pq[j] = swap;
		qp[pq[i]] = i;
		qp[pq[j]] = j;
	}
 
	/***************************************************************************
	 * Heap helper functions.
	 ***************************************************************************/
	private void swim(int k) {
		while (k > 1 && less(k / 2, k)) {
			exch(k, k / 2);
			k = k / 2;
		}
	}
 
	private void sink(int k) {
		while (2 * k <= n) {
			int j = 2 * k;
			if (j < n && less(j, j + 1))
				j++;
			if (!less(k, j))
				break;
			exch(k, j);
			k = j;
		}
	}
 
	/**
	 * Returns an iterator that iterates over the keys on the priority queue in
	 * descending order. The iterator doesn't implement {@code remove()} since
	 * it's optional.
	 *
	 * @return an iterator that iterates over the keys in descending order
	 */
	public Iterator<Integer> iterator() {
		return new HeapIterator();
	}
 
	private class HeapIterator implements Iterator<Integer> {
		// create a new pq
		private IndexMaxPQ<Key> copy;
 
		// add all elements to copy of heap
		// takes linear time since already in heap order so no keys move
		public HeapIterator() {
			copy = new IndexMaxPQ<Key>(pq.length - 1);
			for (int i = 1; i <= n; i++)
				copy.insert(pq[i], keys[pq[i]]);
		}
 
		public boolean hasNext() {
			return !copy.isEmpty();
		}
 
		public void remove() {
			throw new UnsupportedOperationException();
		}
 
		public Integer next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return copy.delMax();
		}
	}
 
	/**
	 * Unit tests the {@code IndexMaxPQ} data type.
	 *
	 * @param args
	 *            the command-line arguments
	 */
	public static void main(String[] args) {
		// insert a bunch of strings
		String[] strings = { "it", "was", "the", "best", "of", "times", "it",
				"was", "the", "worst" };
 
		IndexMaxPQ<String> pq = new IndexMaxPQ<String>(strings.length);
		for (int i = 0; i < strings.length; i++) {
			pq.insert(i, strings[i]);
		}
 
		// print each key using the iterator
		for (int i : pq) {
			System.out.println(i + " " + strings[i]);
		}
 
		System.out.println();
 
		// // increase or decrease the key
		// for (int i = 0; i < strings.length; i++) {
		// if (StdRandom.uniform() < 0.5)
		// pq.increaseKey(i, strings[i] + strings[i]);
		// else
		// pq.decreaseKey(i, strings[i].substring(0, 1));
		// }
 
		// delete and print each key
		while (!pq.isEmpty()) {
			String key = pq.maxKey();
			int i = pq.delMax();
			System.out.println(i + " " + key);
		}
		System.out.println();
 
		// reinsert the same strings
		for (int i = 0; i < strings.length; i++) {
			pq.insert(i, strings[i]);
		}
 
		// delete them in random order
		int[] perm = new int[strings.length];
		for (int i = 0; i < strings.length; i++)
			perm[i] = i;
		// StdRandom.shuffle(perm);
		for (int i = 0; i < perm.length; i++) {
			String key = pq.keyOf(perm[i]);
			pq.delete(perm[i]);
			System.out.println(perm[i] + " " + key);
		}
 
	}
}
