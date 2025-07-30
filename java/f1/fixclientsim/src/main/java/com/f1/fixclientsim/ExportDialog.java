package com.f1.fixclientsim;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import com.f1.utils.IOH;
import com.f1.utils.SH;

public class ExportDialog extends JDialog implements ActionListener {

	private FieldsPanel fieldsPanel;
	private boolean ok;
	private char delim;
	private Component parent;

	public ExportDialog(Component parent, String path) {
		setModal(true);
		Container cp = getContentPane();
		fieldsPanel = new FieldsPanel();
		fieldsPanel.addFileField("file", "File", path, JFileChooser.SAVE_DIALOG);
		fieldsPanel.addTextField("delimiter", "Delimiter", "\\u0001");
		fieldsPanel.addComboField("eventFilter", "Event Filter", new String[] { "both", "All Events", "out", "Only outgoing", "in", "Only incoming" });
		fieldsPanel.addButton("cancel", "cancel", FieldsPanel.BUTTON_ESCAPE, this);
		fieldsPanel.addButton("save", "save", FieldsPanel.BUTTON_ENTER, this);
		cp.add(fieldsPanel);
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if ("cancel".equals(e.getActionCommand())) {
				ok = false;
			} else if ("save".equals(e.getActionCommand())) {
				ok = true;
				File file = new File(fieldsPanel.getValue("file"));
				if (!IOH.isDirectory(file.getParentFile())) {
					throw new RuntimeException("not a file: " + file);
				}
				if (IOH.isDirectory(file)) {
					throw new RuntimeException("Is a directory: " + file);
				}
				this.delim = SH.parseCharHandleSpecial(fieldsPanel.getValue("delimiter"));
			}
			setVisible(false);
		} catch (Exception e2) {
			SwingHelper.showDialog(e2);
		}
	}

	public void setVisible(boolean visible) {
		if (visible)
			setBounds(SwingHelper.center(new Rectangle(0, 0, 300, 300), parent.getBounds()));
		super.setVisible(visible);
	}

	public boolean isOkPressed() {
		return ok;
	}

	public String getPath() {
		return fieldsPanel.getValue("file");
	}

	public char getDelim() {
		return delim;
	}

	public String getDirection() {
		return fieldsPanel.getValue("eventFilter");
	}

}
