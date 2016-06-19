package com.darrienglasser.grouper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class NameCreatorActivity extends AppCompatActivity {

    /**
     * List of names.
     */
    private RecyclerView mRecyclerView;

    /**
     * Button used to save all output and return to previous screen.
     */
    private Button mSaveButton;

    private EditText mId;

    /**
     * Button used to add a row for a new name.
     */
    private Button mNewNameButton;

    /**
     * RecyclerView adapter.
     */
    private NameCreatorActivityAdapter mAdapter;

    /**
     * Optional id passed into activity.
     */
    private String mPassedId;

    /**
     * Access to the database that contains all stored groups.
     */
    Realm mRealm;

    boolean mSetUp = false;

    boolean mGroupsCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_creator);
        mRealm = Realm.getDefaultInstance();

        try {
            mRealm = Realm.getDefaultInstance();
        } catch (RealmMigrationNeededException e) {
            MainActivity.mRealmConfig =
                    new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
            Realm.setDefaultConfiguration(MainActivity.mRealmConfig);
            mRealm = Realm.getDefaultInstance();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String id = extras.getString("id");
            mPassedId = id == null ? "" : id;
            if (mPassedId.isEmpty()) {
                mSetUp = extras.getBoolean("intermediary", false);
            }
        } else {
            mPassedId = "";
        }

        mGroupsCreated = !mPassedId.isEmpty()
                && mRealm.where(NameGroupData.class).equalTo("id", mPassedId).findAll().size() > 0;

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(!mSetUp);

        mId = (EditText) findViewById(R.id.group_id);
        mId.setText(mPassedId);
        mId.setEnabled(!mGroupsCreated);
        mSaveButton = (Button) findViewById(R.id.save_data_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = mId.getText().toString().trim();
                if (isValidId(id)) {
                    mRealm.beginTransaction();
                    if (!mPassedId.isEmpty()) {
                        mRealm.where(NameWrapper.class)
                                .contains("id", mPassedId)
                                .findAll()
                                .deleteAllFromRealm();
                    }
                    if (mAdapter.getItemCount() == 0) {
                        mRealm.where(NameGroupData.class)
                                .equalTo("id", mPassedId)
                                .findAll()
                                .deleteAllFromRealm();
                        
                        mRealm.commitTransaction();
                        finish();
                        return;
                    }
                    List<String> nameList = mAdapter.getNameList();
                    ArrayList<NameWrapper> wrapperList = new ArrayList<>();
                    for (String s : nameList) {
                        if (s == null || s.isEmpty()) {
                            continue;
                        }

                        NameWrapper nameWrapper = new NameWrapper(s, id);
                        wrapperList.add(nameWrapper);
                    }
                    if (wrapperList.size() > 0) {
                        mRealm.copyToRealm(wrapperList);
                        mRealm.commitTransaction();
                        if (mSetUp) {
                            startActivity(new Intent(NameCreatorActivity.this,
                                    NameGroupCreatorActivity.class));
                        } else {
                            finish();
                        }
                    } else {
                        Toast.makeText(
                                NameCreatorActivity.this,
                                "Add some names first!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mNewNameButton = (Button) findViewById(R.id.new_name_button);
        mNewNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.addItem();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.new_name_list);

        List<String> groupedNames = new ArrayList<>();

        if (mPassedId.isEmpty()) {
            groupedNames.add("");
        } else {
            RealmResults<NameWrapper> wrappedNames =
                    mRealm.where(NameWrapper.class).equalTo("id", mPassedId).findAll();

            for (NameWrapper names : wrappedNames) {
                groupedNames.add(names.getName());
            }
        }
        mAdapter = new NameCreatorActivityAdapter(groupedNames);
        mAdapter.setItemClickListener(new NameCreatorActivityAdapter.OnItemClickListener() {
            @Override
            public void onDestroyRowClickListener(View v, int position) {
                mAdapter.removeRow(position);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mSetUp) {
            startActivity(new Intent(NameCreatorActivity.this, NameGroupCreatorActivity.class));
        }
        super.onBackPressed();
    }

    /**
     * Determines if input Id is valid.
     *
     * @return Validity of input.
     */
    private boolean isValidId(String id) {
        if (id.isEmpty()) {
            Toast.makeText(
                    NameCreatorActivity.this, "Please enter an Id", Toast.LENGTH_SHORT).show();

            return false;
        }

        if (mRealm.where(NameWrapper.class).equalTo("id", id).findAll().size() > 0
                && mPassedId.isEmpty()) {

            Toast.makeText(
                    NameCreatorActivity.this,
                    "Group Id must be unique",
                    Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }
}
