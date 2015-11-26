package de.codewing.fragments;

public class Quote {
	
	private int ident;
	private String ts;
	private int rating;
	private String quotetext;
	
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
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getQuotetext() {
		return quotetext;
	}
	public void setQuotetext(String quotetext) {
		this.quotetext = quotetext;
	}
	public Quote(int ident, String ts, int rating, String quotetext) {
		super();
		this.ident = ident;
		this.ts = ts;
		this.rating = rating;
		this.quotetext = quotetext;
	}
	
}
