package ws.huso.dtn.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ws.huso.dtn.servlet.DtnServlet;

public class MessageSender {

	ServletInvoker invoker;

	Message message;

	public MessageSender(int id, int s, int d, ServletInvoker si, String msg) {
		message = new Message(id, s + "", d + "", msg, new ArrayList<String>(),
				ClientMain.ttl);
		this.invoker = si;
	}

	public MessageSender(String encoded, ServletInvoker si) {
		message = new Message(encoded);
		this.invoker = si;
	}

	public MessageSender(Message m, ServletInvoker si) {
		this.message = m;
		this.invoker = si;
	}

	public void send(String newNode) {
		InetSocketAddress addr = invoker.getNodeInfo(Integer.parseInt(newNode));

		if (!message.getHistory().contains(ClientMain.nodeID + ""))
			message.getHistory().add(ClientMain.nodeID + "");

		message.setCurrent(ClientMain.nodeID + "");
		invoker.logMessage(message);

		try {
			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(message.toString()
					.getBytes(), message.toString().getBytes().length, addr);
			socket.send(packet);
			socket.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	public boolean route() {

		if (ClientMain.protocol.equals("1")) {
			// First Contact with history

			List<String> connectedList = invoker.getConnectedNodeList();

			if (connectedList.contains(message.getDest())) {
				message.setTtl(message.getTtl() - 1);
				send(message.getDest());
				return true;
			}

			for (String node : connectedList) {
				if (!message.hasInTheHistory(node)) {
					message.setTtl(message.getTtl() - 1);
					send(node);
					return true;
				}
			}

		} else if (ClientMain.protocol.equals("2")) {
			// First Contact without history

			List<String> connectedList = invoker.getConnectedNodeList();

			if (connectedList.size() == 0)
				return false;

			if (connectedList.contains(message.getDest())) {
				message.setTtl(message.getTtl() - 1);
				send(message.getDest());
				return true;
			}

			int randomInt = new Random(System.nanoTime()).nextInt(connectedList.size());

			message.setTtl(message.getTtl() - 1);
			send(connectedList.get(randomInt));
			return true;

		} else if (ClientMain.protocol.equals("3")) {
			// Epidemic

			List<String> connectedList = invoker.getConnectedNodeList();

			if (connectedList.size() == 0)
				return false;

			message.setTtl(message.getTtl() - 1);

			for (String no : connectedList) {
				send(no);
			}

			return true;
		}

		return false;

	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

}
