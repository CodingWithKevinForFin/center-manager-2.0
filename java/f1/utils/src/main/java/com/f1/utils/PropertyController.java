/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.f1.base.Caster;

/**
 * wraps a properties object and provides convenience methods for accessing property values.
 */
public interface PropertyController {
	/**
	 * returns the value associated with the name supplied cast to the type of the supplied default. If no value is associated then the default value is simply returned. Please
	 * note, that if a property is associated with an empty string that is still considered declared. See also: {@link OH#cast(Object, Class)}
	 * 
	 * @param <C>
	 *            type of value to return
	 * @param name
	 *            the name of the property
	 * @param nonNullDefault
	 *            default value to return, if there is not value associated with the name
	 * @return the value associated with the name.
	 * @throws Exception
	 *             if the value could not be cast to the appropriate type, or if the nonNullDefault param supplied is null
	 * 
	 */
	public <C> C getOptional(String name, C nonNullDefault);

	/**
	 * returns the value associated with the name supplied cast to the type of the supplied type. Please note, that if a property is associated with an empty string that is still
	 * considered declared. See also: {@link OH#cast(Object, Class)}
	 * 
	 * @param <C>
	 *            type of value to return
	 * @param name
	 *            the name of the property
	 * @param returnType
	 *            the type of value to return.
	 * @return the value associated with the name.
	 * @throws Exception
	 *             if the value could not be cast to the appropriate type, or if the nonNullDefault param supplied is null
	 */
	public <C> C getOptional(String name, Class<C> returnType);

	public <C> C getOptional(String name, Caster<C> caster);

	/**
	 * returns the value associated with the supplied name cast to the requested type or throws an exception if no value is associated with supplied name
	 * 
	 * @param <C>
	 *            type of value to return
	 * @param name
	 *            - the name of the property
	 * @param returnType
	 *            the type of value to return
	 * @return the value associated with the name... may be null.
	 * @throws Exception
	 *             if the value could not be cast to the appropriate type, or if the value is not declared in the properties
	 */
	public <C> C getRequired(String name, Class<C> returnType);

	public <C> C getRequired(String name, Caster<C> caster);

	/**
	 * @return the internal values represented as a properties object
	 */
	public Properties getProperties();

	/**
	 * returns the value associated with the name supplied cast to the type of the acceptable values. If no value is associated then the first element of the accpetableValues array
	 * is simply returned. This method will throw an exception if the value associated with the name can not be cast to one of the acceptible values. See also: *
	 * {@link OH#cast(Object, Class)}
	 * 
	 * @param <C>
	 * @param name
	 *            the name of the property
	 * @param acceptableValues
	 *            a list of values which are permissible for return. Must have at least one element
	 * @return the value associated with the name, or the first element in the acceptablevalues array if the value is not declared. Will be one of the acceptable values.
	 * @throws Exception
	 *             if the value could not be cast to the appropriate type, or if the acceptableValues is empty, or the value can not be cast to one of the acceptable values
	 */
	public <C> C getOptionalEnum(String name, C... acceptableValues);
	public <C> C getOptionalEnum(String name, Map<String, C> acceptableValues, C dflt);

	/**
	 * returns the value associated with the name supplied cast to the type of the acceptable values. If no value is associated then an exception is thrown. This method will throw
	 * an exception if the value associated with the name can not be cast to one of the acceptible values. See also: * {@link OH#cast(Object, Class)}
	 * 
	 * @param <C>
	 * @param name
	 *            the name of the property
	 * @param acceptableValues
	 *            a list of values which are permissible for return. Must have at least one element
	 * @return the value associated with the namearray if the value is not. Will be one of the acceptable values.
	 * @throws Exception
	 *             if the property is not declared or the value could not be cast to the appropriate type, or if the acceptableValues is empty, or the value can not be cast to one
	 *             of the acceptable values
	 */
	public <C> C getRequiredEnum(String name, C... acceptableValues);

	/**
	 * useful for investigating where a property was derived from. Support for this is optional
	 * 
	 * @param name
	 *            name of property to inspect.
	 * @return list of sources that contain values for the property. Never null, may be an empty list.
	 */
	List<Property> getPropertySources(String name);

	/**
	 * Convenience method for getting a required property as a string. same as calling getRequired(....,String.class)
	 * 
	 * @param string
	 *            key
	 * @return value, never null
	 */
	public String getRequired(String string);

	/**
	 * Convenience method for getting a required property as a string. same as calling getOptional(....,String.class)
	 * 
	 * @param string
	 *            key
	 * @return value as a string, or null
	 */
	public String getOptional(String string);

	/**
	 * @return all keys which have related properties
	 */
	public Set<String> getKeys();

	public List<String> getAllSources();

	public PropertyController getSubPropertyController(String namespace);

	/**
	 * 
	 * if text contains ${...} then it will be substituted with the corresponding property from this controller
	 * 
	 * @param text
	 *            supplied text to have properies applied to
	 * @return resulting text from applying properties
	 */
	public String applyProperties(String text);

	public Property getProperty(String key);

	/**
	 * Returns a list of all values that were returned by {@link #getOptional(String, Object)} and {@link #getOptionalEnum(String, Object...)} when a specific value was not
	 * available, and the default was used.
	 * 
	 * @return values will be a java.util.Collection if different values were returned for the same key
	 */
	Map<String, Object> getDefaultDeclaredProperties();

	public String toProperiesManifest();

}
