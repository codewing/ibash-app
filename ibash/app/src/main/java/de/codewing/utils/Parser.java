package de.codewing.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.codewing.model.Comment;
import de.codewing.model.Quote;

/**
 * Created by codewing on 21.12.2015.
 */
public class Parser {

    /**
     * Takes a String containing the quotes in the json format and returns a ParseResult object
     *
     * @param json a json string containing quotes
     * @return a ParseResult object
     */
    public static ParseResult<Quote> parseQuotes(String json) {
        ArrayList quotelist = new ArrayList<Quote>();
        boolean lastPage = true;
        try {
            JSONObject root = new JSONObject(json);
            JSONObject inhalte = root.getJSONObject("Inhalte");
            if (inhalte.has("last_page")) {
                lastPage = inhalte.getInt("last_page") == 1;
            }
            JSONArray jsonQuotes = inhalte.getJSONArray("data");

            // add quotes into the table
            for (int i = 0; i < jsonQuotes.length(); i++) {
                JSONObject jsonQuote = jsonQuotes.getJSONObject(i);
                int ident = jsonQuote.getInt("ident");
                String ts = jsonQuote.getString("ts");
                String content = jsonQuote.getString("content");
                content = content.replace("[newline]", "\n");
                int rating = jsonQuote.getInt("rating");

                Quote quote = new Quote(ident, ts, rating, content);
                quotelist.add(quote);
            }

        } catch (Exception e) {
            Log.d("Parser#parseQuotes", "Error: " + e.getMessage());
        }
        return new ParseResult<>(lastPage, quotelist);
    }

    /**
     * Takes a String containing the quotes in the json format and returns a ParseResult object
     *
     * @param json a json string containing quotes
     * @return a ParseResult object
     */
    public static ParseResult<Comment> parseComments(String json) {
        ArrayList commentlist = new ArrayList<Comment>();
        boolean lastPage = true;
        try {
            JSONObject root = new JSONObject(json);
            JSONArray jsonComments = root.getJSONArray("comments");

            // Leere Quotelist erstellen
            commentlist = new ArrayList<Comment>();

            // add quotes into the table
            for (int i = 0; i < jsonComments.length(); i++) {
                JSONObject jsonComment = jsonComments.getJSONObject(i);
                String nick = jsonComment.getString("nick");
                String ts = jsonComment.getString("ts");
                ts.replace(" ", " - ");
                String text = jsonComment.getString("text");
                text = text.replace("[newline]", "\n");

                Comment comment = new Comment(nick, ts, text + "\n");
                commentlist.add(comment);
            }

        } catch (Exception e) {
            Log.d("Parser#parseComments", "Error: " + e.getMessage());
        }
        return new ParseResult<>(lastPage, commentlist);
    }
}
