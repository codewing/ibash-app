package de.codewing.fragments;

import java.util.List;

public class GsonQuotes{
	public Inhalte Inhalte;	
	public static class Inhalte{
		public String last_page;
		public List<Data> data;		
		public static class Data{
			public int ident;
			public String ts;
			public String content;
			public int rating;
		}
	}
	class Statistik{
		String all, today, votes, comments;
	}
}
