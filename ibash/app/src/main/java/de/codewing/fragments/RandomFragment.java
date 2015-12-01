package de.codewing.fragments;

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
import android.widget.ListView;
import android.widget.Toast;

import de.codewing.ibash.LikeOrDislike;
import de.codewing.ibash.R;
import de.codewing.sqlite.SQLiteHelper;
 
public class RandomFragment extends Fragment implements OnClickListener {
	
	CustomListAdapter cla;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_random, container, false);
        
        ListView l = (ListView) rootView.findViewById(R.id.listView_quotes);
        cla = new CustomListAdapter(inflater, getActivity(), "random", l);
        l.setEmptyView(rootView.findViewById(R.id.loadingCircle));
		l.setAdapter(cla);
		l.setOnItemClickListener(cla);
		
		registerForContextMenu(l);
		
		rootView.findViewById(R.id.button_reroll).setOnClickListener(this);
		
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String result = sharedPref.getString("pref_key_randomtab_data", null);
		if(result == null){
			cla.updateDatensaetze(""+1, null);
		}else{
			cla.restoreRandomQuoteList(rootView.findViewById(R.id.loadingCircle));
		}
		
        
        return rootView;
    }

	@Override
	public void onClick(View v) {
		cla.updateDatensaetze(""+1, null);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(getResources().getString(R.string.context_header));
		getActivity().getMenuInflater().inflate(R.menu.listitem_context, menu);
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

		case R.id.context_addtofavi: {
			Log.d("Context chosen", "toFavi");
			SQLiteHelper database = new SQLiteHelper(getActivity());
			if(database.getSingleQuote(itemid) == null){
				database.addFaviQuote(itemid);
				Log.d("toFavi", "successfully added");
				Toast.makeText(getActivity(), getResources().getString(R.string.database_added), Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(getActivity(), getResources().getString(R.string.database_already_added), Toast.LENGTH_SHORT).show();
			}
			
			return true;
		}

		}
		return super.onContextItemSelected(item);
	}
}