package de.codewing.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import de.codewing.ibash.R;
 
public class AboutFragment extends Fragment implements OnClickListener{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

		TextView tvAboutText = (TextView) rootView.findViewById(R.id.textView_about);
		String versionName;
		try{
			versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
		}catch (PackageManager.NameNotFoundException e){
			Log.e("AboutFragment", "VersionName not found! Error: "+e);
			versionName = "not found";
		}
		tvAboutText.setText(tvAboutText.getText().toString().replace("INSERT_VERSION", versionName));

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