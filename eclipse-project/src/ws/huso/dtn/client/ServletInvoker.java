package ws.huso.dtn.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ServletInvoker {

	private String host;
	private String moduleName = "dtn";
	private String serverPort;

	public ServletInvoker(String h, String p) {
		this.host = h;
		this.serverPort = p;
	}

	public InetSocketAddress getNodeInfo(int nodeId) {
		String res = invoke("http://" + host + ":" + serverPort + "/"
				+ moduleName + "/dtn.serv?type=getNodeInfo&nodeId=" + nodeId);
		StringTokenizer tok = new StringTokenizer(res, ":");

		return new InetSocketAddress(tok.nextToken(), Integer.parseInt(tok
				.nextToken()));

	}

	public String getProtocol() {
		return invoke("http://" + host + ":" + serverPort + "/" + moduleName
				+ "/dtn.serv?type=getProtocol");
	}

	public String getTTL() {
		return invoke("http://" + host + ":" + serverPort + "/" + moduleName
				+ "/dtn.serv?type=getTTL");
	}

	public void logMessage(Message m) {
		invoke("http://" + host + ":" + serverPort + "/" + moduleName
				+ "/dtn.serv?type=log&msg=" + m.toString());
	}

	public boolean attach(int nodeId, String IP, int port) {
		return invoke(
				"http://" + host + ":" + serverPort + "/" + moduleName
						+ "/dtn.serv?type=attach&nodeId=" + nodeId + "&IP="
						+ IP + "&port=" + port).equals("OK") ? true : false;
	}

	public boolean detach(int nodeId) {
		return invoke(
				"http://" + host + ":" + serverPort + "/" + moduleName
						+ "/dtn.serv?type=detach&nodeId=" + nodeId)
				.equals("OK") ? true : false;
	}

	public List<String> getNodeList() {
		List<String> result = new ArrayList<String>();

		String list = invoke("http://" + host + ":" + serverPort
				+ "/dtn/dtn.serv?type=getNodeList");

		if (list != null && !list.equals("")) {

			StringTokenizer tok = new StringTokenizer(list, "|");
			while (tok.hasMoreTokens()) {
				result.add(tok.nextToken());
			}

		}

		return result;

	}

	public List<String> getConnectedNodeList() {
		List<String> result = new ArrayList<String>();

		String list = invoke("http://" + host + ":" + serverPort
				+ "/dtn/dtn.serv?type=getConnectedNodeList&nodeId="
				+ ClientMain.nodeID);

		if (!list.equals("ERROR")) {

			StringTokenizer tok = new StringTokenizer(list, "|");
			while (tok.hasMoreTokens()) {
				result.add(tok.nextToken());
			}

		}

		return result;

	}

	private String invoke(String url) {
		try {
			URL servletUrl = new URL(url);
			URLConnection conn = servletUrl.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			return in.readLine().trim();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
