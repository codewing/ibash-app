package de.codewing.model;


public class FaviQuote {
	protected int id;
	protected String ts;

	public FaviQuote(){}

	public FaviQuote(String ts, int id) {
		super();
		this.ts = ts;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}
}
