package com.darrienglasser.grouper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Activity used to display aggregated data.
 */
public class NumberListActionActivity extends AppCompatActivity {
    /**
     * Position where item is located in database.
     */
    private int position;

    /**
     * Data retrieved from database.
     */
    private NumberGroupData mData;

    /**
     * RecyclerView adapter.
     */
    private ActionAdapter mAdapter;

    /**
     * List for holding data.
     */
    private RecyclerView mRecyclerView;

    /**
     * Instance to Realm.
     */
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_list_action);
        position = getIntent().getIntExtra("DataPos", -1);

        mRealm = Realm.getDefaultInstance();
        RealmResults<NumberGroupData> data = mRealm.where(NumberGroupData.class).findAll();
        mData = data.get(position);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (mData == null) {
            Toast.makeText(
                    this,
                    "Impossible scenario. Null data injected. Returning to previous screen",
                    Toast.LENGTH_LONG).show();

            finish();
        }

        setTitle(mData.getName());
        mRecyclerView = (RecyclerView) findViewById(R.id.fullList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter == null) {
            mAdapter = new ActionAdapter(generateRandList(mData));
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        mAdapter.updateDataSet(viewHolder.getAdapterPosition());
                        if (mAdapter.getItemCount() == 0) {
                            finish();
                        }
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);

        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * Helper method.
     * Gets the number of groups, and the size of the subgroups. Then creates a
     * randomized 2D ArrayList of numbers based off of them. Each row in the matrix is then to be
     * used as a data for a separate card in the list.
     *
     * @param data Data to be randomized.
     * @return Randomized data to be used.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ArrayList<String>> generateRandList(NumberGroupData data) {
        int numGroups = data.getNumGroups(), subGroupSize = data.getSgSize();
        ArrayList<ArrayList<String>> groupContainer = new ArrayList<>();
        String[] randomizer = new String[numGroups];
        Random rand = new Random();
        for (int i = 0; i < numGroups; ++i) {
            randomizer[i] = i + 1 + "";
        }

        for (int i = 0; i < numGroups; ++i) {
            String tmp;
            int randSwap = rand.nextInt(numGroups);

            tmp = randomizer[randSwap];
            randomizer[randSwap] = randomizer[i];
            randomizer[i] = tmp;
        }

        ArrayList<String> tmpList = new ArrayList<>(subGroupSize);

        for (int i = 0, count = 0; i < randomizer.length; ++i, ++count) {
            if (count >= subGroupSize) {
                groupContainer.add((ArrayList<String>) tmpList.clone());
                tmpList.clear();
                count = 0;
            }
            tmpList.add(randomizer[i]);
        }
        groupContainer.add(tmpList);

        return groupContainer;
    }
}
