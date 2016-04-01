package com.example.vincent.meetyourfriends;

// IMPORTATIONS
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.vincent.meetyourfriends.db.DbHelper;
import com.example.vincent.meetyourfriends.db.EventsContract;
import com.example.vincent.meetyourfriends.db.UsersContract;
import com.example.vincent.meetyourfriends.db.UsersInEventContract;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Classe pour la création d'un événement
public class CreateEvent extends AppCompatActivity {

    // Déclaration des variables
    private DbHelper mDbHelper;
    private Spinner users;
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner dayEvent, monthEvent, yearEvent, hourEvent, minuteEvent;
    private String mail, eventNameTemp;
    private ArrayList<String> listGuest = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

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

        // Déclaration et affectation de mDbHelper
        mDbHelper = new DbHelper(this);

        // Appel des fonctions pour initialiser l'affichage
        addUserList();
        initializeDate();
        loadData();

        // Déclaration et affecation des variables
        Button btCreate = ((Button)findViewById(R.id.createCreate));
        Button btModify = ((Button)findViewById(R.id.createModify));

        // Appel de la fonction pour récupérer l'utilisateur connecté
        readCacheFile();
        int idUserConnected = getIdUserByMail();

        // Gestion de l'affichage des boutons création et modification
        Intent intent = getIntent();
        if (intent.getStringExtra("mode").equals("create")) {
            btCreate.setVisibility(View.VISIBLE);
            btModify.setVisibility(View.INVISIBLE);
            eventNameTemp = "";
        } else {
            btCreate.setVisibility(View.INVISIBLE);
            btModify.setVisibility(View.VISIBLE);
            eventNameTemp = intent.getStringExtra("eventNameTemp");
        }

    }

    // Adapteur customisé pour l'affichage des utilisateurs invités dans une listview
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {

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

                // Affichage de la liste des événements
                Intent intent = new Intent(this, Events.class);
                CreateEvent.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode pour afficher la localisation
    public void showLocalisation(View view) {

        // Déclaration et affectation des variables
        String eventName = ((EditText)findViewById(R.id.createEventName)).getText().toString();
        String eventDescription = ((EditText)findViewById(R.id.createEventDescription)).getText().toString();
        String eventLongitude = ((EditText)findViewById(R.id.createLong)).getText().toString();
        String eventLatitude = ((EditText)findViewById(R.id.createLat)).getText().toString();
        String day = ((Spinner)findViewById(R.id.dayEvent)).getSelectedItem().toString();
        String month = ((Spinner)findViewById(R.id.monthEvent)).getSelectedItem().toString();
        String year = ((Spinner)findViewById(R.id.yearEvent)).getSelectedItem().toString();
        String hour = ((Spinner)findViewById(R.id.hourEvent)).getSelectedItem().toString();
        String minute = ((Spinner)findViewById(R.id.minuteEvent)).getSelectedItem().toString();
        String mode = getIntent().getStringExtra("mode");

        // Stockage des valeurs dans le intent pour passer d'une activité à l'autre sans perdre les données
        Intent intent = new Intent(this, Localisation.class);
        intent.putExtra("mode", mode);
        intent.putExtra("eventNameTemp", eventNameTemp);
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

        // Affichage de la localisation
        startActivity(intent);
    }

    // Méthode pour initialiser les données lorsque l'utilisateur revient depuis la localisation
    private void loadData() {

        // Déclaration et affectation des variables
        EditText eventName = (EditText)findViewById(R.id.createEventName);
        EditText eventDescription = (EditText)findViewById(R.id.createEventDescription);
        EditText eventLongitude = (EditText)findViewById(R.id.createLong);
        EditText eventLatitude = (EditText)findViewById(R.id.createLat);

        Intent intent = getIntent();

        // Affectation des variables
        eventName.setText(intent.getStringExtra("eventName"));
        eventDescription.setText(intent.getStringExtra("eventDescription"));
        eventLongitude.setText(intent.getStringExtra("eventLongitude"));
        eventLatitude.setText(intent.getStringExtra("eventLatitude"));
        dayEvent.setSelection(((ArrayAdapter) dayEvent.getAdapter()).getPosition(intent.getStringExtra("day")));
        int monthIndex = Integer.parseInt(intent.getStringExtra("month"));
        monthEvent.setSelection(monthIndex-1);
        yearEvent.setSelection(((ArrayAdapter) yearEvent.getAdapter()).getPosition(intent.getStringExtra("year")));
        hourEvent.setSelection(((ArrayAdapter) hourEvent.getAdapter()).getPosition(intent.getStringExtra("hour")));
        minuteEvent.setSelection(((ArrayAdapter) minuteEvent.getAdapter()).getPosition(intent.getStringExtra("minute")));
        listGuest = intent.getStringArrayListExtra("listGuest");

        // Si la liste n'est pas null
        if (listGuest != null) {
            // Affectation de l'adaptateur à la listview
            final ListView listview = (ListView) findViewById(R.id.listGuest);

            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, listGuest);

            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    // Sur le clic d'un utilisateur, on le retire de la liste
                    final String item = (String) parent.getItemAtPosition(position);
                    view.animate().setDuration(1000).alpha(0).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // Suppression de l'utilisateur dans la liste et refresh de l'affichage
                            listGuest.remove(item);
                            adapter.notifyDataSetChanged();
                            view.setAlpha(1);
                        }
                    });
                }
            });
        } else {
            // Nouvelle liste
            listGuest = new ArrayList<String>();
        }
    }

    // Méthode pour ajouter les utilisateurs dans le spinner pour les ajouter en tant qu'invité
    private void addUserList() {

        // Déclaration et affectation de db
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Méthode pour récupérer l'utilisateur connecté
        readCacheFile();

        // Ressortir le prénom et le nom de chaque utilisateur sauf l'utilisateur connecté
        String sql = "SELECT " + UsersContract.UserEntry.KEY_EMAIL + ", " + UsersContract.UserEntry.KEY_FIRSTNAME + ", " + UsersContract.UserEntry.KEY_LASTNAME
                + " FROM " + UsersContract.UserEntry.TABLE_NAME
                + " WHERE " + UsersContract.UserEntry.KEY_EMAIL + " != '" + mail
                + "' ORDER BY " + UsersContract.UserEntry.KEY_LASTNAME
                + ";";

        Cursor c = db.rawQuery(sql, null);

        // Pour chaque utilisateur, l'ajouter dans le spinner
        users = (Spinner)findViewById(R.id.createListUser);
        spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        users.setAdapter(spinnerAdapter);

        while(c.moveToNext()) {
            String userName = c.getString(2) + " " + c.getString(1);
            spinnerAdapter.add(userName);
        }

        spinnerAdapter.notifyDataSetChanged();
    }

    // Méthode pour initialiser les dates dans les spinners
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

    // Méthode pour l'ajout des utilisateurs
    public void addGuest(View view) {

        // Récupération de l'utilisateur sélectionné dans la liste
        Spinner cUser = (Spinner) findViewById(R.id.createListUser);
        String currentUser = cUser.getSelectedItem().toString();

        // Si l'utilisateur n'existe pas déjà dans la liste des invités
        if(guestExist(currentUser)) {

            final ListView listview = (ListView) findViewById(R.id.listGuest);

            // Ajout de l'utilisateur
            listGuest.add(currentUser);

            // Déclaration de notre adapteur customisé pour l'affecter à la listview
            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, listGuest);

            listview.setAdapter(adapter);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                    // Sur le clic d'un utilisateur, on le retire de la liste
                    final String item = (String) parent.getItemAtPosition(position);
                    view.animate().setDuration(1000).alpha(0).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // Suppression de l'utilisateur dans la liste et refresh de l'affichage
                            listGuest.remove(item);
                            adapter.notifyDataSetChanged();
                            view.setAlpha(1);
                        }
                    });
                }
            });
        }
    }

    // Méthode qui détermine si un utilisateur est déjà existant dans la liste des invités
    private boolean guestExist(String user) {

        // Si l'utilisateur est déjà invité retourne faux, sinon vrai
        for(String guest : listGuest) {
            if(guest.equals(user))
                return false;
        }
        return true;
    }

    // Méthode qui crée l'événements
    public void createEvent(View view) {

        // Déclaration et affectation de errorMessage
        TextView errorMessage = (TextView)findViewById(R.id.createEventError);

        // Check si tous les éléments sont remplis et valides
        if (infoChecked("normal")) {
            // On masque le message d'erreur
            errorMessage.setVisibility(View.INVISIBLE);

            Intent intent = new Intent(this, Events.class);

            // Création de l'événement et récupération de l'id
            long idEvent = createEvent();

            // Création de la liste des utilisateurs qui sont invités à l'événement
            createGuest(idEvent);

            // Affichage de la liste des événements
            startActivity(intent);
        } else {
            // On affiche un message d'erreur
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    // Méthode qui permet de modifier un événement
    public void modeModify(View view) {

        // Déclaration et affectation de errorMessage
        TextView errorMessage = (TextView)findViewById(R.id.createEventError);

        // Check si tous les éléments sont remplis et valides
        if (infoChecked("modify")) {
            // On masque le message d'erreur
            errorMessage.setVisibility(View.INVISIBLE);

            // Création des variables à modifier dans la base de donnée
            String eventName = ((EditText)findViewById(R.id.createEventName)).getText().toString();
            String eventDescription = ((EditText)findViewById(R.id.createEventDescription)).getText().toString();
            String eventLongitude = ((EditText)findViewById(R.id.createLong)).getText().toString();
            String eventLatitude = ((EditText)findViewById(R.id.createLat)).getText().toString();
            String date = ((Spinner)findViewById(R.id.dayEvent)).getSelectedItem().toString() + "."
                    + (((Spinner)findViewById(R.id.monthEvent)).getSelectedItemPosition()+1) + "."
                    + ((Spinner)findViewById(R.id.yearEvent)).getSelectedItem().toString();
            String hour = ((Spinner)findViewById(R.id.hourEvent)).getSelectedItem().toString() + "."
                    + ((Spinner)findViewById(R.id.minuteEvent)).getSelectedItem().toString();
            int idEvent = getIdEvent(eventNameTemp);

            // Modification dans la base de donnée
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(EventsContract.EventEntry.KEY_NAME, eventName);
            values.put(EventsContract.EventEntry.KEY_DESCRIPTION, eventDescription);
            values.put(EventsContract.EventEntry.KEY_LONGITUDE, eventLongitude);
            values.put(EventsContract.EventEntry.KEY_LATITUDE, eventLatitude);
            values.put(EventsContract.EventEntry.KEY_DATE, date);
            values.put(EventsContract.EventEntry.KEY_TIME, hour);

            db.update(EventsContract.EventEntry.TABLE_NAME, values, "id=" + idEvent, null);

            // Suppression de la liste des utilisateurs qui sont invités à l'événement
            db.delete(UsersInEventContract.UsersInEventEntry.TABLE_NAME, UsersInEventContract.UsersInEventEntry.KEY_ID_EVENT + "=" + idEvent, null);

            // Création de la liste des utilisateurs qui sont invités à l'événement
            createGuest(idEvent);

            // Retourner sur l'affichage de tous les évenements
            Intent intent = new Intent(this, ShowEvent.class);
            intent.putExtra("eventName", eventName);
            startActivity(intent);
        } else {
            // On affiche un message d'erreur
            errorMessage.setVisibility(View.VISIBLE);
        }
    }

    // Méthode qui insère l'événement dans la base de données et retourne son id
    private long createEvent() {

        // Création des variables à enregistrer dans la base de donnée
        String eventName = ((EditText)findViewById(R.id.createEventName)).getText().toString();
        String eventDescription = ((EditText)findViewById(R.id.createEventDescription)).getText().toString();
        String eventLongitude = ((EditText)findViewById(R.id.createLong)).getText().toString();
        String eventLatitude = ((EditText)findViewById(R.id.createLat)).getText().toString();
        String date = ((Spinner)findViewById(R.id.dayEvent)).getSelectedItem().toString() + "."
                + (((Spinner)findViewById(R.id.monthEvent)).getSelectedItemPosition()+1) + "."
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

        // Retour de l'id
        return idEvent;
    }

    // Méthode qui insère les utilisateurs invités à l'événement dans la base de données (usersInEvent)
    private void createGuest(long idEvent) {

        // Déclaration des variables
        int idUser;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Pour chaque utilisateur dans la liste des invités
        for(String user : listGuest) {

            // Substring du nom et du prénom
            String []ar = user.toString().split("[ ]");

            // Récupération de l'id de l'utilisateur
            idUser = getIdUserByName(ar[0], ar[1]);

            // Insertion dans la base de données
            ContentValues values = new ContentValues();
            values.put(UsersInEventContract.UsersInEventEntry.KEY_ID_EVENT, idEvent);
            values.put(UsersInEventContract.UsersInEventEntry.KEY_ID_USER, idUser);
            db.insert(UsersInEventContract.UsersInEventEntry.TABLE_NAME, null, values);
        }
    }

    // Méthode qui détermine si les données sont remplies correctement
    private boolean infoChecked(String mode) {
        if(checkedEventName(mode) && checkedDescription() && checkedLocation())
            return true;
        else
            return false;
    }

    // Méthode qui détermine si le nom de l'événement est rempli et valide
    private boolean checkedEventName(String mode) {

        // Déclaration et affectation des variables
        TextView errorMessage = (TextView)findViewById(R.id.eventNameExist);
        String eventName = ((EditText)findViewById(R.id.createEventName)).getText().toString();

        // Si l'événement n'est pas rempli
        if(eventName.equals(""))
            return false;
        else {
            // Si l'événement existe déjà dans la base de données
            if(ExistsEventName(eventName, mode)) {
                errorMessage.setVisibility(View.VISIBLE);
                return false;
            } else {
                errorMessage.setVisibility(View.INVISIBLE);
                return true;
            }
        }
    }

    // Méthode qui détermine si la description est remplie
    private boolean checkedDescription() {
        String eventDescription = ((EditText)findViewById(R.id.createEventDescription)).getText().toString();
        if(eventDescription.equals(""))
            return false;
        else
            return true;

    }

    // Méthode qui détermine si la localisation est remplie
    private boolean checkedLocation() {
        String eventLong = ((EditText)findViewById(R.id.createLong)).getText().toString();
        String eventLat = ((EditText)findViewById(R.id.createLat)).getText().toString();
        if(eventLong.equals("") && eventLat.equals(""))
            return false;
        else
            return true;
    }

    // Méthode qui lit le fichier cache pour récupérer l'id de l'utilisateur connecté
    private void readCacheFile() {

        // Déclaration et affectation des variables
        String fileName = "cache.txt";
        String [] temp;
        int ch;
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;

        // Lecture du fichier
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

        // Affectation de l'utilisateur connecté
        mail = temp[0].toString();
    }

    // Méthode qui récupère l'id d'un utilisateur par son mail
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

    // Méthode qui récupère l'id d'un utilisateur par son nom et son prénom
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

    // Méthode qui permet de retourner l'id de l'événement par son nom
    private int getIdEvent(String eventName) {

        // Requête SQL
        Cursor cursor = null;
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String sql = "SELECT id FROM events WHERE name ='" + eventName + "';";
        cursor = db.rawQuery(sql, null);

        // Retour de l'id de l'événement
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    // Méthode qui détermine si le nom de l'événement existe déjà dans la base de données
    private boolean ExistsEventName(String eventName, String mode) {

        Cursor cursor = null;
        String sql = "";
        DbHelper mDbHelper = new DbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if(mode.equals("normal")) {
            sql = "SELECT id FROM events WHERE name ='" + eventName + "';";
        } else {
            sql = "SELECT id FROM events WHERE name ='" + eventName + "' AND name !='" + eventNameTemp + "';";
        }
        cursor = db.rawQuery(sql, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
}