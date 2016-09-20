package ws.huso.dtn.client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

public class ClientMain {

	static String server = "localhost";
	static String serverPort = "8080";
	public static int nodeID;
	public static int port;
	static boolean attached = false;
	static List<String> nodeList;
	static ClientGUI gui;
	static ServletInvoker invoker;
	static List<Message> messageBuffer = new ArrayList<Message>();
	static String protocol;
	static int ttl;
	static String[] algorithmList = new String[] {
			"First Contact (with history)", "First Contact (without history)",
			"Epidemic" };
	static List<String> receivedMessageIDs = new ArrayList<String>();

	public static void main(String[] args) {

		ClientMain m = new ClientMain();
		m.start();

	}

	public void start() {

		nodeID = new Random(System.nanoTime()).nextInt(10000);
		port = new Random(System.nanoTime()).nextInt() % 15000 + 50000;

		server = new String(JOptionPane.showInputDialog(
				"Enter Server IP Adress : ", server));

		serverPort = new String(JOptionPane.showInputDialog(
				"Enter Server port : ", serverPort));

		invoker = new ServletInvoker(server, serverPort);

		try {
			if (invoker.attach(nodeID, InetAddress.getLocalHost()
					.getHostAddress(), port)) {
				attached = true;
			} else {
				JOptionPane.showMessageDialog(null,
						"Cannot attach to the server, control the server IP!");
				System.exit(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (attached) {

			protocol = invoker.getProtocol();
			ttl = Integer.parseInt(invoker.getTTL());

			new UDPThread(this).start();

			nodeList = invoker.getNodeList();

			gui = new ClientGUI(nodeList, invoker, this);

			output("Node " + nodeID + " attached!");

			output("Algorithm: "
					+ algorithmList[Integer.parseInt(protocol) - 1]);
			output("TTL: " + ttl);

		}

	}

	public void bufferMessage(Message mes) {
		boolean found = false;
		for (Message m : messageBuffer) {
			if (m.getUniqueID().equals(mes.getUniqueID()))
				found = true;
		}
		if (!found) {
			messageBuffer.add(mes);
			outputMessage(mes);
		}
	}

	public void checkMessagesInBuffer() {
		for (int i = 0; i < messageBuffer.size();) {
			MessageSender ms = new MessageSender(messageBuffer.get(i), invoker);
			if (ms.route()) {
				outputSentMessage(messageBuffer.get(i));
				messageBuffer.remove(i);
			} else {
				i++;
			}
		}
	}

	public void output(String s) {
		gui.addTextToTextArea(s);
	}

	public void outputMessage(Message m) {
		output("Message buffered:");
		output("MsgID: " + m.getUniqueID() + " Src: " + m.getSource()
				+ " Dest: " + m.getDest());
	}

	public void outputSentMessage(Message m) {
		output("Message sent to next hop:");
		output("MsgID: " + m.getUniqueID() + " Src: " + m.getSource()
				+ " Dest: " + m.getDest());
	}

	public void triggerUpdateNodeList() {
		gui.refreshNodeList();
		// output("Client list updated!");
	}

	public void triggerUpdateProtocol() {
		protocol = invoker.getProtocol();
		output("Algorithm changed: "
				+ algorithmList[Integer.parseInt(protocol) - 1]);
	}

	public void triggerUpdateTTL() {
		ttl = Integer.parseInt(invoker.getTTL());
		output("TTL is updated to: " + ttl);
	}

	public void printMessageBuffer() {
		System.out.println("Message buffer of node " + nodeID);
		System.out.println("--------------------------------");
		for (Message m : messageBuffer) {
			System.out.println(m);
		}
	}

}
