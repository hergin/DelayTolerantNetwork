package ws.huso.dtn.model;

import ws.huso.dtn.servlet.DtnServlet;

public class Link {

	int[] edges = new int[2];

	boolean active = true;

	float bandwidth;

	float length;

	public boolean alreadyExists() {

		for (Link l : DtnServlet.linkList) {
			if ((l.getEdges()[0] == edges[0] && l.getEdges()[1] == edges[1])
					|| (l.getEdges()[0] == edges[1] && l.getEdges()[1] == edges[0]))
				return true;
		}

		return false;

	}

	public int[] getEdges() {
		return edges;
	}

	public void setEdges(int[] edges) {
		this.edges = edges;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public float getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(float bandwidth) {
		this.bandwidth = bandwidth;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

}
