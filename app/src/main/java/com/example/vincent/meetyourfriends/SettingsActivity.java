package com.example.vincent.meetyourfriends;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREP_SYNC_CONN = "prefLanguage";
    private AppCompatDelegate mDelegate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getDelegate().getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Show the Up button in the action bar.
        setupActionBar();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new Settings()).commit();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    private void setupActionBar() {

        getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_PREP_SYNC_CONN)) {
            Preference connectionPref = findPreference(key);

            connectionPref.setSummary(sharedPreferences.getString(key, ""));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(this, Events.class);

                SettingsActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
