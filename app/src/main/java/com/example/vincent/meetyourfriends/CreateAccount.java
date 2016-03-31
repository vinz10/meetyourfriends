package com.example.vincent.meetyourfriends;

// IMPORTATIONS
import android.content.ContentValues;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.vincent.meetyourfriends.db.DbHelper;
import com.example.vincent.meetyourfriends.db.UsersContract;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Classe pour la création d'un compte utilisateur
public class CreateAccount extends AppCompatActivity {

    // Déclaration et affectation de la variable sexe pour le genre
    private static String sexe = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

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

        // Initialisation des spinners pour la date de naissance de l'utilisateur
        Spinner spinnerDays = (Spinner) findViewById(R.id.caSpinnerDays);
        Spinner spinnerMonths = (Spinner) findViewById(R.id.caSpinnerMonths);
        Spinner spinnerYears = (Spinner) findViewById(R.id.caSpinnerYears);

        // Déclaration et affectation des adaptateurs pour les spinners
        ArrayAdapter<CharSequence> adapterDays = ArrayAdapter.createFromResource(this, R.array.days, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterMonths = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterYears = ArrayAdapter.createFromResource(this, R.array.years, android.R.layout.simple_spinner_item);
        adapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Affectation des adaptateurs aux spinners
        spinnerDays.setAdapter(adapterDays);
        spinnerMonths.setAdapter(adapterMonths);
        spinnerYears.setAdapter(adapterYears);
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
                Intent intent = new Intent(this, Login.class);
                CreateAccount.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode qui détecte le clic sur les radio buttons
    public void onRadioButtonClicked(View view) {

        // Déclaration et affectation de la variable checked
        boolean checked = ((RadioButton) view).isChecked();

        // Check si c'est un homme ou une femme
        switch(view.getId()) {
            case R.id.caMan:
                if(checked)
                    sexe = "man";
                break;
            case R.id.caWoman:
                if(checked)
                    sexe = "woman";
                break;
        }
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

    // Méthode qui insère les données dans la base de données
    public void showLogin(View view) {

        // Intent pour afficher le login
        Intent intent = new Intent(this, Login.class);

        // Déclaration et affectation des variables
        TextView caError = (TextView)findViewById(R.id.caError);
        String mail = ((EditText)findViewById(R.id.caMail)).getText().toString();
        String password = ((EditText)findViewById(R.id.caPassword)).getText().toString();
        String firstname = ((EditText)findViewById(R.id.caFirstname)).getText().toString();
        String lastname = ((EditText)findViewById(R.id.caLastname)).getText().toString();
        Spinner day = (Spinner)findViewById(R.id.caSpinnerDays);
        Spinner month = (Spinner)findViewById(R.id.caSpinnerMonths);
        Spinner year = (Spinner)findViewById(R.id.caSpinnerYears);
        String birthdate = year.getSelectedItem().toString() + "." + (month.getSelectedItemPosition()+1) + "." + day.getSelectedItem().toString();

        // Insertion des valeurs dans le contentValues
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UsersContract.UserEntry.KEY_EMAIL, mail);
        values.put(UsersContract.UserEntry.KEY_PASSWORD, password);
        values.put(UsersContract.UserEntry.KEY_SEXE, sexe);
        values.put(UsersContract.UserEntry.KEY_FIRSTNAME, firstname);
        values.put(UsersContract.UserEntry.KEY_LASTNAME, lastname);
        values.put(UsersContract.UserEntry.KEY_BIRTHDATE, birthdate);

        // Exécution de plusieurs tests avant l'insertion des données dans la base de données
        if (mail.equals("")) {
            caError.setHint(R.string.createMailNull);
            caError.setVisibility(View.VISIBLE);
        } else if (!isEmailValid(mail)) {
            caError.setHint(R.string.MailInvalid);
            caError.setVisibility(View.VISIBLE);
        } else if (ExistsEmail(mail)) {
            caError.setHint(R.string.MailExist);
            caError.setVisibility(View.VISIBLE);
        } else if (password.equals("")) {
            caError.setHint(R.string.createPassNull);
            caError.setVisibility(View.VISIBLE);
        } else if (sexe.equals("null")) {
            caError.setHint(R.string.createGenderNull);
            caError.setVisibility(View.VISIBLE);
        } else if (firstname.equals("")) {
            caError.setHint(R.string.createFirstnameNull);
            caError.setVisibility(View.VISIBLE);
        } else if (lastname.equals("")) {
            caError.setHint(R.string.createLastnameNull);
            caError.setVisibility(View.VISIBLE);
        } else {
            caError.setVisibility(View.INVISIBLE);

            // S'il n'y a pas d'erreurs, insertion des données dans la base de données
            db.insert(UsersContract.UserEntry.TABLE_NAME, null, values);

            // Affichage du login
            startActivity(intent);
        }
    }
}