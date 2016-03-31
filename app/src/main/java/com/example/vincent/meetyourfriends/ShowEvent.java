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
import android.widget.AdapterView;
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

import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ShowEvent extends AppCompatActivity implements OnMapReadyCallback {

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapShow);
        mapFragment.getMapAsync(this);

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

        mDbHelper = new DbHelper(this);

        loadIntent();
        loadView();
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

                ShowEvent.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

    private void loadView() {
        final TextView textViewEventName = (TextView)findViewById(R.id.eventNameShow);
        eventName = intent.getStringExtra("eventName");
        textViewEventName.setText(eventName);

        getEvent();

        TextView date = (TextView)findViewById(R.id.showDate);
        TextView description = (TextView)findViewById(R.id.showDescription);

        date.setText(getString(R.string.the) + " " + day + " " + month + " " + year + " " + getString(R.string.at) + " " + hour + ":" + minute);
        description.setText(this.description);

        final ListView guest = (ListView)findViewById(R.id.listGuest);
        comments = (ListView)findViewById(R.id.listComments);

        final ArrayList<String> guestList = getGuests();

        final StableArrayAdapter guestAdapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, guestList);
        guest.setAdapter(guestAdapter);
        guestAdapter.notifyDataSetChanged();

        listComment = getComments();
        if(listComment.size() > 0) {
            commentAdaptater = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, listComment);
            comments.setAdapter(commentAdaptater);
            commentAdaptater.notifyDataSetChanged();
            comments.setSelection((listComment.size() - 1));
        }

    }

    private void getEvent() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sql = "SELECT *"
                + " FROM " + EventsContract.EventEntry.TABLE_NAME
                + " WHERE " + EventsContract.EventEntry.KEY_NAME + " = '" + eventName + "';";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

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

    private ArrayList<String> getGuests() {
        ArrayList<String> guestList = new ArrayList<String>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sql = "SELECT " + UsersInEventContract.UsersInEventEntry.KEY_ID_USER
                + " FROM " + UsersInEventContract.UsersInEventEntry.TABLE_NAME
                + " WHERE " + UsersInEventContract.UsersInEventEntry.KEY_ID_EVENT + " = " + idEvent
                + ";";

        Cursor c = db.rawQuery(sql, null);

        while(c.moveToNext()) {
            int idGuest = c.getInt(0);
            guestList.add(getGuestById(idGuest));
        }

        Collections.sort(guestList);

        return guestList;
    }

    private String getGuestById(int idGuest) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sql = "SELECT " + UsersContract.UserEntry.KEY_FIRSTNAME + ", " + UsersContract.UserEntry.KEY_LASTNAME
                + " FROM " + UsersContract.UserEntry.TABLE_NAME
                + " WHERE " + UsersContract.UserEntry.KEY_ID + " = " + idGuest + ";";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();

        String firstName = c.getString(0);
        String lastName = c.getString(1);

        return lastName + " " + firstName;
    }

    private ArrayList<String> getComments() {
        ArrayList<String> listComments = new ArrayList<String>();

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String sql = "SELECT " + CommentairesContract.CommentairesEntry.KEY_ID_USER + ", " + CommentairesContract.CommentairesEntry.KEY_COMMENTAIRE
                + " FROM " + CommentairesContract.CommentairesEntry.TABLE_NAME
                + " WHERE " + CommentairesContract.CommentairesEntry.KEY_ID_EVENT + " = " + idEvent
                + ";";

        Cursor c = db.rawQuery(sql, null);

        while(c.moveToNext()) {
            String commentaire = getGuestById(c.getInt(0)) + " : " + c.getString(1);
            listComments.add(commentaire);
        }

        return listComments;
    }

    public void addComment(View view) {
        // 1. Check si le champ n'est pas vide
        // 2. Ecrire le commentaire dans la DB
        // 3. Ajouter dans l'ArrayList commentaire
        // 4. Notifier le changement
        String newComment = ((EditText)findViewById(R.id.commentShow)).getText().toString();

        if(newComment != "") {
            readCacheFile();
            int idCurrentUser = getIdUserByMail();

            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(CommentairesContract.CommentairesEntry.KEY_COMMENTAIRE, newComment);
            values.put(CommentairesContract.CommentairesEntry.KEY_ID_USER, idCurrentUser);
            values.put(CommentairesContract.CommentairesEntry.KEY_ID_EVENT, idEvent);

            db.insert(CommentairesContract.CommentairesEntry.TABLE_NAME, null, values);

            listComment.add(getGuestById(idCurrentUser) + " : " + newComment);

            commentAdaptater = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, listComment);
            comments.setAdapter(commentAdaptater);
            commentAdaptater.notifyDataSetChanged();

            comments.setSelection((listComment.size()-1));
        }
    }

    private void deleteComment(View view, String item) {

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
}
