package de.codewing.fragments;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import de.codewing.ibash.R;

 
public class SettingsActivity extends PreferenceActivity {
    
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle paramBundle)
	{
	  super.onCreate(paramBundle);
	  addPreferencesFromResource(R.xml.fragment_settings);
	  getActionBar().setTitle(getResources().getString(R.string.menu_settings));
	  getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}