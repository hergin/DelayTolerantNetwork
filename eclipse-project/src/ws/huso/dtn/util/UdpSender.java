package ws.huso.dtn.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import ws.huso.dtn.client.Message;
import ws.huso.dtn.model.Client;
import ws.huso.dtn.model.Link;
import ws.huso.dtn.servlet.DtnServlet;

public class UdpSender {

	public static void notifyTtlUpdate() {
		Message m = new Message(Message.MESSAGETYPE_TTLCHANGED);
		for (Client c : DtnServlet.nodeMap.values()) {
			send(m, c.getId() + "");
		}
	}

	public static void notifyProtocolUpdate() {
		Message m = new Message(Message.MESSAGETYPE_PROTOCOLCHANGED);
		for (Client c : DtnServlet.nodeMap.values()) {
			send(m, c.getId() + "");
		}
	}

	public static void notifyLinkEdges(Link l) {
		Message m = new Message(Message.MESSAGETYPE_NOTIFYABOUTNEWLINK);
		send(m, l.getEdges()[0] + "");
		send(m, l.getEdges()[1] + "");
	}

	public static void notifyAboutNewClient() {
		Message m = new Message(Message.MESSAGETYPE_CLIENTJOINED);
		for (Client c : DtnServlet.nodeMap.values()) {
			send(m, c.getId() + "");
		}
	}

	private static void send(Message m, String nodeId) {

		InetSocketAddress addr = new InetSocketAddress(DtnServlet.nodeMap.get(
				nodeId).getIP(), DtnServlet.nodeMap.get(nodeId).getPort());

		try {

			DatagramSocket socket = new DatagramSocket();
			DatagramPacket packet = new DatagramPacket(m.toString().getBytes(),
					m.toString().getBytes().length, addr);
			socket.send(packet);
			socket.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

}
