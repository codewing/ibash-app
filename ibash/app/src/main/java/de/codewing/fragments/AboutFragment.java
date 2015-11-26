package de.codewing.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import de.codewing.ibash.R;
 
public class AboutFragment extends Fragment implements OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        
       	rootView.findViewById(R.id.imageButton_blog).setOnClickListener(this);
       	rootView.findViewById(R.id.imageButton_facebook).setOnClickListener(this);
       	rootView.findViewById(R.id.imageButton_twitter).setOnClickListener(this);
        
        return rootView;
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case(R.id.imageButton_blog):{
			Uri uri = Uri.parse("http://www.codewing.de");
			Intent go = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(go);
		}break;
		case(R.id.imageButton_facebook):{
			Uri uri = Uri.parse("https://www.facebook.com/codewing.de");
			Intent go = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(go);
		}break;
		case(R.id.imageButton_twitter):{
			Uri uri = Uri.parse("https://twitter.com/striker503");
			Intent go = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(go);
		}break;
		}
		
	}
 
}