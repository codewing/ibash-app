package de.codewing.model;

public class Comment {
	String nick;
	String ts;
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getTs() {
		return ts;
	}
	public void setTs(String ts) {
		this.ts = ts;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	String text;
}
