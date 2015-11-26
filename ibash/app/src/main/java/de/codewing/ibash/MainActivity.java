package de.codewing.ibash;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import de.codewing.fragments.AboutFragment;
import de.codewing.fragments.BestFragment;
import de.codewing.fragments.FavouritesFragment;
import de.codewing.fragments.NewFragment;
import de.codewing.fragments.PreferenceFragment;
import de.codewing.fragments.QueueFragment;
import de.codewing.fragments.RandomFragment;
import de.codewing.fragments.SearchFragment;
 
public class MainActivity extends AppCompatActivity {

    // Declare Variables
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    MenuListAdapter mMenuAdapter;
    String[] title;
    int[] icon;
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
        
        Resources r = getResources();
 
        // Generate title
        title = new String[] { r.getString(R.string.menu_new), r.getString(R.string.menu_best), r.getString(R.string.menu_random), r.getString(R.string.menu_search), r.getString(R.string.menu_queue), r.getString(R.string.menu_favourites), r.getString(R.string.menu_settings), r.getString(R.string.menu_about), r.getString(R.string.menu_quit) };

        // Generate icon
        icon = new int[] {R.drawable.ic_plus_one, R.drawable.ic_star, R.drawable.ic_dice, R.drawable.ic_magnify, R.drawable.ic_image_filter_none, R.drawable.ic_heart,R.drawable.ic_settings, R.drawable.ic_information_outline, R.drawable.ic_power };
 
        // Locate DrawerLayout in drawer_main.xml
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
 
        // Locate ListView in drawer_main.xml
        mDrawerList = (ListView) findViewById(R.id.listview_drawer);
 
        // Set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
 
        // Pass string arrays to MenuListAdapter
        mMenuAdapter = new MenuListAdapter(MainActivity.this, title, icon);
 
        // Set the MenuListAdapter to the ListView
        mDrawerList.setAdapter(mMenuAdapter);
 
        // Capture listview menu item click
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
 
        // Enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
 
        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
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
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        if (item.getItemId() == android.R.id.home) {
 
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
 
        return super.onOptionsItemSelected(item);
    }
 
    // ListView click listener in the navigation drawer
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            selectItem(position);
        }
    }
 
    @Override
	protected void onResume() {
		super.onResume();
		selectItem(lastItem);
        Log.d("main", "orientation changed");
    }




	private void selectItem(int position) {
		
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Locate Position
        switch (position) {
        case 0:
            ft.replace(R.id.content_frame, fragment_new);
            break;
        case 1:
            ft.replace(R.id.content_frame, fragment_best);
            break;
        case 2:
            ft.replace(R.id.content_frame, fragment_random);
            break;
        case 3:
            ft.replace(R.id.content_frame, fragment_search);
            break;
        case 4:
            ft.replace(R.id.content_frame, fragment_queue);
            break;
        case 5:
            ft.replace(R.id.content_frame, fragment_favourites);
            break;
        case 6:{
            ft.replace(R.id.content_frame, fragment_settings);
        }break;
        case 7:
            ft.replace(R.id.content_frame, fragment_about);
            break;
        case 8:{
            //ft.replace(R.id.content_frame, fragment_quit);
        	finish();
        }break;
        }
        ft.commit();
        lastItem = position;
        mDrawerList.setItemChecked(position, true);

        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        sharedPref.edit().putInt("currently_selected_fragment", position).commit();
 
        // Get the title followed by the position
        setTitle(title[position]);
        // Close drawer
        mDrawerLayout.closeDrawer(mDrawerList);
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
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
		}
		return super.onKeyDown(keyCode, event);
	}
    
}
