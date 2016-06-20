package com.darrienglasser.grouper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;

public class GroupSelector extends AppCompatActivity {

    /**
     * Database used to store user input.
     */
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRealm = Realm.getDefaultInstance();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void cancel(View view) {
        onBackPressed();
    }

    public void saveData(View view) {
        String custString = ((EditText) findViewById(R.id.nameEditor)).getText().toString();
        String numGroupsString = ((EditText) findViewById(R.id.groupEditor)).getText().toString();
        String sgString = ((EditText) findViewById(R.id.sgSizeEditor)).getText().toString();

        if (custString.length() == 0 || numGroupsString.length() == 0 || sgString.length() == 0) {
            Toast.makeText(
                    view.getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
        } else {
            Integer numGroups, numSubGroups;
            try {
                numGroups = Integer.parseInt(numGroupsString);
            } catch (java.lang.NumberFormatException e) {
                numGroups = Integer.MAX_VALUE;
            }

            try {
                numSubGroups = Integer.parseInt(sgString);
            } catch (java.lang.NumberFormatException e) {
                numSubGroups = Integer.MAX_VALUE;
            }

            if (!(numGroups > 0) || !(numSubGroups > 0)) {
                Toast.makeText(
                        GroupSelector.this,
                        "Size cannot be less than 1",
                        Toast.LENGTH_SHORT).show();
            } else {
                final GroupData data = new GroupData(
                        custString, numGroups, numSubGroups);

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(data);
                    }
                });
                finish();
            }
        }
    }
}
