package de.codewing.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import de.codewing.controller.CustomListAdapter;
import de.codewing.ibash.R;
import de.codewing.sqlite.SQLiteHelper;
import de.codewing.utils.LikeOrDislike;

public class FavouritesFragment extends Fragment implements
		OnClickListener {

	CustomListAdapter cla;
	EditText et_pagenumber;
	int pagenumber = 1;
	Button bt_next;
	Button bt_reload;
	Button bt_previous;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_new_best_favi,
				container, false);

		ListView l = (ListView) rootView.findViewById(R.id.listView_quotes);
		cla = new CustomListAdapter(inflater, getActivity(), "favourites", l);
		l.setEmptyView(rootView.findViewById(R.id.loadingCircle));
		l.setAdapter(cla);
		l.setOnItemClickListener(cla);

		registerForContextMenu(l);

		// Enable clicks
		bt_next = (Button) rootView.findViewById(R.id.button_next);
		bt_next.setOnClickListener(this);
		bt_next.setEnabled(false);
		bt_previous = (Button) rootView.findViewById(R.id.button_previous);
		bt_previous.setOnClickListener(this);
		rootView.findViewById(R.id.button_reload).setOnClickListener(this);

		et_pagenumber = (EditText) rootView
				.findViewById(R.id.editText_pagenumber);

		updateListButtonsTextEdit();

		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.button_next): {
			pagenumber++;
		}
			break;

		case (R.id.button_previous): {
			pagenumber--;
		}
			break;

		case (R.id.button_reload): {
			if (!et_pagenumber.getText().toString().equals(""))
				pagenumber = Integer.parseInt(et_pagenumber.getText()
						.toString());
		}
			break;

		}

		updateListButtonsTextEdit();
	}

	private void updateListButtonsTextEdit(){
		// Immer aktualisieren und immer Daten Speichern
		cla.updateFavourites(bt_next, pagenumber);
		et_pagenumber.setText("" + pagenumber);

		// Buttons checken
		// Previous Button
		if (pagenumber == 1 || pagenumber == 0) {
			bt_previous.setEnabled(false);
		} else {
			bt_previous.setEnabled(true);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(getResources().getString(R.string.context_header));
		getActivity().getMenuInflater().inflate(R.menu.listitem_context_favi, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    int index = info.position;
	    int itemid = cla.getItem(index).getId();
		Log.d("Context opened", "onSelect");
		LikeOrDislike lod = new LikeOrDislike(getActivity());
		switch (item.getItemId()) {
		
		case R.id.context_like: {
			lod.execute("http://www.ibash.de/iphone/rate.php?type=pos&id="+itemid);
			return true;
		}

		case R.id.context_dislike: {
			lod.execute("http://www.ibash.de/iphone/rate.php?type=neg&id="+itemid);
			return true;
		}

		case R.id.context_share: {
			Log.d("Context chosen", "share");
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, cla.getItem(index).getQuotetext() + "\n"+getActivity().getResources().getString(R.string.shared_via));
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			return true;
		}

		case R.id.context_removefromfavi: {
			Log.d("Context chosen", "delete Favi");
			SQLiteHelper database = new SQLiteHelper(getActivity());
			database.removeFaviQuote(itemid);

			SharedPreferences sharedPref = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			int displayed_items = Integer.parseInt(sharedPref.getString("pref_key_numberofitems", "10"));
			if(database.getFaviCount() <= displayed_items * (pagenumber-1)){
				pagenumber--;
			}
			updateListButtonsTextEdit();
			
			return true;
		}

		}
		return super.onContextItemSelected(item);
	}
}
