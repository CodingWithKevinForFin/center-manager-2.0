package com.f1.ami.web.tree;

import java.util.List;

import com.f1.utils.OH;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.Tuple2;

public class AmiWebTreeRecursiveBuilder<K, V> {
	private BasicMultiMap.List<Tuple2<K, K>, Node> entriesByKey = new BasicMultiMap.List<Tuple2<K, K>, Node>();
	private BasicMultiMap.List<Tuple2<K, K>, Node> entriesByRecursiveParent = new BasicMultiMap.List<Tuple2<K, K>, Node>();

	private BasicMultiMap.List<Tuple2<K, K>, Node> entriesByNodeKey = new BasicMultiMap.List<Tuple2<K, K>, Node>();
	private BasicMultiMap.List<Tuple2<K, K>, Node> entriesByGroupParent = new BasicMultiMap.List<Tuple2<K, K>, Node>();
	public static final Object NULL_KEY = new Object();

	public class Node {
		final public K key;
		final public K groupingParentKey;
		final public K recursiveParentKey;
		final public K groupRootNodeUid;
		final public V value;
		protected boolean needsUpdate;

		public Node(K key, K groupingParentKey, K recursiveParentKey, K groupRootNodeUid, V value, boolean needsUpdate) {
			this.key = key;
			this.groupingParentKey = groupingParentKey;
			this.recursiveParentKey = recursiveParentKey;
			this.groupRootNodeUid = groupRootNodeUid;
			this.value = value;
			this.needsUpdate = true;
		}
		public boolean needsUpdate() {
			return needsUpdate;
		}
		public void setNeedsUpdate(boolean bool) {
			this.needsUpdate = bool;
		}
	}

	public void add2(K groupRootNodeUid, K nodeKey, K groupingParent, V value) {
		K nk = (K) OH.noNull(nodeKey, NULL_KEY);
		K gp = (K) OH.noNull(groupingParent, NULL_KEY);

		Tuple2<K, K> keyTuple = new Tuple2<K, K>(groupRootNodeUid, nk);
		Tuple2<K, K> groupParentTuple = new Tuple2<K, K>(groupRootNodeUid, gp);
		Node val = new Node(nodeKey, groupingParent, null, groupRootNodeUid, value, true);

		entriesByNodeKey.putMulti(keyTuple, val);
		entriesByGroupParent.putMulti(groupParentTuple, val);
	}
	public void remove2(K groupRootNodeUid, K nodeKey, K groupingParent) {
		K nk = (K) OH.noNull(nodeKey, NULL_KEY);
		K gp = (K) OH.noNull(groupingParent, NULL_KEY);

		Tuple2<K, K> keyTuple = new Tuple2<K, K>(groupRootNodeUid, nk);
		Tuple2<K, K> groupParentTuple = new Tuple2<K, K>(groupRootNodeUid, gp);

		Node val = entriesByNodeKey.getMulti(keyTuple);
		entriesByNodeKey.removeMultiAndKeyIfEmpty(keyTuple, val);
		entriesByGroupParent.removeMultiAndKeyIfEmpty(groupParentTuple, val);
	}

	public V getValueByKey2(Tuple2<K, K> key) {
		AmiWebTreeRecursiveBuilder<K, V>.Node node = entriesByNodeKey.getMulti(key);
		return node == null ? null : node.value;
	}
	public List<Node> buildKeyByGroupingParent(Tuple2<K, K> nodeKey, List<Node> sink) {
		if (!entriesByGroupParent.containsKey(nodeKey))
			return sink;
		List<AmiWebTreeRecursiveBuilder<K, V>.Node> allNodesWithKey = entriesByGroupParent.get(nodeKey);
		for (AmiWebTreeRecursiveBuilder<K, V>.Node node : allNodesWithKey) {
			sink.add(node);
			node.needsUpdate = true;
		}

		return sink;
	}
	public List<Node> buildKeyByNodeKey(Tuple2<K, K> nodeKey, List<Node> sink) {
		if (!entriesByNodeKey.containsKey(nodeKey))
			return sink;
		List<AmiWebTreeRecursiveBuilder<K, V>.Node> allNodesWithKey = entriesByNodeKey.get(nodeKey);
		for (AmiWebTreeRecursiveBuilder<K, V>.Node node : allNodesWithKey) {
			sink.add(node);
			node.needsUpdate = true;
		}

		return sink;
	}

	public void add(K key, K groupingParent, K recursiveParent, V value) {
		Tuple2<K, K> groupingToKeyTuple = new Tuple2<K, K>(groupingParent, key);
		Tuple2<K, K> groupingToRecursiveParentTuple = new Tuple2<K, K>(groupingParent, recursiveParent);
		Node val = new Node(key, groupingParent, recursiveParent, null, value, true);
		entriesByKey.putMulti(groupingToKeyTuple, val);
		entriesByRecursiveParent.putMulti(groupingToRecursiveParentTuple, val);
	}
	public void remove(K key, K groupingParent, K recursiveParent) {
		Tuple2<K, K> groupingToKeyTuple = new Tuple2<K, K>(groupingParent, key);
		Tuple2<K, K> groupingToRecursiveParentTuple = new Tuple2<K, K>(groupingParent, recursiveParent);

		Node val = entriesByKey.getMulti(groupingToKeyTuple);
		entriesByKey.removeMultiAndKeyIfEmpty(groupingToKeyTuple, val);
		entriesByRecursiveParent.removeMultiAndKeyIfEmpty(groupingToRecursiveParentTuple, val);
	}

	public V getValueByKey(Tuple2<K, K> key) {
		AmiWebTreeRecursiveBuilder<K, V>.Node node = entriesByKey.getMulti(key);
		return node == null ? null : node.value;
	}

	public List<Node> buildKeyByGrouping(Tuple2<K, K> key, List<Node> sink) {
		if (!entriesByKey.containsKey(key))
			return sink;
		List<AmiWebTreeRecursiveBuilder<K, V>.Node> allNodesWithKey = entriesByKey.get(key);
		for (AmiWebTreeRecursiveBuilder<K, V>.Node node : allNodesWithKey) {
			sink.add(node);
			node.needsUpdate = true;

		}

		return sink;
	}
	public List<Node> buildKeyByRecursiveParent(Tuple2<K, K> key, List<Node> sink) {
		if (!entriesByRecursiveParent.containsKey(key))
			return sink;
		List<AmiWebTreeRecursiveBuilder<K, V>.Node> allNodesWithKey = entriesByRecursiveParent.get(key);
		for (AmiWebTreeRecursiveBuilder<K, V>.Node node : allNodesWithKey) {
			sink.add(node);
			node.needsUpdate = true;
		}

		return sink;
	}

}