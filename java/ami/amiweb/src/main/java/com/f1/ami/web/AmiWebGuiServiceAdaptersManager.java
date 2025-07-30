package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.FastPrintStream;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebGuiServiceAdaptersManager {

	private static final Logger log = LH.get();
	private Map<String, Wrapper> adapters = new LinkedHashMap<String, Wrapper>();
	private AmiWebService service;
	private Collection<AmiWebGuiServicePlugin> gsa;
	private boolean adaptersLoaded = false;

	public AmiWebGuiServiceAdaptersManager(AmiWebService service, Collection<AmiWebGuiServicePlugin> gsa) {
		this.service = service;
		this.gsa = gsa;
	}

	public void init() {
		Set<String> descriptions = new HashSet<String>();
		Set<String> classnames = new HashSet<String>();
		Set<Class> classes = new HashSet<Class>();
		this.adapters.clear();
		for (AmiWebGuiServicePlugin i : gsa) {
			try {
				AmiWebGuiServiceAdapter a = i.createGuiIntegrationAdapter(service);
				String id = a.getGuiServiceId();
				Wrapper value = new Wrapper(id, a);
				a.init(value);
				CH.putOrThrow(this.adapters, id, value, "Duplicate GuiServiceId");
				CH.addOrThrow(descriptions, value.description);
				CH.addOrThrow(classnames, value.amiscriptClassname);
				CH.addOrThrow(classes, a.getClass());
			} catch (Exception e) {
				throw new RuntimeException("Error with GuiServicePlugin: " + i, e);
			}
		}
	}
	public void onInitDone() {
		for (String adapter : this.getAdapterIds()) {
			this.getCallbacks(adapter).initCallbacksLinkedVariables();
		}
	}
	public AmiWebAmiScriptCallbacks getCallbacks(String s) {
		return CH.getOrThrow(this.adapters, s).callbacks;
	}
	public String getDescription(String s) {
		return CH.getOrThrow(this.adapters, s).description;
	}

	public String getAmiscriptClassname(String s) {
		return CH.getOrThrow(this.adapters, s).amiscriptClassname;
	}
	public Set<String> getAdapterIds() {
		return this.adapters.keySet();
	}

	private class AmiGuiServiceMemberMethod extends AmiAbstractMemberMethod<AmiWebGuiServiceAdapter> {

		final private ParamsDefinition cb;
		final private Wrapper wrapper;
		final private String[] paramNames;
		private String[] paramDescriptions;

		public AmiGuiServiceMemberMethod(ParamsDefinition cb, Wrapper wrapper) {
			super((Class) wrapper.adapter.getClass(), cb.getMethodName(), cb.getReturnType(), cb.isVarArg(), cb.getParamTypes());
			this.cb = cb;
			this.wrapper = wrapper;
			this.paramNames = new String[cb.getParamsCount()];
			this.paramDescriptions = new String[cb.getParamsCount()];
			for (int i = 0; i < this.paramNames.length; i++) {
				this.paramNames[i] = cb.getParamName(i);
				this.paramDescriptions[i] = cb.getParamDescriptionHtml(i);
			}
		}

		public String[] getParamNames() {
			return this.paramNames;
		}
		public String[] getParamDescriptions() {
			return this.paramDescriptions;
		}
		@Override
		public java.lang.String getDescription() {
			String r = this.cb.getDescriptionHtml();
			if (SH.is(r))
				return r;
			return "Callback for " + wrapper.description;
		}

		@Override
		protected String[] buildParamDescriptions() {
			return null;
		};

		@Override
		protected String[] buildParamNames() {
			return null;
		}
		@Override
		protected String getHelp() {
			return null;
		}
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebGuiServiceAdapter targetObject, Object[] params, DerivedCellCalculator caller) {
			OH.assertEqIdentity(targetObject, wrapper.adapter);
			try {
				return this.wrapper.adapter.onAmiScriptMethod(cb.getMethodName(), params);
			} catch (Exception e) {
				service.getPortletManager().showAlert("Error with GuiServicePlugin '" + wrapper.id + "' porcessing amiscript method: " + cb.getMethodName(), e);
				return null;
			}
		}

		@Override
		public boolean isReadOnly() {
			return false;
		}

	}

	private class Wrapper implements AmiWebGuiServiceAdapterPeer {

		final private String id;
		final private String description;
		final private String amiscriptClassname;
		final private AmiWebGuiServiceAdapter adapter;
		final private AmiWebAmiScriptCallbacks callbacks;
		final private List<AmiGuiServiceMemberMethod> methods = new ArrayList<AmiWebGuiServiceAdaptersManager.AmiGuiServiceMemberMethod>();
		final private String layoutAlias = "";
		private BasicMethodFactory methodFactory;

		public Wrapper(String id, AmiWebGuiServiceAdapter i) {
			this.id = id;
			this.adapter = i;
			this.description = i.getDescription();
			this.amiscriptClassname = i.getAmiscriptClassname();
			this.callbacks = new AmiWebAmiScriptCallbacks(service, service);
			this.callbacks.setAmiLayoutAlias(layoutAlias);
			for (ParamsDefinition cb : i.getAmiScriptCallbacks()) {
				this.callbacks.registerCallbackDefinition(cb);
			}
			for (ParamsDefinition cb : i.getAmiscriptMethods()) {
				AmiGuiServiceMemberMethod method = new AmiGuiServiceMemberMethod(cb, this);
				this.methods.add(method);
			}
			for (String s : service.getLayoutFilesManager().getFullAliasesByPriority()) {
				AmiWebScriptManagerForLayout sm = service.getScriptManager(s);
				if (sm.forNameNoThrow(amiscriptClassname) != null)
					throw new RuntimeException("Duplicate classname: " + amiscriptClassname);
				this.methodFactory = sm.getMethodFactory();
				methodFactory.addVarType(this.amiscriptClassname, i.getClass());
				for (AmiGuiServiceMemberMethod method : this.methods)
					methodFactory.addMemberMethod(method);
			}
			service.getVarsManager().addGlobalVar("__" + amiscriptClassname, i, (Class) i.getClass(), AmiWebVarsManager.SOURCE_PREDEFINED);
		}
		@Override
		public Object executeAmiScriptCallback(String callback, Object[] args) {
			try {
				ParamsDefinition def = this.callbacks.getAmiScriptCallbackDefinition(callback);
				Object[] args2 = def.castArguments(args);
				if (args2 == null) {
					StringBuilder sink = new StringBuilder();
					sink.append('(');
					for (int i = 0; i < args.length; i++) {
						if (i > 0)
							sink.append(',');
						sink.append(args[i] == null ? "null" : methodFactory.forType(args[i].getClass()));
					}
					sink.append(')');
					service.getPortletManager()
							.showAlert("Error with GuiServicePlugin '" + id + "' argument mismatch for method: " + def.toString(methodFactory) + ". Supplied arguments: " + sink);
					return null;
				}
				return this.callbacks.executeArgs(callback, args2);
			} catch (Exception e) {
				service.getPortletManager().showAlert("Error with GuiServicePlugin '" + id + "' calling amiscript callback: " + callback, e);
				return null;
			}
		}

		@Override
		public void executeJavascriptCallback(String methodName, Object[] jsonObject) {
			try {
				String data = ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(jsonObject);
				JsFunction func = new JsFunction(service.getPortletManager().getPendingJs(), null, "callAmiGuiServiceJavascript");
				func.addParamQuoted(id).addParamQuoted(methodName).addParamQuoted(data).end();
			} catch (Exception e) {
				service.getPortletManager().showAlert("Error with GuiServicePlugin '" + id + "' calling javascript method: " + methodName + "(...)", e);
			}
		}
		@Override
		public AmiWebService getService() {
			return service;
		}

	}

	public void callJsInit() {
		loadAdaptersIfNeeded();
	}
	private void callJsInitAdapter(StringBuilder out, Wrapper i) {
		out.append("log('AMI GuiServicePlugin Manager:Initializing GUIServicePlugin: ");
		WebHelper.escapeHtml(i.id, out);
		out.append("');\n");
		String js;
		String js2;
		try {
			js = i.adapter.getJavascriptInitialization();
			js2 = i.adapter.getJavascriptNewInstance();
		} catch (Exception e) {
			out.append("log('AMI GuiServicePlugin Manager:Initialization failed on server side');\n");
			service.getPortletManager().showAlert("Critical Error with GuiServicePlugin '" + i.id + "' generating javascript initialization: " + e.getMessage(), e);
			return;
		}
		if (SH.is(js))
			out.append(js).append('\n');
		out.append("{ var t=");
		out.append(js2);
		out.append(";\n");
		out.append("registerGuiService('");
		out.append(i.id);
		out.append("',t);}\n");
	}

	public void handleCallback(Map<String, String> attributes, HttpRequestAction action) {
		String id = null;
		try {
			id = CH.getOrThrow(attributes, "guiserviceid");
			Wrapper wrapper = CH.getOrThrow(this.adapters, id);
			String method = CH.getOrThrow(attributes, "gsmethod");
			String data = CH.getOrThrow(attributes, "gsdata");
			List args = (List) ObjectToJsonConverter.INSTANCE_COMPACT.stringToObject(data);
			Object[] args2 = AH.toArray(args, Object.class);
			wrapper.adapter.onCallFromJavascript(method, args2);
		} catch (Exception e) {
			service.getPortletManager().showAlert("Error with GuiServicePlugin '" + id + "' processing callback from javascript on java adapter", e);
		}
	}
	public void callJsLoadLibraries(FastPrintStream out) {
		if (!this.adapters.isEmpty()) {
			for (Wrapper i : this.adapters.values()) {
				for (String lib : i.adapter.getJavascriptLibraries()) {
					if (SH.startsWith(lib, "https://") || SH.startsWith(lib, "http://"))
						out.append("<script type=\"text/javascript\" src=\"" + lib + "?\"></script>");
					else
						out.append("<script type=\"text/javascript\" src=\"amiweb/" + lib + "?\"></script>");
				}
			}
		}
	}
	public void init(String alias, Map config, StringBuilder sb) {
		if (config != null) {
			List<Map> callbacks = (List) config.get("adapters");
			if (callbacks == null)
				return;
			for (Map m : callbacks) {
				String id = CH.getOrThrow(String.class, m, "id");
				Map cb = CH.getOrThrow(Map.class, m, "callbacks");
				Wrapper a = this.adapters.get(id);
				if (a != null)
					a.callbacks.init(null, alias, cb, sb);
			}
		}
	}
	public Map getConfiguration() {
		if (this.adapters.size() == 0)
			return Collections.EMPTY_MAP;
		List<Map> callbacks = new ArrayList<Map>(this.adapters.size());
		for (Wrapper i : this.adapters.values())
			callbacks.add(CH.m("id", i.id, "callbacks", i.callbacks.getConfiguration()));
		return CH.m("adapters", callbacks);
	}
	public void fireOnPageLoading() {
		for (Wrapper i : this.adapters.values())
			try {
				i.adapter.onPageLoading();
			} catch (Exception e) {
				LH.warning(log, "Error for adapter ", i.id, e);
			}
	}
	public void fireOnLayoutStartup() {
		// rebuild/load new layout
		loadAdaptersIfNeeded();
		for (Wrapper i : this.adapters.values())
			try {
				i.adapter.onLayoutStartup();
			} catch (Exception e) {
				LH.warning(log, "Error for adapter ", i.id, e);
			}
	}

	public void loadAdaptersIfNeeded() {
		StringBuilder out = this.service.getPortletManager().getPendingJs();
		if (this.isAdaptersLoaded()) {
			for (Wrapper i : this.adapters.values()) {
				callJsCloseAdapter(out, i);
			}
		}
		this.setAdaptersLoaded(true);
		if (!this.adapters.isEmpty()) {
			out.append("log('AMI GuiServicePlugin Manager:Initialization the following GUIServicePlugins: ");
			SH.join(',', this.adapters.keySet(), out);
			out.append("');\n");
			for (Wrapper i : this.adapters.values()) {
				callJsInitAdapter(out, i);
			}
			out.append("log('AMI GuiServicePlugin Manager:Initialization process complete');\n");
		}
	}

	private void callJsCloseAdapter(StringBuilder out, Wrapper i) {
		out.append("log('Closing GUIServicePlugin: ");
		WebHelper.escapeHtml(i.id, out);
		out.append("');\n");
		String js = i.adapter.getJavascriptCloseInstance();
		if (js != null)
			i.executeJavascriptCallback(js, null);
	}

	public void recompileAmiscript() {
		for (Wrapper i : this.adapters.values())
			try {
				i.callbacks.recompileAmiscript();
			} catch (Exception e) {
				LH.warning(log, "Error for adapter ", i.id, e);
			}
	}
	public boolean isAdaptersLoaded() {
		return adaptersLoaded;
	}

	public void setAdaptersLoaded(boolean load) {
		this.adaptersLoaded = load;
	}
}
