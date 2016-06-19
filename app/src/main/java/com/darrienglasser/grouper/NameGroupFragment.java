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
public class NameGroupFragment extends Fragment {
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
    private NameGroupAdapter mAdapter;

    /**
     * Access to the database that contains all stored groups.
     */
    private Realm mRealm;

    public NameGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_name_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            mRealm = Realm.getDefaultInstance();
        } catch(RealmMigrationNeededException e) {
            MainActivity.mRealmConfig =
                    new RealmConfiguration.Builder(
                            getActivity()).deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(MainActivity.mRealmConfig);
            mRealm = Realm.getDefaultInstance();
        }

        rootView = view.getRootView();
        mDefaultText = (TextView) view.findViewById(R.id.emptyText);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fullList);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRealm.where(NameWrapper.class).findAll().size() == 0) {
                    startActivity(new Intent(getContext(), IntermediaryNameCreatorActivity.class));
                } else {
                    startActivity(new Intent(getContext(), NameGroupCreatorActivity.class));
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkDbExistence()) {
            final RealmResults<NameGroupData> query =
                    mRealm.where(NameGroupData.class).findAll();

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
                            mAdapter.removeRow(viewHolder.getAdapterPosition(), mRealm);
                            if (mAdapter.getItemCount() == 1) {
                                reDrawDefaultLayout();
                            }
                        }
                    };

            mAdapter = new NameGroupAdapter(query, getContext());

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
            itemTouchHelper.attachToRecyclerView(mRecyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(mAdapter);
            mDefaultText.setVisibility(View.GONE);
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
        return mRealm.where(NameGroupData.class).findAll().size() > 0;
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
