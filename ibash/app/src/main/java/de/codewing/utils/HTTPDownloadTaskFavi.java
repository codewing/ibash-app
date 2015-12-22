package de.codewing.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import de.codewing.controller.callbacks.QuotesCallback;
import de.codewing.model.Quote;

/**
 * Created by codewing on 03.12.2015.
 */
public class HTTPDownloadTaskFavi extends AsyncTask<String, Integer, String> {
    QuotesCallback callbackObj;
    public HTTPDownloadTaskFavi(QuotesCallback callbackObj) {
        this.callbackObj = callbackObj;
    }

    @Override
    protected String doInBackground(String... params) {
        // Create quotes variable for sent values to server
        String data = null;
        try {
            data = URLEncoder.encode("ids", "iso-8859-1")
                    + "=" + URLEncoder.encode(params[0], "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        String result = "";
        BufferedReader reader = null;

        // Send quotes
        try {
            // Defined URL  where to send quotes
            URL url = new URL("http://www.ibash.de/iphone/quotearray.php");

            // Send POST quotes request
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

    // Parse quotes and update the quotelist
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParseResult<Quote> parseResult = Parser.parseQuotes(result);
        callbackObj.onReceiveQuotes(parseResult.getElements());
    }


}
