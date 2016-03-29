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

    private final ArrayList<String> listGuest = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String colorAB = sharedPreferences.getString(SettingsActivity.KEY_PREF_COLORAB, "");
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
        Intent intent = new Intent(this, Localisation.class);
        startActivity(intent);
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
        /*
        1. Création d'une zone pour chaque invité (bouton + label)
        2. Lors de l'ajout, supprimer du spinner pour éviter les doublons
        3. Ajouter la zone dans le scrollview
         */

        // Ce que tu avais fait avant......


/*        LinearLayout guestContent = new LinearLayout(this);
        Button removeGuest = new Button(this);
        TextView guestName = new TextView(this);

        guestContent.setOrientation(LinearLayout.HORIZONTAL);
        guestName.setText(users.getSelectedView().toString());
        removeGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //1. Supprimer le guest de la liste
                //2. Rajouter le guest dans le spinner pour pouvoir le remettre
                //3. Remettre le spinner par ordre alphabétique

               listGuest.removeView(v.getRootView());

            }
        });
*/

        // Ce que j'ai fait moi
        final ListView listview = (ListView) findViewById(R.id.listGuest);

        Spinner cUser = (Spinner) findViewById(R.id.createListUser);
        String currentUser = cUser.getSelectedItem().toString();

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

    private boolean infoChecked() { return true; }

    private boolean checkedEventName() { return true; }

    private boolean checkedDescription() { return true; }

    private boolean checkedLocation() { return true; }

    private boolean checkedDate() { return true; }

    private boolean checkedTime() { return true; }

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
}
