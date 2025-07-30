package com.f1.fixclientsim;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Simple;

public class FieldsPanel extends JPanel implements KeyListener {

	public static final int BUTTON_OTHER = 0;
	public static final int BUTTON_ESCAPE = 1;
	public static final int BUTTON_ENTER = 2;
	private int location = 0;
	final private JPanel buttonsPanel;
	final private Map<String, JComponent> fields = new HashMap<String, JComponent>();
	private JButton enterButton;
	private JButton escapeButton;

	public Map<String, JComponent> getFields() {
		return fields;
	}

	public FieldsPanel() {
		setLayout(new GridBagLayout());
		buttonsPanel = new JPanel(new FlowLayout());
		final Insets insets = new Insets(3, 3, 3, 3);
		add(buttonsPanel, new GridBagConstraints(0, 1000, 2, 1, 1, 100, GridBagConstraints.NORTH, GridBagConstraints.NONE, insets, 0, 0));
		addKeyListener(this);
	}

	public void addTextField(String name, String title) {
		addTextField(name, title, "");
	}

	public JTextField addTextField(String name, String title, String defaultValue) {
		final JTextField field = new JTextField(defaultValue);
		addField(name, title, field);
		location++;
		return field;
	}

	public void addFileField(String name, String title, String deflt, int dialogType) {
		addField(name, title, new FileField(deflt, dialogType));
	}

	public void addField(String name, String title, JComponent component) {
		component.addKeyListener(this);
		CH.putOrThrow(fields, name, component);
		final Insets insets = new Insets(3, 3, 3, 3);
		add(new JLabel(title + ":"), new GridBagConstraints(0, location, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, insets, 0, 0));
		add(component, new GridBagConstraints(1, location, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		location++;
	}

	public JButton addButton(String name, String title, int buttonType, ActionListener listener) {
		JButton button = new JButton(title);
		button.setActionCommand(name);
		buttonsPanel.add(button);
		button.addActionListener(listener);
		if (buttonType == BUTTON_ENTER) {
			if (enterButton != null)
				throw new RuntimeException("enter button already supplied");
			enterButton = button;
		} else if (buttonType == BUTTON_ESCAPE) {
			if (escapeButton != null)
				throw new RuntimeException("escape button already supplied");
			escapeButton = button;
		}
		return button;
	}

	public String getValue(String field) {
		JComponent c = CH.getOrThrow(fields, field);
		if (c instanceof FileField) {
			return ((FileField) c).getPath();
		} else {
			Object r = SwingHelper.getValue(c);
			if (r instanceof ComboField)
				return ((ComboField) r).getKey();
			return OH.toString(r);
		}
	}

	public void setValue(String field, String value) {
		JComponent c = CH.getOrThrow(fields, field);
		if (c instanceof FileField) {
			((FileField) c).setPath(value);
		} else if (c instanceof JComboBox) {
			SwingHelper.setValue(c, new ComboField(value, null));
		} else {
			SwingHelper.setValue(c, value);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		for (JComponent e : fields.values())
			e.setEnabled(enabled);
		for (Component e : buttonsPanel.getComponents())
			e.setEnabled(enabled);
	}

	public void addComboField(String name, String title, String[] objects) {
		addComboField(name, title, objects, null);

	}

	public void addComboField(String name, String title, String[] objects, String dflt) {
		JComboBox component = new JComboBox();
		if (objects.length % 2 != 0)
			throw new IllegalArgumentException("should be event sized array: " + objects.length);
		for (int i = 0; i < objects.length; i += 2) {
			component.addItem(new ComboField(objects[i], objects[i + 1]));
		}
		for (String s : objects)
			if (dflt != null)
				component.setSelectedItem(dflt);
		addField(name, title, component);
	}

	public static class ComboField {

		final private String key;
		final private String text;

		@Override
		public int hashCode() {
			return OH.hashCode(key);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ComboField))
				return false;
			return OH.eq(((ComboField) obj).key, key);
		}

		public ComboField(String key, String text) {
			this.key = key;
			this.text = text;
		}

		public String getKey() {
			return key;
		}

		public String toString() {
			return text;
		}

	}

	public class FileField extends JPanel implements ActionListener {
		private final JButton button = new JButton("browse");
		private final JTextField field = new JTextField();
		private final int dialogType;

		public FileField(String path, int dialogType) {
			super(new BorderLayout());
			field.setText(path);
			add(field, BorderLayout.CENTER);
			add(button, BorderLayout.EAST);
			button.addActionListener(this);
			this.dialogType = dialogType;
		}

		public String getPath() {
			return field.getText();
		}

		public void setPath(String path) {
			field.setText(path);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			File path = new File(getPath());
			if (path.isDirectory())
				chooser.setCurrentDirectory(path);
			else
				chooser.setSelectedFile(path);
			chooser.setDialogType(dialogType);
			if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				setPath(chooser.getSelectedFile().getPath());
			}
		}
	}

	public void setComboFields(String field, String[] objects) {
		JComboBox c = (JComboBox) CH.getOrThrow(Caster_Simple.OBJECT, fields, field);
		c.removeAllItems();
		if (objects.length % 2 != 0)
			throw new IllegalArgumentException("should be event sized array: " + objects.length);
		for (int i = 0; i < objects.length; i += 2) {
			c.addItem(new ComboField(objects[i], objects[i + 1]));
		}
	}

	public void setEnabled(String field, boolean enabled) {
		CH.getOrThrow(fields, field).setEnabled(enabled);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER && enterButton != null)
			enterButton.doClick();
		else if (e.getKeyChar() == KeyEvent.VK_ESCAPE && escapeButton != null)
			escapeButton.doClick();
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
