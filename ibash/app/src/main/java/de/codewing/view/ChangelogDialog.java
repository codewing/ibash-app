package de.codewing.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import de.codewing.ibash.R;

/**
 * Created by codewing on 13.12.2015.
 */
public class ChangeLogDialog extends DialogFragment  {

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Changelog")
                .setMessage(R.string.update_news)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPref = PreferenceManager
                                .getDefaultSharedPreferences(getActivity());
                        int versionCodeCurrent;
                        try{
                            versionCodeCurrent = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                        }catch (PackageManager.NameNotFoundException e){
                            Log.e("ChangeLogDialog", "VersionCode not found! Error: " + e);
                            versionCodeCurrent = -1;
                        }

                        sharedPref.edit().putInt("changelog_versioncode", versionCodeCurrent).apply();
                    }
                }).create();
    }
}
