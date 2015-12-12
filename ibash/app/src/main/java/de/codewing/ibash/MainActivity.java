package de.codewing.ibash;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;

import de.codewing.view.AboutFragment;
import de.codewing.view.BestFragment;
import de.codewing.view.ChangeLogDialog;
import de.codewing.view.FavouritesFragment;
import de.codewing.view.NewFragment;
import de.codewing.view.PreferenceFragment;
import de.codewing.view.QueueFragment;
import de.codewing.view.RandomFragment;
import de.codewing.view.SearchFragment;
 
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Declare Variables
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mDrawerToggle;
    Fragment fragment_new = new NewFragment();
    Fragment fragment_best = new BestFragment();
    Fragment fragment_random = new RandomFragment();
    Fragment fragment_search = new SearchFragment();
    Fragment fragment_queue = new QueueFragment();
    Fragment fragment_favourites = new FavouritesFragment();
    Fragment fragment_about = new AboutFragment();
    Fragment fragment_settings = new PreferenceFragment();
    
    //handler for settings display bug
    int lastItem = 0;
    
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from drawer_main.xml
        setContentView(R.layout.drawer_main);
 
        // Get the Title
        mTitle = mDrawerTitle = getTitle();

        // Locate DrawerLayout in drawer_main.xml
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
 
        // Locate ListView in drawer_main.xml
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
 
        // Set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);

        mNavigationView.setNavigationItemSelectedListener(this);
 
        // Enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
 
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close) {
 
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }
 
            public void onDrawerOpened(View drawerView) {
                // Set the title on the action when drawer open
                getSupportActionBar().setTitle(mDrawerTitle);
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //get last state
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            //fresh start of the app
            lastItem = Integer.parseInt(sharedPref.getString("pref_key_home_fragment", "0"));
            selectItem(lastItem);
        }else{
            //orientation changes
            selectItem(sharedPref.getInt("currently_selected_fragment",0));
        }

        int versionCodePreferences = sharedPref.getInt("changelog_versioncode",-1);
        int versionCodeCurrent;
        try{
            versionCodeCurrent = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        }catch (PackageManager.NameNotFoundException e){
            Log.e("ChangeLogDialog", "VersionCode not found! Error: " + e);
            versionCodeCurrent = -1;
        }
        if(versionCodeCurrent != versionCodePreferences){
            ChangeLogDialog changeLogDialog = new ChangeLogDialog();
            changeLogDialog.show(getFragmentManager(),"fragment_changelogdialog");
        }
    }

 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        if (item.getItemId() == android.R.id.home) {
 
            if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawer(mNavigationView);
            } else {
                mDrawerLayout.openDrawer(mNavigationView);
            }
        }
 
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectItem(item.getItemId());
        return false;
    }

 
    @Override
	protected void onResume() {
		super.onResume();
		selectItem(lastItem);
        Log.d("main", "orientation changed");
    }




	private void selectItem(int itemID) {

        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);

        int selectedPosition = sharedPref.getInt("currently_selected_fragment", 0);
        setNavSelectionState(selectedPosition, false);

		
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Locate Position
        switch (itemID) {
            case 0:
            case R.id.drawer_item_new:
                itemID = 0;
                ft.replace(R.id.content_frame, fragment_new);
                break;

            case 1:
            case R.id.drawer_item_best:
                itemID = 1;
                ft.replace(R.id.content_frame, fragment_best);
                break;

            case 2:
            case R.id.drawer_item_random:
                itemID = 2;
                ft.replace(R.id.content_frame, fragment_random);
                break;

            case 3:
            case R.id.drawer_item_search:
                itemID = 3;
                ft.replace(R.id.content_frame, fragment_search);
                break;

            case 4:
            case R.id.drawer_item_queue:
                itemID = 4;
                ft.replace(R.id.content_frame, fragment_queue);
                break;

            case 5:
            case R.id.drawer_item_favourites:
                itemID = 5;
                ft.replace(R.id.content_frame, fragment_favourites);
                break;

            case 6:
            case R.id.drawer_item_settings:{
                itemID = 6;
                ft.replace(R.id.content_frame, fragment_settings);
            }break;

            case 7:
            case R.id.drawer_item_about:
                itemID = 7;
                ft.replace(R.id.content_frame, fragment_about);
                break;

            case 8:
            case R.id.drawer_item_quit:{
                itemID = 8;
                //ft.replace(R.id.content_frame, fragment_quit);
                finish();
            }break;
        }
        ft.commit();
        lastItem = itemID;

        setNavSelectionState(itemID, true);

        sharedPref.edit().putInt("currently_selected_fragment", itemID).apply();

        // Close drawer
        mDrawerLayout.closeDrawer(mNavigationView);
    }

    private void setNavSelectionState(int pos, boolean state){
        int[] menuPath = posToPath(pos);
        if(state){
            mNavigationView.getMenu().getItem(menuPath[0]).getSubMenu().getItem(menuPath[1]).setChecked(true);
            // Get the title of the selected item
            setTitle(mNavigationView.getMenu().getItem(menuPath[0]).getSubMenu().getItem(menuPath[1]).getTitle());
        }else{
            mNavigationView.getMenu().getItem(menuPath[0]).getSubMenu().getItem(menuPath[1]).setChecked(false);
        }
    }

    private int[] posToPath(int pos){
        int[] index = new int[2];

        if(pos <= 5){
            index[0] = 0;
            index[1] = pos;
        }else{
            index[0] = 1;
            index[1] = pos - 6;
        }
        return index;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() ==  82 ){
			if (mDrawerLayout.isDrawerOpen(mNavigationView)) {
                mDrawerLayout.closeDrawer(mNavigationView);
            } else {
                mDrawerLayout.openDrawer(mNavigationView);
            }
		}
		return super.onKeyDown(keyCode, event);
	}
    
}
