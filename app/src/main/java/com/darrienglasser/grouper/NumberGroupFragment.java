package com.darrienglasser.grouper;


import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.exceptions.RealmMigrationNeededException;


/**
 * A simple {@link Fragment} subclass.
 */
public class NumberGroupFragment extends Fragment {
    /**
     * Fragment tag.
     */
    private static final String TAG = "NUMBERED_LIST_FRAGMENT";

    /**
     * Root view.
     */
    private View rootView;

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
    private Context mContext;

    public NumberGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = container.getRootView();

        mContext = getContext();

        Realm.setDefaultConfiguration(MainActivity.mRealmConfig);
        try {
            mRealm = Realm.getDefaultInstance();
        } catch(RealmMigrationNeededException e) {
            MainActivity.mRealmConfig =
                    new RealmConfiguration.Builder(
                            getActivity()).deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(MainActivity.mRealmConfig);
            mRealm = Realm.getDefaultInstance();
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_number_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        mDefaultText = (TextView) view.findViewById(R.id.emptyText);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), NumberGroupCreatorActivity.class));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        if (checkDbExistence()) {
            try {
                RealmQuery<NumberGroupData> query = mRealm.where(NumberGroupData.class);
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fullList);
                ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                        new ItemTouchHelper.SimpleCallback(
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
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                mRecyclerView.setAdapter(mAdapter);
                mDefaultText.setVisibility(View.GONE);
            } catch (NullPointerException e) {
                Log.e(TAG, "Database not initialized.");
                mDefaultText.setVisibility(View.VISIBLE);
            }
        } else {
            mDefaultText.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Checks to see if there are any items in the database.
     *
     * @return Status of database existence.
     */
    private boolean checkDbExistence() {
        return mRealm.where(NumberGroupData.class).findAll().size() > 0;
    }

    // FIXME: Animation does not appear on first open
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
