package com.darrienglasser.grouper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;

public class NameListActionActivity extends AppCompatActivity {

    /**
     * RecyclerView.
     */
    private RecyclerView mRecyclerView;

    /**
     * List of data.
     */
    private ArrayList<ArrayList<String>> mGroupContainer;

    /**
     * Database accessor.
     */
    private Realm mRealm;

    /**
     * Position of id NameGroupData. Passed in from NameGroupFragment.
     */
    private int mPosition;

    /**
     * Data for current group of names.
     */
    private NameGroupData mGroupData;

    /**
     * Data adapter.
     */
    private ActionAdapter mAdapter;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_list_action);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPosition = extras.getInt("position");
        } else {
            mPosition = -1;
        }

        mRealm = Realm.getDefaultInstance();
        mRecyclerView = (RecyclerView) findViewById(R.id.fullList);

        RealmResults<NameGroupData> query = mRealm.where(NameGroupData.class).findAll();
        mGroupData = query.get(mPosition);
        RealmResults<NameWrapper> idQuery = mRealm.where(NameWrapper.class)
                .equalTo("id", mGroupData.getId()).findAll();

        ArrayList<String> tmpList = new ArrayList<>(idQuery.size());
        for (NameWrapper wrapper : idQuery) {
            tmpList.add(wrapper.getName());
        }

        Collections.shuffle(tmpList);

        mGroupContainer = new ArrayList<>();
        ArrayList<String> sgList = new ArrayList<>(mGroupData.getSubGroupSize());
        for (int i = 0; i < tmpList.size(); ++i) {
            sgList.add(tmpList.get(i));
            if (sgList.size() >= mGroupData.getSubGroupSize()) {
                mGroupContainer.add((ArrayList<String>) sgList.clone());
                sgList.clear();
            }
        }
        if (sgList.size() > 0) {
            mGroupContainer.add((ArrayList<String>) sgList.clone());
        }

        ItemTouchHelper.SimpleCallback SimpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
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
                mAdapter.updateDataSet(viewHolder.getAdapterPosition());
                if (mAdapter.getItemCount() == 0) {
                    finish();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(SimpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new ActionAdapter(mGroupContainer);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle(mGroupData.getGroupName());
    }
}
