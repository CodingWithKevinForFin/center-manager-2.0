package com.f1.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class MergeSort {

	public static <T> void sort(List<T> data, Comparator<T> comparator) {
		int size = data.size();
		if (size < 1000 * 100)
			Collections.sort(data, comparator);
		else {
			// never more than 64 threads!
			int threads = Math.min(64, Math.min(4 * Runtime.getRuntime().availableProcessors(), 2 * (int) (Math.log(size) / Math.log(2))));
			sort(data, comparator, threads);
		}
	}

	public static <T> void sort(List<T> data, Comparator<T> comparator, int threads) {
		int size = data.size() / threads;
		List<List<T>> sections = new ArrayList<List<T>>(threads);
		AtomicReference<List<T>> waitingList = new AtomicReference<List<T>>(null);
		int start = 0;
		CountDownLatch countDown = new CountDownLatch(threads);
		for (int i = 0; i < threads - 1; i++) {
			sections.add((data.subList(start, start += size)));
			new Thread(new Sorter<T>(sections.get(i), comparator, countDown, waitingList)).start();
		}
		sections.add((data.subList(start, data.size())));
		new Thread(new Sorter<T>(sections.get(sections.size() - 1), comparator, countDown, waitingList)).start();
		try {
			countDown.await();
		} catch (InterruptedException e) {
			throw OH.toRuntime(e);
		}
		data.clear();
		data.addAll(waitingList.get());
	}

	public static void main2(String a[]) {

		String[] arr2 = new String[1000 * 1000 * 1];
		Random rnd = new Random(123);
		for (int i = 0; i < 1000 * 1000 * 1; i++)
			arr2[i] = Long.toString(rnd.nextLong());
		for (int c = 0; c < 10; c++) {
			String[] arr = Arrays.copyOf(arr2, arr2.length);
			Duration d = new Duration();
			Arrays.sort(arr);
			d.stampMsStdout();
		}
	}

	public static <T> List<T> merge(List<List<T>> data, Comparator<T> comparator, List<T> sink) {
		int len = 0;
		Node<T> head = null;
		for (List<T> l : data) {
			final Node<T> node = new Node<T>(l, comparator);
			if (!node.nextVal())
				continue;
			if (head == null)
				head = node;
			else
				head = head.insert(node);
			len += node.size;
		}
		if (sink == null)
			sink = new ArrayList<T>(len);
		while (head != null) {
			sink.add(head.val);
			if (!head.nextVal()) {
				head = head.next;
			} else if (head.next != null)
				head = head.next.insert(head);
		}
		return sink;
	}

	static public String toString(Node n) {
		StringBuilder sb = new StringBuilder();
		List<Comparable> o = new ArrayList();
		while (n != null) {
			o.add((Comparable) n.val);
			sb.append(n.val).append(", ");
			n = n.next;
		}
		return sb.toString() + " ==> " + CH.isSorted(o);
	}

	public static <T> List<T> merge3(List<List<T>> data, Comparator<T> comparator) {
		int len = 0;
		int listCount = data.size();
		for (List<T> l : data)
			len += l.size();
		List<T> r = new ArrayList<T>(len);
		int offsets[] = new int[data.size()];
		while (len-- > 0) {
			int min = -1;
			T minValue = null;
			for (int i = 0; i < listCount; i++) {
				List<T> l = data.get(i);
				if (offsets[i] < l.size()) {
					T val = l.get(offsets[i]);
					if (min == -1 || comparator.compare(val, minValue) < 0) {
						min = i;
						minValue = val;
					}
				}
			}
			offsets[min]++;
			r.add(minValue);
		}
		return r;
	}

	public static <T> List<T> merge2(List<List<T>> data, Comparator<T> comparator) {
		int len = 0;
		for (List<T> l : data)
			len += l.size();
		List<T> r = new ArrayList<T>(len);
		for (List<T> l : data)
			r.addAll(l);
		Collections.sort(r, comparator);

		return r;
	}

	public static class Node<T> {
		Node<T> next;
		T val;
		int offset;
		final List<T> list;
		final int size;
		private Comparator<T> comp;

		public Node(List<T> list, Comparator<T> comp) {
			this.comp = comp;
			this.list = list;
			this.size = list.size();
		}

		public boolean nextVal() {
			if (offset == size)
				return false;
			val = list.get(offset++);
			return true;
		}

		public Node<T> insert(Node<T> node) {
			final T v = node.val;
			node.next = null;
			if (comp.compare(v, val) < 0) {
				node.next = this;
				return node;
			}
			Node<T> n = this;
			while (n.next != null) {
				if (comp.compare(v, n.next.val) < 0) {
					node.next = n.next;
					n.next = node;
					return this;
				}
				n = n.next;
			}
			n.next = node;
			return this;
		}
	}

	public static class Sorter<T> implements Runnable {

		private CountDownLatch countDown;
		private List<T> list;
		private Comparator<T> comp;
		private AtomicReference<List<T>> waitingList;

		public Sorter(List<T> list, Comparator<T> comparator, CountDownLatch countDown, AtomicReference<List<T>> waitingList) {
			this.list = list;
			this.comp = comparator;
			this.countDown = countDown;
			this.waitingList = waitingList;
		}

		@Override
		public void run() {
			List<T> list = this.list;
			Collections.sort(list, comp);
			while (true) {
				List<T> existing = waitingList.get();
				if (existing == null) {
					if (waitingList.compareAndSet(existing, list)) {
						countDown.countDown();
						return;
					}
				} else {
					if (waitingList.compareAndSet(existing, null)) {
						list = merge(list, existing, comp, null);
					}
				}
			}

		}

	}

	static public <T> List<T> merge(List<T> list1, List<T> list2, Comparator<T> comp2, List<T> sink) {

		int o1 = 0, s1 = list1.size();
		int o2 = 0, s2 = list2.size();
		List<T> r = sink == null ? new ArrayList<T>(s1 + s2) : sink;
		if (s1 == 0) {
			r.addAll(list2);
			return r;
		}
		if (s2 == 0) {
			r.addAll(list1);
			return r;
		}
		T v1 = list1.get(0), v2 = list2.get(0);
		for (;;) {
			if (comp2.compare(v1, v2) < 0) {
				r.add(v1);
				if (++o1 == s1) {
					while (o2 < s2)
						r.add(list2.get(o2++));
					return r;
				} else
					v1 = list1.get(o1);
			} else {
				r.add(v2);
				if (++o2 == s2) {
					while (o1 < s1)
						r.add(list1.get(o1++));
					return r;
				} else
					v2 = list2.get(o2);
			}

		}
	}
}
