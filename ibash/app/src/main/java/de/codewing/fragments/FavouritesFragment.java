package de.codewing.fragments;

import android.content.Intent;
import android.os.Bundle;
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

import de.codewing.ibash.LikeOrDislike;
import de.codewing.ibash.R;
import de.codewing.sqlite.SQLiteHelper;

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
		View rootView = inflater.inflate(R.layout.fragment_favourites,
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

		cla.updateFavourites(bt_next);

		// Buttons checken
		// Previous Button
		if (pagenumber == 1 || pagenumber == 0) {
			bt_previous.setEnabled(false);
		} else {
			bt_previous.setEnabled(true);
		}

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
			if (!et_pagenumber.getText().toString().equals("")
					&& et_pagenumber.getText().toString() != null)
				pagenumber = Integer.parseInt(et_pagenumber.getText()
						.toString());
		}
			break;

		}
		// Immer aktualisieren und immer Daten Speichern
		cla.updateFavourites(bt_next);
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
	    int itemid = cla.getItem(index).getIdent();
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
			cla.updateFavourites(bt_next);
			
			return true;
		}

		}
		return super.onContextItemSelected(item);
	}
}