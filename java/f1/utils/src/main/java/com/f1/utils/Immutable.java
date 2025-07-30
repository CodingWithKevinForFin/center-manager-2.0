package com.f1.utils;

/**
 * marker interface indicating that instances of this class can not be mutated
 * (hence are thread safe & don't need cloning). Classes implementing this
 * interface should generally be made final (otherwise someone could extend and
 * make mutable)
 */
public interface Immutable {

}
