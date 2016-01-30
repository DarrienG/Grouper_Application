package com.darrienglasser.grouper;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN_ACTIVITY";

    /**
     * Default text shown to use if no groups created.
     */
    private TextView mDefaultText;

    /**
     * Holds list of data.
     */
    private RecyclerView mRecyclerView;

    /**
     * Adapter for RecyclerView.
      */
    private DataAdapter mAdapter;

    /**
     * Access to the database that contains all stored groups.
     */
    private Realm mRealm;

    /** Application context. */
    private static Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GroupSelector.class);
                startActivity(intent);
            }
        });

        mContext = getApplicationContext();
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        mRealm = Realm.getDefaultInstance();

        // Set colors for TextView
        mDefaultText = (TextView) findViewById(R.id.emptyText);
        mDefaultText.setTextColor(Color.parseColor("#000000"));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (checkDbExistence()) {
            try {
                RealmQuery<GroupData> query = mRealm.where(GroupData.class);
                mRecyclerView = (RecyclerView) findViewById(R.id.fullList);
                ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                        0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            RecyclerView.ViewHolder target) {

                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        mAdapter.removeItem(viewHolder.getAdapterPosition(), mRealm);
                        if (mAdapter.getItemCount() == 0) {
                            reDrawDefaultLayout();
                        }
                    }
                };

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(mRecyclerView);

                mAdapter = new DataAdapter(query.findAll(), mContext);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setAdapter(mAdapter);
                mDefaultText.setVisibility(View.GONE);
            } catch (NullPointerException e) {
                Log.e(TAG, "Database not initialized.");
                mDefaultText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Snackbar.make(
                        findViewById(R.id.mainView),
                        "Settings to be implemented in a later update",
                        Snackbar.LENGTH_LONG).show();

                break;
            case R.id.action_purge_all:
                if (mAdapter != null && mAdapter.getItemCount() > 0) {
                    mAdapter.destroyAll(mRealm);
                    reDrawDefaultLayout();
                } else {
                    Snackbar.make(
                            findViewById(R.id.mainView),
                            "Nothing to delete!",
                            Snackbar.LENGTH_LONG).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks to see if there are any items in the database.
     *
     * @return Status of database existence.
     */
    private boolean checkDbExistence() {
        return !mRealm.isEmpty();
    }

    // BUG: Animation does not appear on first open
    /**
     * Redraws default text with circle animation.
     */
    private void reDrawDefaultLayout() {
        int cx = mDefaultText.getMeasuredWidth() / 2;
        int cy = mDefaultText.getMeasuredHeight() / 2;

        int finalRadius = Math.max(
                mDefaultText.getWidth(),
                mDefaultText.getHeight()) / 2;

        Animator anim =
                ViewAnimationUtils.createCircularReveal(mDefaultText, cx, cy, 0, finalRadius);

        mDefaultText.setVisibility(View.VISIBLE);
        anim.start();
    }
}
