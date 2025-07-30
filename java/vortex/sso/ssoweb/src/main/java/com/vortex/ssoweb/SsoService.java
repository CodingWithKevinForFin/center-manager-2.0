package com.vortex.ssoweb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Action;
import com.f1.base.PartialMessage;
import com.f1.container.ResultMessage;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletBuilder;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletService;
import com.f1.suite.web.portal.PortletUserConfigStore;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.IdWebCellFormatter;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.MemoryWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.suite.web.table.impl.PercentWebCellFormatter;
import com.f1.suite.web.table.impl.ToggleButtonCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.Formatter;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.OneToMany;
import com.f1.utils.SH;
import com.sso.messages.CreateSsoGroupResponse;
import com.sso.messages.CreateSsoUserResponse;
import com.sso.messages.LoginSsoUserResponse;
import com.sso.messages.QuerySsoEventsRequest;
import com.sso.messages.QuerySsoEventsResponse;
import com.sso.messages.QuerySsoGroupRequest;
import com.sso.messages.QuerySsoGroupResponse;
import com.sso.messages.QuerySsoHistoryRequest;
import com.sso.messages.QuerySsoHistoryResponse;
import com.sso.messages.QuerySsoUserResponse;
import com.sso.messages.ResetPasswordResponse;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoRequest;
import com.sso.messages.SsoResponse;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoGroupRequest;
import com.sso.messages.UpdateSsoGroupResponse;
import com.sso.messages.UpdateSsoUserResponse;

public class SsoService implements PortletService, PortletUserConfigStore {

	final static private Logger log = Logger.getLogger(SsoService.class.getName());
	//private Map<Message, String> requestToPortletId = new IdentityHashMap<Message, String>();
	public static final String ID = "SsoService";
	public static BasicWebCellFormatter memoryFormatter = new MemoryWebCellFormatter().setDefaultWidth(100).lockFormatter();
	final private BasicWebCellFormatter basicFormatter = new BasicWebCellFormatter().setDefaultWidth(200).lockFormatter();
	final private Map<String, SsoUsersTablePortlet> userPortlets = new HashMap<String, SsoUsersTablePortlet>();
	final private Map<String, SsoEventsTablePortlet> eventPortlets = new HashMap<String, SsoEventsTablePortlet>();

	//private Map<Long, SsoUser> usersMap = new HashMap<Long, SsoUser>();
	private Map<Long, SsoGroupAttribute> attMap = new HashMap<Long, SsoGroupAttribute>();
	private OneToMany<Long, SsoGroupAttribute> userToAttribute = new OneToMany<Long, SsoGroupAttribute>();
	private Collection<SsoUpdateEvent> events = new ArrayList<SsoUpdateEvent>();

	//private Map<String, Long> nameToGroup = new HashMap<String, Long>();
	private Map<String, SsoGroupsTablePortlet> groupPortlets = new HashMap<String, SsoGroupsTablePortlet>();

	private Set<Class<? extends Action>> INTERESTED = (Set) CH.s(QuerySsoUserResponse.class, LoginSsoUserResponse.class, CreateSsoUserResponse.class, CreateSsoGroupResponse.class,
			ResetPasswordResponse.class, UpdateSsoUserResponse.class, UpdateSsoGroupResponse.class, SsoUpdateEvent.class, QuerySsoHistoryResponse.class,
			QuerySsoGroupResponse.class, QuerySsoEventsResponse.class);

	//	private Logger logger = new Logger(null);
	// final private RequestOutputPort<Message, AgentSnapshot>
	// snapshotRequestPort;
	final private PortletManager manager;
	private MapWebCellFormatter userStatusFormatter;
	private MapWebCellFormatter userEncodingFormatter;
	private MapWebCellFormatter eventTypeFormatter;
	private BasicWebCellFormatter percentFormatter;
	private BasicWebCellFormatter quantityFormatter;
	private BasicWebCellFormatter symbolWebCellFormatter;
	private BasicWebCellFormatter timeWebCellFormatter;
	private BasicWebCellFormatter showButtonWebCellFormatter;
	boolean first = true, uploaded = false;
	private Map<PartialMessage, String> requestToPortlet = new HashMap<PartialMessage, String>();
	private MapWebCellFormatter groupRevisionFormatter;
	private SsoWebTreeManager ssoWebTreeManager = new SsoWebTreeManager();
	private String namespace;
	private Map<String, PortletBuilder<? extends SsoUserDialog>> userDialogs = new LinkedHashMap<String, PortletBuilder<? extends SsoUserDialog>>();

	public SsoService(PortletManager manager) {
		// this.snapshotRequestPort = snapshotRequestPort;
		this.manager = manager;
		this.namespace = manager.getTools().getRequired("sso.namespace");

		final LocaleFormatter localeFormatter = manager.getLocaleFormatter();
		Formatter timeFormatter = localeFormatter.getDateFormatter(LocaleFormatter.DATETIME);
		Formatter n = localeFormatter.getNumberFormatter(0);
		Formatter pn = localeFormatter.getPercentFormatter(2);

		percentFormatter = new PercentWebCellFormatter(pn).setDefaultWidth(100).lockFormatter();
		quantityFormatter = new NumberWebCellFormatter(n).setDefaultWidth(80).lockFormatter();

		symbolWebCellFormatter = new BasicWebCellFormatter().setCssClass("bold").setDefaultWidth(60).lockFormatter();
		timeWebCellFormatter = new NumberWebCellFormatter(timeFormatter).setDefaultWidth(120).lockFormatter();

		userStatusFormatter = new MapWebCellFormatter(manager.getTextFormatter());
		userStatusFormatter.addEntry(SsoUser.STATUS_ENABLED, "ENABLED", "_cna=green");
		userStatusFormatter.addEntry(SsoUser.STATUS_DISABLED, "DISABLED", "c_cna=red");
		userStatusFormatter.addEntry(SsoUser.STATUS_LOCKED, "LOCKED", "_cna=grey");
		userStatusFormatter.setDefaultWidth(80).lockFormatter();

		userEncodingFormatter = new MapWebCellFormatter(manager.getTextFormatter());
		userEncodingFormatter.addEntry(SsoUser.ENCODING_PLAIN, "PLAIN");
		userEncodingFormatter.addEntry(SsoUser.ENCODING_CHECKSUM64, "CHECKSUM64");
		userEncodingFormatter.setDefaultWidth(60).lockFormatter();

		eventTypeFormatter = new MapWebCellFormatter(manager.getTextFormatter());
		eventTypeFormatter.addEntry(SsoUpdateEvent.USER_CREATE, "User Create");
		eventTypeFormatter.addEntry(SsoUpdateEvent.USER_LOGIN, "User Login");
		eventTypeFormatter.addEntry(SsoUpdateEvent.USER_RESET, "User Reset");
		eventTypeFormatter.addEntry(SsoUpdateEvent.USER_UPDATE, "User Update");
		eventTypeFormatter.addEntry(SsoUpdateEvent.GROUP_CREATE, "Group Create");
		eventTypeFormatter.addEntry(SsoUpdateEvent.GROUP_UPDATE, "Group Update");
		eventTypeFormatter.addEntry(SsoUpdateEvent.USER_EMAIL, "Email Sent To User");
		eventTypeFormatter.setDefaultWidth(80).lockFormatter();

		groupRevisionFormatter = new MapWebCellFormatter(manager.getTextFormatter());
		groupRevisionFormatter.addEntry(0, "ADDED");
		groupRevisionFormatter.addEntry(65535, "REMOVED");
		groupRevisionFormatter.setDefaultWidth(60).lockFormatter();

		this.showButtonWebCellFormatter = new ToggleButtonCellFormatter("image_show_dn", "image_show_up", "shown", "hidden").setDefaultWidth(30).lockFormatter();

		QuerySsoGroupRequest groupRequest = manager.getGenerator().nw(QuerySsoGroupRequest.class);
		groupRequest.setNamespace(namespace);
		groupRequest.setClientLocation(manager.getState().getWebStatesManager().getRemoteAddress());
		manager.sendRequestToBackend("SSO", groupRequest);

		QuerySsoEventsRequest eventsRequest = manager.getGenerator().nw(QuerySsoEventsRequest.class);
		groupRequest.setNamespace(namespace);
		manager.sendRequestToBackend("SSO", eventsRequest);
	}
	public BasicWebCellFormatter getTimeWebCellFormatter() {
		return timeWebCellFormatter;
	}

	public MapWebCellFormatter getEventTypeFormatter() {
		return eventTypeFormatter;
	}

	public MapWebCellFormatter getUserStatusFormatter() {
		return userStatusFormatter;
	}

	public MapWebCellFormatter getUserEncodingFormatter() {
		return userEncodingFormatter;
	}

	@Override
	public String getServiceId() {
		return ID;
	}

	public void addPortlet(Portlet portlet) {

		if (portlet instanceof SsoEventsTablePortlet) {
			SsoEventsTablePortlet p = (SsoEventsTablePortlet) portlet;
			CH.putOrThrow(eventPortlets, portlet.getPortletId(), p);
			for (SsoUpdateEvent event : this.events)
				p.add(event);
		} else if (portlet instanceof SsoUsersTablePortlet) {
			SsoUsersTablePortlet p = (SsoUsersTablePortlet) portlet;
			CH.putOrThrow(userPortlets, portlet.getPortletId(), p);
			for (SsoWebGroup user : ssoWebTreeManager.getGroupsByType(SsoGroup.GROUP_TYPE_USER))
				p.add(user);
		}
	}
	public void removePortlet(Portlet portlet) {

		if (portlet instanceof SsoUsersTablePortlet) {
			CH.removeOrThrow(userPortlets, portlet.getPortletId());
		} else if (portlet instanceof SsoEventsTablePortlet) {
			CH.removeOrThrow(eventPortlets, portlet.getPortletId());
		}

		LH.info(log, SH.afterFirst("" + portlet.getClass(), " ") + " " + portlet.getPortletId() + " removed from service");
	}

	@Override
	public void onBackendAction(Action action) {
		if (action instanceof SsoUpdateEvent) {
			SsoUpdateEvent response = (SsoUpdateEvent) action;
			doEvent(response);
		}
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> action) {
		LH.info(log, action.getAction().getClass() + " response from backend");
		Action a = action.getAction();
		if (!((SsoResponse) a).getOk()) {
			manager.showAlert(((SsoResponse) a).getMessage());
			return;
		}
		if (a instanceof QuerySsoUserResponse) {
		} else if (a instanceof QuerySsoGroupResponse) {
			QuerySsoGroupResponse response = (QuerySsoGroupResponse) a;
			ssoWebTreeManager.onSnapshot(response);
			for (SsoUsersTablePortlet p : this.userPortlets.values())
				for (SsoWebGroup user : ssoWebTreeManager.getGroupsByType(SsoGroup.GROUP_TYPE_USER))
					p.add(user);
		} else if (a instanceof QuerySsoEventsResponse) {
			QuerySsoEventsResponse response = (QuerySsoEventsResponse) a;
			this.events.addAll(response.getEvents());
			for (SsoEventsTablePortlet p : this.eventPortlets.values())
				for (SsoUpdateEvent event : response.getEvents()) {
					p.add(event);
				}
		} else if (a instanceof CreateSsoUserResponse) {
			CreateSsoUserResponse response = (CreateSsoUserResponse) a;
			String portletId = requestToPortlet.remove(action.getRequestMessage().getAction());
			if (!response.getOk()) {
				manager.showAlert(response.getMessage());
				return;
			}
			Portlet portlet = manager.getPortlet(portletId);
			portlet.getParent().removeChild(portletId);
			portlet.onClosed();

			SsoUser user = response.getUser();
			List<SsoUser> users = new ArrayList<SsoUser>();
			users.add(user);
			//doUsers(users);

			List<SsoGroupAttribute> attributes = response.getGroupAttributes();
			//doAttributes(attributes);
		} else if (a instanceof CreateSsoGroupResponse) {
			CreateSsoGroupResponse response = (CreateSsoGroupResponse) a;
			String portletId = requestToPortlet.remove(action.getRequestMessage().getAction());
			if (!response.getOk()) {
				manager.showAlert(response.getMessage());
				return;
			}
			//manager.showAlert("Group Created");
			Portlet portlet = manager.getPortlet(portletId);
			//portlet.getParent().removeChild(portletId);
			//portlet.onClosed();

			SsoGroup group = response.getGroup();
			List<SsoGroup> groups = new ArrayList<SsoGroup>();
			groups.add(group);
			//doGroups(groups);
		} else if (a instanceof UpdateSsoUserResponse) {
			UpdateSsoUserResponse response = (UpdateSsoUserResponse) a;
			String portletId = requestToPortlet.remove(action.getRequestMessage().getAction());
			if (!response.getOk()) {
				manager.showAlert(response.getMessage());
				return;
			}
			//manager.showAlert("User Updated");
			//Portlet portlet = manager.getPortlet(portletId);
			//portlet.getParent().removeChild(portletId);
			//portlet.onClosed();
			SsoUser user = response.getSsoUser();
			List<SsoUser> users = new ArrayList<SsoUser>();
			users.add(user);
			//doUsers(users);

			//doAttributes(attributes);

		} else if (a instanceof UpdateSsoGroupResponse) {
			UpdateSsoGroupResponse response = (UpdateSsoGroupResponse) a;
			String portletId = requestToPortlet.remove(action.getRequestMessage().getAction());
			if (!response.getOk()) {
				manager.showAlert(response.getMessage());
				return;
			}
			//manager.showAlert("Group Updated");
			//Portlet portlet = manager.getPortlet(portletId);
			//portlet.getParent().removeChild(portletId);
			//portlet.onClosed();
			//SsoGroup group = response.getSsoGroup();
			//List<SsoGroup> groups = new ArrayList<SsoGroup>();
			//groups.add(group);
			//doGroups(groups);

		} else if (a instanceof QuerySsoHistoryResponse) {
			QuerySsoHistoryResponse response = (QuerySsoHistoryResponse) a;
			if (!response.getOk())
				manager.showAlert(response.getMessage());
			else {
				QuerySsoHistoryRequest request = (QuerySsoHistoryRequest) action.getRequestMessage().getAction();
				String portletId = requestToPortlet.remove(action.getRequestMessage().getAction());
				FastTablePortlet portlet = (FastTablePortlet) manager.getPortlet(portletId);
				if (null != portlet) {
					DesktopPortlet desktop = PortletHelper.findParentByType(portlet, DesktopPortlet.class);
					if (null != desktop) {
						for (long id : request.getIds()) {
							if (request.getType() == QuerySsoHistoryRequest.TYPE_USER) {
								SsoUsersTablePortlet p = (SsoUsersTablePortlet) manager.buildPortlet(new SsoUsersTablePortlet.Builder().getPortletBuilderId());
								p.clear();
								p.setIsHistory();
								//TODO:
								//for (SsoUser user : response.getSsoUser().get(id)) {
								//p.add(user);
								//}
								//desktop.addChild(tempUser.getFirstName() + " " + tempUser.getLastName() + " History", p);
							} else {
								SsoGroupsTablePortlet p = (SsoGroupsTablePortlet) manager.buildPortlet(new SsoGroupsTablePortlet.Builder().getPortletBuilderId());
								p.clear();
								p.setIsHistory();
								if (response.getParentGroups().get(id) != null)
									for (SsoGroup group : response.getParentGroups().get(id)) {
										group.setName(ssoWebTreeManager.getGroup(group.getId()).getName());
										p.add(new SsoWebGroup(group));
									}
								desktop.addChild("Group History", p);
							}
						}
					} else {
						LH.warning(log, "No desktop portlet available");
					}
				} else {
					LH.warning(log, "No portlet available");
				}
			}
		}
	}
	private void doEvent(SsoUpdateEvent event) {
		ssoWebTreeManager.onEvent(event);
		events.add(event);
		for (SsoEventsTablePortlet p : eventPortlets.values())
			p.add(event);

	}
	@Override
	public Set<Class<? extends Action>> getInterestedBackendMessages() {
		return INTERESTED;
	}

	public WebCellFormatter getIdFormatter(String prepend) {
		return new IdWebCellFormatter(prepend);
	}

	public BasicWebCellFormatter getBasicFormatter() {
		return basicFormatter;
	}

	public BasicWebCellFormatter getBasicNotNullFormatter(String notNull) {
		return new BasicWebCellFormatter().setNullValue(notNull);
	}
	public BasicWebCellFormatter getQuantityFormatter() {
		return quantityFormatter;
	}

	public BasicWebCellFormatter getSymbolWebCellFormatter() {
		return symbolWebCellFormatter;
	}

	public void getHistory(String portletId, Set<Long> ids) {
		QuerySsoHistoryRequest request = manager.getGenerator().nw(QuerySsoHistoryRequest.class);
		List<Long> requestIds = new ArrayList<Long>();
		requestIds.addAll(ids);
		request.setIds(requestIds);
		request.setType(QuerySsoHistoryRequest.TYPE_USER);
		requestToPortlet.put(request, portletId);
		manager.sendRequestToBackend("QuerySsoHistory", request);
	}

	public void sendGroupHistoryRequest(String portletId, Set<Long> ids, byte type) {
		QuerySsoHistoryRequest request = manager.getGenerator().nw(QuerySsoHistoryRequest.class);
		List<Long> requestIds = new ArrayList<Long>();
		requestIds.addAll(ids);
		request.setIds(requestIds);
		request.setType(type);
		request.setNamespace(namespace);
		requestToPortlet.put(request, portletId);
		manager.sendRequestToBackend("QuerySsoHistory", request);

	}

	public void sendRequestToBackend(String portletId, SsoRequest request) {
		request.setNamespace(namespace);
		request.setNamespace(manager.getState().getWebStatesManager().getRemoteAddress());
		if (portletId == null)
			manager.sendRequestToBackend("SSO", request);
		else
			manager.sendRequestToBackend("SSO", portletId, request);
	}

	public List<String> getGroupNames(Set<Long> ids) {
		List<String> groups = new ArrayList<String>();
		if (ids != null) {
			for (long id : ids) {
				SsoWebGroup group = ssoWebTreeManager.getGroup(id);
				if (group != null)
					groups.add(group.getName());
			}
		}
		return groups;
	}
	public MapWebCellFormatter getGroupRevisionFormatter() {
		return groupRevisionFormatter;
	}
	public SsoWebTreeManager getSsoTree() {
		return ssoWebTreeManager;
	}

	public void addSsoPortlet(SsoPortlet ssoPortlet) {
		this.ssoWebTreeManager.addSsoPortlet(ssoPortlet);
	}

	public void removeSsoPortlet(SsoPortlet ssoPortlet) {
		this.ssoWebTreeManager.removeSsoPortlet(ssoPortlet);
	}
	@Override
	public void saveFile(String key, String value) {
		SsoWebUser webuser = (SsoWebUser) manager.getState().getWebStatesManager().getUser();
		SsoUser user = webuser.getSsoUser();
		if (user == null)
			throw new RuntimeException("not logged in");
		Object webattribute = manager.getState().getWebStatesManager().getUserAttributes().get(key);
		SsoGroupAttribute attribute = null;//TODO: webattribute == null ? null : webattribute.getAttribute();
		if (attribute != null) {
			attribute = attribute.clone();
			if (value == null)
				attribute.setRevision(65535);
			attribute.setType(SsoGroupAttribute.TYPE_JSON);
			attribute.setValue(value);
		} else {
			attribute = manager.getGenerator().nw(SsoGroupAttribute.class);
			attribute.setKey(key);
			attribute.setType(SsoGroupAttribute.TYPE_JSON);
			attribute.setValue(value);
			attribute.setGroupId(user.getGroupId());
		}
		UpdateSsoGroupRequest request = manager.getGenerator().nw(UpdateSsoGroupRequest.class);
		request.setGroupId(user.getGroupId());
		request.setGroupAttributes(CH.l(attribute));
		request.setNamespace(namespace);
		request.setClientLocation(manager.getState().getWebStatesManager().getRemoteAddress());
		manager.getState().getWebStatesManager().getUserAttributes().put(key, attribute.getValue());//todo this should only be done after succesful save
		sendRequestToBackend(null, request);
	}
	@Override
	public String loadFile(String key) {
		Object att = manager.getState().getWebStatesManager().getUserAttributes().get(key);
		return (String) att;
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
	public void addToMemberTree(SsoGroup group, List<SsoGroupAttribute> list) {
		SsoGroupsTreePortlet tree = new SsoGroupsTreePortlet(manager.generateConfig());
		tree.setItemToAdd(group, list);
		manager.showDialog("Add Member to Tree", tree);
	}
	public void addDialogForUsers(PortletBuilder<? extends SsoUserDialog> builder) {
		CH.putOrThrow(userDialogs, builder.getPortletBuilderId(), builder);
	}
	public Map<String, PortletBuilder<? extends SsoUserDialog>> getSsoUserDialogs() {
		return userDialogs;
	}
	//	@Override
	//	public PortletFile getFile(String key) {
	//		// TODO Auto-generated method stub
	//		return null;
	//	}
	@Override
	public void putSetting(String key, Object value) {
		saveFile(key, (String) value);
	}
	@Override
	public String getSettingString(String key) {
		return loadFile(key);

	}
	@Override
	public void saveSettings() {
	}

	@Override
	public <T> T getSetting(String key, Class<T> type) {
		return OH.cast(getSettingString(key), type);
	}
	@Override
	public void removeSetting(String key) {
		saveFile(key, null);
	}
	@Override
	public void handleCallback(Map<String, String> attributes, HttpRequestAction action) {
		// TODO Auto-generated method stub

	}
	@Override
	public Set<String> getFiles() {
		throw new UnsupportedOperationException();
	}
	@Override
	public void removeFile(String key) {
		throw new UnsupportedOperationException();
	}
	//	@Override
	//	public boolean isFileWriteable(String key) {
	//		throw new UnsupportedOperationException();
	//	}
	@Override
	public void setFileWriteable(String name, boolean b) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void moveFile(String name, String newFile) {
		throw new UnsupportedOperationException();
	}

}
