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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.example.vincent.meetyourfriends.db.CommentairesContract;
import com.example.vincent.meetyourfriends.db.DbHelper;
import com.example.vincent.meetyourfriends.db.EventsContract;
import com.example.vincent.meetyourfriends.db.UsersContract;
import com.example.vincent.meetyourfriends.db.UsersInEventContract;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

// Classe pour la afficher un événement
public class ShowEvent extends AppCompatActivity implements OnMapReadyCallback {

    // Déclaration des variables
    private GoogleMap mMap;
    private DbHelper mDbHelper;
    private Intent intent = getIntent();
    private int idEvent, idCreatorEvent;
    private String mail;
    private String eventName, description, date, time, longitude, latitude;
    private String[] dateSplit, hourSplit;
    private String day, month, year, hour, minute;
    private ListView comments;
    private ArrayList<String> listComment;
    private StableArrayAdapter commentAdaptater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event);

        // Notification pour Google Map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapShow);
        mapFragment.getMapAsync(this);

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

        // Affectation de mDbHelper
        mDbHelper = new DbHelper(this);

        // Appel des fonctions pour charger les données
        loadIntent();
        loadView();
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
                ShowEvent.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode à l'initialisation de la Google Map
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sierre and move the camera
        LatLng hesSierre = new LatLng(46.2930614, 7.536943899999983);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(hesSierre).zoom(10).build();
        mMap.addMarker(new MarkerOptions().position(hesSierre).title("HES-SO Sierre"));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void loadIntent() {
        intent = getIntent();
    }

    // Méthode pour charger les données
    private void loadView() {

        // Déclaration et affectation des variables
        final TextView textViewEventName = (TextView)findViewById(R.id.eventNameShow);
        eventName = intent.getStringExtra("eventName");
        textViewEventName.setText(eventName);

        // Appel de la fonction pour récupérer l'événement
        getEvent();

        // Déclaration et affectation de la date et la description
        TextView date = (TextView)findViewById(R.id.showDate);
        TextView description = (TextView)findViewById(R.id.showDescription);
        date.setText(getString(R.string.the) + " " + day + " " + month + " " + year + " " + getString(R.string.at) + " " + hour + ":" + minute);
        description.setText(this.description);

        // Affectation des utilisateurs invités à l'événement
        final ListView guest = (ListView)findViewById(R.id.listGuest);
        final ArrayList<String> guestList = getGuests();
        final StableArrayAdapter guestAdapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, guestList);
        guest.setAdapter(guestAdapter);
        guestAdapter.notifyDataSetChanged();

        // Affecation des commentaires
        comments = (ListView)findViewById(R.id.listComments);

        listComment = getComments();
        if(listComment.size() > 0) {
            commentAdaptater = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, listComment);
            comments.setAdapter(commentAdaptater);
            commentAdaptater.notifyDataSetChanged();
            comments.setSelection((listComment.size() - 1));
        }
    }

    // Méhtode qui permet de récupérer l'événement sélectionné
    private void getEvent() {

        // Déclaration et affectation de db
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Requête SQL
        String sql = "SELECT *"
                + " FROM " + EventsContract.EventEntry.TABLE_NAME
                + " WHERE " + EventsContract.EventEntry.KEY_NAME + " = '" + eventName + "';";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

        // Affectation des valeurs
        idEvent = c.getInt(0);
        description = c.getString(2);
        date = c.getString(3);
        dateSplit = date.split("[.]");
        day = dateSplit[0];
        month = dateSplit[1];
        year = dateSplit[2];
        time = c.getString(4);
        hourSplit = time.split("[.]");
        hour = hourSplit[0];
        minute = hourSplit[1];
        longitude = c.getString(5);
        latitude = c.getString(6);
        idCreatorEvent = c.getInt(7);
    }

    // Méthode qui permet de récupérer les utilisateurs invités à l'événement
    private ArrayList<String> getGuests() {

        // Déclaration et affectation des variables
        ArrayList<String> guestList = new ArrayList<String>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Requête SQL
        String sql = "SELECT " + UsersInEventContract.UsersInEventEntry.KEY_ID_USER
                + " FROM " + UsersInEventContract.UsersInEventEntry.TABLE_NAME
                + " WHERE " + UsersInEventContract.UsersInEventEntry.KEY_ID_EVENT + " = " + idEvent
                + ";";

        Cursor c = db.rawQuery(sql, null);

        // Ajout des utilisateurs
        while(c.moveToNext()) {
            int idGuest = c.getInt(0);
            guestList.add(getGuestById(idGuest));
        }

        // Tri de la liste
        Collections.sort(guestList);

        // Retour de la liste
        return guestList;
    }

    // Méthode qui permet de récupérer un invité en fonction de son identifiant
    private String getGuestById(int idGuest) {

        // Déclaration et affectation de db
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Requête SQL
        String sql = "SELECT " + UsersContract.UserEntry.KEY_FIRSTNAME + ", " + UsersContract.UserEntry.KEY_LASTNAME
                + " FROM " + UsersContract.UserEntry.TABLE_NAME
                + " WHERE " + UsersContract.UserEntry.KEY_ID + " = " + idGuest + ";";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

        // Récupération du nom et du prénom
        String firstName = c.getString(0);
        String lastName = c.getString(1);

        // Retour du nom et du prénom
        return lastName + " " + firstName;
    }

    // Méthode qui permet de récupérer les commentaires
    private ArrayList<String> getComments() {

        // Déclaration et affectation des variables
        ArrayList<String> listComments = new ArrayList<String>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Requête SQL
        String sql = "SELECT " + CommentairesContract.CommentairesEntry.KEY_ID_USER + ", " + CommentairesContract.CommentairesEntry.KEY_COMMENTAIRE
                + " FROM " + CommentairesContract.CommentairesEntry.TABLE_NAME
                + " WHERE " + CommentairesContract.CommentairesEntry.KEY_ID_EVENT + " = " + idEvent
                + ";";

        Cursor c = db.rawQuery(sql, null);

        // Ajout des commentaires
        while(c.moveToNext()) {
            String commentaire = getGuestById(c.getInt(0)) + " : " + c.getString(1);
            listComments.add(commentaire);
        }

        // Retour de la liste des commmentaires
        return listComments;
    }

    // Méthode qui permet d'ajouter un commentaire
    public void addComment(View view) {

        // Déclaration et affectation de newComment
        String newComment = ((EditText)findViewById(R.id.commentShow)).getText().toString();

        // Check si le champ n'est pas vide
        if(newComment != "") {

            // Lecture du fichier cache pour récupérer l'utilisateur connecté
            readCacheFile();

            // Récupération de l'id de l'utilisateur connecté
            int idCurrentUser = getIdUserByMail();

            // Déclaration et affectation de db
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Insertion du commentaire dans la base de données
            ContentValues values = new ContentValues();
            values.put(CommentairesContract.CommentairesEntry.KEY_COMMENTAIRE, newComment);
            values.put(CommentairesContract.CommentairesEntry.KEY_ID_USER, idCurrentUser);
            values.put(CommentairesContract.CommentairesEntry.KEY_ID_EVENT, idEvent);
            db.insert(CommentairesContract.CommentairesEntry.TABLE_NAME, null, values);

            // Refresh de l'affichage des commentaires
            listComment.add(getGuestById(idCurrentUser) + " : " + newComment);

            commentAdaptater = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, listComment);
            comments.setAdapter(commentAdaptater);
            commentAdaptater.notifyDataSetChanged();

            comments.setSelection((listComment.size()-1));
        }
    }

    // Méthode qui permet de supprimer un commentaire
    private void deleteComment(View view, String item) {

    }

    // Méthode qui permet de récupérer l'id de l'utilisateur connecté
    private int getIdUserByMail() {

        // Déclaration et affectation de db
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Requête SQL
        String sql = "SELECT " + UsersContract.UserEntry.KEY_ID
                + " FROM " + UsersContract.UserEntry.TABLE_NAME
                + " WHERE " + UsersContract.UserEntry.KEY_EMAIL + " = '" + mail
                + "';";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

        // Retour de l'id
        return c.getInt(0);
    }

    // Méthode qui lit le cache pour récupérer l'utilisateur connecté
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

        // Affectation du mail
        mail = temp[0].toString();
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
}