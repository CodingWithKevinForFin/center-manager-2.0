/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * @author marker interface (on top of Getter) to indicate that elements returned from {@link #get(Object)} will be new instances
 * 
 */
public interface Factory<K, R> extends Getter<K, R> {

}
