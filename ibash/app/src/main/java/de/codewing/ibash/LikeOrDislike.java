package de.codewing.ibash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LikeOrDislike extends AsyncTask<String, Integer, String>{
	
	Context context;
	String result = "";

	public LikeOrDislike(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		String urlStr = params[0];

		URL url;
		InputStream is = null;
		BufferedReader br;
		String line;

		try {
			url = new URL(urlStr);
			is = url.openStream(); // throws an IOException
			br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));

			while ((line = br.readLine()) != null) {
				result += line;
			}
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
				// nothing to see here
			}
		}
		Log.d("Download Like", result);
		return result;
	}
	
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if(!result.equalsIgnoreCase("Sie haben dieses Zitat bereits bewertet!")){
			Toast.makeText(context, context.getResources().getString(R.string.rating_success), Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(context, context.getResources().getString(R.string.already_rated), Toast.LENGTH_SHORT).show();
		}
	}

}
