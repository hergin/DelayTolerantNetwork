package ws.huso.dtn.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ws.huso.dtn.client.Message;
import ws.huso.dtn.graph.GraphViz;
import ws.huso.dtn.model.Client;
import ws.huso.dtn.model.Link;
import ws.huso.dtn.servlet.DtnServlet;
import ws.huso.dtn.util.UdpSender;

import com.opensymphony.xwork2.ActionSupport;

public class MainAction extends ActionSupport {

	private List<Link> linkList;

	private Link link;

	private List<Client> clientList;

	private List<Message> messageList;

	private String message;

	private String nodeId;

	private boolean linkFlag;

	private String newImageName;

	private String messageStatusPath;

	private String requestedMessageID;

	private Message selectedMessage;

	private String currentProtocol;

	private String currentTTL;

	public String getMyPage() {

		return SUCCESS;

	}

	public String getClients() {

		clientList = new ArrayList<Client>();

		for (Client c : DtnServlet.nodeMap.values()) {
			if (!c.isDead())
				clientList.add(c);
		}

		return SUCCESS;

	}

	public String getConfigPage() {

		currentProtocol = DtnServlet.protocol;

		currentTTL = DtnServlet.ttl;

		return SUCCESS;

	}

	public String generateRandomNetwork() {

		int nodeNumber = 0;

		for (Client c : DtnServlet.nodeMap.values()) {
			if (!c.isDead())
				nodeNumber++;
		}

		Random r = new Random(System.nanoTime());

		for (int i = 0; i < nodeNumber; i++) {
			Link l = new Link();
			l.setBandwidth(10);
			l.setLength(100);

			int r1;
			do {
				r1 = r.nextInt(nodeNumber);
			} while (((Client) DtnServlet.nodeMap.values().toArray()[r1])
					.isDead());

			int r2;
			do {
				r2 = r.nextInt(nodeNumber);
			} while (r2 == r1
					|| ((Client) DtnServlet.nodeMap.values().toArray()[r2])
							.isDead());

			l.setEdges(new int[] {
					Integer.parseInt(((Client) DtnServlet.nodeMap.values()
							.toArray()[r1]).getId()),
					Integer.parseInt(((Client) DtnServlet.nodeMap.values()
							.toArray()[r2]).getId()) });

			if (!l.alreadyExists())
				DtnServlet.linkList.add(l);
		}

		return SUCCESS;

	}

	public String modifyProtocol() {

		if (!DtnServlet.protocol.equals(currentProtocol)) {
			DtnServlet.protocol = currentProtocol;
			UdpSender.notifyProtocolUpdate();
		}

		if (!DtnServlet.ttl.equals(currentTTL)) {
			DtnServlet.ttl = currentTTL;
			UdpSender.notifyTtlUpdate();
		}

		return SUCCESS;

	}

	public String getMessages() {

		messageList = new ArrayList<Message>();

		messageList.addAll(DtnServlet.messageMap.values());

		return SUCCESS;

	}

	public String getMessageDetails() {

		selectedMessage = DtnServlet.messageMap.get(requestedMessageID);

		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());

		for (Client c : DtnServlet.nodeMap.values()) {
			gv.add(c.getId());
			if (c.getId().equals(selectedMessage.getSource()))
				gv.add("[color=black, style=filled, fontcolor=white]");
			else if (c.getId().equals(selectedMessage.getDest()))
				gv.add("[peripheries=2,color=black, style=filled, fontcolor=white]");
			else if (c.getId().equals(selectedMessage.getCurrent()))
				gv.add("[shape=box, peripheries=2, color=black, style=filled, fontcolor=white]");
			else if (selectedMessage.getHistory() != null
					&& selectedMessage.getHistory().contains(c.getId()))
				gv.add("[shape=box, color=black, style=filled, fontcolor=white]");
			else if (c.isDead())
				gv.add("[style=filled, color=gray84, fillcolor=gray84,fontcolor=white]");

			gv.addln();
		}

		for (Link l : DtnServlet.linkList) {
			gv.add(l.getEdges()[0] + " -- " + l.getEdges()[1]);
			if (!l.isActive())
				gv.add("[style=dotted]");
			gv.addln();
		}

		gv.addln(gv.end_graph());

		String type = "gif";
		messageStatusPath = "message" + new Random(System.nanoTime()).nextInt() + ".gif";

		File out = new File("../webapps/dtn/images/"
				+ messageStatusPath); // Windows
		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);

		return SUCCESS;

	}

	public String deleteClient() {
		DtnServlet.nodeMap.get(nodeId).setDead(true);

		for (int i = 0; i < DtnServlet.linkList.size(); i++) {
			if (Integer.parseInt(nodeId) == DtnServlet.linkList.get(i)
					.getEdges()[0]
					|| Integer.parseInt(nodeId) == DtnServlet.linkList.get(i)
							.getEdges()[1]) {
				DtnServlet.linkList.get(i).setActive(false);
			}
		}

		return SUCCESS;
	}

	public String getLinks() {

		linkList = DtnServlet.linkList;

		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph());

		for (Client c : DtnServlet.nodeMap.values()) {
			gv.add(c.getId());
			if (c.isDead())
				gv.add("[style=filled, color=gray84, fillcolor=gray84,fontcolor=white]");
			gv.addln();
		}

		for (Link l : linkList) {
			gv.add(l.getEdges()[0] + " -- " + l.getEdges()[1]);
			if (DtnServlet.nodeMap.get(l.getEdges()[0] + "").isDead()
					|| DtnServlet.nodeMap.get(l.getEdges()[1] + "").isDead())
				gv.add("[style=dotted,color=gray]");
			else if (!l.isActive())
				gv.add("[style=dotted]");
			gv.addln();
		}

		gv.addln(gv.end_graph());

		String type = "gif";
		newImageName = "graph" + new Random(System.nanoTime()).nextInt() + ".gif";

		File out = new File("../webapps/dtn/images/"
				+ newImageName); // Windows
		gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);

		if (DtnServlet.linkList.size() != 0)
			linkFlag = true;
		else
			linkFlag = false;

		return SUCCESS;

	}

	public String activateLink() {

		if (DtnServlet.nodeMap.get(link.getEdges()[0] + "").isDead()
				|| DtnServlet.nodeMap.get(link.getEdges()[1] + "").isDead()) {
			message = "You can not activate a link of dead client!";
			return ERROR;
		}

		for (int i = 0; i < DtnServlet.linkList.size(); i++) {
			if (link.getEdges()[0] == DtnServlet.linkList.get(i).getEdges()[0]
					&& link.getEdges()[1] == DtnServlet.linkList.get(i)
							.getEdges()[1]) {
				DtnServlet.linkList.get(i).setActive(true);
				UdpSender.notifyLinkEdges(DtnServlet.linkList.get(i));
			}
		}

		return SUCCESS;
	}

	public String deactivateLink() {

		for (int i = 0; i < DtnServlet.linkList.size(); i++) {
			if (link.getEdges()[0] == DtnServlet.linkList.get(i).getEdges()[0]
					&& link.getEdges()[1] == DtnServlet.linkList.get(i)
							.getEdges()[1])
				DtnServlet.linkList.get(i).setActive(false);
		}

		return SUCCESS;
	}

	public String deleteLink() {

		if (DtnServlet.nodeMap.get(link.getEdges()[0] + "").isDead()
				|| DtnServlet.nodeMap.get(link.getEdges()[1] + "").isDead()) {
			message = "You can not delete a link of dead client!";
			return ERROR;
		}

		for (int i = 0; i < DtnServlet.linkList.size(); i++) {
			if (link.getEdges()[0] == DtnServlet.linkList.get(i).getEdges()[0]
					&& link.getEdges()[1] == DtnServlet.linkList.get(i)
							.getEdges()[1])
				DtnServlet.linkList.remove(i);
		}

		return SUCCESS;
	}

	public String addLink() {

		if (link.alreadyExists()) {
			message = "This link already exists! (Between "
					+ link.getEdges()[0] + " and " + link.getEdges()[1] + ")";
			return ERROR;
		} else if (link.getEdges()[0] == link.getEdges()[1]) {
			message = "You can't add a self link!";
			return ERROR;
		} else {
			DtnServlet.linkList.add(link);
			UdpSender.notifyLinkEdges(link);
		}
		return SUCCESS;
	}

	public String toAddLink() {

		clientList = new ArrayList<Client>();

		for (Client c : DtnServlet.nodeMap.values()) {
			if (!c.isDead())
				clientList.add(c);
		}

		return SUCCESS;
	}

	public List<Link> getLinkList() {
		return linkList;
	}

	public void setLinkList(List<Link> linkList) {
		this.linkList = linkList;
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public List<Client> getClientList() {
		return clientList;
	}

	public void setClientList(List<Client> clientList) {
		this.clientList = clientList;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isLinkFlag() {
		return linkFlag;
	}

	public void setLinkFlag(boolean linkFlag) {
		this.linkFlag = linkFlag;
	}

	public String getNewImageName() {
		return newImageName;
	}

	public void setNewImageName(String newImageName) {
		this.newImageName = newImageName;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public List<Message> getMessageList() {
		return messageList;
	}

	public void setMessageList(List<Message> messageList) {
		this.messageList = messageList;
	}

	public String getMessageStatusPath() {
		return messageStatusPath;
	}

	public void setMessageStatusPath(String messageStatusPath) {
		this.messageStatusPath = messageStatusPath;
	}

	public String getRequestedMessageID() {
		return requestedMessageID;
	}

	public void setRequestedMessageID(String requestedMessageID) {
		this.requestedMessageID = requestedMessageID;
	}

	public Message getSelectedMessage() {
		return selectedMessage;
	}

	public void setSelectedMessage(Message selectedMessage) {
		this.selectedMessage = selectedMessage;
	}

	public String getCurrentProtocol() {
		return currentProtocol;
	}

	public void setCurrentProtocol(String currentProtocol) {
		this.currentProtocol = currentProtocol;
	}

	public String getCurrentTTL() {
		return currentTTL;
	}

	public void setCurrentTTL(String currentTTL) {
		this.currentTTL = currentTTL;
	}

}
