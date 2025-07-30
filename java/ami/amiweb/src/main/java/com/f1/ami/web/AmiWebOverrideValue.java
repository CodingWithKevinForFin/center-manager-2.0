package com.f1.ami.web;

import com.f1.base.Clearable;
import com.f1.utils.OH;
import com.f1.utils.SH;

/**
 * <p>
 * A helper class that can toggle between two values of the same type: the underlying value and the override value. It is initialized with the underlying value, and allows for an
 * override.
 * 
 * <p>
 * Underlying ---> Override ---> Materialized/Current Value = Override
 * <p>
 * Underlying ------------------> Materialized/Current Value = Underlying
 * <p>
 * This is useful for any situation that requires a default state and run time state.
 *
 * @param <T>
 *            any type
 */
public class AmiWebOverrideValue<T> implements Clearable {

	private boolean isOverride = false;
	private T value = null;
	private T oValue = null;

	public AmiWebOverrideValue(T value) {
		this.value = value;
	}

	/**
	 * 
	 * @return the materialized value
	 */
	public T get() {
		return isOverride ? oValue : value;
	}

	public T getOverride() {
		return oValue;
	}

	/**
	 * 
	 * @return the underlying value
	 */
	public T getValue() {
		return value;
	}

	public boolean isOverride() {
		return this.isOverride;
	}
	/**
	 * <p>
	 * Sets the <i>underlying</i> value with the option to clear the override
	 * 
	 * @param value
	 * @param clearOverride
	 * @return true if materialized value changed
	 */
	public boolean set(T value, boolean clearOverride) {
		T orig = get();
		this.value = value;
		if (clearOverride && this.isOverride) {
			this.oValue = null;
			this.isOverride = false;
		}
		return OH.ne(orig, get());
	}

	/**
	 * 
	 * 
	 * @param value
	 * @return true if materialized value changed, false otherwise
	 */
	public boolean setOverride(T value) {
		// old value
		T orig = get();
		isOverride = true;
		this.oValue = value;
		// compare old value to new value
		return OH.ne(orig, get());
	}

	/**
	 * <p>
	 * Checks the parameter against the <i>underlying</i> value: if equal -> clears the override; if different -> updates the override; no op if this has no override.
	 * 
	 * 
	 * @param value
	 * @return true/false to indicate if the materialized value changed as a result
	 */
	public boolean setOverrideOrClearIfSame(T value) {
		// old value
		T orig = get();
		if (OH.eq(this.value, value)) {
			if (!isOverride)
				return false;
			this.isOverride = false;
			this.oValue = null;
			return OH.ne(orig, get());
		}
		isOverride = true;
		this.oValue = value;
		// compare old value to new value
		return OH.ne(orig, get());
	}

	/**
	 * 
	 * @return true if materialized value changed, false otherwise
	 * 
	 */
	public boolean clearOverride() {
		if (!isOverride)
			return false;
		T orig = get();
		this.oValue = null;
		this.isOverride = false;
		return OH.ne(orig, get());
	}

	/**
	 * clears the underlying and the override value
	 */
	@Override
	public void clear() {
		this.oValue = this.value = null;
		this.isOverride = false;
	}

	@Override
	public String toString() {
		if (!isOverride)
			return SH.toString(value);
		else {
			return oValue + " (Overriding " + value + ")";
		}
	}

	public static boolean clearOverrides(AmiWebOverrideValue<?>... values) {
		boolean r = false;
		for (AmiWebOverrideValue<?> t : values)
			if (t.clearOverride())
				r = true;
		return r;
	}

	/**
	 * 
	 * @param override
	 *            if true, returns the 'materialized' aka 'current' value; If false, returns the underlying value
	 * @return The current or the underlying value
	 * 
	 */
	public T getValue(boolean override) {
		return override && this.isOverride ? this.oValue : this.value;
	}
	/**
	 * 
	 * @param value
	 * @param override
	 *            if true, update the override, otherwise clear the override and update the underlying value
	 * @return true if current value changed as a result, false otherwise
	 */
	public boolean setValue(T value, boolean override) {
		return override ? this.setOverrideOrClearIfSame(value) : this.set(value, true);
	}

	/**
	 * <p>
	 * updates the underlying value to the override value
	 * 
	 * @return true if this has an override and the underlying value changed as a result
	 */
	public boolean setOverrideToDefault() {
		return isOverride && this.set(this.oValue, true);
	}

}
