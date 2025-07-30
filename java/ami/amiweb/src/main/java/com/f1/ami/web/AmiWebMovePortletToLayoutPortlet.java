package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.CH;
import com.f1.utils.IterableIterator;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebMovePortletToLayoutPortlet extends GridPortlet implements FormPortletListener, ConfirmDialogListener {

	private FormPortlet form;
	private FormPortletSelectField<String> alias;
	private FormPortletButton cancelButton;
	private FormPortletButton okButton;
	private AmiWebAliasPortlet target;
	private FormPortletCheckboxField moveChildPanels;
	private FormPortletCheckboxField moveDatamodels;
	private FormPortletCheckboxField moveRelationships;

	public AmiWebMovePortletToLayoutPortlet(PortletConfig config, AmiWebAliasPortlet target) {
		super(config);
		addChild(this.form = new FormPortlet(generateConfig()));
		this.target = target;
		this.form.addField(new FormPortletTextField("Current Layout: ").setValue(AmiWebUtils.formatLayoutAlias(target.getAmiLayoutFullAlias())).setDisabled(true));
		this.form.addField(this.alias = new FormPortletSelectField<String>(String.class, "Move To Layout: "));
		this.moveChildPanels = new FormPortletCheckboxField("Move Children: ").setValue(Boolean.FALSE);
		this.moveDatamodels = new FormPortletCheckboxField("Move Datamodels: ").setValue(Boolean.FALSE);
		this.moveRelationships = new FormPortletCheckboxField("Move Relationships: ").setValue(Boolean.FALSE);
		this.form.addButton(this.okButton = new FormPortletButton("Move"));
		this.form.addButton(this.cancelButton = new FormPortletButton("Cancel"));
		this.form.addFormPortletListener(this);
		AmiWebAliasPortlet p = target.getAmiParent();
		if (CH.isntEmpty(target.getAmiChildren()))
			this.form.addField(this.moveChildPanels);
		updateEnabled();
		setSuggestedSize(400, 200);
		for (AmiWebLayoutFile i : target.getService().getLayoutFilesManager().getLayoutByFullAlias(p.getAmiLayoutFullAlias()).getChildrenRecursive(true)) {
			this.alias.addOption(i.getFullAlias(), AmiWebUtils.formatLayoutAlias(i.getFullAlias()));
		}
		this.alias.setValue(target.getAmiLayoutFullAlias());

	}
	private void updateEnabled() {
		Collection<AmiWebAliasPortlet> cs = new ArrayList<AmiWebAliasPortlet>();
		if (moveChildPanels.getValue()) {
			PortletHelper.findPortletsByType(this.target, AmiWebAliasPortlet.class, cs);
		} else
			cs.add(this.target);
		boolean hasdm = false;
		boolean hasrel = false;
		if (cs.size() > 0) {
			Set<String> adn = new HashSet<String>();
			for (AmiWebAliasPortlet t : cs) {
				adn.add(t.getAmiLayoutFullAliasDotId());
				if (t instanceof AmiWebDmPortlet)
					for (String s : ((AmiWebDmPortlet) t).getUsedDmAliasDotNames()) {
						hasdm = true;
						break;
					}
			}
			for (AmiWebDmLink i : target.getService().getDmManager().getDmLinks()) {
				if (adn.contains(i.getSourceDmAliasDotName()) || adn.contains(i.getTargetDmAliasDotName())) {
					hasrel = true;
					break;
				}
			}
		}
		if (hasdm)
			this.form.addFieldNoThrow(this.moveDatamodels);
		else
			this.form.removeFieldNoThrow(this.moveDatamodels);
		if (hasrel)
			this.form.addFieldNoThrow(this.moveRelationships);
		else
			this.form.removeFieldNoThrow(this.moveRelationships);
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			close();
		if (button == this.okButton) {
			doMove(false);
		}
	}
	public void doMove(boolean allowRename) {
		AmiWebService service = target.getService();
		final String fullAlias = this.alias.getValue();
		if (!moveChildPanels.getBooleanValue()) {
			for (AmiWebAliasPortlet child : this.target.getAmiChildren())
				if (!AmiWebUtils.isParentAliasOrSame(fullAlias, child.getAmiLayoutFullAlias())) {
					getManager().showAlert("Move failed: Child Panel '" + child.getAmiLayoutFullAliasDotId()
							+ "' will violate Layout ownership.<P>(You can check '<i>move children</i>' to resolve this)");
					return;
				}

		}
		Set<AmiWebAliasPortlet> cs = new LinkedHashSet<AmiWebAliasPortlet>();
		if (moveChildPanels.getValue()) {
			PortletHelper.findPortletsByType(this.target, AmiWebAliasPortlet.class, cs);
		} else
			cs.add(this.target);
		Map<AmiWebAliasPortlet, String> panelIdsMapping = new IdentityHashMap<AmiWebAliasPortlet, String>();
		HashSet<String> panelIdsExisting = new HashSet<String>(service.getPanelIdsByFullAlias(fullAlias));
		panelIdsExisting.addAll(service.getLayoutFilesManager().getLayoutByFullAlias(fullAlias).getHiddenPanelIds());
		for (AmiWebAliasPortlet t : cs) {
			if (OH.ne(t.getAmiLayoutFullAlias(), fullAlias)) {
				if (panelIdsExisting.contains(t.getAmiPanelId())) {
					String newId = SH.getNextId(t.getAmiPanelId(), panelIdsExisting);
					panelIdsExisting.add(newId);
					panelIdsMapping.put(t, newId);
				}
			}
		}
		Set<String> adn = new HashSet<String>();
		for (AmiWebAliasPortlet t : cs)
			adn.add(t.getAmiLayoutFullAliasDotId());
		List<AmiWebDmLink> needsMoving = new ArrayList<AmiWebDmLink>();
		List<AmiWebDmLink> couldMoving = new ArrayList<AmiWebDmLink>();
		for (AmiWebDmLink i : target.getService().getDmManager().getDmLinks()) {
			if ((adn.contains(i.getSourceDmAliasDotName()) || adn.contains(i.getTargetDmAliasDotName()))
					&& !AmiWebUtils.isParentAliasOrSame(i.getAmiLayoutFullAlias(), fullAlias)) {//relationship is part of this move and because we moving up or over the relationship needs to move
				needsMoving.add(i);
			} else if (adn.contains(i.getSourceDmAliasDotName()) && adn.contains(i.getTargetDmAliasDotName())) {//relationship is fully a part of this move
				couldMoving.add(i);
			}
		}
		for (AmiWebAliasPortlet t : cs) {
			if (t instanceof AmiWebRealtimePortlet) {
				AmiWebRealtimePortlet rt = (AmiWebRealtimePortlet) t;
				for (String s : rt.getLowerRealtimeIds()) {
					if (SH.startsWith(s, AmiWebManagers.FEED))
						continue;
					String aliasDotName = SH.afterFirst(s, ':');
					String alias = AmiWebUtils.getAliasFromAdn(aliasDotName);
					if (!AmiWebUtils.isParentAliasOrSame(alias, fullAlias)) {
						getManager().showAlert("Move failed: Panel '" + t.getAmiLayoutFullAliasDotId() + "'" + "' will not have visibility to upstream datatype: " + aliasDotName);
						return;
					}
				}
			}
		}
		Map<AmiWebDmLink, String> linksMapping = new IdentityHashMap<AmiWebDmLink, String>();
		HashSet<String> linksExisting = new HashSet<String>(service.getDmManager().getDmLinkIdsByFullAlias(fullAlias));
		if (!moveRelationships.getBooleanValue()) {
			if (!needsMoving.isEmpty()) {
				getManager().showAlert("Move failed: Relationship '" + needsMoving.get(0).getAmiLayoutFullAliasDotId()
						+ "' will violate Layout ownership.<P>(You can check '<i>move Relationships</i>' to resolve this)");
				return;
			}
		} else {
			for (AmiWebDmLink t : IterableIterator.create(needsMoving, couldMoving)) {
				if (linksExisting.contains(t.getRelationshipId())) {
					String newId = SH.getNextId(t.getRelationshipId(), linksExisting);
					linksExisting.add(newId);
					linksMapping.put(t, newId);
				}
			}
		}
		Map<AmiWebDm, String> dmsMapping = new IdentityHashMap<AmiWebDm, String>();
		HashSet<String> dmsExisting = new HashSet<String>(service.getDmManager().getDmNames(fullAlias));
		if (!moveDatamodels.getBooleanValue()) {
			for (AmiWebAliasPortlet t : cs) {
				if (t instanceof AmiWebDmPortlet) {
					for (String s : ((AmiWebDmPortlet) t).getUsedDmAliasDotNames()) {
						if (!AmiWebUtils.isParentAliasOrSame(fullAlias, s)) {
							getManager().showAlert("Move failed: Panel '" + t.getAmiLayoutFullAliasDotId() + "' references datamodel '" + s
									+ "' and that will violate Layout ownership.<P>(You can check '<i>move Datamodels</i>' to resolve this)");
							return;
						}
					}
				}
			}

		} else {
			//We are going to be moving datamodels, need to make sure none of them have panels that are not part of this move and would violate layout visibility rules
			for (AmiWebAliasPortlet t : cs)
				if (t instanceof AmiWebDmPortlet)
					for (String s : ((AmiWebDmPortlet) t).getUsedDmAliasDotNames()) {
						if (OH.ne(fullAlias, AmiWebUtils.getAliasFromAdn(s))) {
							String dmName = AmiWebUtils.getNameFromAdn(s);
							for (AmiWebDmPortlet panel : service.getDmManager().getPanelsForDmAliasDotName(s)) {
								if (!cs.contains(panel)) {
									if (!AmiWebUtils.isParentAliasOrSame(panel.getAmiLayoutFullAlias(), fullAlias)) {
										getManager().showAlert("Move failed: Datamodel '" + s + "' can not be moved because it would no longer be visible to dependent panel '"
												+ panel.getAmiLayoutFullAlias() + "')");
										return;
									}
								}
							}
							AmiWebDm dm = service.getDmManager().getDmByAliasDotName(s);
							if (dmsExisting.contains(dm.getDmName())) {
								String newId = SH.getNextId(dm.getDmName(), dmsExisting);
								dmsExisting.add(newId);
								dmsMapping.put(dm, newId);
							}
						}
					}
		}
		if (!allowRename) {
			if (!panelIdsMapping.isEmpty() || !dmsMapping.isEmpty() || !linksMapping.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				sb.append("There are naming conflict(s). Auto-resolve the following?<P>");
				for (Entry<AmiWebAliasPortlet, String> i : panelIdsMapping.entrySet())
					sb.append("Rename Panel '").append(i.getKey().getAmiPanelId()).append("' to '").append(i.getValue()).append("'<BR>");
				for (Entry<AmiWebDm, String> i : dmsMapping.entrySet())
					sb.append("Rename Datamodel '").append(i.getKey().getDmName()).append("' to '").append(i.getValue()).append("'<BR>");
				for (Entry<AmiWebDmLink, String> i : linksMapping.entrySet())
					sb.append("Rename Relationship '").append(i.getKey().getRelationshipId()).append("' to '").append(i.getValue()).append("'<BR>");
				getManager().showDialog("Confirm Refactor",
						new ConfirmDialogPortlet(generateConfig(), sb.toString(), ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("RENAME"));
				return;
			}
		}
		for (AmiWebAliasPortlet i : cs) {
			String mapping = panelIdsMapping.get(i);
			i.setAdn(AmiWebUtils.getFullAlias(fullAlias, mapping != null ? mapping : i.getAmiPanelId()));
		}
		if (moveDatamodels.getBooleanValue()) {
			for (AmiWebAliasPortlet t : cs) {
				if (t instanceof AmiWebDmPortlet) {
					AmiWebDmPortlet dmp = (AmiWebDmPortlet) t;
					for (String s : dmp.getUsedDmAliasDotNames()) {
						if (OH.ne(fullAlias, s)) {
							AmiWebDm dm = service.getDmManager().getDmByAliasDotName(s);
							String mapping = dmsMapping.get(dm);
							dm.setAliasDotName(AmiWebUtils.getFullAlias(fullAlias, mapping != null ? mapping : dm.getDmName()));
						}
					}
				}
			}
		}
		if (moveRelationships.getBooleanValue()) {
			for (AmiWebDmLink i : couldMoving) {
				if (OH.ne(i.getAmiLayoutFullAlias(), fullAlias)) {
					service.getDmManager().removeDmLink(i.getLinkUid());
					i.setAmiLayoutFullAlias(fullAlias);
					service.getDmManager().addDmLink(i);
					i.bind();
				}
			}
			for (AmiWebDmLink i : needsMoving) {
				if (OH.ne(i.getAmiLayoutFullAlias(), fullAlias)) {
					service.getDmManager().removeDmLink(i.getLinkUid());
					String mapping = dmsMapping.get(i);
					i.setAmiLayoutFullAlias(AmiWebUtils.getCommanAliasRoot(fullAlias, mapping != null ? null : i.getAmiLayoutFullAlias()));
					service.getDmManager().addDmLink(i);
					i.bind();
				}
			}

		}
		service.getDesktop().updateDashboard();
		close();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.moveChildPanels)
			updateEnabled();
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("RENAME".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id))
				doMove(true);
			return true;
		}
		return false;
	}

}
