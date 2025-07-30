package com.vortex.ssoweb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.suite.web.tree.impl.WebTreeHelper;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.structs.BasicMultiMap;
import com.sso.messages.CreateSsoGroupRequest;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoGroupMemberRequest;
import com.sso.messages.UpdateSsoGroupRequest;
import com.vortex.ssoweb.NodeSelectionInterPortletMessage.Mask;

public class SsoGroupsTreePortlet extends FastTreePortlet implements WebTreeContextMenuFactory, WebTreeContextMenuListener, SsoPortlet {
	private static final Logger log = Logger.getLogger(SsoGroupsTreePortlet.class.getName());
	private Set<WebTreeNode> clipboard = new IdentityHashSet<WebTreeNode>();

	private static final int MODE_COPY = 1;
	private static final int MODE_CUT = 2;
	private static final int MODE_NONE = 3;
	private int mode;
	private SsoService service;
	private BasicMultiMap.List<SsoWebGroup, WebTreeNode> nodes = new BasicMultiMap.List<SsoWebGroup, WebTreeNode>();
	private BasicPortletSocket socket;
	private SsoGroup groupToAdd;
	private List<SsoGroupAttribute> attributesToAdd;

	public SsoGroupsTreePortlet(PortletConfig portletConfig) {
		super(portletConfig);
		getTree().setContextMenuFactory(this);
		getTree().addMenuContextListener(this);
		this.service = (SsoService) getManager().getService(SsoService.ID);
		getTree().getTreeManager().getRoot().setName("Groups");
		SsoWebTreeManager ssoTree = this.service.getSsoTree();
		WebTreeNode root = super.getTree().getTreeManager().getRoot();
		for (SsoWebGroup group : ssoTree.getRoots())
			addGroup(root, group);
		service.addSsoPortlet(this);
		this.socket = addSocket(true, "selection", "Node Selection", true, CH.s(NodeSelectionInterPortletMessage.class), null);
	}

	private WebTreeNode addGroup(WebTreeNode root, SsoWebGroup group) {
		//WebTreeNode r = super.getTree().getTreeManager().createNode(group.getName() + "[" + group.getGroupId() + "]", root, false).setData(group).setCssClass("clickable");
		WebTreeNode r = super.getTree().getTreeManager().createNode(group.getName(), root, false).setData(group).setCssClass("clickable");
		switch (group.getType()) {
			case SsoGroup.GROUP_TYPE_USER:
				r.setIcon("portlet_icon_user");
				break;
			case SsoGroup.GROUP_TYPE_HOST:
				r.setIcon("portlet_icon_host");
				break;
			case SsoGroup.GROUP_TYPE_GENERIC:
				r.setIcon("portlet_icon_group");
				break;
			case SsoGroup.GROUP_TYPE_REGION:
				r.setIcon("portlet_icon_region");
				break;
			case SsoGroup.GROUP_TYPE_PROCESS:
				r.setIcon("portlet_icon_process");
				break;
			case SsoGroup.GROUP_TYPE_ENVIRONMENT:
				r.setIcon("portlet_icon_environment");
				break;
			case SsoGroup.GROUP_TYPE_DEPLOYMENT:
				r.setIcon("portlet_icon_deployment");
				break;
			case SsoGroup.GROUP_TYPE_EXPECTATION:
				r.setIcon("portlet_icon_eye");
				break;
			case SsoGroup.GROUP_TYPE_ACCOUNT:
				r.setIcon("portlet_icon_account");
				break;
		}
		nodes.putMulti(group, r);
		for (SsoWebGroup child : group.getChildren().values())
			addGroup(r, child);
		return r;
	}

	public static class Builder extends AbstractPortletBuilder<SsoGroupsTreePortlet> {

		public Builder() {
			super(SsoGroupsTreePortlet.class);
		}

		public static final String ID = "ssoGroupTreePortlet";

		@Override
		public SsoGroupsTreePortlet buildPortlet(PortletConfig portletConfig) {
			return new SsoGroupsTreePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Members Tree";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		if (selected.size() == 1) {
			SsoWebGroup node = (SsoWebGroup) selected.get(0).getData();

			//null is for root
			if (node instanceof SsoWebGroup || node == null) {
				children.add(new BasicWebMenuLink("Create Item", true, "createGroup"));
				children.add(new BasicWebMenuLink("Create user", true, "createUser"));
				if (mode != MODE_NONE)
					children.add(new BasicWebMenuLink("Paste", true, "paste"));
				if (node.getType() == SsoGroup.GROUP_TYPE_USER) {
					children.add(new BasicWebMenuLink("Edit", true, "edit"));
				}
			}
		}
		children.add(new BasicWebMenuLink("Remove from Group", true, "remove"));
		children.add(new BasicWebMenuLink("Copy", true, "copy"));
		children.add(new BasicWebMenuLink("Cut", true, "cut"));
		children.add(new BasicWebMenuLink("Delete", true, "delete"));
		return new BasicWebMenu("", true, children);
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		if ("createGroup".equals(action)) {
			NewSsoGroupFormPortlet p = new NewSsoGroupFormPortlet(generateConfig());
			SsoWebGroup node = (SsoWebGroup) tree.getSelected().get(0).getData();
			p.setGroup(node);
			getManager().showDialog("Add Group", p);
		} else if ("createUser".equals(action)) {
			NewSsoUserFormPortlet p = new NewSsoUserFormPortlet(generateConfig());
			SsoWebGroup node = (SsoWebGroup) tree.getSelected().get(0).getData();
			p.setGroup(node);
			getManager().showDialog("Add User", p);
		} else if ("cut".equals(action)) {
			setMode(MODE_CUT, tree.getSelected());
		} else if ("copy".equals(action)) {
			setMode(MODE_COPY, tree.getSelected());
		} else if ("remove".equals(action)) {
			for (WebTreeNode child : tree.getSelected()) {
				SsoWebGroup ssochild = (SsoWebGroup) child.getData();
				SsoWebGroup ssogroup = (SsoWebGroup) child.getParent().getData();
				if (ssogroup == null)//can't remove from root!
					continue;
				SsoGroupMember groupMember = service.getSsoTree().getGroupMember(ssogroup.getGroupId(), ssochild.getType(), ssochild.getGroupId());
				groupMember = groupMember.clone();
				groupMember.setRevision(65535);
				UpdateSsoGroupMemberRequest request = nw(UpdateSsoGroupMemberRequest.class);
				request.setSsoGroupMember(groupMember);
				service.sendRequestToBackend(getPortletId(), request);
			}
		} else if ("paste".equals(action)) {
			WebTreeNode selection = tree.getSelected().get(0);
			for (WebTreeNode child : clipboard) {
				SsoWebGroup ssochild = (SsoWebGroup) child.getData();
				{
					SsoWebGroup ssogroup = (SsoWebGroup) selection.getData();
					if (ssochild.getParents().containsKey(ssogroup.getGroupId()))
						continue;
					SsoGroupMember groupMember = nw(SsoGroupMember.class);
					groupMember.setGroupId(ssogroup.getGroupId());
					groupMember.setMemberId(ssochild.getGroupId());
					UpdateSsoGroupMemberRequest request = nw(UpdateSsoGroupMemberRequest.class);
					request.setSsoGroupMember(groupMember);
					service.sendRequestToBackend(getPortletId(), request);
				}
				if (mode == MODE_CUT) {
					SsoWebGroup ssogroup = (SsoWebGroup) child.getParent().getData();
					if (ssogroup == null)//can't remove from root!
						continue;
					SsoGroupMember groupMember = service.getSsoTree().getGroupMember(ssogroup.getGroupId(), ssochild.getType(), ssochild.getGroupId());
					groupMember = groupMember.clone();
					groupMember.setRevision(65535);
					UpdateSsoGroupMemberRequest request = nw(UpdateSsoGroupMemberRequest.class);
					request.setSsoGroupMember(groupMember);
					service.sendRequestToBackend(getPortletId(), request);
				}
			}
			setMode(MODE_NONE, null);
		} else if ("delete".equals(action)) {
			for (WebTreeNode node : tree.getSelected()) {
				SsoWebGroup ssoNode = (SsoWebGroup) node.getData();
				if (ssoNode instanceof SsoWebGroup) {
					SsoWebGroup group = (SsoWebGroup) ssoNode;
					UpdateSsoGroupRequest request = nw(UpdateSsoGroupRequest.class);
					request.setGroup(group.getGroup().clone());
					request.setGroupId(group.getGroupId());
					//TODO:request.setGroup(group.getPeer().clone());
					request.getGroup().setRevision(65535);
					service.sendRequestToBackend(getPortletId(), request);
				}
			}
		} else if ("edit".equals(action)) {
			SsoWebGroup node = (SsoWebGroup) tree.getSelected().get(0).getData();
			SsoUser user = (SsoUser) node.getPeer();
			NewSsoUserFormPortlet p = new NewSsoUserFormPortlet(generateConfig());
			p.setUserToEdit(user);
			getManager().showDialog("Edit User", p);
		}
	}

	private void setMode(int mode, Collection<WebTreeNode> selected) {
		for (WebTreeNode n : clipboard) {
			WebTreeHelper.removeCssClass(n, "clipboarder", true);
		}
		clipboard.clear();
		this.mode = mode;
		if (selected != null)
			for (WebTreeNode n : selected) {
				if (clipboard.add(n)) {
					WebTreeHelper.applyCssClass(n, "clipboarder", true);
					n.setSelected(false);
				}
			}
	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode target) {
		if (target == null)
			return;
		if (groupToAdd != null) {
			CreateSsoGroupRequest request = nw(CreateSsoGroupRequest.class);
			SsoGroup group = groupToAdd;
			group.setNow(EH.currentTimeMillis());
			request.setGroup(group);
			List<SsoGroupAttribute> groupAttributes = attributesToAdd;
			SsoWebGroup parentGroup = (SsoWebGroup) target.getData();
			if (parentGroup == null)
				request.setParentGroups(OH.EMPTY_LONG_ARRAY);
			else
				request.setParentGroups(AH.longs(parentGroup.getGroupId()));
			request.setGroupAttributes(groupAttributes);
			service.sendRequestToBackend(getPortletId(), request);
			close();
		} else if (socket.hasConnections()) {
			NodeSelectionInterPortletMessage msg = new NodeSelectionInterPortletMessage();
			List<Mask> masks = new ArrayList<Mask>();
			int priority = 0;
			Set<Long> selectedGroupIds = new HashSet<Long>();
			Set<Long> expectationIds = new HashSet<Long>();
			for (WebTreeNode node : tree.getSelected()) {
				Mask mask = null;
				SsoWebGroup data = (SsoWebGroup) node.getData();
				if (data != null) {
					selectedGroupIds.add(data.getGroupId());
					searchChildrenForExpections(data, expectationIds);
				}
				for (;;) {
					if (data != null) {
						String name = data.getName();
						String type = data.getTypeName();
						for (SsoGroupAttribute att : data.getGroupAttributes().values()) {
							if (att.getKey().endsWith("_mask")) {
								String maskKey = SH.stripSuffix(att.getKey(), "_mask", true);
								mask = new Mask(priority++, name, type, maskKey, att.getValue(), mask, data.getGroupId());
							}
						}
						Object peer = data.getPeer();
						if (peer instanceof SsoUser) {
							mask = new Mask(priority++, name, type, "user", ((SsoUser) peer).getUserName(), mask, data.getGroupId());
						}
					}
					node = node.getParent();
					if (node == null)
						break;
					data = (SsoWebGroup) node.getData();
				}
				if (mask != null)
					masks.add(mask);
			}
			msg.setSelectedGroupIds(selectedGroupIds);
			msg.setMasks(masks);
			msg.setExpectationIds(expectationIds);
			socket.sendMessage(msg);
		}
		//node.setIsExpanded(!node.getIsExpanded());
	}
	private void searchChildrenForExpections(SsoWebGroup data, Set<Long> expectationIds) {
		if (data.getType() == SsoGroup.GROUP_TYPE_EXPECTATION)
			expectationIds.add(Long.parseLong(Caster_String.INSTANCE.cast(data.getGroupAttributes().get("id").getValue())));
		for (SsoWebGroup child : data.getChildren().values())
			searchChildrenForExpections(child, expectationIds);

	}

	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClosed() {
		super.onClosed();
		service.removeSsoPortlet(this);
	}

	@Override
	public void onNewGroup(SsoWebGroup group) {
		if (group.getParents().size() == 0) {
			addGroup(getTree().getTreeManager().getRoot(), group);
		} else {
			for (SsoWebGroup i : group.getParents().values()) {
				for (WebTreeNode p : CH.i(nodes.get(i)))
					if (p != null)
						addGroup(p, group);
			}
		}
	}

	@Override
	public void onEvent(SsoUpdateEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoveGroup(SsoWebGroup group) {
		for (final WebTreeNode node : CH.i(this.nodes.remove(group))) {
			for (WebTreeNode child : CH.l(node.getChildren())) {
				final SsoWebGroup ssoWebChild = (SsoWebGroup) child.getData();

				if (ssoWebChild.getParents().isEmpty()) {
					getTreeManager().removeNode(child);
					getTreeManager().getRoot().addChild(child);
				}
			}
			getTreeManager().removeNode(node);
		}
	}

	@Override
	public void onRemoveGroupMember(SsoGroupMember member, SsoWebGroup group, SsoWebGroup node) {
		for (WebTreeNode parentNode : CH.i(nodes.get(group))) {
			inner: for (WebTreeNode child : parentNode.getChildren()) {
				if (child.getData() == node) {
					parentNode.removeChild(child);
					if (node.getParents().isEmpty())
						getTreeManager().getRoot().addChild(child);
					break inner;
				}
			}
		}
	}

	@Override
	public void onNewGroupMember(SsoGroupMember member, SsoWebGroup group, SsoWebGroup child) {

		boolean added = false;
		for (WebTreeNode parentNode : CH.i(nodes.get(group))) {
			addGroup(parentNode, child);
			added = true;
		}
		if (added) {
			for (WebTreeNode childNode : CH.i(nodes.get(child))) {
				if (childNode.getParent() == getTreeManager().getRoot()) {
					getTreeManager().removeNode(childNode);
					break;
				}
			}
		}
	}

	@Override
	public void onNewGroupAttribute(SsoWebGroup group, SsoGroupAttribute attribute) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoveGroupAttribute(SsoWebGroup group, SsoGroupAttribute attribute) {
		// TODO Auto-generated method stub

	}

	public void setItemToAdd(SsoGroup group, List<SsoGroupAttribute> list) {
		this.groupToAdd = group;
		this.attributesToAdd = list;
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}

}
