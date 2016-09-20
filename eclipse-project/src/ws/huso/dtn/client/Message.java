package ws.huso.dtn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Message {

	int id;

	String source;

	String dest;

	String msg;

	String current;

	int dropped = 0;
	
	public static int NOTDROPPED = 0;
	public static int DROPPED_TTL = 1;
	public static int DROPPED_DEST_DETACHED = 2;

	int ttl;

	List<String> history;

	int messageType;

	public static int MESSAGETYPE_NORMALMESSAGE = 0;
	public static int MESSAGETYPE_NOTIFYABOUTNEWLINK = 1;
	public static int MESSAGETYPE_CLIENTJOINED = 2;
	public static int MESSAGETYPE_PROTOCOLCHANGED = 3;
	public static int MESSAGETYPE_TTLCHANGED = 4;

	public Message(int id, String src, String dest, String msg,
			List<String> hist, int ttl) {
		this.messageType = MESSAGETYPE_NORMALMESSAGE;
		this.id = id;
		this.source = src;
		this.dest = dest;
		this.msg = msg;
		this.history = hist;
		this.ttl = ttl;
	}

	public Message(int type) {
		this.messageType = type;
	}

	public Message(String encoded) {
		StringTokenizer tok = new StringTokenizer(encoded, "|");

		messageType = Integer.parseInt(tok.nextToken());

		if (messageType == MESSAGETYPE_NORMALMESSAGE) {
			id = Integer.parseInt(tok.nextToken());
			source = new String(tok.nextToken());
			dest = new String(tok.nextToken());
			current = new String(tok.nextToken());
			ttl = Integer.parseInt(tok.nextToken());
			dropped = Integer.parseInt(tok.nextToken());
			msg = new String(tok.nextToken().replace('+', ' '));

			if (tok.hasMoreTokens()) {
				StringTokenizer tok2 = new StringTokenizer(tok.nextToken(), "_");
				history = new ArrayList<String>();

				while (tok2.hasMoreTokens()) {
					history.add(tok2.nextToken());
				}
			}
		}

	}

	public String getUniqueID() {
		return source + "-" + id;
	}

	public boolean hasInTheHistory(String node) {
		for (String s : history) {
			if (s.equals(node))
				return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		if (messageType == MESSAGETYPE_NORMALMESSAGE) {
			sb.append(messageType + "|" + id + "|" + source + "|" + dest + "|"
					+ current + "|" + ttl + "|" + dropped + "|"
					+ msg.replace(' ', '+') + "|");

			if (history != null && history.size() != 0) {
				for (String s : history) {
					sb.append(s + "_");
				}
			}
		} else {
			sb.append(messageType);
		}

		return sb.toString();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDest() {
		return dest;
	}

	public void setDest(String dest) {
		this.dest = dest;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<String> getHistory() {
		return history;
	}

	public void setHistory(List<String> history) {
		this.history = history;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}

	public int getDropped() {
		return dropped;
	}

	public void setDropped(int dropped) {
		this.dropped = dropped;
	}

}
