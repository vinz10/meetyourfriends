package com.example.vincent.meetyourfriends;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.vincent.meetyourfriends.db.CommentairesContract;
import com.example.vincent.meetyourfriends.db.DbHelper;
import com.example.vincent.meetyourfriends.db.EventsContract;
import com.example.vincent.meetyourfriends.db.UsersContract;
import com.example.vincent.meetyourfriends.db.UsersInEventContract;
import com.google.android.gms.actions.ItemListIntents;

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CreateEvent extends AppCompatActivity {

    private DbHelper mDbHelper;
    private Spinner users;
    private ArrayAdapter<String> spinnerAdapter;

    private Spinner dayEvent, monthEvent, yearEvent, hourEvent, minuteEvent;

    private String mail;

    private ArrayList<String> listGuest = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String colorAB = sharedPreferences.getString(SettingsActivity.KEY_PREF_COLORAB, "#0A0A2A");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorAB)));
        boolean logo = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_LOGOAB, true);
        actionBar.setDisplayUseLogoEnabled(logo);
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
        addUserList();
        initializeDate();
        loadData();
    }

    // J'ai ajouté ça
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

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

        String eventName = ((EditText)findViewById(R.id.createEventName)).getText().toString();
        String eventDescription = ((EditText)findViewById(R.id.createEventDescription)).getText().toString();
        String eventLongitude = ((EditText)findViewById(R.id.createLong)).getText().toString();
        String eventLatitude = ((EditText)findViewById(R.id.createLat)).getText().toString();
        String day = ((Spinner)findViewById(R.id.dayEvent)).getSelectedItem().toString();
        String month = ((Spinner)findViewById(R.id.monthEvent)).getSelectedItem().toString();
        String year = ((Spinner)findViewById(R.id.yearEvent)).getSelectedItem().toString();
        String hour = ((Spinner)findViewById(R.id.hourEvent)).getSelectedItem().toString();
        String minute = ((Spinner)findViewById(R.id.minuteEvent)).getSelectedItem().toString();

        Intent intent = new Intent(this, Localisation.class);
        intent.putExtra("eventName", eventName);
        intent.putExtra("eventDescription", eventDescription);
        intent.putExtra("eventLongitude", eventLongitude);
        intent.putExtra("eventLatitude", eventLatitude);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        intent.putExtra("hour", hour);
        intent.putExtra("minute", minute);
        intent.putExtra("listGuest", listGuest);
        startActivity(intent);
    }

    private void loadData() {
        EditText eventName = (EditText)findViewById(R.id.createEventName);
        EditText eventDescription = (EditText)findViewById(R.id.createEventDescription);
        EditText eventLongitude = (EditText)findViewById(R.id.createLong);
        EditText eventLatitude = (EditText)findViewById(R.id.createLat);

        Intent intent = getIntent();

        eventName.setText(intent.getStringExtra("eventName"));
        eventDescription.setText(intent.getStringExtra("eventDescription"));
        eventLongitude.setText(intent.getStringExtra("eventLongitude"));
        eventLatitude.setText(intent.getStringExtra("eventLatitude"));

        dayEvent.setSelection(((ArrayAdapter) dayEvent.getAdapter()).getPosition(intent.getStringExtra("day")));
        monthEvent.setSelection(((ArrayAdapter) monthEvent.getAdapter()).getPosition(intent.getStringExtra("month")));
        yearEvent.setSelection(((ArrayAdapter) yearEvent.getAdapter()).getPosition(intent.getStringExtra("year")));
        hourEvent.setSelection(((ArrayAdapter) hourEvent.getAdapter()).getPosition(intent.getStringExtra("hour")));
        minuteEvent.setSelection(((ArrayAdapter) minuteEvent.getAdapter()).getPosition(intent.getStringExtra("minute")));

        listGuest = intent.getStringArrayListExtra("listGuest");

        if (listGuest != null) {
            final ListView listview = (ListView) findViewById(R.id.listGuest);

            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, listGuest);

            listview.setAdapter(adapter);
        } else {
            listGuest = new ArrayList<String>();
        }
    }

    private void addUserList() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        readCacheFile();

        // Ressortir le prénom et le nom de chaque utilisateur
        String sql = "SELECT " + UsersContract.UserEntry.KEY_EMAIL + ", " + UsersContract.UserEntry.KEY_FIRSTNAME + ", " + UsersContract.UserEntry.KEY_LASTNAME
                + " FROM " + UsersContract.UserEntry.TABLE_NAME
                + " WHERE " + UsersContract.UserEntry.KEY_EMAIL + " != '" + mail
                + "' ORDER BY " + UsersContract.UserEntry.KEY_LASTNAME
                + ";";

        Cursor c = db.rawQuery(sql, null);

        // Pour chaque utilisateur l'ajouter dans le spinner
        users = (Spinner)findViewById(R.id.createListUser);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        users.setAdapter(spinnerAdapter);

        while(c.moveToNext()) {
            String userName = c.getString(2) + " " + c.getString(1);
            spinnerAdapter.add(userName);
        }

        spinnerAdapter.notifyDataSetChanged();

        //listGuest = (ScrollView)findViewById(R.id.CreateListGuest);
    }

    private void initializeDate() {
        dayEvent = (Spinner)findViewById(R.id.dayEvent);
        ArrayAdapter<CharSequence> adapterDays = ArrayAdapter.createFromResource(this,
                R.array.days, android.R.layout.simple_spinner_item);
        adapterDays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayEvent.setAdapter(adapterDays);

        monthEvent = (Spinner)findViewById(R.id.monthEvent);
        ArrayAdapter<CharSequence> adapterMonth = ArrayAdapter.createFromResource(this,
                R.array.months, android.R.layout.simple_spinner_item);
        adapterMonth.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthEvent.setAdapter(adapterMonth);

        yearEvent = (Spinner)findViewById(R.id.yearEvent);
        ArrayAdapter<CharSequence> adapterYear = ArrayAdapter.createFromResource(this,
                R.array.yearEvent, android.R.layout.simple_spinner_item);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearEvent.setAdapter(adapterYear);

        hourEvent = (Spinner)findViewById(R.id.hourEvent);
        ArrayAdapter<CharSequence> adapterHour = ArrayAdapter.createFromResource(this,
                R.array.hours, android.R.layout.simple_spinner_item);
        adapterHour.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hourEvent.setAdapter(adapterHour);

        minuteEvent = (Spinner)findViewById(R.id.minuteEvent);
        ArrayAdapter<CharSequence> adapterMinute = ArrayAdapter.createFromResource(this,
                R.array.minutes, android.R.layout.simple_spinner_item);
        adapterMinute.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minuteEvent.setAdapter(adapterMinute);
    }

    public void addGuest(View view) {
        Spinner cUser = (Spinner) findViewById(R.id.createListUser);
        String currentUser = cUser.getSelectedItem().toString();

        if(guestExist(currentUser)) {
            // Ce q ue j'ai fait moi
            final ListView listview = (ListView) findViewById(R.id.listGuest);

            listGuest.add(currentUser);

            // J'ai ajouté cette méthode un peu plus haut
            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, listGuest);

            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                    final String item = (String) parent.getItemAtPosition(position);
                    view.animate().setDuration(1000).alpha(0).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            listGuest.remove(item);
                            adapter.notifyDataSetChanged();
                            view.setAlpha(1);
                        }
                    });
                }

            });
        }

    }

    private boolean guestExist(String user) {
        // Si le user est déjà invité retourne faux, sinon vrai
        for(String guest : listGuest) {
            if(guest.equals(user))
                return false;
        }
        return true;
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
            Intent intent = new Intent(this, ShowEvent.class);

            long idEvent = createEvent();
            createGuest(idEvent);

            startActivity(intent);

        } else {
            TextView errorMessage = (TextView)findViewById(R.id.createEventError);
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    private long createEvent() {
        // Création des variables à enregistrer dans la base de donnée
        String eventName = ((EditText)findViewById(R.id.createEventName)).getText().toString();
        String eventDescription = ((EditText)findViewById(R.id.createEventDescription)).getText().toString();
        String eventLongitude = ((EditText)findViewById(R.id.createLong)).getText().toString();
        String eventLatitude = ((EditText)findViewById(R.id.createLat)).getText().toString();
        String date = ((Spinner)findViewById(R.id.dayEvent)).getSelectedItem().toString() + "."
                + ((Spinner)findViewById(R.id.monthEvent)).getSelectedItem().toString() + "."
                + ((Spinner)findViewById(R.id.yearEvent)).getSelectedItem().toString();
        String hour = ((Spinner)findViewById(R.id.hourEvent)).getSelectedItem().toString() + "."
                + ((Spinner)findViewById(R.id.minuteEvent)).getSelectedItem().toString();
        int idCreator = getIdUserByMail();

        // Insertion dans la base de donnée
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EventsContract.EventEntry.KEY_NAME, eventName);
        values.put(EventsContract.EventEntry.KEY_DESCRIPTION, eventDescription);
        values.put(EventsContract.EventEntry.KEY_LONGITUDE, eventLongitude);
        values.put(EventsContract.EventEntry.KEY_LATITUDE, eventLatitude);
        values.put(EventsContract.EventEntry.KEY_DATE, date);
        values.put(EventsContract.EventEntry.KEY_TIME, hour);
        values.put(EventsContract.EventEntry.KEY_ID_USER, idCreator);

        long idEvent = db.insert(EventsContract.EventEntry.TABLE_NAME, null, values);

        return idEvent;
    }

    private void createGuest(long idEvent) {
        int idUser;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        for(String user : listGuest) {
            String []ar = user.toString().split("[ ]");

            idUser = getIdUserByName(ar[0], ar[1]);

            ContentValues values = new ContentValues();
            values.put(UsersInEventContract.UsersInEventEntry.KEY_ID_EVENT, idEvent);
            values.put(UsersInEventContract.UsersInEventEntry.KEY_ID_USER, idUser);

            db.insert(UsersInEventContract.UsersInEventEntry.TABLE_NAME, null, values);
        }
    }

    private boolean infoChecked() {
        if(checkedEventName() && checkedDescription() && checkedLocation())
            return true;
        else
            return false;
    }

    private boolean checkedEventName() {
        String eventName = ((EditText)findViewById(R.id.createEventName)).getText().toString();
        if(eventName.equals(""))
            return false;
        else
            return true;
    }

    private boolean checkedDescription() {
        String eventDescription = ((EditText)findViewById(R.id.createEventDescription)).getText().toString();
        if(eventDescription.equals(""))
            return false;
        else
            return true;

    }

    private boolean checkedLocation() {
        String eventLong = ((EditText)findViewById(R.id.createLong)).getText().toString();
        String eventLat = ((EditText)findViewById(R.id.createLat)).getText().toString();
        if(eventLong.equals("") && eventLat.equals(""))
            return false;
        else
            return true;
    }

    private void readCacheFile() {
        // Lecture du fichier cache
        String fileName = "cache.txt";
        String [] temp;
        int ch;

        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;

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

        mail = temp[0].toString();
    }

    private int getIdUserByMail() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "SELECT " + UsersContract.UserEntry.KEY_ID
                + " FROM " + UsersContract.UserEntry.TABLE_NAME
                + " WHERE " + UsersContract.UserEntry.KEY_EMAIL + " = '" + mail
                + "';";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c.getInt(0);
    }

    private int getIdUserByName(String lastname, String firstname) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "SELECT " + UsersContract.UserEntry.KEY_ID
                + " FROM " + UsersContract.UserEntry.TABLE_NAME
                + " WHERE " + UsersContract.UserEntry.KEY_LASTNAME + " = '" + lastname
                + "' AND " + UsersContract.UserEntry.KEY_FIRSTNAME + " = '" + firstname
                + "';";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        return c.getInt(0);
    }
}
