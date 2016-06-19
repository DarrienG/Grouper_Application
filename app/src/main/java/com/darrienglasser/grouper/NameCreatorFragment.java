package com.darrienglasser.grouper;


import android.animation.Animator;
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
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;


/**
 * A simple {@link Fragment} subclass.
 */
public class NameCreatorFragment extends Fragment {
    /**
     * Fragment tag.
     */
    private static final String TAG = "NAME_CREATOR_FRAGMENT";

    private View mRootView;

    /**
     * Access to the database that contains all stored groups.
     */
    Realm mRealm;

    /**
     * List containing lists of names.
     */
    RecyclerView mRecyclerView;

    /**
     * Default text shown.
     */
    TextView mDefaultText;

    /**
     * Data adapter.
     */
    NameCreatorMainAdapter mAdapter;

    public NameCreatorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Realm.setDefaultConfiguration(MainActivity.mRealmConfig);
        try {
            mRealm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            MainActivity.mRealmConfig =
                    new RealmConfiguration.Builder(
                            getActivity()).deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(MainActivity.mRealmConfig);
            mRealm = Realm.getDefaultInstance();
        }

        mRootView = container.getRootView();
        return inflater.inflate(R.layout.fragment_name_creator, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDefaultText = (TextView) view.findViewById(R.id.emptyText);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.fullList);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), NameCreatorActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkDbExistence()) {
            try {
                final RealmResults<NameWrapper> query =
                        mRealm.where(NameWrapper.class).distinct("id");

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
                            public void onSwiped(
                                    final RecyclerView.ViewHolder viewHolder, int direction) {

                                mAdapter.removeRow(viewHolder.getAdapterPosition());
                                // For some reason, this adapter is special, and 1 = 0
                                if (mAdapter.getItemCount() == 1) {
                                    reDrawDefaultLayout();
                                }
                            }
                        };

                mAdapter = new NameCreatorMainAdapter(query, mRealm);
                mAdapter.setOnClickListener(new NameCreatorMainAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, final int position) {
                        Intent intent = new Intent(getContext(), NameCreatorActivity.class);
                        intent.putExtra("id", query.get(position).getId());
                        startActivity(intent);
                    }
                });

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
                itemTouchHelper.attachToRecyclerView(mRecyclerView);
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
        return mRealm.where(NameWrapper.class).findAll().size() > 0;
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
