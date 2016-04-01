package com.example.vincent.meetyourfriends;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vincent.meetyourfriends.db.CommentairesContract;
import com.example.vincent.meetyourfriends.db.DbHelper;
import com.example.vincent.meetyourfriends.db.EventsContract;
import com.example.vincent.meetyourfriends.db.UsersContract;
import com.example.vincent.meetyourfriends.db.UsersInEventContract;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Classe pour la modification d'un compte utilisateur
public class ModifyAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_account);

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

        // Appel de la fonction pour afficher le bouton retour sur la barre d'actions
        setupActionBar();

        // Déclaration et affectation des variables
        String fileName = "cache.txt";
        String [] temp;
        int ch;
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;

        // Lecture du fichier cache pour récupérer le mail et le mot de passe de l'utilisateur connecté
        try {
            fis = openFileInput(fileName);
            try {
                while( (ch = fis.read()) != -1)
                    fileContent.append((char)ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        temp = fileContent.toString().split(";");

        // Récupération de l'utilisateur connecté avec son mot de passe
        EditText eCurrentMail = ((EditText)findViewById(R.id.maCurrentMail));
        EditText eCurrentPassword = ((EditText)findViewById(R.id.maCurrentPassword));
        eCurrentMail.setText(temp[0].toString());
        eCurrentPassword.setText(temp[1].toString());
    }

    // Méthode pour afficher le bouton retour dans la barre d'actions
    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // Méthode qui gère la navigation de la barre d'actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Action retour
                Intent intent = new Intent(this, Events.class);
                ModifyAccount.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode qui détermine si un email est validé avec un pattern
    public static boolean isEmailValid(String email) {

        // Déclaration et affectation de la variable isValid
        boolean isValid = false;

        // Déclaration du pattern
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        // Conversion du mail
        CharSequence inputStr = email;

        // Check si le mail est valide
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    // Méthode qui détermine si l'adresse mail existe déjà dans la BD
    public boolean ExistsEmail(String email) {

        // Requête SQL
        Cursor cursor = null;
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = "SELECT id FROM users WHERE email='" + email + "';";
        cursor = db.rawQuery(sql, null);

        // Retour true or false en fonction de la requête
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Méthode qui permet de modifier l'email
    public void modifyEmail(View view) {

        // Déclaration et affectation des variables
        EditText eCurrentMail = ((EditText)findViewById(R.id.maCurrentMail));
        EditText eCurrentPassword = ((EditText)findViewById(R.id.maCurrentPassword));
        EditText eNewMail = ((EditText)findViewById(R.id.maNewMail));
        EditText eConfirmMail = ((EditText)findViewById(R.id.maConfirmMail));
        TextView errorMail = ((TextView) findViewById(R.id.maErrorEmail));

        // Exécution de tests pour les nouveaux emails
        if (eNewMail.getText().toString().isEmpty()) {
            errorMail.setHint(R.string.newMailnull);
            errorMail.setVisibility(View.VISIBLE);
        } else if (!isEmailValid(eNewMail.getText().toString())) {
            errorMail.setHint(R.string.MailInvalid);
            errorMail.setVisibility(View.VISIBLE);
        } else if (ExistsEmail(eNewMail.getText().toString())) {
            errorMail.setHint(R.string.MailExist);
            errorMail.setVisibility(View.VISIBLE);
        } else if (eConfirmMail.getText().toString().isEmpty()) {
            errorMail.setHint(R.string.confirmMailnull);
            errorMail.setVisibility(View.VISIBLE);
        } else if (eNewMail.getText().toString().equals(eConfirmMail.getText().toString())) {
            // Test OK

            // On masque le message d'erreur
            errorMail.setVisibility(View.INVISIBLE);

            // Récupération de l'id de l'utilisateur
            int id = 0;
            Cursor cursor = null;
            DbHelper mDbHelper = new DbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            String sql = "SELECT id FROM users WHERE email='" + eCurrentMail.getText().toString() + "';";
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            id = cursor.getInt(0);

            // Modification dans la base de données
            ContentValues values = new ContentValues();
            values.put(UsersContract.UserEntry.KEY_EMAIL, eNewMail.getText().toString());
            db.update(UsersContract.UserEntry.TABLE_NAME, values, "id="+id, null);

            // Modification du fichier cache
            this.deleteFile("cache.txt");
            String file = "cache.txt";
            String output = eNewMail.getText().toString() + ";" + eCurrentPassword.getText().toString();
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(file, Context.MODE_PRIVATE);
                outputStream.write(output.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
            startActivity(getIntent());
        } else {
            errorMail.setHint(R.string.mailNotSame);
            errorMail.setVisibility(View.VISIBLE);
        }
    }

    // Méthode qui permet de modifier le mot de passe
    public void modifyPassword(View view) {

        // Déclaration et affectation des variables
        EditText eCurrentMail = ((EditText)findViewById(R.id.maCurrentMail));
        EditText eNewPassword = ((EditText)findViewById(R.id.maNewPassword));
        EditText eConfirmPassword = ((EditText)findViewById(R.id.maConfirmPassword));
        TextView errorPassword = ((TextView) findViewById(R.id.maErrorPassword));

        // Exécution des tests pour les nouveaux mot de passe
        if (eNewPassword.getText().toString().isEmpty()) {
            errorPassword.setHint(R.string.newPasswordnull);
            errorPassword.setVisibility(View.VISIBLE);
        } else if (eConfirmPassword.getText().toString().isEmpty()) {
            errorPassword.setHint(R.string.confirmPasswordlnull);
            errorPassword.setVisibility(View.VISIBLE);
        } else if (eNewPassword.getText().toString().equals(eConfirmPassword.getText().toString())) {
            // Test OK

            // On masque le message d'erreur
            errorPassword.setVisibility(View.INVISIBLE);

            // Récupération de l'id de l'utilisateur
            int id = 0;
            Cursor cursor = null;
            DbHelper mDbHelper = new DbHelper(this);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            String sql = "SELECT id FROM users WHERE email='" + eCurrentMail.getText().toString() + "';";
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            id = cursor.getInt(0);

            // Modification dans la base de données
            ContentValues values = new ContentValues();
            values.put(UsersContract.UserEntry.KEY_PASSWORD, eNewPassword.getText().toString());
            db.update(UsersContract.UserEntry.TABLE_NAME, values, "id="+id, null);

            // Modification du fichier cache
            this.deleteFile("cache.txt");
            String file = "cache.txt";
            String output = eCurrentMail.getText().toString() + ";" + eNewPassword.getText().toString();
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(file, Context.MODE_PRIVATE);
                outputStream.write(output.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            finish();
            startActivity(getIntent());
        } else {
            errorPassword.setHint(R.string.passwordNotSame);
            errorPassword.setVisibility(View.VISIBLE);
        }
    }

    // Méthode qui permet de supprimer l'utilisateur
    public void deleteAccount(View view) {

        // Message d'alerte avant la suppression
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.confirmeDelete);
        alertDialog.setMessage(R.string.confirmMessage);
        alertDialog.setPositiveButton(R.string.confirmOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Appel de la méthode pour supprimer définitivement l'utilisateur
                deleteAccount();
            } });


        alertDialog.setNegativeButton(R.string.confirmCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // On revient sur la page sans rien faire
                finish();
                startActivity(getIntent());
            } });

        alertDialog.setIcon(R.drawable.ic_action_warning);
        alertDialog.show();
    }

    // Méthode qui supprime définitivement l'utilisateur
    public void deleteAccount() {

        // Déclaration et affectation de eCurrentMail
        EditText eCurrentMail = ((EditText)findViewById(R.id.maCurrentMail));

        // Récupération de l'id de l'utilisateur
        int id = 0;
        Cursor cursor = null;
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = "SELECT id FROM users WHERE email='" + eCurrentMail.getText().toString() + "';";
        cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        id = cursor.getInt(0);

        // Requête SQL
        String sql2 = "DELETE FROM " + CommentairesContract.CommentairesEntry.TABLE_NAME
                + " WHERE " + CommentairesContract.CommentairesEntry.KEY_ID_USER + " = " + id + ";";

        // Suppression de l'évenement
        db.execSQL(sql2);

        String sql1 = "DELETE FROM " + UsersInEventContract.UsersInEventEntry.TABLE_NAME
                + " WHERE " + UsersInEventContract.UsersInEventEntry.KEY_ID_USER + " = " + id + ";";

        // Suppression de l'évenement
        db.execSQL(sql1);

        // Activer le delete onCascade
        String activeCascade = "PRAGMA foreign_keys = ON";
        db.execSQL(activeCascade);

        // Suppression dans la base de données
        db.delete(UsersContract.UserEntry.TABLE_NAME, UsersContract.UserEntry.KEY_ID + "=" + id, null);

        // Suppression du fichier cache
        this.deleteFile("cache.txt");

        // Affichage de la page d'accueil
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}