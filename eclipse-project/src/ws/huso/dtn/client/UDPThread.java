package ws.huso.dtn.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.JOptionPane;

class UDPThread extends Thread {

	protected DatagramSocket socket = null;
	int port;
	ClientMain parent;

	public UDPThread(ClientMain c) {
		super("UDPThread");
		this.port = c.port;
		this.parent = c;
	}

	@Override
	public void run() {
		while (true) {
			try {
				socket = new DatagramSocket(port);
				byte[] buf = new byte[1024];

				DatagramPacket packet = new DatagramPacket(buf, buf.length);

				socket.receive(packet);
				socket.close();

				String receivedMsg = new String(packet.getData(), 0,
						packet.getLength());

				Message m = new Message(receivedMsg);

				if (m.getMessageType() == Message.MESSAGETYPE_CLIENTJOINED) {
					parent.triggerUpdateNodeList();
				} else if (m.getMessageType() == Message.MESSAGETYPE_PROTOCOLCHANGED) {
					parent.triggerUpdateProtocol();
				} else if (m.getMessageType() == Message.MESSAGETYPE_TTLCHANGED) {
					parent.triggerUpdateTTL();
				} else if (m.getMessageType() == Message.MESSAGETYPE_NOTIFYABOUTNEWLINK) {
					parent.checkMessagesInBuffer();
				} else if (m.getMessageType() == Message.MESSAGETYPE_NORMALMESSAGE) {
					if (m.dest.equals(parent.nodeID + "")) {
						if (!parent.receivedMessageIDs
								.contains(m.getUniqueID())) {
							if (!m.getHistory().contains(parent.nodeID + ""))
								m.getHistory().add(parent.nodeID + "");
							m.setCurrent(parent.nodeID + "");
							parent.invoker.logMessage(m);
							parent.output("Message received from node"
									+ m.getSource() + ".\nMsgID: "
									+ m.getUniqueID() + " Content: "
									+ m.getMsg());
							parent.receivedMessageIDs.add(m.getUniqueID());
						}
					} else {
						if (m.getTtl() > 0) {
							MessageSender ms = new MessageSender(m,
									parent.invoker);

							if (parent.protocol.equals("3")) {
								parent.bufferMessage(m);
							}

							if (!ms.route()) {
								m.setCurrent(parent.nodeID + "");
								parent.invoker.logMessage(m);
								parent.bufferMessage(m);
							}
						} else {
							if (!parent.protocol.equals("3"))
								m.setDropped(Message.DROPPED_TTL);
							parent.invoker.logMessage(m);
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
