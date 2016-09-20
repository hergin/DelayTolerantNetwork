package ws.huso.dtn.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ws.huso.dtn.client.Message;
import ws.huso.dtn.model.Client;
import ws.huso.dtn.model.Link;
import ws.huso.dtn.util.UdpSender;

public class DtnServlet extends HttpServlet {

	public static Map<String, Client> nodeMap = new HashMap<String, Client>();
	public static ArrayList<Link> linkList = new ArrayList<Link>();
	public static Map<String, Message> messageMap = new HashMap<String, Message>();
	public static String protocol = "1";
	public static String ttl = "12";

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String type = req.getParameter("type");

		if (type.equals("attach")) {

			Client c = new Client();
			c.setId(req.getParameter("nodeId"));
			c.setIP(req.getParameter("IP"));
			c.setPort(Integer.parseInt(req.getParameter("port")));

			nodeMap.put(c.getId(), c);

			resp.getWriter().write("OK");

			UdpSender.notifyAboutNewClient();

		} else if (type.equals("log")) {

			Message m = new Message(req.getParameter("msg"));

			if (!messageMap.containsKey(m.getUniqueID())) {
				messageMap.put(m.getUniqueID(), m);
			} else {
				messageMap.get(m.getUniqueID()).getHistory()
						.addAll(m.getHistory());
				messageMap.get(m.getUniqueID()).setCurrent(m.getCurrent());
				if (m.getDropped() == 1)
					messageMap.get(m.getUniqueID()).setDropped(
							Message.DROPPED_TTL);
				if (m.getTtl() < messageMap.get(m.getUniqueID()).getTtl())
					messageMap.get(m.getUniqueID()).setTtl(m.getTtl());
			}

			// if (protocol.equals("1") || protocol.equals("2")) {
			// if (!messageMap.containsKey(m.getUniqueID())) {
			// messageMap.put(m.getUniqueID(), m);
			// } else {
			// messageMap.remove(m.getUniqueID());
			// messageMap.put(m.getUniqueID(), m);
			// }
			// } else if (protocol.equals("3")) {
			// if (!messageMap.containsKey(m.getUniqueID())) {
			// messageMap.put(m.getUniqueID(), m);
			// } else {
			// if (m.getTtl() < messageMap.get(m.getUniqueID()).getTtl())
			// messageMap.get(m.getUniqueID()).setTtl(m.getTtl());
			//
			// if (m.getDropped() == 1)
			// messageMap.get(m.getUniqueID()).setDropped(1);
			//
			// messageMap.get(m.getUniqueID()).getHistory()
			// .addAll(m.getHistory());
			// }
			// }

			resp.getWriter().write("OK");

		} else if (type.equals("detach")) {

			nodeMap.get(req.getParameter("nodeId")).setDead(true);

			for (int i = 0; i < linkList.size(); i++) {
				if (Integer.parseInt(req.getParameter("nodeId")) == linkList
						.get(i).getEdges()[0]
						|| Integer.parseInt(req.getParameter("nodeId")) == linkList
								.get(i).getEdges()[1]) {
					linkList.get(i).setActive(false);
				}
			}

			for (Message m : messageMap.values()) {
				if (req.getParameter("nodeId").equals(m.getDest())
						&& m.getDropped() == Message.NOTDROPPED) {
					messageMap.get(m.getUniqueID()).setDropped(
							Message.DROPPED_DEST_DETACHED);
				}
			}

			UdpSender.notifyAboutNewClient();

			resp.getWriter().write("OK");

		} else if (type.equals("getNodeList")) {

			for (Client c : nodeMap.values()) {
				if (!c.isDead())
					resp.getWriter().write(c.getId() + "|");
			}

		} else if (type.equals("getConnectedNodeList")) {

			int node = Integer.parseInt(req.getParameter("nodeId"));
			boolean found = false;

			for (Link l : linkList) {
				if (l.getEdges()[0] == node && l.isActive()) {
					resp.getWriter().write(l.getEdges()[1] + "|");
					found = true;
				} else if (l.getEdges()[1] == node && l.isActive()) {
					resp.getWriter().write(l.getEdges()[0] + "|");
					found = true;
				}
			}

			if (!found)
				resp.getWriter().write("ERROR");

		} else if (type.equals("getNodeInfo")) {

			resp.getWriter().write(
					nodeMap.get(req.getParameter("nodeId")).getIP());
			resp.getWriter().write(
					":" + nodeMap.get(req.getParameter("nodeId")).getPort());

		} else if (type.equals("getProtocol")) {

			resp.getWriter().write(protocol);

		} else if (type.equals("getTTL")) {

			resp.getWriter().write(ttl);

		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
