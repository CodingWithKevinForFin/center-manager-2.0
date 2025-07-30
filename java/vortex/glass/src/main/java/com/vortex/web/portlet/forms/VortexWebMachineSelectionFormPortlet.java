package com.vortex.web.portlet.forms;

import java.util.List;
import java.util.Map;

import com.f1.base.Getter;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.CH;
import com.vortex.web.portlet.tables.VortexWebMachineInstancesTablePortlet;
import com.vortex.web.portlet.tables.VortexWebTablePortlet;

public abstract class VortexWebMachineSelectionFormPortlet extends GridPortlet implements FormPortletListener {

	private Selection selectionMode;
	private VortexWebMachineInstancesTablePortlet hosts;
	private FormPortletButton cancelButton;
	private FormPortletButton submitButton;

	public enum Selection {
		Single((byte) 0),
		Multi((byte) 1);

		private final byte t;

		private Selection(byte t) {
			this.t = t;
		}
	}

	public VortexWebMachineSelectionFormPortlet(PortletConfig config, Selection s) {
		super(config);
		this.selectionMode = s;

		hosts = new VortexWebMachineInstancesTablePortlet(generateConfig());
		hosts.getTable().addMenuListener(new WebContextMenuListener() {

			@Override
			public void onVisibleRowsChanged(FastWebTable fastWebTable) {
			}

			@Override
			public void onSelectedChanged(FastWebTable t) {
				switch (selectionMode) {
					case Single:
						submitButton.setEnabled(t.hasSelectedRows() && t.getSelectedRows().size() == 1);
						break;
					case Multi:
						submitButton.setEnabled(t.hasSelectedRows());
						break;
					default:
						submitButton.setEnabled(false);
				}
			}

			@Override
			public void onContextMenu(WebTable table, String action) {
			}

			@Override
			public void onCellClicked(WebTable table, Row row, WebColumn col) {
			}

			@Override
			public void onCellMousedown(WebTable table, Row row, WebColumn col) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNoSelectedChanged(FastWebTable fastWebTable) {
				// TODO Auto-generated method stub

			}
		});
		addChild(hosts);

		cancelButton = new FormPortletButton("Canel");
		submitButton = new FormPortletButton(selectionMode == Selection.Single ? "Select Only One" : "Select");

		submitButton.setEnabled(false);

		FormPortlet f = new FormPortlet(generateConfig());
		f.addButton(cancelButton);
		f.addButton(submitButton);
		addChild(f, 0, 1);

		f.addFormPortletListener(this);
	}

	private static final Getter<Row, String> muidGetter = new Getter<Row, String>() {
		@Override
		public String get(Row key) {
			return key.get(VortexWebTablePortlet.MUID, String.class);
		}
	};

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (submitButton == button) {
			List<Row> rows = hosts.getTable().getSelectedRows();
			String[] selectedMUIds = new String[rows.size()];
			selectedMUIds = CH.l(rows, muidGetter).toArray(selectedMUIds);

			onSelection(selectedMUIds);
		}

		this.close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	public abstract void onSelection(String[] selectedMUIds);
}
