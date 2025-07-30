package com.vortex.web.portlet.forms;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeListener;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Int;
import com.f1.utils.structs.Tuple3;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeCreateDeploymentEnvironmentRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeCreateDeploymentEnvironmentResponse;
import com.vortex.client.VortexClientBackup;
import com.vortex.client.VortexClientDeployment;
import com.vortex.client.VortexClientDeploymentSet;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientManager;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.portlet.forms.VortexWebMachineSelectionFormPortlet.Selection;

public class VortexWebCopyDeploymentsFormPortlet extends GridPortlet implements WebTreeNodeListener, WebTreeContextMenuListener, WebTreeContextMenuFactory, FormPortletListener {

	private DividerPortlet divider;
	private FormPortlet buttons;
	private FastTreePortlet tree;
	private Portlet currentForm;
	private Portlet firstForm;
	private Map<Portlet, WebTreeNode> nodes = new IdentityHashMap<Portlet, WebTreeNode>();
	private VortexWebEyeService service;
	private FormPortletButton copyButton;

	public VortexWebCopyDeploymentsFormPortlet(PortletConfig config) {
		super(config);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		divider = new DividerPortlet(generateConfig(), true);
		divider.setOffset(.4);
		buttons = new FormPortlet(generateConfig());
		tree = new FastTreePortlet(generateConfig());
		tree.getTreeManager().addListener(this);
		HtmlPortlet header = new HtmlPortlet(generateConfig(), "", "comment_header");
		GridPortlet grid2 = new GridPortlet(generateConfig());
		grid2.addChild(header, 0, 0);
		grid2.addChild(tree, 0, 1);
		divider.addChild(grid2);
		header.setHtml("<div style=\"width:100%;height:100%;background:url('rsc/headers/provision.jpg') left no-repeat\")</div>");
		//addChild(header, 0, 0, 1, 1);
		addChild(divider, 0, 0, 1, 1);
		addChild(buttons, 0, 1, 1, 1);
		grid2.setRowSize(0, 120);
		setRowSize(1, 45);
		setSuggestedSize(1200, 750);
		tree.getTree().setRootLevelVisible(false);
		tree.getTree().setContextMenuFactory(this);
		tree.getTree().addMenuContextListener(this);
		this.buttons.addFormPortletListener(this);
		this.buttons.addButton(copyButton = new FormPortletButton("Create Copy"));
	}

	public WebTreeNode addDeploymentSetToCopy(WebTreeNode parent, VortexClientDeploymentSet bp) {
		VortexWebDeploymentSetFormPortlet deploymentSetForm = new VortexWebDeploymentSetFormPortlet(generateConfig());
		deploymentSetForm.setIconToAdd();
		deploymentSetForm.setDeploymentSetToCopy(bp);
		deploymentSetForm.clearButtons();
		WebTreeNode dsNode = addNode(parent, bp, deploymentSetForm);
		dsNode.setIcon("portlet_icon_environment");
		for (VortexClientDeployment deployment : bp.getDeployments())
			addDeploymentToCopy(dsNode, deployment);
		return dsNode;
	}
	public void addDeploymentToCopy(WebTreeNode parent, VortexClientDeployment deployment) {
		VortexWebDeploymentFormPortlet depForm = new VortexWebDeploymentFormPortlet(generateConfig());
		depForm.setIconToAdd();
		if (parent != null)
			depForm.disableDeploymentSetField();
		depForm.setDeploymentToCopy(deployment);
		depForm.clearButtons();
		WebTreeNode depNode = addNode(parent, deployment, depForm);
		depNode.setIcon("portlet_icon_deployment");
		for (VortexClientBackup backup : deployment.getBackups().values()) {
			VortexWebBackupFormPortlet backupForm = new VortexWebBackupFormPortlet(generateConfig());
			backupForm.setIconToAdd();
			backupForm.setBackupToCopy(backup);
			backupForm.disableDeploymentField();
			backupForm.clearButtons();
			WebTreeNode backupNode = addNode(depNode, backup, backupForm);
			backupNode.setIcon("portlet_icon_backup");
		}
	}

	private WebTreeNode addNode(WebTreeNode parent, VortexClientEntity<? extends VortexEyeEntity> data, VortexWebMetadataFormPortlet form) {
		form.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				VortexWebMetadataFormPortlet form = (VortexWebMetadataFormPortlet) e.getSource();
				WebTreeNode node = nodes.get(form);
				node.setName(describe(toEntity(form)));
			}
		});

		if (parent == null)
			parent = tree.getTreeManager().getRoot();
		WebTreeNode r = tree.getTreeManager().createNode(describe(toEntity(form)), parent, true,
				new Tuple3<Object, Boolean, Portlet>(data, parent != tree.getTreeManager().getRoot(), form));
		r.setCssClass("clickable");
		nodes.put(form, r);
		if (currentForm == null) {
			firstForm = currentForm = form;
			divider.addChild(form);
		} else
			getManager().onPortletAdded(form);
		return r;
	}

	@Override
	public void onSelectionChanged(WebTreeNode node) {
		if (!node.getSelected())
			return;
		Tuple3<Object, Boolean, VortexWebMetadataFormPortlet> tuple = getData(node);
		Portlet form = tuple.getC();
		if (form == currentForm)
			return;
		if (currentForm != null) {
			divider.removeChild(currentForm.getPortletId());
			currentForm.setVisible(false);
		}
		divider.addChild(currentForm = form);
	}

	@Override
	public void onContextMenu(final FastWebTree tree, String action) {
		if ("dontCopy".equals(action)) {
			for (WebTreeNode node : CH.l(tree.getSelected())) {
				Tuple3<Object, Boolean, VortexWebMetadataFormPortlet> tuple = getData(node);
				tree.getTreeManager().removeNode(node);
				nodes.remove(tuple.getC());
				if (tuple != null) {
					if (tuple.getC() == currentForm) {
						divider.removeChild(currentForm.getPortletId());
						currentForm = null;
						divider.addChild(firstForm);
						currentForm = firstForm;
					}
				}
			}
		} else if ("setTargetMachine".equals(action)) {
			VortexWebMachineSelectionFormPortlet p = new VortexWebMachineSelectionFormPortlet(generateConfig(), Selection.Single) {
				@Override
				public void onSelection(String[] selectedMUIds) {
					if (AH.isntEmpty(selectedMUIds)) {
						for (WebTreeNode n : tree.getSelected()) {
							Tuple3<Object, Boolean, VortexWebMetadataFormPortlet> data = getData(n);
							if (data.getC() instanceof VortexWebDeploymentFormPortlet) {
								VortexWebDeploymentFormPortlet dp = (VortexWebDeploymentFormPortlet) data.getC();
								dp.setMuid(selectedMUIds[0]);

								n.setName(describe(toEntity(dp)));
							}
						}
					}
				}

			};

			//			final FormPortlet f = new FormPortlet(generateConfig());
			//
			//			final FormPortletSelectField<String> targetMuidField = new FormPortletSelectField<String>(String.class, "Target Machine");
			//			f.addField(new FormPortletTitleField("Deployment Target"));
			//			f.addField(targetMuidField);
			//			for (VortexClientMachine i : service.getAgentManager().getAgentMachinesSortedByHostname())
			//				targetMuidField.addOption(i.getData().getMachineUid(), i.getHostName());
			//
			//			final FormPortletButton cancelBtn = new FormPortletButton("Cancel");
			//			final FormPortletButton submitBtn = new FormPortletButton("Submit");
			//			//			submitBtn.setEnabled(false);
			//			f.addButton(cancelBtn);
			//			f.addButton(submitBtn);
			//
			//			f.addFormPortletListener(new FormPortletListener() {
			//
			//				@Override
			//				public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
			//				}
			//
			//				@Override
			//				public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
			//				}
			//
			//				@Override
			//				public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			//					if (button == submitBtn) {
			//						for (WebTreeNode n : tree.getSelected()) {
			//							Tuple3<Object, Boolean, VortexWebMetadataFormPortlet> data = getData(n);
			//							if (data.getC() instanceof VortexWebDeploymentFormPortlet) {
			//								VortexWebDeploymentFormPortlet dp = (VortexWebDeploymentFormPortlet) data.getC();
			//								dp.targetMuidField.setValue(targetMuidField.getValue());
			//
			//								n.setName(describe(toEntity(dp)));
			//							}
			//						}
			//					}
			//
			//					f.close();
			//				}
			//			});
			getManager().showDialog("Select Deployment Target", p);
		}
	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		if (CH.isEmpty(selected))
			return null;

		boolean anyDeployments = false;
		for (WebTreeNode sel : selected) {
			Tuple3<Object, Boolean, VortexWebMetadataFormPortlet> tuple = getData(sel);
			//			if (tuple == null || !tuple.getB())
			//				return null;

			if (tuple.getA() instanceof VortexClientDeployment)
				anyDeployments = true;
		}

		BasicWebMenu p = new BasicWebMenu();
		p.addChild(new BasicWebMenuLink("Don't copy", true, "dontCopy"));

		if (anyDeployments) {
			p.addChild(new BasicWebMenuDivider());
			p.addChild(new BasicWebMenuLink("Set Target Machine", true, "setTargetMachine"));
		}

		return p;
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	private VortexEyeEntity[] toEntity(VortexWebMetadataFormPortlet form) {
		if (form instanceof VortexWebDeploymentFormPortlet) {
			return ((VortexWebDeploymentFormPortlet) form).toDeployment();
		} else if (form instanceof VortexWebDeploymentSetFormPortlet) {
			return AH.a(((VortexWebDeploymentSetFormPortlet) form).toDeploymentSet());
		} else if (form instanceof VortexWebBackupFormPortlet) {
			return AH.a(((VortexWebBackupFormPortlet) form).toBackup());
		} else
			throw new RuntimeException("unknown form type: " + form);

	}
	private String describe(VortexEyeEntity[] entities) {
		if (AH.isntEmpty(entities)) {
			boolean many = entities.length > 1;

			//assume that we can only get many when multiple target machines are selected to dup the dep to multiple machines

			VortexEyeEntity entity = entities[0];
			VortexClientManager am = service.getAgentManager();
			if (entity instanceof VortexDeployment) {
				VortexDeployment dep = (VortexDeployment) entity;
				StringBuilder sb = new StringBuilder();
				sb.append(am.getBuildProcedure(dep.getProcedureId()).getData().getName());
				sb.append(" [").append(many ? entities.length + " Machines" : am.getAgentMachineByUid(dep.getTargetMachineUid()).getHostName()).append(":")
						.append(dep.getTargetDirectory()).append("]");
				return sb.toString();
			} else if (entity instanceof VortexDeploymentSet) {
				VortexDeploymentSet dep = (VortexDeploymentSet) entity;
				return dep.getName();
			} else if (entity instanceof VortexEyeBackup) {
				VortexEyeBackup backup = (VortexEyeBackup) entity;
				StringBuilder sb = new StringBuilder();
				sb.append(backup.getSourcePath()).append(" --> ").append(am.getBackupDestination(backup.getBackupDestinationId()).getDescription());
				return sb.toString();
			} else
				throw new RuntimeException("unknown entity type: " + entity);
		}

		return "NONE";
	}

	@Override
	public void onNodeAdded(WebTreeNode node) {
	}
	@Override
	public void onNodeRemoved(WebTreeNode node) {
	}
	@Override
	public void onStyleChanged(WebTreeNode node) {
	}
	@Override
	public void onExpanded(WebTreeNode node) {
	}
	@Override
	public void onNodesAddedToVisible(List<WebTreeNode> nodes) {
	}
	@Override
	public void onRemovingNodesFromVisible(List<WebTreeNode> nodes) {
	}
	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		copyButton.setEnabled(false);
		WebTreeNode root = tree.getTreeManager().getRoot();
		Mutable.Int nextId = new Mutable.Int(-1);
		VortexEyeCreateDeploymentEnvironmentRequest req = nw(VortexEyeCreateDeploymentEnvironmentRequest.class);
		req.setBackups(new ArrayList<VortexEyeBackup>());
		req.setDeployments(new ArrayList<VortexDeployment>());
		req.setDeploymentSets(new ArrayList<VortexDeploymentSet>());
		for (WebTreeNode rootChild : root.getChildren())
			processNodeForBuildingRequest(null, rootChild, req, nextId);
		service.sendRequestToBackend(getPortletId(), req);
	}
	private void processNodeForBuildingRequest(VortexEyeEntity parentEntity, WebTreeNode node, VortexEyeCreateDeploymentEnvironmentRequest req, Int nextId) {
		VortexEyeEntity[] entities = toEntity(getData(node).getC());
		for (VortexEyeEntity entity : entities) {
			entity.setId(--nextId.value);
			if (entity instanceof VortexEyeBackup) {
				VortexEyeBackup backup = (VortexEyeBackup) entity;
				if (parentEntity != null)
					backup.setDeploymentId(parentEntity.getId());
				req.getBackups().add(backup);
			} else if (entity instanceof VortexDeployment) {
				VortexDeployment dep = (VortexDeployment) entity;
				if (parentEntity != null)
					dep.setDeploymentSetId(parentEntity.getId());
				req.getDeployments().add(dep);
			} else if (entity instanceof VortexDeploymentSet) {
				VortexDeploymentSet dep = (VortexDeploymentSet) entity;
				req.getDeploymentSets().add(dep);
			}
			for (WebTreeNode childNode : node.getChildren()) {
				processNodeForBuildingRequest(entity, childNode, req, nextId);
			}
		}
	}

	private Tuple3<Object, Boolean, VortexWebMetadataFormPortlet> getData(WebTreeNode child) {
		return (Tuple3<Object, Boolean, VortexWebMetadataFormPortlet>) child.getData();
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyeCreateDeploymentEnvironmentResponse res = (VortexEyeCreateDeploymentEnvironmentResponse) result.getActionNoThrowable();
		if (res != null) {
			if (res.getOk())
				close();
			else
				getManager().showAlert(res.getMessage());
		}

		copyButton.setEnabled(true);
		super.onBackendResponse(result);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(WebTreeNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNodeChanged(WebTreeNode node) {
		// TODO Auto-generated method stub

	}

}
