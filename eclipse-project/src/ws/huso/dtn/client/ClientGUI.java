package ws.huso.dtn.client;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientGUI extends JFrame implements ActionListener, WindowListener {

	ServletInvoker invoker;
	JComboBox comboBox;
	JTextField textField;
	JTextArea textArea;
	ClientMain parent;
	int msgId = 1;

	public ClientGUI(List<String> list, ServletInvoker invoker, ClientMain par) {

		super("Node " + ClientMain.nodeID);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		this.invoker = invoker;
		this.parent = par;

		Dimension d = new Dimension(150, 40);

		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel label = new JLabel("Destination :");
		label.setPreferredSize(d);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		getContentPane().add(label, c);

		comboBox = new JComboBox(list.toArray());
		comboBox.setPreferredSize(d);
		DefaultComboBoxModel model = new DefaultComboBoxModel(invoker
				.getNodeList().toArray());
		comboBox.setModel(model);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		getContentPane().add(comboBox, c);

		label = new JLabel("Message :");
		label.setPreferredSize(d);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		getContentPane().add(label, c);

		textField = new JTextField();
		textField.setPreferredSize(d);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		getContentPane().add(textField, c);

		JButton button = new JButton("Send");
		button.addActionListener(this);
		button.setPreferredSize(new Dimension(d.width * 2, d.height));
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		c.gridx = 0;
		c.gridy = 2;
		getContentPane().add(button, c);

		textArea = new JTextArea(5, 20);
		textArea.setCaretPosition(textArea.getDocument().getLength());
		textArea.setEditable(true);

		JScrollPane scrollPane = new JScrollPane(textArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2;
		getContentPane().add(scrollPane, c);

		Dimension paneSize = getSize();
		Dimension screenSize = getToolkit().getScreenSize();
		setLocation((screenSize.width - paneSize.width) / 2,
				(screenSize.height - paneSize.height) / 2);

		addWindowListener(this);

		pack();
		setVisible(true);

	}

	public void addTextToTextArea(String s) {
		textArea.append(s + "\n");
		textArea.setCaretPosition(textArea.getText().length());

	}

	public void refreshNodeList() {
		DefaultComboBoxModel model = new DefaultComboBoxModel(invoker
				.getNodeList().toArray());
		comboBox.removeAllItems();
		comboBox.setModel(model);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (Integer.parseInt((String) comboBox.getSelectedItem()) == parent.nodeID) {
			JOptionPane.showMessageDialog(null,
					"You can't send message to self!", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else if (textField.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(null,
					"Message field is empty, write a message first!", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else {

			MessageSender ms = new MessageSender(msgId++, ClientMain.nodeID,
					Integer.parseInt((String) comboBox.getSelectedItem()),
					invoker, textField.getText().trim());

			if (!ms.route()) {
				parent.bufferMessage(ms.getMessage());
			}

		}

	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent ev) {

		invoker.detach(ClientMain.nodeID);

	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

}