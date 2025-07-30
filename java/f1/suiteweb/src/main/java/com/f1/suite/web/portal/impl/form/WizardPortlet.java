package com.f1.suite.web.portal.impl.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.utils.CH;

public class WizardPortlet extends GridPortlet implements FormPortletListener {

	public static class Stage {

		public Stage(Portlet portlet, FormPortletButton... buttons) {
			this.portlet = portlet;
			this.buttons = CH.l(buttons);
		}

		private final Portlet portlet;
		private final List<FormPortletButton> buttons;

		public Portlet getPanel() {
			return this.portlet;
		}

		public List<FormPortletButton> getButtons() {
			return this.buttons;
		}

	}

	private HtmlPortlet headerPortlet;
	private FormPortlet buttonsPortlet;
	private GridPortlet panelPortlet;
	private FormPortletButton nextButton;
	private FormPortletButton backButton;
	private List<Stage> panels = new ArrayList<Stage>();
	private int currentPosition;

	public WizardPortlet(PortletConfig config) {
		super(config);
		headerPortlet = addChild(new HtmlPortlet(generateConfig()), 0, 0);
		panelPortlet = addChild(new GridPortlet(generateConfig()), 0, 1);
		buttonsPortlet = addChild(new FormPortlet(generateConfig()), 0, 2);
		setRowSize(0, 0);
		setRowSize(2, 40);
		this.backButton = buttonsPortlet.addButton(new FormPortletButton(""));
		this.nextButton = buttonsPortlet.addButton(new FormPortletButton(""));
		this.currentPosition = 0;
		this.buttonsPortlet.addFormPortletListener(this);
		updateButtons();
	}

	protected void updateButtons() {
		boolean isCancel = false;
		boolean isFinish = false;
		if (currentPosition == 0) {
			isCancel = true;
			this.backButton.setName("Cancel");
		} else
			this.backButton.setName("<< Back");
		if (currentPosition + 1 == getPanelsCount()) {
			isFinish = true;
			this.nextButton.setName("Finish");
		} else
			this.nextButton.setName("Next >>");
		if (this.panels.size() > 0) {
			List<FormPortletButton> buttons = this.panels.get(this.currentPosition).getButtons();
			if (!this.buttonsPortlet.getButtons().isEmpty() || !buttons.isEmpty()) {
				this.buttonsPortlet.clearButtons();
				if (isCancel == true && isFinish == true) {
					this.buttonsPortlet.addButton(this.nextButton);
					this.buttonsPortlet.addButton(this.backButton);
				} else {
					this.buttonsPortlet.addButton(this.backButton);
					this.buttonsPortlet.addButton(this.nextButton);
				}
				for (FormPortletButton button : buttons)
					this.buttonsPortlet.addButton(button);
			}
		}
	}
	public int getPanelsCount() {
		return panels.size();
	}

	protected void addPanel(int position, Portlet portlet, FormPortletButton... buttons) {
		Stage stage = new Stage(portlet, buttons);
		this.panels.add(position, stage);
		if (this.currentPosition == position) {
			setActivePanel(stage);
		}
		updateButtons();
	}
	public void setActivePortlet(Portlet portlet) {
		for (int i = 0, l = this.panels.size(); i < l; i++) {
			if (this.panels.get(i).portlet == portlet) {
				setActivePanel(i);
				return;
			}
		}
		throw new NoSuchElementException("Portlet not a panel in this wizard: " + portlet);
	}
	protected void setActivePanel(Stage stage) {
		if (this.panelPortlet.getChildrenCount() > 0)
			this.panelPortlet.removeChild(CH.first(this.panelPortlet.getChildren().keySet()));
		this.panelPortlet.addChild(stage.getPanel());
		getManager().onPortletAdded(stage.getPanel());
		updateButtons();
	}
	protected void setActivePanel(int i) {
		this.currentPosition = i;
		setActivePanel(panels.get(i));
	}

	protected int getActivePanel() {
		return this.currentPosition;
	}
	public Portlet getActivePortlet() {
		return this.panels.get(this.currentPosition).portlet;
	}

	@Override
	public void close() {
		Portlet p = this.getActivePortlet();
		super.close();
		for (Stage panel : getPanels()) {
			if (p == panel.getPanel())
				continue;//already been handled by super.close()
			panel.getPanel().close();
		}
	}

	public void onUserClose() {
		this.close();
	}

	private List<Stage> getPanels() {
		return this.panels;
	}

	protected void addPanel(Portlet portlet, FormPortletButton... buttons) {
		this.addPanel(getPanelsCount(), portlet, buttons);
	}
	protected Portlet removePanel(int index) {
		boolean needsUpdate = currentPosition == index;
		if (currentPosition > index)
			currentPosition--;
		Stage r = this.panels.remove(index);
		r.getPanel().close();
		if (needsUpdate)
			setActivePanel(currentPosition);
		return r.getPanel();
	}

	public void setHeaderHtml(String html, String style, int heightPx) {
		setRowSize(0, heightPx);
		this.headerPortlet.setHtml(html);
		this.headerPortlet.setCssClass(style);
	}

	protected HtmlPortlet getHeaderPortlet() {
		return this.headerPortlet;
	}

	protected FormPortlet getButtons() {
		return this.buttonsPortlet;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == nextButton) {
			if (currentPosition + 1 < getPanelsCount()) {
				if (onNextButton(getPanelAt(currentPosition), getPanelAt(currentPosition + 1))) {
					setActivePanel(currentPosition + 1);
				}
			} else
				onUserFinishedButton();
		} else if (button == backButton) {
			if (currentPosition == 0)
				onUserCanceledButton();
			else {
				if (onBackButton(getPanelAt(currentPosition), getPanelAt(currentPosition - 1))) {
					setActivePanel(currentPosition - 1);
				}
			}
		} else
			onCustomButtonPressed(portlet, button);

	}

	public void onCustomButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	private Portlet getPanelAt(int pos) {
		return panels.get(pos).portlet;
	}

	protected boolean onBackButton(Portlet active, Portlet prev) {
		return true;
	}

	protected boolean onNextButton(Portlet active, Portlet next) {
		return true;
	}

	protected void onUserFinishedButton() {
		close();
	}

	protected void onUserCanceledButton() {
		close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	protected FormPortletButton getNextButton() {
		return this.nextButton;
	}
	protected FormPortletButton getBackButton() {
		return this.backButton;
	}

}
