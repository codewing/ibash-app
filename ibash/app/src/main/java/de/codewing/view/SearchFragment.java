package de.codewing.view;

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
import android.widget.Toast;

import de.codewing.controller.CustomListAdapter;
import de.codewing.ibash.R;
import de.codewing.sqlite.SQLiteHelper;
import de.codewing.utils.LikeOrDislike;
 
public class SearchFragment extends Fragment implements OnClickListener{

	CustomListAdapter listAdapter;
	EditText etPageNumber;
	EditText etSearchTerm;
	Integer pageNumber = 1;
	Button btNext;
	Button btReload;
	Button btPrevious;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        
        //Implement ListView + ListAdapter
        ListView listView = (ListView) rootView.findViewById(R.id.listView_quotes);
        listAdapter = new CustomListAdapter(inflater, getActivity(), null, listView);
        listView.setEmptyView(rootView.findViewById(R.id.empty_List));
		rootView.findViewById(R.id.loadingCircle).setVisibility(View.INVISIBLE);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(listAdapter);
		
		registerForContextMenu(listView);
        
		//Implement Buttons + TextEdits
		etSearchTerm = (EditText) rootView.findViewById(R.id.editText_search);
		rootView.findViewById(R.id.button_search).setOnClickListener(this);
		btNext = (Button) rootView.findViewById(R.id.button_next);
		btNext.setOnClickListener(this);
		btNext.setEnabled(false);
		btPrevious = (Button) rootView.findViewById(R.id.button_previous);
		btPrevious.setOnClickListener(this);
		btPrevious.setEnabled(false);
		btReload = (Button) rootView.findViewById(R.id.button_reload);
		btReload.setOnClickListener(this);
		
		etPageNumber = (EditText) rootView.findViewById(R.id.editText_pagenumber);
		
        return rootView;
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case(R.id.button_search):{
			Log.d("Search: ", etSearchTerm.getText().toString());
			pageNumber = 1;
			listAdapter.updateDatensaetze("" + pageNumber, btNext, etSearchTerm.getText().toString(), false);
			getActivity().findViewById(R.id.loadingCircle).setVisibility(View.VISIBLE);
			pageNumber = 1;
			etPageNumber.setText(pageNumber.toString());
			btNext.setEnabled(false);
		}break;
		
		case (R.id.button_next): {
			pageNumber++;
			listAdapter.updateDatensaetze("" + pageNumber, btNext, etSearchTerm.getText().toString(), false);
			etPageNumber.setText(pageNumber.toString());
			btNext.setEnabled(false);
		}break;

		case (R.id.button_previous): {
			pageNumber--;
			listAdapter.updateDatensaetze(pageNumber.toString(), btNext, etSearchTerm.getText().toString(), false);
			etPageNumber.setText(pageNumber.toString());
			btNext.setEnabled(false);
		}break;

		case (R.id.button_reload):{
			if(!etPageNumber.getText().toString().isEmpty())
				pageNumber = Integer.parseInt(etPageNumber.getText().toString());
			listAdapter.updateDatensaetze(pageNumber.toString(), btNext, etSearchTerm.getText().toString(), false);
			etPageNumber.setText(pageNumber.toString());
		}break;

		}

		// Buttons checken
		// Previous Button
		if (pageNumber == 1 || pageNumber == 0) {
			btPrevious.setEnabled(false);
		} else {
			btPrevious.setEnabled(true);
		}
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
	    int itemid = listAdapter.getItem(index).getId();
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
			sendIntent.putExtra(Intent.EXTRA_TEXT, listAdapter.getItem(index).getQuotetext() + "\n"+getActivity().getResources().getString(R.string.shared_via));
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