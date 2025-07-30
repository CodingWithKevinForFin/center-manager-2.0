/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.f1.base.ObjectGenerator;
import com.f1.container.ContainerTools;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.WebState;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.impl.PortletNotification;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.suite.web.portal.style.PortletStyleManager;
import com.f1.utils.BundledTextFormatter;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.Tuple2;

public interface PortletManager extends PortletListener, PortletBackendInterface {

	//	String ID = "PORTLET_MANAGER";

	public PortletContainer getRoot();

	public void setCurrentAction(HttpRequestResponse action);

	public HttpRequestResponse getCurrentRequestAction();
	public HttpRequestResponse getLastRequestAction();

	public void drainPendingJs(StringBuilder sb);

	public Portlet getPortlet(String portletId);

	public Portlet getPortletNoThrow(String portletId);

	public StringBuilder getPendingJs();

	public void setBreakJs();

	public String generateId();

	public WebState getState();

	public void addPortletBuilder(PortletBuilder portletAppBuilder);
	public void addPortletBuilder(String portletid, PortletBuilder portletAppBuilder);

	public ObjectGenerator getGenerator();

	public void registerService(PortletService service);

	public PortletService getService(String id);
	public PortletService getServiceNoThrow(String id);

	public void addPortletListener(PortletListener listener);

	public void removePortletlistener(PortletListener listener);

	public void addPortletManagerListener(PortletManagerListener listener);

	public void removePortletManagerListener(PortletManagerListener listener);

	public PortletBackend getBackend();

	public PortletBuilder getPortletBuilder(String builderId);

	public Map<String, Object> getConfiguration(String basePortletId);

	public Portlet init(Map<String, Object> configuration, String containerPorletId, StringBuilder warningsSink, Map<String, String> origToNewPortletIdMappingSink,
			boolean forceNewPortletIds);

	public Portlet init(Map<String, Object> configuration, String containerPorletId, StringBuilder warningsSink);

	public void handleCallback(Map<String, String> attributes, HttpRequestAction action);

	public Portlet buildPortlet(String porletBuilderId);

	public BundledTextFormatter getTextFormatter();

	public LocaleFormatter getLocaleFormatter();

	void showAlert(String text);
	void showAlert(String text, Throwable exception);

	PortletNotification showNotification(String title, String body, String imageUrl, Map<String, String> options);

	void showContextMenu(WebMenu menu, WebMenuListener listener, int x, int y, Map<String, Object> options);
	void showContextMenu(WebMenu menu, WebMenuListener listener, int x, int y);
	void showContextMenu(WebMenu menu, WebMenuListener listener);
	void closeContextMenu();

	public RootPortletDialog showDialog(String title, Portlet p);

	public RootPortletDialog showDialog(String title, Portlet p, int width, int height);
	public RootPortletDialog showDialog(String title, Portlet p, int width, int height, boolean modal);
	public Set<String> getBuilders();

	public PortletMetrics getPortletMetrics();

	public PortletUserConfigStore getUserConfigStore();

	public void setUserConfigStore(PortletUserConfigStore userConfigStore);

	public String describeUser();

	public PortletConfig generateConfig();
	public void loadConfig();
	public void saveConfig();

	//downloads
	public boolean hasPendingDownloads();
	public PortletDownload popPendingDownload();
	public void pushPendingDownload(PortletDownload download);

	public void playAudio(String audioUrl);

	public long getNow();

	boolean getIsLoadingConfig();

	public void close();

	Portlet getCallbackTarget();

	Set<String> getPortletIds();

	ObjectToJsonConverter getJsonConverter();

	public ContainerTools getTools();

	Map<String, Object> getMetadataConfig();
	Object getMetadata(String name);
	Object putMetadata(String name, Object value);
	Object removeMetadata(String name);
	Set<String> getMetadataNames();

	public PortletDownload handleContentRequest(Map<String, String> params, HttpRequestAction action);

	long getPageUid();

	RootPortlet showPopupWindow(Portlet portlet, int left, int top, int width, int height, String title);

	public RootPortlet getCurrentRootPortlet();

	void removePortletNotificationListener(PortletNotificationListener listener);
	void addPortletNotificationListener(PortletNotificationListener listener);

	public PortletStyleManager getStyleManager();

	public void focusPortlet(Portlet Portlet);

	Portlet getFocusedPortlet();

	public boolean getDebugLayout();

	public void setDebugLayout(boolean b);

	RootPortlet getPopoutForPortletId(String portletId);

	public void setSecurityModel(PortletManagerSecurityModel securityModel);
	public PortletManagerSecurityModel getSecurityModel();

	public PortletMenuManager getMenuManager();
	//	void raiseSecurityViolation(String reason); Moved to SecurityModel

	//	public WebUser getUser();

	String getDefaultBrowserTitle();

	public void handlGeneralError(String ticket, Exception e);

	public void sendRestRequest(boolean isPost, URL target, String data, int timeoutMs, PortletManagerRestCallResponseListener listener);

	boolean getIsSecureConnection();

	String getCurrentSeqNum();

	String getUserName();

	boolean getIsClosed();
	boolean getIsOpen();//getIsClosed is false and not in a 'closing' state

	void setUrlParams(LinkedHashMap<String, String> url);
	public String putUrlParam(String key, String value);//value=null to remove
	LinkedHashMap<String, String> getUrlParams();//DO NOT MODIFY THIS MAP

	public void requestFocusOnField(Portlet portet, String attachmentId);
	public void onFieldFocused(Portlet portet, String attachmentId);
	public void onFieldBlured(Portlet portet, String attachmentId);

	public Tuple2<Portlet, String> getFocusedField();

}
