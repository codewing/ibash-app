package de.codewing.model;

public class Quote extends FaviQuote {

	private int rating;
	private String quotetext;

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
		super(ts, ident);
		this.rating = rating;
		this.quotetext = quotetext;
	}
	
}
