package com.f1.fixclientsim;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

import com.f1.utils.IOH;
import com.f1.utils.SH;

public class ImportDialog extends JDialog implements ActionListener {

	private FieldsPanel fieldsPanel;
	private boolean ok;
	private char delim;
	private Component parent;
	private int repeat, start;
	private String prefix;
	private String session;
	private File file;

	public ImportDialog(Component parent, String path) {
		setModal(true);
		Container cp = getContentPane();
		fieldsPanel = new FieldsPanel();
		fieldsPanel.addFileField("file", "File", path, JFileChooser.OPEN_DIALOG);
		fieldsPanel.addTextField("delimiter", "Delimiter", "\\u0001");
		fieldsPanel.addTextField("repeat", "Repeat", "1");
		fieldsPanel.addTextField("start", "ID Start", "1");
		fieldsPanel.addTextField("prefix", "ID Prefix", "IMP-%i-");
		fieldsPanel.addComboField("session", "Session", new String[] { "", "<no sessions>" });
		fieldsPanel.addButton("cancel", "cancel", FieldsPanel.BUTTON_ESCAPE, this);
		fieldsPanel.addButton("open", "open", FieldsPanel.BUTTON_ENTER, this);
		cp.add(fieldsPanel);
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if ("cancel".equals(e.getActionCommand())) {
				ok = false;
			} else if ("open".equals(e.getActionCommand())) {
				ok = true;
				File file = new File(fieldsPanel.getValue("file"));
				if (!IOH.isFile(file)) {
					throw new RuntimeException("file not found: " + file);
				}
				this.session = fieldsPanel.getValue("session");
				this.delim = SH.parseCharHandleSpecial(fieldsPanel.getValue("delimiter"));
				this.repeat = SH.parseInt(fieldsPanel.getValue("repeat"));
				this.start = SH.parseInt(fieldsPanel.getValue("start"));
				this.prefix = fieldsPanel.getValue("prefix");
				if (SH.isnt(session))
					throw new RuntimeException("No sessions available. Use file -> connect");
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

	public int getRepeat() {
		return repeat;
	}

	public int getStart() {
		return start;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setStart(int start) {
		this.start = start;
		fieldsPanel.setValue("start", SH.toString(start));
	}

	public void setSessions(List<String> sessions) {
		if (sessions.size() == 0) {
			fieldsPanel.setComboFields("session", new String[] { "", "<no sessions>" });
			fieldsPanel.setEnabled("session", false);
		} else {
			fieldsPanel.setComboFields("session", sessions.toArray(new String[sessions.size()]));
			fieldsPanel.setEnabled("session", false);
		}
	}

	public String getSession() {
		return session;
	}

}
