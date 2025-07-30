package com.f1.utils.impl;

import java.util.HashMap;
import java.util.Map;

import com.f1.utils.Tree;

public class BasicTree<K, V> implements Tree<K, V> {

	private Map<K, V> leafs = new HashMap<K, V>();
	private Map<K, Tree<K, V>> trees = new HashMap<K, Tree<K, V>>();

	@Override
	public Map<K, V> getLeafs() {
		return leafs;
	}

	@Override
	public Map<K, Tree<K, V>> getTrees() {
		return trees;
	}

}
