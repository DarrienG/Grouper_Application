package com.darrienglasser.grouper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView versionCode = (TextView) findViewById(R.id.build_version_view);
        versionCode.setText(String.format(
                getString(R.string.build_version), BuildConfig.VERSION_NAME));

        TextView sourceView = (TextView) findViewById(R.id.source_view);
        sourceView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView realmView = (TextView) findViewById(R.id.realm_view);
        realmView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
