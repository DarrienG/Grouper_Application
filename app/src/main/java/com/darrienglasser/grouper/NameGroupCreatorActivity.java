package com.darrienglasser.grouper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class NameGroupCreatorActivity extends AppCompatActivity {

    /**
     * Spinner where user chooses the id of names to pick from.
     */
    private Spinner mIdSpinner;

    /**
     * Button used to save results.
     */
    private Button mSaveButton;

    /**
     * Cancel button.
     */
    private Button mCancelButton;

    /**
     * Field where user can enter a custom name for their group.
     */
    private EditText mCustNameEditor;

    /**
     * Field dwhere user can enter subgroup size for their group.
     */
    private EditText mSgSizeEditor;

    /**
     * Database accessor.
     */
    private Realm mRealm;

    /**
     * Integer parsed from EditText;
     */
    private int mParsedInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_group_creator);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRealm = Realm.getDefaultInstance();
        mIdSpinner = (Spinner) findViewById(R.id.group_spinner);
        mSaveButton = (Button) findViewById(R.id.save_data_button);
        mCancelButton = (Button) findViewById(R.id.cancelButton);
        mCustNameEditor = (EditText) findViewById(R.id.nameEditor);
        mSgSizeEditor = (EditText) findViewById(R.id.sgSizeEditor);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validInput()) {
                    final NameGroupData data =
                            new NameGroupData(
                                    mCustNameEditor.getText().toString(),
                                    mIdSpinner.getSelectedItem().toString(),
                                    mParsedInt);

                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            realm.copyToRealm(data);
                        }
                    });

                    Intent intent = new Intent(NameGroupCreatorActivity.this, MainActivity.class);
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(
                            NameGroupCreatorActivity.this,
                            "Please enter all fields",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


        RealmResults<NameWrapper> results = mRealm.where(NameWrapper.class).distinct("id");
        if (results.size() == 0) {
            startActivity(
                    new Intent(
                            NameGroupCreatorActivity.this,
                            IntermediaryNameCreatorActivity.class));

        }
        String[] distinctId = new String[results.size()];
        for (int i = 0; i < results.size(); ++i) {
            distinctId[i] = results.get(i).getId();
        }
        ArrayAdapter<String> groupNames = new ArrayAdapter<>(
                getApplicationContext(), R.layout.spinner_item, distinctId);

        mIdSpinner.setAdapter(groupNames);
    }

    /**
     * Checks input to ensure it is valid.
     */
    private boolean validInput() {
        try {
            mParsedInt = Integer.parseInt(mSgSizeEditor.getText().toString());
        } catch (NumberFormatException e) {
            mParsedInt = 10000;
        }

        return !mCustNameEditor.getText().toString().isEmpty()
                && !mSgSizeEditor.toString().isEmpty()
                && mParsedInt > 0;
    }
}
