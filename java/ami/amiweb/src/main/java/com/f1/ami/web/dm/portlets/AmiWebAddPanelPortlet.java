package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.web.AmiWebAliasPortlet;
import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebEditAmiScriptCallbacksPortlet;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSpecialPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz;
import com.f1.ami.web.graph.AmiWebGraphNode;
import com.f1.ami.web.graph.AmiWebGraphNode_Datamodel;
import com.f1.ami.web.graph.AmiWebGraphNode_Datasource;
import com.f1.ami.web.graph.AmiWebGraphNode_Realtime;
import com.f1.base.Row;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.WizardPortlet;
import com.f1.suite.web.portal.impl.visual.TilesListener;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

//Various work flows
//(3) select DS in modeler -> select 'add DM' ->                                      [show table chooser] -> [show DM editor] 
//(4) select DM in modeler -> select 'edit DM' ->                                                             [show DM editor] 
//(5) select DM in modeler -> select 'copy DM' ->                                                             [show DM editor]
public class AmiWebAddPanelPortlet extends WizardPortlet implements ConfirmDialogListener, TilesListener, PortletListener, AmiWebSpecialPortlet, AmiWebDmTreeListener {

	private AmiWebDm dbForViz;
	final private AmiWebDmTreePortlet dmGraphPortlet;
	final private AmiWebEditDmPortlet addDmPortlet;
	//	final private GridPortlet addDmGridPortlet;
	final private String currentPortletId;
	final private AmiWebService service;
	final private AmiWebDmChooseDatasourceTilesPortlet chooseDataPortlet;
	final private AmiWebAddVisualizationPortlet addVisualizationPortlet;
	private AmiWebDmsImpl origDatamodel;
	private List<? extends AmiWebDm> dmsForDmt;
	private List<AmiWebDatasourceWrapper> datasources;
	//	private InnerPortlet addDmContainer;
	private ConfirmDialogPortlet waitingForChooserDialog;
	private boolean skipDmEditor = false;
	final private String baseAlias;
	final private boolean isCopy;
	private String editedDmAri;
	private AmiWebDmsImpl editedDatamodel;
	private Map<String, Object> origConfig;
	private String origLayoutAlias;
	private boolean needsCleanupBeforeClose = false;
	private List<String> realtimes;

	private AmiWebAddPanelPortlet(PortletConfig config, String currentPortletId, List<AmiWebDatasourceWrapper> datasources, List<AmiWebDmsImpl> datamodels, List<String> realtimes,
			AmiWebDmsImpl origDatamodel, boolean isCopy) {
		super(config);
		this.isCopy = isCopy;
		this.service = AmiWebUtils.getService(getManager());
		this.currentPortletId = currentPortletId;
		this.origDatamodel = origDatamodel;
		this.setEditedDatamodel(null);
		this.dmsForDmt = datamodels;
		this.datasources = datasources;
		this.realtimes = realtimes;
		this.baseAlias = currentPortletId == null ? "" : ((AmiWebAliasPortlet) getManager().getPortlet(currentPortletId)).getAmiParent().getAmiLayoutFullAlias();

		this.addDmPortlet = new AmiWebEditDmPortlet(generateConfig(), baseAlias);
		if (this.currentPortletId == null && CH.isEmpty(datasources))
			if (this.needsNewDm())
				this.createEmptyDm(null);
			else
				this.saveOrigConfig();

		//If we're copying/editing existing dm, just need the dm editor portlet.
		if (origDatamodel != null) {
			this.chooseDataPortlet = null;
			this.dmGraphPortlet = null;
		} else {
			//If coming in fresh, start with the datamodeler
			if (datasources == null && datamodels == null) { // this is creating a visualization which triggers the rest
				this.dmGraphPortlet = new AmiWebDmTreePortlet(generateConfig(), this.service, baseAlias, true, null);
				this.dmGraphPortlet.setOverrideDblClick(this);
				this.dmGraphPortlet.expandDatamodels();
				//				this.dmGraphPortlet.getHeader().updateBlurbPortletLayout("AMI Creator Wizard", AmiWebDmGraphPortlet.DATAMODELER_HELP_HTML);
				addPanel(this.dmGraphPortlet);
				this.chooseDataPortlet = new AmiWebDmChooseDatasourceTilesPortlet(generateConfig());
				addPanel(this.chooseDataPortlet);
				this.chooseDataPortlet.addTilesListener(this);
			} else {//otherwise, let's initialize the editor and skip the datamodeler, this is make a new datamodel
				//				this.addDmPortlet.initDatasources(this.editedDatamodel, Collections.EMPTY_LIST, datasources, datamodels);
				this.dmGraphPortlet = null;
				if (editedDatamodel != null && service.getDesktop().getSpecialPortlet(AmiWebDmTreePortlet.class) != null)
					service.getDesktop().getSpecialPortlet(AmiWebDmTreePortlet.class).setDmToFocus(editedDatamodel.getDmName());

				if (CH.isntEmpty(datasources)) {
					this.chooseDataPortlet = new AmiWebDmChooseDatasourceTilesPortlet(generateConfig());
					addPanel(this.chooseDataPortlet);
					this.chooseDataPortlet.addTilesListener(this);
					this.chooseDataPortlet.init(datasources);
				} else {
					this.chooseDataPortlet = null;
				}
			}

		}

		addPanel(addDmPortlet);

		//If we've targeted a place to add the visualization, let the user choose a visual
		if (this.currentPortletId != null) {
			this.addVisualizationPortlet = new AmiWebAddVisualizationPortlet(generateConfig(), null, baseAlias);
			addPanel(this.addVisualizationPortlet);
		} else
			this.addVisualizationPortlet = null;
	}
	private boolean needsNewDm() {
		return (this.isCopy == true || this.origDatamodel == null);
	}
	private void saveOrigConfig() {
		if (this.origDatamodel == null)
			return;
		this.origConfig = this.origDatamodel.getConfiguration();
		this.origLayoutAlias = this.origDatamodel.getAmiLayoutFullAlias();
		this.setEditedDatamodel(this.origDatamodel);
		this.addDmPortlet.setDatamodel((AmiWebDmsImpl) editedDatamodel, false);
		this.editedDmAri = this.editedDatamodel.getAri();
		this.needsCleanupBeforeClose = true;
	}
	private void createEmptyDm(String baseDmName) {
		// If it's a copy of a datamodel or, if original datamodel is null we need a new dm 
		this.setEditedDatamodel(this.addDmPortlet.createEmptyDm());
		if (CH.isntEmpty(this.dmsForDmt))
			this.editedDatamodel.setQueryOnMode(AmiWebDmsImpl.QUERY_ON_NONE);
		else
			this.editedDatamodel.setQueryOnMode(AmiWebDmsImpl.QUERY_ON_STARTUP);

		// Case is copy of a datamodel
		if (this.origDatamodel != null && this.isCopy == true) {
			String dmName = this.service.getDmManager().getNextDmName(AmiWebUtils.toSuggestedVarname(this.origDatamodel.getDmName()), origDatamodel.getAmiLayoutFullAlias());
			this.editedDatamodel.init(this.origDatamodel.getAmiLayoutFullAlias(), this.origDatamodel.getConfiguration(), null, new StringBuilder());
			this.editedDatamodel.setAliasDotName(AmiWebUtils.getFullAlias(this.origDatamodel.getAmiLayoutFullAlias(), dmName));
			this.service.getDmManager().addDm(this.editedDatamodel);
			this.addDmPortlet.setDatamodel((AmiWebDmsImpl) editedDatamodel, false);
		}
		// Other cases which need a new datamodel
		else {
			String description = this.dmsForDmt.size() == 1 ? this.dmsForDmt.get(0).getDmName() : "datamodel";
			String dmName = this.getService().getDmManager().getNextDmName(AmiWebUtils.toSuggestedVarname(baseDmName != null ? baseDmName : description), this.baseAlias);
			this.editedDatamodel.setAliasDotName(AmiWebUtils.getFullAlias(this.baseAlias, dmName));
			this.service.getDmManager().addDm(this.editedDatamodel);
			this.addDmPortlet.setDatamodel((AmiWebDmsImpl) editedDatamodel, false);
			this.addDmPortlet.initDatasources(this.editedDatamodel, Collections.EMPTY_LIST, this.datasources, this.dmsForDmt, this.realtimes);
		}

		this.editedDmAri = this.editedDatamodel.getAri();
		this.needsCleanupBeforeClose = true;

	}
	private void cleanUpBeforeClose() {
		if (!needsCleanupBeforeClose)
			return;
		this.needsCleanupBeforeClose = false;
		if (this.needsNewDm())
			this.service.getDmManager().removeDm(this.editedDatamodel.getAmiLayoutFullAliasDotId());
		else {
			String currConfigString = ObjectToJsonConverter.INSTANCE_COMPACT_SORTING.objectToString(this.editedDatamodel.getConfiguration());
			String origConfigString = ObjectToJsonConverter.INSTANCE_COMPACT_SORTING.objectToString(this.origConfig);
			if (!SH.equals(currConfigString, origConfigString)) {

				StringBuilder warningsSink = new StringBuilder();
				this.editedDatamodel.init(this.origLayoutAlias, this.origConfig, null, warningsSink);
				if (SH.isntEmpty(warningsSink))
					getManager().showAlert(warningsSink.toString());

				if (this.service.getDmManager().canRunDm())
					this.editedDatamodel.doStartup();
				this.editedDatamodel.onChanged();
			}
		}
		this.setEditedDatamodel(null);
	}
	//	//(3)
	public AmiWebAddPanelPortlet(PortletConfig config, String panelId, List<AmiWebDatasourceWrapper> datasources, List<AmiWebDmsImpl> datamodels, List<String> realtimes) {
		this(config, panelId, datasources, datamodels, realtimes, null, false);
	}
	//(4) & (5) 
	public AmiWebAddPanelPortlet(PortletConfig config, AmiWebDmsImpl dm, boolean isCopy) {
		this(config, null, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST, dm, isCopy);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.getNextButton()) {
			if (super.getActivePortlet() == this.dmGraphPortlet) {
				this.moveFromDatamodelGraphStageToNextStage(this.dmGraphPortlet.getSelectedNodes());
				return;
			} else if (super.getActivePortlet() == this.chooseDataPortlet) {
				this.prepareToMoveToAddDatamodelStage();
				return;
			} else if (super.getActivePortlet() == this.addDmPortlet) {
				if (!this.addDmPortlet.getIsTestRun()) {
					getManager().showDialog("Confirm",
							new ConfirmDialogPortlet(generateConfig(), "We recommend running a test before continuing.  Run a test now?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this)
									.updateButton(ConfirmDialogPortlet.ID_YES, "Run Test Now").updateButton(ConfirmDialogPortlet.ID_NO, "Skip Test & Continue")
									.setCallback("NO_TEST"));
					return;
				} else {
					moveToVisualizationStage();
					return;
				}
			} else if (super.getActivePortlet() == this.addVisualizationPortlet) {
				AmiWebVizwiz<?> vizwiz = this.addVisualizationPortlet.getVizwiz();
				if (vizwiz != null && !vizwiz.preview())
					return;
				if (!this.addVisualizationPortlet.verifyIds())
					return;
				AmiWebPortlet p = (AmiWebPortlet) this.addVisualizationPortlet.removePreviewPortlet();
				if (p == null) {
					getManager().showAlert("Please choose a visualization type");
					return;
				}
				Portlet toReplace = this.getManager().getPortletNoThrow(currentPortletId);
				if (toReplace == null) {
					AmiWebUtils.createNewDesktopPortlet(this.service.getDesktop(), p);
				} else {
					service.getDesktop().replacePortlet(currentPortletId, p);
				}
			}
		} else if (button == this.getBackButton()) {
			if (super.getActivePortlet() == this.addVisualizationPortlet) { // go back from visualization portlet
				if (this.dbForViz != null) {
					//					this.service.getDmManager().removeDm(dbForViz.getAliasDotName());
					dbForViz = null;
				}
				this.addVisualizationPortlet.setDm(null);
				if (this.skipDmEditor) {
					if (this.origDatamodel != null) {
						this.origDatamodel = null;
						this.editedDatamodel = null;
						this.editedDmAri = null;
						setActivePortlet(this.dmGraphPortlet);
						return;
					}
					this.skipDmEditor = false;
				}
			} else if (super.getActivePortlet() == this.addDmPortlet) {
				//				if (this.origDatamodel != null)
				//					this.addDmPortlet.revertChanges((AmiWebDmsImpl) this.origDatamodel);
				cleanUpBeforeClose();
				if (getActivePanel() != 0)
					if (this.origDatamodel != null) {
						setActivePortlet(this.dmGraphPortlet);
						return;
					} else if (this.chooseDataPortlet != null && CH.isEmpty(this.chooseDataPortlet.getDatasources())) {
						setActivePortlet(this.dmGraphPortlet);
						return;
					}
			}
		}
		super.onButtonPressed(portlet, button);
	}

	private void moveFromDatamodelGraphStageToNextStage(List<AmiWebGraphNode<?>> nodes) {
		int numNodes = nodes.size();
		if (numNodes == 0) {
			getManager().showDialog("Add Datamodel",
					new ConfirmDialogPortlet(generateConfig(), "Please select a datasource or datamodel to contiunue", ConfirmDialogPortlet.TYPE_ALERT, this));
			return;
		}
		List<AmiWebDatasourceWrapper> datasources = new ArrayList<AmiWebDatasourceWrapper>();
		List<AmiWebDmsImpl> datamodels = new ArrayList<AmiWebDmsImpl>();
		List<String> rtList = new ArrayList<String>();
		for (AmiWebGraphNode<?> data : nodes) {
			switch (data.getType()) {
				case AmiWebGraphNode.TYPE_DATASOURCE:
					AmiWebDatasourceWrapper ds = ((AmiWebGraphNode_Datasource) data).getInner();
					datasources.add(ds);
					break;
				case AmiWebGraphNode.TYPE_DATAMODEL:
					AmiWebDmsImpl dm = ((AmiWebGraphNode_Datamodel) data).getInner();
					if (dm.isTransient()) {
						getManager().showAlert("Can not create panels on transient datamodels");
						return;
					}
					datamodels.add(dm);
					break;
				case AmiWebGraphNode.TYPE_FEED:
					rtList.add(((AmiWebGraphNode_Realtime) data).getId());
					break;
				default:
					getManager().showAlert("Can only create datamodels on datasources, datamodels, or real time feeds");
					return;
			}
		}
		this.datasources = datasources;
		this.dmsForDmt = datamodels;
		this.realtimes = rtList;
		if (datasources.size() > 0) {
			this.chooseDataPortlet.populate(datasources);
			this.setActivePortlet(this.chooseDataPortlet);
			return;
		} else if (datamodels.size() == 1) {
			this.origDatamodel = datamodels.get(0);
			if (this.origDatamodel.getResponseOutSchema().isEmpty()) {
				getManager().showAlert("This datamodel does not have any tables");
				return;
			}
			if (this.currentPortletId != null) {
				FormPortletCheckboxField input = new FormPortletCheckboxField("No, add blender to selected datamodel first.");
				getManager().showDialog("Confirm",
						new ConfirmDialogPortlet(generateConfig(), "Create visualization on this datamodel?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this, input)
								.setCallback("ADD_BLENDER").setCorrelationData(datamodels));
				input.setLeftPosPct(.30).setTopPosPct(.40).setLabelPaddingPx(5);
				input.setLabelSide((byte) 3);
				input.setLabelWidthPx(300);
				return;
			}
			this.addVisualizationPortlet.setDm(origDatamodel);
			this.setActivePortlet(this.addVisualizationPortlet);
			this.addVisualizationPortlet.showChooseTableDialog(this.origDatamodel);
			this.skipDmEditor = true;
			return;
		} else {
			// ensure we have a dm when user moves from dm-er to dm
			if (needsNewDm())
				createEmptyDm(null);
			else
				this.addDmPortlet.initDatasources(this.editedDatamodel, Collections.EMPTY_LIST, Collections.EMPTY_LIST, datamodels, rtList);
			this.setActivePortlet(this.addDmPortlet);
			return;
		}
	}
	private void prepareToMoveToAddDatamodelStage() {
		this.waitingForChooserDialog = this.chooseDataPortlet.prepareForSelectedSchema();
		this.waitingForChooserDialog.addPortletListener(this);
		this.chooseDataPortlet.isReadyForAddDatamodelStage();
	}
	private void moveToAddDatamodelStage() {
		List<AmiDatasourceTable> selected = this.chooseDataPortlet.getSelectedSchema();
		if (selected.size() == 0) {
			getManager().showDialog("Confirm",
					new ConfirmDialogPortlet(generateConfig(), "You have not selected any tables, continue?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("NO_TABLES"));
			return;
		}

		if (this.editedDatamodel == null) {
			this.createEmptyDm(selected.size() == 1 ? selected.get(0).getName() : null);
		}
		this.addDmPortlet.initDatasources(this.editedDatamodel, this.chooseDataPortlet.getSelectedSchema(), this.chooseDataPortlet.getDatasources(), this.dmsForDmt,
				this.realtimes);
		super.onButtonPressed(this.getButtons(), this.getNextButton());
	}

	private void moveToVisualizationStage() {
		String beforeApplyAliasDotName = this.editedDatamodel.getAmiLayoutFullAliasDotId();
		String beforeApplyLayoutAlias = this.editedDatamodel.getAmiLayoutFullAlias();
		String beforeApplyAri = this.editedDatamodel.getAri();

		String afterApplyLayoutAlias = this.addDmPortlet.getDmAlias();
		String afterApplyDmName = this.addDmPortlet.getDmName();
		String afterApplyAliasDotName = this.addDmPortlet.getAliasDotName();
		String afterApplyAri = this.service.getDmManager().getDmAri(afterApplyDmName, afterApplyLayoutAlias);

		if (service.getLayoutFilesManager().getLayoutByFullAlias(afterApplyLayoutAlias).isReadonly()) {
			getManager().showAlert("Datamodel is readonly, can not apply changes");
			return;
		}
		if (SH.isnt(afterApplyDmName) || !AmiUtils.isValidVariableName(afterApplyDmName, false, false, false)) {
			getManager().showAlert("Datamodel ID missing or invalid");
			return;
		}

		// Compare the target datamodel (one is being edited) ari to the one generated by the forms

		boolean ariChanged = !SH.equals(beforeApplyAri, afterApplyAri); // If ari has changed we will have to remove and add the datamodel
		boolean layoutAliasChanged = !SH.equals(beforeApplyLayoutAlias, afterApplyLayoutAlias);
		boolean aliasDotNameChanged = !SH.equals(beforeApplyAliasDotName, afterApplyAliasDotName);

		// If layout alias changed we should check dependencies
		// This check should not have to happen if we're making a new dm 
		if (layoutAliasChanged && this.origDatamodel != null && this.isCopy == false) {
			for (AmiWebDmPortlet panel : this.service.getDmManager().getPanelsForDmAliasDotName(this.editedDatamodel.getAmiLayoutFullAliasDotId())) {
				if (!AmiWebUtils.isParentAliasOrSame(panel.getAmiLayoutFullAlias(), afterApplyLayoutAlias)) {
					getManager().showAlert(
							"Datamodel Can not be moved to layout because it would no longer be visible to dependent panel '" + panel.getAmiLayoutFullAliasDotId() + "'");
					return;
				}
			}
			for (AmiWebDmLink rel : this.service.getDmManager().getDmLinksToDmAliasDotName(this.origDatamodel.getAmiLayoutFullAliasDotId())) {
				if (!AmiWebUtils.isParentAliasOrSame(rel.getAmiLayoutFullAlias(), afterApplyLayoutAlias)) {
					getManager().showAlert(
							"Datamodel Can not be moved to layout because it would no longer be visible to dependent relationship '" + rel.getAmiLayoutFullAliasDotId() + "'");
					return;
				}
			}

		}

		final AmiWebDmsImpl dm;
		StringBuilder warningsSink = new StringBuilder();

		try {
			dm = (AmiWebDmsImpl) this.editedDatamodel;

			if (aliasDotNameChanged && this.service.getDmManager().isDmRegistered(afterApplyDmName, afterApplyLayoutAlias)) {
				getManager().showAlert("This dm alias + dmname is already added to the dm manager: " + afterApplyAliasDotName);
				return;
			}

			if (ariChanged && this.getService().getDomObjectsManager().isManaged(afterApplyAri)) {
				getManager().showAlert("This ari is already added to the dom objects manager: " + afterApplyAri);
				return;
			}

			if (!applyToDatamodel(dm)) {
				if (this.origConfig != null) {
					dm.init(beforeApplyLayoutAlias, this.origConfig, null, warningsSink);
					if (SH.isntEmpty(warningsSink))
						getManager().showAlert(warningsSink.toString());
					if (this.service.getDmManager().canRunDm())
						dm.doStartup();
					dm.onChanged();
				}
				return;
			}
			if (ariChanged) {
				dm.setAliasDotName(afterApplyAliasDotName);
			}
			dm.setCrontab(this.addDmPortlet.getCrontab(), this.addDmPortlet.getCrontabTimezone());
			//if (this.service.getDmManager().canRunDm())
			//	dm.doStartup();
			dm.onChanged();
		} catch (Exception e) {
			if (SH.isntEmpty(warningsSink))
				getManager().showAlert(warningsSink.toString());
			getManager().showAlert(e.getMessage(), e);
			return;
		}

		if (this.addVisualizationPortlet != null) {
			if (dm.getResponseOutSchema().getTableNamesSorted().isEmpty()) {
				getManager().showAlert("Datamodel must produce at least one table in order to add a vizualization");
				dm.close();
				return;
			}
			this.addVisualizationPortlet.showChooseTableDialog(dm);
			this.dbForViz = dm;
			this.addVisualizationPortlet.setDm(dm);
			this.setActivePortlet(this.addVisualizationPortlet);
			if (this.service.getDmManager().canRunDm())
				dm.doStartup();
		} else {
			this.needsCleanupBeforeClose = false;
			close();
			if (this.service.getDmManager().canRunDm())
				dm.doStartup();
		}

	}
	private boolean applyToDatamodel(AmiWebDmsImpl dm) {
		if (!this.addDmPortlet.applyToDatamodel(dm)) {
			getManager().showAlert("Could not create datamodel, See Errors tab for details");
			return false;
		}
		return true;
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		String cb = source.getCallback();
		if ("NO_TABLES".equals(cb)) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				if (this.editedDatamodel == null)
					this.createEmptyDm(null);
				AmiWebEditDmPortlet amiWebEditDmsPortlet = (AmiWebEditDmPortlet) this.addDmPortlet;
				amiWebEditDmsPortlet.initDatasources(this.editedDatamodel, Collections.EMPTY_LIST, this.chooseDataPortlet.getDatasources(), this.dmsForDmt, this.realtimes);
				setActivePortlet(this.addDmPortlet);
			}
			return true;
		} else if ("NO_TEST".equals(cb)) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				this.addDmPortlet.runTestOnPendingPortlets();
			} else {
				moveToVisualizationStage();
			}
		} else if ("ADD_BLENDER".equals(cb)) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				Boolean b = (Boolean) source.getInputFieldValue();
				if (Boolean.TRUE.equals(b)) {
					this.origDatamodel = null;
					List<? extends AmiWebDm> datamodels = (List<? extends AmiWebDm>) source.getCorrelationData();
					this.dmsForDmt = datamodels;
					this.createEmptyDm(null);
					this.setActivePortlet(this.addDmPortlet);
				} else {
					this.addVisualizationPortlet.setDm(origDatamodel);
					this.setActivePortlet(this.addVisualizationPortlet);
					this.addVisualizationPortlet.showChooseTableDialog(this.origDatamodel);
					this.skipDmEditor = true;
				}
			}
		} else if ("ADD_BLANK_DM".equals(cb)) {
			this.addDmPortlet.initDatasources(this.editedDatamodel, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST, Collections.EMPTY_LIST);
			this.setActivePortlet(this.addDmPortlet);
			return true;
		}
		return true;

	}

	public AmiWebDmTreePortlet getDmPortlet() {
		return this.dmGraphPortlet;
	}
	@Override
	public void onUserClose() {
		this.cleanUpBeforeClose();
		super.onUserClose();
	}
	@Override
	public void onClosed() {
		if (this.needsCleanupBeforeClose)
			this.cleanUpBeforeClose();
		this.service.getAmiWebDmEditorsManager().onPortletClosed(this);
		this.addDmPortlet.removeTableListener();
		//TODO: I think this goes away
		for (PortletListener listener : new ArrayList<PortletListener>(getPortletListeners()))
			listener.onPortletClosed(this);
		if (this.dbForViz != null) {
			AmiWebDm dms = (AmiWebDm) dbForViz;
			if (!dms.getQueryOnStartup() && dms.getLowerDmAliasDotNames().isEmpty()) {
				dms.getResponseTableset().clearTables();
				dms.fireDmDataChanged();
			}
		}
		if (this.dmGraphPortlet != null) {
			this.dmGraphPortlet.setOverrideDblClick(null);
		}
		if (this.chooseDataPortlet != null) {
			this.chooseDataPortlet.removeTilesListener(this);
		}
		this.service.getDmManager().verifyDms();
		setEditedDatamodel(null);
		super.onClosed();
	}
	@Override
	public void onContextMenu(TilesPortlet tiles, String action) {

	}

	@Override
	public void onTileClicked(TilesPortlet table, Row row) {

	}

	@Override
	public void onSelectedChanged(TilesPortlet tiles) {

	}

	@Override
	public void onVisibleRowsChanged(TilesPortlet tiles) {

	}

	@Override
	public void onDoubleclick(TilesPortlet tilesPortlet, Row tile) {
		if (super.getActivePortlet() == this.chooseDataPortlet && this.chooseDataPortlet.getSelectedSchema().size() > 0) { // Don't proceed if user double-clicks on white space
			this.prepareToMoveToAddDatamodelStage();
		}
	}

	@Override
	public void onDoubleClicked(List<AmiWebGraphNode<?>> nodes) {
		this.moveFromDatamodelGraphStageToNextStage(nodes);

	}

	@Override
	protected void onUserFinishedButton() {
		if (this.addVisualizationPortlet != null && this.addVisualizationPortlet.getVizwiz() != null)
			this.addVisualizationPortlet.getVizwiz().preview();
		this.needsCleanupBeforeClose = false;
		super.onUserFinishedButton();
	}
	@Override
	public void onPortletAdded(Portlet newPortlet) {

	}
	@Override
	public void onPortletClosed(Portlet oldPortlet) {
		if (this.waitingForChooserDialog == oldPortlet) {
			this.waitingForChooserDialog = null;
			this.moveToAddDatamodelStage();
		}
	}
	@Override
	public void onSocketConnected(PortletSocket initiator, PortletSocket remoteSocket) {

	}
	@Override
	public void onSocketDisconnected(PortletSocket initiator, PortletSocket remoteSocket) {

	}
	@Override
	public void onPortletParentChanged(Portlet newPortlet, PortletContainer oldParent) {

	}
	@Override
	public void onJavascriptQueued(Portlet portlet) {

	}
	@Override
	public void onPortletRenamed(Portlet portlet, String oldName, String newName) {

	}
	@Override
	public void onLocationChanged(Portlet portlet) {

	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ESCAPE.equals(keyEvent.getKey()))
			return true;
		return super.onUserKeyEvent(keyEvent);
	}
	public AmiWebDmChooseDatasourceTilesPortlet getChooseDataPortlet() {
		return chooseDataPortlet;
	}
	public AmiWebService getService() {
		return this.service;
	}
	public String getEditedDmAri() {
		return this.editedDmAri;
	}
	public AmiWebDm getEditedDm() {
		return this.editedDatamodel;
	}
	public String getNameFieldValue() {
		if (this.addDmPortlet != null) {
			return this.addDmPortlet.getDmName();
		}
		return null;
	}
	private void setEditedDatamodel(AmiWebDmsImpl editedDatamodel) {
		if (this.editedDatamodel == editedDatamodel)
			return;
		if (this.editedDatamodel != null)
			this.editedDatamodel.setIsInEdit(false);
		this.editedDatamodel = editedDatamodel;
		if (this.editedDatamodel != null)
			editedDatamodel.setIsInEdit(true);
	}
	public AmiWebEditAmiScriptCallbacksPortlet getCallbacksEditor() {
		return this.addDmPortlet == null ? null : this.addDmPortlet.getEditor();
	}
}
