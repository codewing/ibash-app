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
import android.widget.ListView;

import de.codewing.controller.CustomListAdapter;
import de.codewing.ibash.R;
import de.codewing.utils.LikeOrDislike;
 
public class QueueFragment extends Fragment implements OnClickListener{
	
	CustomListAdapter cla;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        
        ListView l = (ListView) rootView.findViewById(R.id.listView_quotes);
        cla = new CustomListAdapter(inflater, getActivity(), null, l);
        cla.updateDatensaetze(null, null, null, true);
		l.setAdapter(cla);
		l.setOnItemClickListener(cla);
		
		registerForContextMenu(l);
        
		rootView.findViewById(R.id.button_reload).setOnClickListener(this);
		
        return rootView;
    }

	@Override
	public void onClick(View v) {
		cla.updateDatensaetze(null, null, null, true);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(getResources().getString(R.string.context_header));
		getActivity().getMenuInflater().inflate(R.menu.listitem_context_queue, menu);
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
			sendIntent.putExtra(Intent.EXTRA_TEXT, cla.getItem(index).getContent() + "\n" + getActivity().getResources().getString(R.string.shared_via));
			sendIntent.setType("text/plain");
			startActivity(sendIntent);
			return true;
		}

		}
		return super.onContextItemSelected(item);
	}
}