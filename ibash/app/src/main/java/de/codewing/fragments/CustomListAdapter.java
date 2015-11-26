package de.codewing.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import de.codewing.ibash.CommentActivity;
import de.codewing.ibash.R;
import de.codewing.sqlite.FaviQuote;
import de.codewing.sqlite.SQLiteHelper;

public class CustomListAdapter extends BaseAdapter implements OnItemClickListener, OnItemLongClickListener {
	private final LayoutInflater mInflater;
	private CustomListAdapter mAdapter = this;
	private final String type;
	private Activity activity;
	private ListView listview;
	public int lastpage = 0;
	Button bt_next;
	public Quote gewaehlterDatensatz = new Quote(1337, "NOW", 1337, "Errorquote");
	public int page;

	public CustomListAdapter(LayoutInflater linflater, Activity activity,
			String type, ListView lv) {
		// mInflater = (LayoutInflater) activity.getLayoutInflater(); //
		// MyActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mInflater = linflater;
		this.type = type;
		this.activity = activity;
		this.listview = lv;
		lv.setDivider(null);
	}

	public int getCount() {
		return quotelist.size();
	}

	public Quote getItem(int position) {
		return quotelist.get(position);
	}

	public long getItemId(int position) {
		return (long) position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout itemView = (LinearLayout) mInflater.inflate(R.layout.listitem_std, parent, false);
		bindView(itemView, position);
		return itemView;
	}

	// Add values to an item
	private void bindView(LinearLayout view, int position) {
		Quote quote = getItem(position);
		view.setId((int) getItemId(position));
		TextView tv_id = (TextView) view.findViewById(R.id.ident);
		TextView tv_ts = (TextView) view.findViewById(R.id.timestamp);
		TextView tv_rating = (TextView) view.findViewById(R.id.rating);
		TextView tv_quote = (TextView) view.findViewById(R.id.quote);
		tv_id.setText("" + quote.getIdent());
		tv_ts.setText(quote.getTs());
		tv_rating.setText("" + quote.getRating());
		tv_quote.setText(quote.getQuotetext()+"\n");

		// Colorize rating?
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(activity);
		boolean colorize = sharedPref.getBoolean("pref_key_colorizerating",
				true);
		if (colorize) {
			if (quote.getRating() < 0) {
				tv_rating.setTextColor(Color.RED);
			}
			if (quote.getRating() == 0) {
				tv_rating.setTextColor(Color.YELLOW);
			}
			if (quote.getRating() > 0) {
				tv_rating.setTextColor(Color.rgb(50, 205, 50));
			}

		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// Meldung ausgeben oder Intent bauen und Activity starten
		Log.d("Clicker", "ListItem clicked: "+id);
		gewaehlterDatensatz = quotelist.get(position);
		Bundle quote = new Bundle();
		quote.putInt("key_quote_id", gewaehlterDatensatz.getIdent());
		quote.putString("key_quote_ts", gewaehlterDatensatz.getTs());
		quote.putInt("key_quote_rating", gewaehlterDatensatz.getRating());
		quote.putString("key_quote_content", gewaehlterDatensatz.getQuotetext());
		Intent launchComment = new Intent(activity, CommentActivity.class);
		launchComment.putExtras(quote);
		activity.startActivity(launchComment);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		Log.d("Clicker", "ListItem LONGclicked: "+id);
		return false;
	}

	// Datensatzliste
	private ArrayList<Quote> quotelist = new ArrayList<Quote>();

	public void updateDatensaetze(String pagenumber, Button bt_next) {
		this.bt_next = bt_next;
		// Loading circle setzen
		listview.setEmptyView(activity.findViewById(R.id.loadingCircle));

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(activity);
		String displayed_items = sharedPref.getString("pref_key_numberofitems",
				"10");

		// Quotes herunterladen
		HTTPDownloadTask dl = new HTTPDownloadTask();
		dl.type = type;
		dl.execute("http://www.ibash.de/iphone/quotes2.php?order=" + type
				+ "&number=" + displayed_items + "&page=" + pagenumber);
	}
	
	public void updateFavourites(Button bt_next) {
		this.bt_next = bt_next;
		// Loading circle setzen
		listview.setEmptyView(activity.findViewById(R.id.loadingCircle));

		// Quotes herunterladen
		HTTPDownloadTaskFavi dl = new HTTPDownloadTaskFavi();
		dl.execute("http://www.ibash.de/iphone/quotearray.php");
	}

	public void updateDatensaetze(String pagenumber, Button bt_next, String searchterm,
			Boolean warte) {
		this.bt_next = bt_next;
		// Loading circle setzen
		listview.setEmptyView(activity.findViewById(R.id.loadingCircle));
		// Visibility 0 = visible
		// activity.findViewById(R.id.loadingCircle).setVisibility(0);

		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(activity);
		String displayed_items = sharedPref.getString("pref_key_numberofitems",
				"10");

		// Quotes herunterladen
		HTTPDownloadTask dl = new HTTPDownloadTask();
		dl.istWarte = warte;
		if (warte) {
			dl.execute("http://www.ibash.de/iphone/warte.php");
		} else {
			dl.execute("http://www.ibash.de/iphone/query.php?term="+ searchterm + "&number=" + displayed_items + "&page="+ pagenumber);
		}

	}

	public void restoreRandomQuoteList(View loadingCircle) {
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(activity);
		String result = sharedPref.getString("pref_key_randomtab_data", null);

		// Wenn nichts heruntergeladen wurde dann auch nichts setzen
		listview.setEmptyView(activity.findViewById(R.id.empty_List));
		// Visibility 8 = Invis + no space
		loadingCircle.setVisibility(View.INVISIBLE);

		// Quotes umformen
		Gson gson = new Gson();
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

			mAdapter.notifyDataSetChanged();

		} catch (Exception e) {
			Toast.makeText(activity, "Error: " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}

	private class HTTPDownloadTask extends AsyncTask<String, Integer, String> {

		String result = "";
		String type = "new";
		boolean istWarte = false;
		

		@Override
		protected String doInBackground(String... params) {
			String urlStr = params[0];
			
			Log.d("istWarte", ""+istWarte);
			
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

			Log.d("Downloaded", result);
			return result;
		}

		// Wenn die Daten heruntergeladen wurden in die Zitate reinstecken
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Wenn nichts heruntergeladen wurde dann auch nichts setzen
			listview.setEmptyView(activity.findViewById(R.id.empty_List));
			// Visibility 8 = Invis + no space
			activity.findViewById(R.id.loadingCircle).setVisibility(View.INVISIBLE);

			// Quotes umformen
			Gson gson = new Gson();
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

				mAdapter.notifyDataSetChanged();
				Log.d("type - nachher download", type);
				if (type.equals("random")) {
					Editor edit = PreferenceManager.getDefaultSharedPreferences(activity).edit();
					edit.putString("pref_key_randomtab_data", result);
					edit.commit();
				}

				// Wenn es die Warteschlange ist gibt es keine letzte Seite!
				// Random eigentlich auch aber fail gecoded :D
				Log.d("istWarte - nach dl", ""+istWarte);
				if (!istWarte && !type.equalsIgnoreCase("random")) {
					lastpage = Integer.parseInt(gquotes.Inhalte.last_page);
					if(lastpage == 1){
						bt_next.setEnabled(false);
					}else{
						bt_next.setEnabled(true);
					}
				}
				listview.setSelection(0);

			} catch (Exception e) {
				Log.d("ListAdapter", "Error: " + e.getMessage());
			}

			/*
			 * Editor edit =
			 * PreferenceManager.getDefaultSharedPreferences(activity).edit();
			 * edit.putString("pref_key_randomtab_data", result); edit.commit();
			 * 
			 * SharedPreferences sharedPref =
			 * PreferenceManager.getDefaultSharedPreferences(activity); String
			 * res_text = sharedPref.getString("pref_key_randomtab_data", null);
			 */

		}

	}
	
	private class HTTPDownloadTaskFavi extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			String urlStr = params[0];
			
			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(activity);
			String displayed_items = sharedPref.getString("pref_key_numberofitems",
					"10");
			
			SQLiteHelper database = new SQLiteHelper(activity);
			List<FaviQuote> fqlist = database.getFaviQuotes(Integer.parseInt(displayed_items), page);
			
			String ids = "";
			for(int i = 0; i < fqlist.size(); i++){
				if(i == 0){
					ids += fqlist.get(i).getId();
				}else{
					ids += "," +fqlist.get(i).getId();
				}
			}
			
			Log.d("Favlist-A", "IDS: "+ids);
			
			
			// Create data variable for sent values to server 
	           
            String data = null;
			try {
				data = URLEncoder.encode("ids", "iso-8859-1")
				             + "=" + URLEncoder.encode(ids, "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
 
            
            String result = "";
            BufferedReader reader=null;
 
            // Send data
          try
          {
            
              // Defined URL  where to send data
              URL url = new URL(urlStr);
               
           // Send POST data request
 
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();
        
            // Get the server response
             
          reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"iso-8859-1"));
          StringBuilder sb = new StringBuilder();
          String line = null;
          
          // Read Server Response
          while((line = reader.readLine()) != null)
              {
                     // Append server response in string
                     sb.append(line + "\n");
              }
              
              
              result = sb.toString();
          }
          catch(Exception ex)
          {
               
          }
          finally
          {
              try
              {
   
                  reader.close();
              }
 
              catch(Exception ex) {}
          }

			Log.d("Downloaded", result);
			return result;
		}

		// Wenn die Daten heruntergeladen wurden in die Zitate reinstecken
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d("Favlist-A", "result: "+result);
			// Wenn nichts heruntergeladen wurde dann auch nichts setzen
			listview.setEmptyView(activity.findViewById(R.id.empty_List));
			// Visibility 8 = Invis + no space
			activity.findViewById(R.id.loadingCircle).setVisibility(View.INVISIBLE);

			// Quotes umformen
			Gson gson = new Gson();
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

				mAdapter.notifyDataSetChanged();
				listview.setSelection(0);

			} catch (Exception e) {
				Log.d("ListAdapter", "Error: " + e.getMessage());
			}

			/*
			 * Editor edit =
			 * PreferenceManager.getDefaultSharedPreferences(activity).edit();
			 * edit.putString("pref_key_randomtab_data", result); edit.commit();
			 * 
			 * SharedPreferences sharedPref =
			 * PreferenceManager.getDefaultSharedPreferences(activity); String
			 * res_text = sharedPref.getString("pref_key_randomtab_data", null);
			 */

		}

	}

}
