/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.util.Map;

/**
 * 
 * represents a row within a table, note this implements {@link Map}. The keys in the map match the column ids (see {@link Column#getId()}) and the values represent the values in
 * this row. The prefered (performance-wise) way of getting/setting data is by index as it avoids the map lookup. All rows are "owned" by a Table. Rows may not participate in
 * multiple tables at the same time
 * 
 */
public interface Row extends Map<String, Object>, NameSpaceCalcFrame, IterableAndSize<Object> {

	/**
	 * 
	 * @return the "owning Table"
	 */
	public Table getTable();

	/**
	 * IMPORANT: FOR PERFORMANCE ONLY... do not mutate the contents of the returned array.
	 * 
	 * @return direct access to the underlying data of the row.
	 */
	public Object[] getValues();
	public Object[] getValuesCloned();

	/**
	 * IMPORANT: this will not fire updates and should only be used for extreme performance. Directly updates the underlying array. For perforance reasons, no sanitiy check is done
	 * and the array is NOT copied. Be sure the data is in the proper format before setting.
	 */
	public void setValues(Object[] values);

	/**
	 * @param i
	 *            - zero indexed position of value to get (see {@link Column#getLocation()})
	 * @return the value at supplied position for this row
	 */
	public Object getAt(int i);

	/**
	 * 
	 * Convenience method for getting a value and automatically casting to the appropriate type. WIll throw exception if can not cast.
	 * 
	 * @param key
	 *            - column id of value to get (see {@link Column#getLocation()})
	 * @param clazz
	 *            - the type of value to return
	 * @return the value at supplied key for this row
	 */
	public <T> T get(Object key, Class<T> clazz);

	/**
	 * 
	 * Convenience method for getting a value and automatically casting to the appropriate type. WIll throw exception if can not cast.
	 * 
	 * @param key
	 *            - column id of value to get (see {@link Column#getLocation()})
	 * @param caster
	 *            - the type of value to return
	 * @return the value at supplied key for this row
	 */
	public <T> T get(Object key, Caster<T> caster);

	/**
	 * 
	 * Convenience method for getting a value and automatically casting to the appropriate type. Will throw exception if can not cast.
	 * 
	 * @param i
	 *            - zero indexed position of value to get (see {@link Column#getLocation()})
	 * @param clazz
	 *            - the type of value to return
	 * @return the value at supplied position for this row
	 */
	public <T> T getAt(int i, Class<T> clazz);

	/**
	 * 
	 * Convenience method for getting a value and automatically casting to the appropriate type. WIll throw exception if can not cast.
	 * 
	 * @param key
	 *            - column id of value to get (see {@link Column#getLocation()})
	 * @param caster
	 *            - the type of value to return
	 * @return the value at supplied key for this row
	 */
	public <T> T getAt(int i, Caster<T> clazz);

	/**
	 * 
	 * @param i
	 *            - zero indexed position of value to get (see {@link Column#getLocation()})
	 * @param value
	 *            - value to store
	 * 
	 * @return the old value at the position
	 */
	public Object putAt(int i, Object value);

	/**
	 * @return the location of the row within the table (zero being the first fow). See {@link TableList} for details.
	 */
	public int getLocation();

	/**
	 * @return the unique id that this row represents.
	 */
	public int getUid();

}
