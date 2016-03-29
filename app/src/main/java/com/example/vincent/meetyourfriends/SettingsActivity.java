package com.example.vincent.meetyourfriends;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_LANGUAGE = "prefLanguage";
    public static final String KEY_PREF_COLORAB = "barColor";
    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String colorAB = sharedPreferences.getString(SettingsActivity.KEY_PREF_COLORAB, "");
        ActionBar actionBar = getDelegate().getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorAB)));
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Show the Up button in the action bar.
        setupActionBar();

        getFragmentManager().beginTransaction().replace(android.R.id.content, new Settings()).commit();

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(KEY_PREF_LANGUAGE)) {
            String langPref = sharedPreferences.getString(SettingsActivity.KEY_PREF_LANGUAGE, "");
            changeLang(langPref);
        }

        finish();
        startActivity(getIntent());
    }

    public void changeLang(String lang)
    {
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang.toLowerCase());
        res.updateConfiguration(conf, dm);
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

    public static class Settings extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings);
        }
    }
}