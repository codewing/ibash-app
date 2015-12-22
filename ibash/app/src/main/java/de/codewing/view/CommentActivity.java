package de.codewing.view;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.codewing.controller.CommentAdapter;
import de.codewing.ibash.R;
import de.codewing.model.Comment;
import de.codewing.utils.ParseResult;
import de.codewing.utils.Parser;

public class CommentActivity extends AppCompatActivity {
	public ArrayList<Comment> commentlist = new ArrayList<Comment>();
	ListView lv;
	CommentAdapter commentadapter;
	Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_comment);
		activity = this;
		commentadapter = new CommentAdapter(this);
		lv = (ListView) findViewById(R.id.listView_comments);
		lv.setAdapter(commentadapter);
		lv.setEmptyView(findViewById(R.id.loadingCircle));
		
		updateComments(getIntent().getExtras().getInt("key_quote_id"));

		ActionBar actionbar = getSupportActionBar();
		actionbar.setHomeButtonEnabled(true);
		actionbar.setTitle("#"+getIntent().getExtras().getInt("key_quote_id"));
		
		Log.d("Commentlist-2", ""+commentlist.size());
	}

	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	        return true;
	    default: return super.onOptionsItemSelected(item);  
	    }
	}
	
	

	private void updateComments(int id) {
		HTTPDownloadTask dl = new HTTPDownloadTask();
		dl.execute("http://www.ibash.de/iphone/comments.php?id="+id);
	}

	private class HTTPDownloadTask extends AsyncTask<String, Integer, String> {

		String result="";
		
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

			Log.d("Downloaded", result);
			return result;
		}

		// Wenn die Daten heruntergeladen wurden in die Zitate reinstecken
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Log.d("Comments:", "result: " + result);
			// Wenn nichts heruntergeladen wurde dann auch nichts setzen
			lv.setEmptyView(findViewById(R.id.empty_List));
			// Visibility 8 = Invis + no space
			findViewById(R.id.loadingCircle).setVisibility(View.INVISIBLE);

			try {
				ParseResult<Comment> parseResult = Parser.parseComments(result);
				commentlist.clear();
				commentlist.addAll(parseResult.getElements());

				commentadapter.notifyDataSetChanged();

			} catch (Exception e) {
				Log.d(this.getClass().getName() + "#onPostExecute", "Error: " + e.getMessage());
			}
			
		}

	}
}
