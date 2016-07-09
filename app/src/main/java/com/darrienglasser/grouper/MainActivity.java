package com.darrienglasser.grouper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN_ACTIVITY";

    /**
     * Drawer layout.
     */
    private DrawerLayout mDrawerLayout;

    /**
     * Toggle for Actionbar Drawer.
     */
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    /**
     * Navigation view for drawer.
     */
    private NavigationView mNavView;

    /**
     * Nav view toolbar.
     */
    private Toolbar mToolbar;

    /**
     * Access to shared preferences.
     */
    private SharedPreferences mSharedPrefs;

    /**
     * Global realm configuration.
     */
    public static RealmConfiguration mRealmConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPrefs = getPreferences(MODE_PRIVATE);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackground(
                new ColorDrawable(
                        ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));

        if (mRealmConfig == null) {
            mRealmConfig = new RealmConfiguration.Builder(this).build();
        }

        Realm.setDefaultConfiguration(mRealmConfig);

        mNavView = (NavigationView) findViewById(R.id.navigation_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainView);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_closed);

        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // no op
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // no op
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // no op
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // no op
            }
        });

        mNavView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.numbered_list:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_view, new NumberGroupFragment()).commit();

                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        setTitle(item.getTitle());
                        break;
                    case R.id.named_list:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_view, new NameGroupFragment()).commit();

                        item.setChecked(true);
                        setTitle(item.getTitle());
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.saved_names:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.content_view, new NameCreatorFragment()).commit();

                        item.setChecked(true);
                        setTitle(item.getTitle());
                        mDrawerLayout.closeDrawers();
                        break;
                    case R.id.about_app:
                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
                        mDrawerLayout.closeDrawers();
                        break;
                }

                mNavView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                // Save layout to restore later
                saveLayoutState(item.getItemId());
                return false;
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        restoreLayoutState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Helper method. Saves the current state of the nav drawer. This persists across the app
     * lifecycle.
     *
     * @param tab Current tab selected.
     */
    private void saveLayoutState(int tab) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putInt(TAG, tab);
        editor.apply();
    }

    /**
     * Helper method. Restores the layout previously selected by the user.
     */
    private void restoreLayoutState() {
         int store = mSharedPrefs.getInt(TAG, -1);

        if (store != -1) {
            mNavView.setCheckedItem(store);
            setTitle(mNavView.getMenu().findItem(store).getTitle());
        } else {
            mNavView.getMenu().getItem(0).setChecked(true);
            setTitle(mNavView.getMenu().getItem(0).getTitle());
        }

        switch (store) {
            case R.id.numbered_list:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_view, new NumberGroupFragment()).commit();

                break;
            case R.id.named_list:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_view, new NameGroupFragment()).commit();
                break;

            case R.id.saved_names:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_view, new NameCreatorFragment()).commit();
                break;
            default:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_view, new NumberGroupFragment())
                        .commit();

                break;
        }
    }
}

