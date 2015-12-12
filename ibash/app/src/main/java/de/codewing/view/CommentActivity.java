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

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.codewing.controller.CommentAdapter;
import de.codewing.ibash.R;
import de.codewing.model.Comment;
import de.codewing.model.GsonComment;

public class CommentActivity extends AppCompatActivity {
	ListView lv;
	CommentAdapter commentadapter;
	Activity activity;
	public ArrayList<Comment> commentlist = new ArrayList<Comment>();
	
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
			Log.d("Comments:", "result: "+result);
			// Wenn nichts heruntergeladen wurde dann auch nichts setzen
			lv.setEmptyView(findViewById(R.id.empty_List));
			// Visibility 8 = Invis + no space
			findViewById(R.id.loadingCircle).setVisibility(View.INVISIBLE);

			// Quotes umformen
			Gson gson = new Gson();
			try {
				JsonReader reader = new JsonReader(new StringReader(result));
				reader.setLenient(true);
				
				GsonComment gcomments = gson.fromJson(reader, GsonComment.class);
				
				// Leere Quotelist erstellen
				commentlist = new ArrayList<Comment>();

				
				// add quotes into the table
				for (int i = 0; i < gcomments.comments.size(); i++) {
					Log.d("C-Tag", "4."+i + "\n"+ gcomments.comments.get(i).getText());
					String nick = gcomments.comments.get(i).getNick();
					String ts = gcomments.comments.get(i).getTs();
					ts.replace(" ", " - ");
					String text = gcomments.comments.get(i).getText();
					text = text.replace("[newline]", "\n");
					Comment comment = new Comment();
					comment.setNick(nick);
					comment.setTs(ts);
					comment.setText(text+"\n");
					commentlist.add(comment);
				}
				commentadapter.notifyDataSetChanged();

			} catch (Exception e) {
				Log.d("ListAdapter-Comment", "Error: " + e.getMessage());
			}
			
		}

	}
}
