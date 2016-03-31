package com.example.vincent.meetyourfriends;

// IMPORTATIONS
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import java.util.Locale;

// Classe pour la gestion des préférences de l'utilisateur
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    // Déclaration et affectation des variables
    public static final String KEY_PREF_LANGUAGE = "prefLanguage";
    public static final String KEY_PREF_COLORAB = "barColor";
    public static final String KEY_PREF_LOGOAB = "icon";
    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        // Déclaration et affectation des préférences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Gestion de la barre d'actions
        String colorAB = sharedPreferences.getString(SettingsActivity.KEY_PREF_COLORAB, "#0A0A2A");
        ActionBar actionBar = getDelegate().getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorAB)));
        boolean logo = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_LOGOAB, true);
        actionBar.setDisplayUseLogoEnabled(logo);
        actionBar.setDisplayShowHomeEnabled(true);

        // Appel de la fonction pour afficher le bouton retour sur la barre d'actions
        setupActionBar();

        // Récupération du fragment des préférences
        getFragmentManager().beginTransaction().replace(android.R.id.content, new Settings()).commit();

        // Liaison des préférences avec le ChangeListener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    // Méthode qui s'exécute lorsque l'utilisateur change des préférences
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        // Si l'utilisateur change la langue
        if (key.equals(KEY_PREF_LANGUAGE)) {
            // On change la langue
            String langPref = sharedPreferences.getString(SettingsActivity.KEY_PREF_LANGUAGE, "");
            changeLang(langPref);
        }

        // On refresh l'affichage
        finish();
        startActivity(getIntent());
    }

    // Méthode qui permet de changer la langue de l'application
    public void changeLang(String lang) {
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang.toLowerCase());
        res.updateConfiguration(conf, dm);
    }

    // Cette méthode permet d'importer une librairie pour rendre l'activité compatible avec AppCompatActivity et ainsi la barre d'actions
    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }

    // Méthode pour afficher le bouton retour dans la barre d'actions
    private void setupActionBar() {
        getDelegate().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Méthode qui gère la navigation de la barre d'actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Action retour
                Intent intent = new Intent(this, Events.class);
                SettingsActivity.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Fragment des préférences
    public static class Settings extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Affectation des préférences depuis les ressources (settings.xml)
            addPreferencesFromResource(R.xml.settings);
        }
    }
}