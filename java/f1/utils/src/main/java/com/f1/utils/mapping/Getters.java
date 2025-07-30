package com.f1.utils.mapping;

import com.f1.base.Getter;

public class Getters {

	public static final Getter<Object, Class<?>> GET_CLASS = new Getter<Object, Class<?>>() {
		@Override
		public Class<?> get(Object key) {
			return key == null ? null : key.getClass();
		}
	};

}
