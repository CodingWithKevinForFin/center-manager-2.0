package com.f1.ami.web;

import java.util.List;

import com.f1.utils.structs.table.derived.ParamsDefinition;

/**
 * Responsible for:<BR>
 * 1. Initiating a singleton javascript object, see {@link #getJavascriptInitialization()}<BR>
 * 2. Processing incoming calls from javascript (see {@link #onCallFromJavascript(String, Object, AmiWebGuiServiceAdapterPeer)}) and passing the call onto amiscript(see
 * {@link AmiWebGuiServiceAdapterPeer#executeAmiScriptCallback(String, Object[])}<BR>
 * 3. Processing incoming calls from amiscript (see {@link #onAmiScriptMethod(String, Object[], AmiWebGuiServiceAdapterPeer)} and passing the call onto javascript(see
 * {@link AmiWebGuiServiceAdapterPeer#executeJavascriptCallback(String, Object)}<BR>
 * 
 */

public interface AmiWebGuiServiceAdapter {

	/**
	 * gets called directly after constructions, users should hold onto the peer for callbacks, etc.
	 * 
	 * @param peer
	 */
	public void init(AmiWebGuiServiceAdapterPeer peer);
	/**
	 * 
	 * @return a unique id for identifying this GuiService, this is the Id that is transmitted from web to server. Ex: <i>MYJSADAPTER</i>
	 */
	public String getGuiServiceId();

	/**
	 * 
	 * @return a user understandable name for this GuiService. Ex: <i>My Javascript Adapter</i>
	 */
	public String getDescription();

	/**
	 * @return the name of the class that represents this adapter within the amiscript language. Ex: <i>JsAdapter</i>
	 */
	public String getAmiscriptClassname();

	/**
	 * @return a list of methods that can be called on this call (see {@link #getAmiscriptClassname()}) via amiscript.
	 */
	public List<ParamsDefinition> getAmiscriptMethods();

	/**
	 * Receives the method name and arguments from an amiscript call and then generally, in turn, calls a method on javascript via
	 * {@link AmiWebGuiServiceAdapterPeer#executeJavascriptCallback(String, Object)}
	 * 
	 * @param name
	 *            the name of the method executed via amiscript
	 * @param arg
	 *            arguments passed into the method
	 * @param peer
	 *            an interface for executing amiscript or javascript
	 * @return the value to be returned to the executing amiscript.
	 */
	public Object onAmiScriptMethod(String name, Object[] arg);

	/**
	 * @return a list of amiscript callbacks that designers of the dashboard can register for. These callbacks then be called using
	 *         {@link AmiWebGuiServiceAdapterPeer#executeAmiScriptCallback(String, Object[])}
	 */
	public List<ParamsDefinition> getAmiScriptCallbacks();

	/**
	 * Receives the method name and arguments from a javascript call and then generally, in turn, calls a method on amiscript via
	 * {@link AmiWebGuiServiceAdapterPeer#executeAmiScriptCallback(String, Object[])}
	 * 
	 * @param name
	 *            the name of the method executed via amiscript
	 * @param arg
	 *            arguments passed into the method
	 * @param peer
	 *            an interface for executing amiscript or javascript
	 */
	public void onCallFromJavascript(String name, Object[] args);

	/**
	 * @return a list of javascript libraries to load, if the string starts with http:// or https:// then it's considered a fully qualified url
	 */
	public List<String> getJavascriptLibraries();

	/**
	 * @return javascript that should be executed when the page loads.
	 */
	public String getJavascriptInitialization();

	/**
	 * @return the full javascript for creating the singleton that lives in the web browser. Ex: <i>new MyClass();</i>
	 */
	public String getJavascriptNewInstance();

	/**
	 * called when web page is reloaded, for example, when a user logs in or when a use presses F5
	 */
	public void onPageLoading();

	/**
	 * called when a new layout is loaded, remember a layout can be loaded without a page refresh
	 */
	public void onLayoutStartup();

	/**
	 * @return the full javascript to close the plugin. Called when loading a new layout or when rebuilding current layout
	 */
	public String getJavascriptCloseInstance();
}
