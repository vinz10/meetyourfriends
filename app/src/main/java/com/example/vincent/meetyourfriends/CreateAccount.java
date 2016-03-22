package com.example.vincent.meetyourfriends;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.vincent.meetyourfriends.db.DbHelper;
import com.example.vincent.meetyourfriends.db.UsersContract;

public class CreateAccount extends AppCompatActivity {

    private static String sexe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Show the Up button in the action bar.
        setupActionBar();

        Spinner spinnerDays = (Spinner) findViewById(R.id.caSpinnerDays);
        Spinner spinnerMonths = (Spinner) findViewById(R.id.caSpinnerMonths);
        Spinner spinnerYears = (Spinner) findViewById(R.id.caSpinnerYears);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapterDays = ArrayAdapter.createFromResource(this,
                R.array.days, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterMonths = ArrayAdapter.createFromResource(this,
                R.array.months, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterYears = ArrayAdapter.createFromResource(this,
                R.array.years, android.R.layout.simple_spinner_item);

        adapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterMonths.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterYears.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDays.setAdapter(adapterDays);
        spinnerMonths.setAdapter(adapterMonths);
        spinnerYears.setAdapter(adapterYears);
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //

                //add the infos about the apartment to the intent -> so we can show the last intruduced apartment

                Intent intent = new Intent(this, Login.class);

                CreateAccount.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

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

    public void showLogin(View view) {

        Intent intent = new Intent(this, Login.class);

        String mail = ((EditText)findViewById(R.id.caMail)).getText().toString();
        String password = ((EditText)findViewById(R.id.caPassword)).getText().toString();
        String firstname = ((EditText)findViewById(R.id.caFirstname)).getText().toString();
        String lastname = ((EditText)findViewById(R.id.caLastname)).getText().toString();
        Spinner day = (Spinner)findViewById(R.id.caSpinnerDays);
        Spinner month = (Spinner)findViewById(R.id.caSpinnerMonths);
        Spinner year = (Spinner)findViewById(R.id.caSpinnerYears);
        String birthdate = year.getSelectedItem().toString() + "." + (month.getSelectedItemPosition()+1) + "." + day.getSelectedItem().toString();

        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UsersContract.UserEntry.KEY_EMAIL, mail);
        values.put(UsersContract.UserEntry.KEY_PASSWORD, password);
        values.put(UsersContract.UserEntry.KEY_SEXE, sexe);
        values.put(UsersContract.UserEntry.KEY_FIRSTNAME, firstname);
        values.put(UsersContract.UserEntry.KEY_LASTNAME, lastname);
        values.put(UsersContract.UserEntry.KEY_BIRTHDATE, birthdate);

        //db.execSQL(UsersContract.UserEntry.SQL_DELETE_USERS);
        //db.execSQL(UsersContract.UserEntry.CREATE_TABLE_USERS);
        db.insert(UsersContract.UserEntry.TABLE_NAME, null, values);

        startActivity(intent);
    }

}
