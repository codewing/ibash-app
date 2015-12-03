package de.codewing.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import de.codewing.model.Quote;
import de.codewing.view.GsonQuotes;

/**
 * Created by codewing on 03.12.2015.
 */
public class HTTPDownloadTaskFavi extends AsyncTask<String, Integer, String> {
    @Override
    protected String doInBackground(String... params) {
        // Create data variable for sent values to server
        String data = null;
        try {
            data = URLEncoder.encode("ids", "iso-8859-1")
                    + "=" + URLEncoder.encode(params[0], "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String result = "";
        BufferedReader reader = null;

        // Send data
        try {
            // Defined URL  where to send data
            URL url = new URL("http://www.ibash.de/iphone/quotearray.php");

            // Send POST data request
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "iso-8859-1"));
            StringBuilder sb = new StringBuilder();

            String line;
            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line);
                sb.append("\n");
            }


            result = sb.toString();
        } catch (Exception ex) {
            Log.e(this.getClass().getName(), ex.getMessage());
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                Log.e(this.getClass().getName(), ex.getMessage());
            }
        }

        Log.d(this.getClass().getName(), "Result: "+result);
        return result;
    }

    // Wenn die Daten heruntergeladen wurden in die Zitate reinstecken
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //parse Quotes
        Gson gson = new Gson();
        ArrayList quotelist;
        try {
            JsonReader reader = new JsonReader(new StringReader(result));
            reader.setLenient(true);
            GsonQuotes gquotes = gson.fromJson(reader, GsonQuotes.class);

            // Leere Quotelist erstellen
            quotelist = new ArrayList<Quote>();

            // add quotes into the table
            for (int i = 0; i < gquotes.Inhalte.data.size(); i++) {

                int ident = gquotes.Inhalte.data.get(i).ident;
                String ts = gquotes.Inhalte.data.get(i).ts;
                int rating = gquotes.Inhalte.data.get(i).rating;
                String content = gquotes.Inhalte.data.get(i).content;
                content = content.replace("[newline]", "\n");

                Quote quote = new Quote(ident, ts, rating, content);
                quotelist.add(quote);
            }

        } catch (Exception e) {
            Log.d("ListAdapter", "Error: " + e.getMessage());
        }
    }
}
