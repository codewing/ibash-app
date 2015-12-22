package de.codewing.model;


public class FaviQuote {
	protected int ident;
	protected String ts;

	public FaviQuote(){}

	public FaviQuote(String ts, int ident) {
		super();
		this.ts = ts;
		this.ident = ident;
	}

	public int getIdent() {
		return ident;
	}

	public void setIdent(int ident) {
		this.ident = ident;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}
}
