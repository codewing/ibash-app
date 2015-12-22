package de.codewing.controller;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.codewing.controller.callbacks.QuotesCallback;
import de.codewing.ibash.R;
import de.codewing.model.FaviQuote;
import de.codewing.model.Quote;
import de.codewing.sqlite.SQLiteHelper;
import de.codewing.utils.HTTPDownloadTaskFavi;
import de.codewing.utils.ParseResult;
import de.codewing.utils.Parser;
import de.codewing.view.CommentActivity;

public class CustomListAdapter extends BaseAdapter implements QuotesCallback, OnItemClickListener, OnItemLongClickListener {
    private final LayoutInflater mInflater;
    private final String type;
    public int lastpage = 0;
    public Quote gewaehlterDatensatz = new Quote(1337, "NOW", 1337, "Errorquote");
    public int page;
    Button bt_next;
    private CustomListAdapter mAdapter = this;
    private Activity activity;
    private ListView listview;
    // Datensatzliste
    private ArrayList<Quote> quotelist = new ArrayList();

    public CustomListAdapter(LayoutInflater linflater, Activity activity,
                             String type, ListView lv) {

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
        TextView tv_id = (TextView) view.findViewById(R.id.id);
        TextView tv_ts = (TextView) view.findViewById(R.id.timestamp);
        TextView tv_rating = (TextView) view.findViewById(R.id.rating);
        TextView tv_quote = (TextView) view.findViewById(R.id.quoteText);
        tv_id.setText("" + quote.getIdent());
        tv_ts.setText(quote.getTs());
        tv_rating.setText("" + quote.getRating());
        tv_quote.setText(quote.getContent() + "\n");

        // Colorize
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(activity);
        boolean greyText = sharedPref.getBoolean("pref_key_use_grey_text", true);
        if(!greyText){
            tv_quote.setTextColor(Color.BLACK);
        }

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
        Log.d("Clicker", "ListItem clicked: " + id);
        gewaehlterDatensatz = quotelist.get(position);
        Bundle quote = new Bundle();
        quote.putInt("key_quote_id", gewaehlterDatensatz.getIdent());
        quote.putString("key_quote_ts", gewaehlterDatensatz.getTs());
        quote.putInt("key_quote_rating", gewaehlterDatensatz.getRating());
        quote.putString("key_quote_content", gewaehlterDatensatz.getContent());
        Intent launchComment = new Intent(activity, CommentActivity.class);
        launchComment.putExtras(quote);
        activity.startActivity(launchComment);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Clicker", "ListItem LONGclicked: " + id);
        return false;
    }

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

    public void updateFavourites(Button bt_next, int pageNumber) {
        this.bt_next = bt_next;
        // Loading circle setzen
        listview.setEmptyView(activity.findViewById(R.id.loadingCircle));

        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(activity);
        int displayed_items = Integer.parseInt(sharedPref.getString("pref_key_numberofitems", "10"));
        SQLiteHelper sqLiteHelper = new SQLiteHelper(activity);
        int faviCount = sqLiteHelper.getFaviCount();
        if (pageNumber * displayed_items >= faviCount) {
            bt_next.setEnabled(false);
        } else {
            bt_next.setEnabled(true);
        }

        // Quotes herunterladen
        HTTPDownloadTaskFavi dl = new HTTPDownloadTaskFavi(this);
        dl.execute(getFaviIDs(pageNumber));
    }

    private String getFaviIDs(int pageNumber){
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(activity);
        String displayed_items = sharedPref.getString("pref_key_numberofitems",
                "10");

        SQLiteHelper database = new SQLiteHelper(activity);
        List<FaviQuote> fqlist = database.getFaviQuotes(Integer.parseInt(displayed_items), pageNumber);

        String ids = "";
        for (int i = 0; i < fqlist.size(); i++) {
            if (i == 0) {
                ids += fqlist.get(i).getIdent();
            } else {
                ids += "," + fqlist.get(i).getIdent();
            }
        }

        Log.d("Favlist-A", "IDS: " + ids);
        return ids;
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
            if(searchterm.startsWith("#") && searchterm.length() > 1){
                HTTPDownloadTaskFavi dlf = new HTTPDownloadTaskFavi(this);
                dlf.execute(searchterm.substring(1));
            }else{
                dl.execute("http://www.ibash.de/iphone/query.php?term=" + searchterm + "&number=" + displayed_items + "&page=" + pagenumber);
            }
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

        //Parse quotes
        try {
            if (result == null) return;

            ParseResult parseResult = Parser.parseQuotes(result);
            quotelist.clear();
            quotelist.addAll(parseResult.getElements());

            mAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(activity, "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onReceiveQuotes(ArrayList<Quote> quotes) {

        this.quotelist = quotes;
        // Wenn nichts heruntergeladen wurde dann auch nichts setzen
        listview.setEmptyView(activity.findViewById(R.id.empty_List));
        // Visibility 8 = Invis + no space
        activity.findViewById(R.id.loadingCircle).setVisibility(View.INVISIBLE);

        mAdapter.notifyDataSetChanged();

        listview.setSelection(0);
    }

    private class HTTPDownloadTask extends AsyncTask<String, Integer, String> {

        String result = "";
        String type = "new";
        boolean istWarte = false;


        @Override
        protected String doInBackground(String... params) {

            Log.d("istWarte", "" + istWarte);
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

            //Parse quotes
            try {
                ParseResult<Quote> parseResult = Parser.parseQuotes(result);
                boolean lastPage = parseResult.isLastPage();
                quotelist.clear();
                quotelist.addAll(parseResult.getElements());

                mAdapter.notifyDataSetChanged();

                Log.d("type - nachher download", type);
                if (type.equals("random")) {
                    Editor edit = PreferenceManager.getDefaultSharedPreferences(activity).edit();
                    edit.putString("pref_key_randomtab_data", result);
                    edit.commit();
                }

                // Wenn es die Warteschlange ist gibt es keine letzte Seite!
                // Random eigentlich auch aber fail gecoded :D
                Log.d("istWarte - nach dl", "" + istWarte);
                if (!istWarte && !type.equalsIgnoreCase("random")) {
                    if (lastPage) {
                        bt_next.setEnabled(false);
                    } else {
                        bt_next.setEnabled(true);
                    }
                }
                listview.setSelection(0);

            } catch (Exception e) {
                Log.d(this.getClass().getName() + "#onPostExecute", "Error: " + e.getMessage());
            }

        }

    }

}
