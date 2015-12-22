package de.codewing.model;

public class Quote extends FaviQuote {

	private int rating;
	private String content;

	public Quote(int ident, String ts, int rating, String content) {
		super(ts, ident);
		this.rating = rating;
		this.content = content;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
