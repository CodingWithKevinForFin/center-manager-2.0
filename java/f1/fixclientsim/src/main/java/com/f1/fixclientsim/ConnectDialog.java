package com.f1.fixclientsim;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import com.f1.utils.DetailedException;
import com.f1.utils.LH;
import com.f1.utils.casters.Caster_Integer;

public class ConnectDialog extends JDialog implements ActionListener {

	private static final Logger log = Logger.getLogger(ConnectDialog.class.getName());
	private JTextField portField;
	private JTextField hostField;
	private JButton cancelButton;
	private JButton connectButton;
	private FixClientSimManager sessionManager;
	private String senderCompId;
	private FieldsPanel panel;
	private FixClientSimSession session;
	private Thread runningThread;
	private JProgressBar progressBar;

	public ConnectDialog(JFrame parent, FixClientSimManager sessionManager) {
		setModal(true);
		setTitle("Connect");
		panel = new FieldsPanel();
		panel.addTextField("host", "Host", "localHost");
		panel.addTextField("port", "Port", "9878");
		panel.addTextField("protocol", "Protocol", "FIX.4.2");
		panel.addTextField("senderCompId", "Sender Comp ID", "CLIENT1");
		panel.addTextField("targetCompId", "Target Comp ID", "F1OMS1");
		panel.addTextField("senderSeqNum", "Sender Seq. Number (optional)", "");
		panel.addTextField("targetSeqNum", "Target Seq. Number (optional)", "");
		this.cancelButton = panel.addButton("cancel", "Cancel", FieldsPanel.BUTTON_ESCAPE, this);
		this.connectButton = panel.addButton("connect", "Connect", FieldsPanel.BUTTON_ENTER, this);
		this.sessionManager = sessionManager;
		add(panel, BorderLayout.CENTER);
		progressBar = new JProgressBar(0, 40);
		add(progressBar, BorderLayout.SOUTH);
		setBounds(SwingHelper.center(new Rectangle(0, 0, 300, 300), parent.getBounds()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("cancel".equals(e.getActionCommand())) {
			if (runningThread != null) {
				runningThread.interrupt();
				runningThread = null;
			} else {
				setVisible(false);
			}
		} else if ("connect".equals(e.getActionCommand())) {
			this.session = null;
			connectButton.setText("Connecting");
			panel.setEnabled(false);
			this.runningThread = new Thread(new DoConnect());
			this.runningThread.start();
			cancelButton.setEnabled(true);
		}
	}

	public class DoConnect implements Runnable {

		@Override
		public void run() {
			try {
				String host = panel.getValue("host");
				int port = Caster_Integer.INSTANCE.cast(panel.getValue("port"));
				String beginString = panel.getValue("protocol");
				String senderCompId = panel.getValue("senderCompId");
				String targetCompId = panel.getValue("targetCompId");
				String targetSeqNumStr = panel.getValue("targetSeqNum");
				String senderSeqNumStr = panel.getValue("senderSeqNum");
				int targetSeqNum = targetSeqNumStr.isEmpty() ? -1 : Caster_Integer.INSTANCE.cast(targetSeqNumStr);
				int senderSeqNum = senderSeqNumStr.isEmpty() ? -1 : Caster_Integer.INSTANCE.cast(senderSeqNumStr);
				session = sessionManager.createSession(host, port, beginString, senderCompId, targetCompId, senderSeqNum, targetSeqNum);
				int count = 0;
				while (session.getState() == FixClientSimSession.CONNECTING && count++ < 40) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						break;
					}
					connectButton.setText("Connecting");
					progressBar.setValue(count);
					connectButton.repaint();
				}
				if (!session.isConnected()) {
					JOptionPane.showMessageDialog(ConnectDialog.this, "Could not connect", "error connecting", JOptionPane.ERROR_MESSAGE);
					sessionManager.closeSession(session);
					session = null;
				} else {
					setVisible(false);
				}
			} catch (DetailedException ex) {
				LH.warning(log, "error", ex);
				JOptionPane.showMessageDialog(ConnectDialog.this, ex.toLegibleString(), "Error", JOptionPane.ERROR_MESSAGE);
			} catch (Exception ex) {
				LH.warning(log, "error", ex);
				JOptionPane.showMessageDialog(ConnectDialog.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			progressBar.setValue(0);
			panel.setEnabled(true);
			connectButton.setText("Connect");
		}
	}

	public FixClientSimSession getSession() {
		return session;
	}

}

