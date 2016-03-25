package com.example.vincent.meetyourfriends;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.widget.Toast;

import java.util.Locale;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREP_LANGUAGE = "prefLanguage";
    private AppCompatDelegate mDelegate;
    private Locale myLocale;

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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String langPref = sharedPreferences.getString(SettingsActivity.KEY_PREP_LANGUAGE, "");

        Toast.makeText(getApplicationContext(), langPref, Toast.LENGTH_LONG).show();

        changeLang("fr");
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Toast.makeText(getApplicationContext(), "fr", Toast.LENGTH_LONG).show();

        if (key.equals(KEY_PREP_LANGUAGE)) {

            Toast.makeText(getApplicationContext(), "fr", Toast.LENGTH_LONG).show();
            final Preference connectionPref = findPreference(key);

            connectionPref.setSummary(sharedPreferences.getString(key, ""));

            String languageID = sharedPreferences.getString(SettingsActivity.KEY_PREP_LANGUAGE, "2");

            String lang = "en";

            switch (languageID) {
                case "1":
                    //Locale localeEN = new Locale("en_US");
                    //setLocale(localeEN);
                    lang = "en";
                    Toast.makeText(getApplicationContext(), "en", Toast.LENGTH_LONG).show();
                    break;
                case "2":
                    //Locale localeCH = new Locale("fr_CH");
                    //setLocale(localeCH);
                    lang = "fr";
                    Toast.makeText(getApplicationContext(), "fr", Toast.LENGTH_LONG).show();
                    break;
            }
            changeLang(lang);

        }
    }

    public void changeLang(String lang)
    {

        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void saveLocale(String lang)
    {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();
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

}
