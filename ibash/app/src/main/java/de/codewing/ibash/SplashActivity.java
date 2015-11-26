package de.codewing.ibash;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SplashActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// Listener hinzufügen
		Button connect = (Button) findViewById(R.id.button_splash_connect);
		connect.setOnClickListener(this);
		Button quit = (Button) findViewById(R.id.button_splash_quit);
		quit.setOnClickListener(this);
		final Context context_sa = this;

		// Settings auslesen
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean autostart = sharedPref.getBoolean("pref_key_autostart", true);

		if (autostart) {
			quit.setEnabled(false);
			connect.setEnabled(false);

			new CountDownTimer(3000, 1000) {

				public void onTick(long millisUntilFinished) {
				}

				public void onFinish() {
					if (isOnline()) {
						// If the App has a connection to the Webz start it
						Intent launchMain = new Intent(context_sa,
								MainActivity.class);
						startActivity(launchMain);
						finish();
					} else {
						Button quit = (Button) findViewById(R.id.button_splash_quit);
						Button connect = (Button) findViewById(R.id.button_splash_connect);
						quit.setEnabled(true);
						connect.setEnabled(true);
						Toast.makeText(
								context_sa,
								getResources()
										.getString(R.string.no_connection),
								Toast.LENGTH_SHORT).show();
					}
				}
			}.start();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case (R.id.button_splash_connect): {
			if (isOnline()) {
				// If the App has a connection to the Webz start it
				Intent launchMain = new Intent(this, MainActivity.class);
				startActivity(launchMain);
				this.finish();
			} else {
				Toast.makeText(this,
						getResources().getString(R.string.no_connection),
						Toast.LENGTH_SHORT).show();
			}
		}
			break;
		case (R.id.button_splash_quit): {
			finish();
		}
			break;
		}
	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

}
