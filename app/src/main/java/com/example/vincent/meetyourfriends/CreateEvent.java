package com.example.vincent.meetyourfriends;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.vincent.meetyourfriends.db.CommentairesContract;
import com.example.vincent.meetyourfriends.db.DbHelper;
import com.example.vincent.meetyourfriends.db.EventsContract;
import com.example.vincent.meetyourfriends.db.UsersContract;
import com.example.vincent.meetyourfriends.db.UsersInEventContract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateEvent extends AppCompatActivity implements View.OnClickListener {

    private DbHelper mDbHelper;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerAdapter;

    private Button dateButton;
    private DatePickerDialog selectedDate;

    private Button timeButton;
    private TimePickerDialog selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        // Show the Up button in the action bar.
        setupActionBar();

        // Création des tables
        mDbHelper = new DbHelper(this);

        //SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //db.execSQL(EventsContract.EventEntry.SQL_DELETE_EVENTS);
        //db.execSQL(EventsContract.EventEntry.CREATE_TABLE_EVENTS);
        //db.execSQL(UsersInEventContract.UsersInEventEntry.SQL_DELETE_USERSINEVENT);
        //db.execSQL(UsersInEventContract.UsersInEventEntry.CREATE_TABLE_USERSINEVENT);

        // Inserer code à partir d'ici
        addUserList(mDbHelper);

        initializeView();
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

                Intent intent = new Intent(this, Events.class);

                CreateEvent.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLocalisation(View view) {
        Intent intent = new Intent(this, Localisation.class);
        startActivity(intent);
    }

    private void addUserList(DbHelper mDbHelper) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Ressortir le prénom et le nom de chaque utilisateur
        String[] projection = {UsersContract.UserEntry.KEY_FIRSTNAME,
                UsersContract.UserEntry.KEY_LASTNAME};

        // Trier les utilisateurs par leurs noms de famille
        String sortOrder = UsersContract.UserEntry.KEY_LASTNAME;

        Cursor c = db.query(UsersContract.UserEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);

        // Pour chaque utilisateur l'ajouter dans le spinner
        spinner = (Spinner)findViewById(R.id.createListUser);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        while(c.moveToNext()) {
            String userName = c.getString(1) + " " + c.getString(0);
            spinnerAdapter.add(userName);
        }

        spinnerAdapter.notifyDataSetChanged();
    }

    private void initializeView() {
        dateButton = (Button)findViewById(R.id.dateButton);
        dateButton.setOnClickListener(this);

        timeButton = (Button)findViewById(R.id.timeButton);
        timeButton.setOnClickListener(this);
    }

    public void chooseDate(View view) {
        /*
        1. Open a Dialog
        2. Choose Date
        3. Write Date into selectedDate
         */



        Calendar newCalendar = Calendar.getInstance();
        selectedDate = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                TextView date = (TextView)findViewById(R.id.selectedDate);

                date.setText(dayOfMonth + "." + monthOfYear + "." + year);
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void chooseTime(View view) {
        /*
        1. Open a Dialog
        2. Choose Time
        3. Write Date into selectedTime
         */
        Date current = new Date();
        selectedTime = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {

          @Override
          public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
              TextView time = (TextView)findViewById(R.id.selectedTime);

              time.setText(hourOfDay + "h" + minute);
          }
        }, current.getHours(), current.getMinutes(),true);
    }

    public void addGuest(View view) {
        /*
        1. Création d'une zone pour chaque invité (bouton + label)
        2. Lors de l'ajout, supprimer du spinner pour éviter les doublons
        3. Ajouter la zone dans le scrollview
         */


    }

    public void removeGuest() {
        /*
        1. Supprimer le guest de la liste
        2. Rajouter le guest dans le spinner pour pouvoir le remettre
        3. Remettre le spinner par ordre alphabétique
         */


    }

    public void createEvent(View view) {
        /*
        1. Checker si tous les éléments sont rempli
            - S'il manque un élément, l'identiquer à l'utilisateur
        2. Créer l'event et retourner l'id
        3. Créer les UserInEvent avec l'id des users invités et l'id de l'event
        4. Afficher la liste des events
         */
        if (infoChecked()) {
            ContentValues values = new ContentValues();
        }
    }

    private boolean infoChecked() {

        return true;
    }

    private boolean checkedEventName() {

        return true;
    }

    private boolean checkedLocation() {

        return true;
    }

    @Override
    public void onClick(View view) {
        if(view == dateButton) {
            selectedDate.show();
        } else if(view == timeButton) {
            selectedTime.show();
        }
    }
}
