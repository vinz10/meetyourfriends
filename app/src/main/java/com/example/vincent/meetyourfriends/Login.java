package com.example.vincent.meetyourfriends;

// IMPORTATIONS
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.vincent.meetyourfriends.db.DbHelper;
import java.io.FileOutputStream;
import java.util.Locale;

// Classe pour la page de connexion
public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Déclaration et affectation des préférences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Gestion de la barre d'actions
        String colorAB = sharedPreferences.getString(SettingsActivity.KEY_PREF_COLORAB, "#0A0A2A");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorAB)));
        boolean logo = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_LOGOAB, true);
        actionBar.setDisplayUseLogoEnabled(logo);
        actionBar.setDisplayShowHomeEnabled(true);

        // Gestion de la langue
        String langPref = sharedPreferences.getString(SettingsActivity.KEY_PREF_LANGUAGE, "");
        changeLang(langPref);

        setContentView(R.layout.activity_login);

        new EndpointsAsyncTask().execute(new Pair<Context, String>(this, "Vincent"));
    }

    // Méthode qui change la langue de l'application
    public void changeLang(String lang) {
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang.toLowerCase());
        res.updateConfiguration(conf, dm);
    }

    // Méthode qui affiche la page pour créer un compte utilisateur
    public void createAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }

    // Méthode qui se connecte et affiche la liste des événements
    public void showEvents(View view) {

        // Déclaration et affectation des variables
        String mail = ((EditText)findViewById(R.id.lMail)).getText().toString();
        String password = ((EditText)findViewById(R.id.lPassword)).getText().toString();
        Cursor cursor = null;
        Cursor cursor1 = null;
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        TextView error = ((TextView) findViewById(R.id.lErrorMessage));

        // Requête SQL pour le mail et le mot de passe
        String sql = "SELECT * FROM users WHERE email='" + mail + "' AND password='" + password +"';";
        cursor = db.rawQuery(sql, null);

        // Requête SQL pour le mail
        String sql1 = "SELECT * FROM users WHERE email='" + mail + "';";
        cursor1 = db.rawQuery(sql1, null);

        // Check email vide
        if (mail.isEmpty()) {
            error.setHint(R.string.erroremailnull);
            error.setVisibility(View.VISIBLE);
        }
        // Check mot de passe vide
        else if (password.isEmpty()) {
            error.setHint(R.string.errorpasswordnull);
            error.setVisibility(View.VISIBLE);
        }
        // Check email invalide
        else if (cursor1.getCount()<1) {
            error.setHint(R.string.errormailinvalid);
            error.setVisibility(View.VISIBLE);
        } else {
            // Check correct
            if (cursor.getCount()>0) {

                // On masque le message d'erreur
                error.setVisibility(View.INVISIBLE);

                // Création du fichier cache pour la 'Session'
                String file = "cache.txt";
                String output = mail + ";" + password;
                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput(file, Context.MODE_PRIVATE);
                    outputStream.write(output.getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Affichage des événements
                Intent intent = new Intent(this, Events.class);
                startActivity(intent);
            } else {
                // Message d'erreur
                error.setHint(R.string.errorlogin);
                error.setVisibility(View.VISIBLE);
                EditText ePassword = ((EditText) findViewById(R.id.lPassword));
                ePassword.setText("");
            }
        }
    }
}