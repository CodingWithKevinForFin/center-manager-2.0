package com.f1.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import com.f1.utils.structs.Tuple2;

public class LocalToolkit {
	public static final int MAX_COLLECTION_SIZE_FOR_REUSE = 2000;
	public static final int MAX_STRINGBUILDER_FOR_REUSE = 10000;

	public static final short STATE_BORROWED_TREEMAP = 1;
	public static final short STATE_BORROWED_HASHMAP = 2;
	public static final short STATE_BORROWED_ARRAYLIST = 4;
	public static final short STATE_BORROWED_HASHSET = 8;
	public static final short STATE_BORROWED_STRINGBUILDER = 16;
	public static final short STATE_BORROWED_TUPLE2 = 32;

	private short stateMask = 0;
	private TreeMap<?, ?> treeMap;
	private HashMap<?, ?> hashMap;
	private ArrayList<?> arrayList;
	private HashSet<?> hashSet;
	private StringBuilder stringBuilder;
	private Tuple2 tuple2;

	//#### TreeMap ####
	public TreeMap<?, ?> borrowTreeMap() {
		flagBorrowed(STATE_BORROWED_TREEMAP);
		if (treeMap == null)
			treeMap = new TreeMap<Object, Object>();
		return treeMap;
	}
	public void returnTreeMap(TreeMap<?, ?> treeMap) {
		if (treeMap != this.treeMap)
			throw new RuntimeException("not from this tool kit");
		flagNotBorrowed(STATE_BORROWED_TREEMAP);
		if (treeMap.size() > MAX_COLLECTION_SIZE_FOR_REUSE)
			this.treeMap = null;
		else
			treeMap.clear();
	}

	//#### HashMap ####
	public HashMap<?, ?> borrowHashMap() {
		flagBorrowed(STATE_BORROWED_HASHMAP);
		if (hashMap == null)
			hashMap = new HashMap<Object, Object>();
		return hashMap;
	}
	public void returnHashMap(HashMap<?, ?> hashMap) {
		if (hashMap != this.hashMap)
			throw new RuntimeException("not from this tool kit");
		flagNotBorrowed(STATE_BORROWED_HASHMAP);
		if (hashMap.size() > MAX_COLLECTION_SIZE_FOR_REUSE)
			this.hashMap = null;
		else
			hashMap.clear();
	}

	public <T> ArrayList<T> borrowArrayList(Class<T> clazz) {
		return (ArrayList<T>) borrowArrayList();

	}
	//#### ArrayList ####
	public ArrayList<?> borrowArrayList() {
		flagBorrowed(STATE_BORROWED_ARRAYLIST);
		if (arrayList == null)
			arrayList = new ArrayList<Object>();
		return arrayList;
	}
	public void returnArrayList(ArrayList<?> arrayList) {
		if (arrayList != this.arrayList)
			throw new RuntimeException("not from this tool kit");
		flagNotBorrowed(STATE_BORROWED_ARRAYLIST);
		if (arrayList.size() > MAX_COLLECTION_SIZE_FOR_REUSE)
			this.arrayList = null;
		else
			arrayList.clear();
	}

	//#### HashSet ####
	public HashSet<?> borrowHashSet() {
		flagBorrowed(STATE_BORROWED_HASHSET);
		if (hashSet == null)
			hashSet = new HashSet<Object>();
		return hashSet;
	}
	public void returnHashSet(HashSet<?> hashSet) {
		if (hashSet != this.hashSet)
			throw new RuntimeException("not from this tool kit");
		flagNotBorrowed(STATE_BORROWED_HASHSET);
		if (hashSet.size() > MAX_COLLECTION_SIZE_FOR_REUSE)
			this.hashSet = null;
		else
			hashSet.clear();
	}

	//#### StringBuilder ####
	public StringBuilder borrowStringBuilder() {
		flagBorrowed(STATE_BORROWED_STRINGBUILDER);
		if (stringBuilder == null)
			stringBuilder = new StringBuilder();
		return stringBuilder;
	}
	public void returnStringBuilder(StringBuilder stringBuilder) {
		if (stringBuilder != this.stringBuilder)
			throw new RuntimeException("not from this tool kit");
		flagNotBorrowed(STATE_BORROWED_STRINGBUILDER);
		if (stringBuilder.length() > MAX_STRINGBUILDER_FOR_REUSE)
			this.stringBuilder = null;
		else
			stringBuilder.setLength(0);
	}

	//#### StringBuilder ####
	public Tuple2 borrowTuple2() {
		flagBorrowed(STATE_BORROWED_TUPLE2);
		if (tuple2 == null)
			tuple2 = new Tuple2();
		return tuple2;
	}
	public void returnTuple2(Tuple2 tuple2) {
		if (tuple2 != this.tuple2)
			throw new RuntimeException("not from this tool kit");
		flagNotBorrowed(STATE_BORROWED_TUPLE2);
		tuple2.clear();
	}

	private void flagNotBorrowed(short state) {
		assertBorrowed(state);
		stateMask ^= state;
	}
	private void flagBorrowed(short state) {
		assertNotBorrowed(state);
		stateMask |= state;
	}

	public void assertNotBorrowed(short state) {
		if (isBorrowed(state))
			throw new IllegalStateException("alreadyBorrowed");
	}
	public void assertBorrowed(short state) {
		if (!isBorrowed(state))
			throw new IllegalStateException("never Borrowed");
	}

	private boolean isBorrowed(short state) {
		return MH.anyBits(this.stateMask, state);
	}

	public short getBorrowedMask() {
		return stateMask;
	}

	public void assertNothingBorrowed() {
		if (stateMask != 0)
			throw new IllegalStateException("things borrowed: " + SH.toString(stateMask));
	}
	public void returnAll() {
		if (stateMask != 0) {
			if (MH.anyBits(stateMask, STATE_BORROWED_ARRAYLIST))
				this.arrayList.clear();
			if (MH.anyBits(stateMask, STATE_BORROWED_STRINGBUILDER))
				this.stringBuilder.setLength(0);
			if (MH.anyBits(stateMask, STATE_BORROWED_HASHMAP))
				this.hashMap.clear();
			if (MH.anyBits(stateMask, STATE_BORROWED_HASHSET))
				this.hashSet.clear();
			if (MH.anyBits(stateMask, STATE_BORROWED_TREEMAP))
				this.treeMap.clear();
			this.stateMask = 0;
		}
	}

}
