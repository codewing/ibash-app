package de.codewing.ibash;

import java.util.List;

public class GsonComment {
	public List<Comments> comments;		
	public static class Comments{
		public String nick;
		public String ts;
		public String text;
	}
}
