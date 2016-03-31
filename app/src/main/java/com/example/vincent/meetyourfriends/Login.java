package com.example.vincent.meetyourfriends;

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
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vincent.meetyourfriends.db.DbHelper;
import com.example.vincent.meetyourfriends.db.EventsContract;
import com.example.vincent.meetyourfriends.db.UsersInEventContract;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String colorAB = sharedPreferences.getString(SettingsActivity.KEY_PREF_COLORAB, "#0A0A2A");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorAB)));
        boolean logo = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_LOGOAB, true);
        actionBar.setDisplayUseLogoEnabled(logo);
        actionBar.setDisplayShowHomeEnabled(true);

        String langPref = sharedPreferences.getString(SettingsActivity.KEY_PREF_LANGUAGE, "");
        changeLang(langPref);

        setContentView(R.layout.activity_login);
    }

    public void changeLang(String lang)
    {
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang.toLowerCase());
        res.updateConfiguration(conf, dm);
    }

    public void createAccount(View view) {
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }

    public void showEvents(View view) {

        String mail = ((EditText)findViewById(R.id.lMail)).getText().toString();
        String password = ((EditText)findViewById(R.id.lPassword)).getText().toString();
        Cursor cursor = null;
        Cursor cursor1 = null;
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        TextView error = ((TextView) findViewById(R.id.lErrorMessage));

        String sql = "SELECT * FROM users WHERE email='" + mail + "' AND password='" + password +"';";
        cursor = db.rawQuery(sql, null);

        String sql1 = "SELECT * FROM users WHERE email='" + mail + "';";
        cursor1 = db.rawQuery(sql1, null);

        // EMAIL vide
        if (mail.isEmpty()) {
            error.setHint(R.string.erroremailnull);
            error.setVisibility(View.VISIBLE);
        }
        // PASSWORD vide
        else if (password.isEmpty()) {
            error.setHint(R.string.errorpasswordnull);
            error.setVisibility(View.VISIBLE);
        }
        // EMAIL invalide
        else if (cursor1.getCount()<1) {
            error.setHint(R.string.errormailinvalid);
            error.setVisibility(View.VISIBLE);
        } else {
            // EMAIL et PASSWORD correct
            if (cursor.getCount()>0) {
                error.setVisibility(View.INVISIBLE);

                // Création du fichier cache
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

                Intent intent = new Intent(this, Events.class);
                startActivity(intent);
            } else {
                error.setHint(R.string.errorlogin);
                error.setVisibility(View.VISIBLE);
                EditText ePassword = ((EditText) findViewById(R.id.lPassword));
                ePassword.setText("");
            }
        }
    }

}