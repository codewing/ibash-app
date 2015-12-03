package de.codewing.view;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import de.codewing.ibash.R;

 
public class PreferenceFragment extends PreferenceFragmentCompat {

	@Override
	public void onCreatePreferences(Bundle bundle, String s) {
		addPreferencesFromResource(R.xml.fragment_settings);
	}
}