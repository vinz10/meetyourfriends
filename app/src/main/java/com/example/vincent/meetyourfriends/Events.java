package com.example.vincent.meetyourfriends;

// IMPORTATIONS
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

// Classe pour l'affichage des événements
public class Events extends AppCompatActivity {

    // Déclaration des variables
    private DbHelper mDbHelper;
    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

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

        // Affectation de mDbHelper
        mDbHelper = new DbHelper(this);

        // Affichage des événements dans une listview
        final ListView listview = (ListView) findViewById(R.id.listview);
        final ArrayList<String> eventList = getEventList();
        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, eventList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                // Sur le clic d'un événement, on l'affiche en détail

                Intent intent = new Intent(view.getContext(), ShowEvent.class);

                // Mettre le nom de l'event dans l'intent
                String eventName = parent.getItemAtPosition(position).toString();
                intent.putExtra("eventName", eventName);

                // Affichage de l'événement
                startActivity(intent);
            }
        });
    }

    // Adapteur customisé pour l'affichage des listviews
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

    // Méthode qui gère la navigation de la barre d'actions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Cette méthode affecte le menu main à la barre d'actions
        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Méthode qui lit le fichier cache pour récupérer le mot de passe et le mail de l'utilisateur connecté
    private void readCacheFile() {

        // Déclaration et affectation des variables
        String fileName = "cache.txt";
        String [] temp;
        int ch;
        StringBuffer fileContent = new StringBuffer("");
        FileInputStream fis;

        // Lecture du fichier cache
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

        // Affectation du mail de l'utilisateur connecté
        mail = temp[0].toString();
    }

    // Méthode qui gère la navigation de la barre d'actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show:
                // Action pour créer un événement
                Intent intent = new Intent(this, CreateEvent.class);
                intent.putExtra("mode", "create");
                Events.this.startActivity(intent);
                return true;
            case R.id.action_user:
                // Action pour modifier les valeurs de l'utilisateurs
                intent = new Intent(this, ModifyAccount.class);
                Events.this.startActivity(intent);
                return true;
            case R.id.action_settings:
                // Action pour modifier les préférences de l'application
                intent = new Intent(this, SettingsActivity.class);
                Events.this.startActivity(intent);
                return true;
            case R.id.action_logout:
                // Action pour se déconnecter de l'application

                // Suppression du fichier cache
                this.deleteFile("cache.txt");

                intent = new Intent(this, Login.class);
                Events.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Méthode qui récupère les événements
    private ArrayList<String> getEventList() {

        // Déclaration et affectation de db
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Récupération de l'utilisateur connecté
        int idUser = getUserIdByMail();

        // Requête pour récupérer les événements en fonction de l'utilisateur connecté
        String sql = "SELECT DISTINCT E." + EventsContract.EventEntry.KEY_NAME
                + " FROM " + EventsContract.EventEntry.TABLE_NAME + " E, " + UsersInEventContract.UsersInEventEntry.TABLE_NAME + " UE"
                + " WHERE E." + EventsContract.EventEntry.KEY_ID + " = UE." + UsersInEventContract.UsersInEventEntry.KEY_ID_EVENT
                + " AND UE." + UsersInEventContract.UsersInEventEntry.KEY_ID_USER + " = " + idUser
                + " OR E." + EventsContract.EventEntry.KEY_ID_USER + " = " + idUser
                + " ORDER BY E." + EventsContract.EventEntry.KEY_NAME
                + ";";

        Cursor c = db.rawQuery(sql, null);

        ArrayList<String> eventList = new ArrayList<String>();

        while(c.moveToNext()) {
            // Ajout des événements dans la liste
            eventList.add(c.getString(0));
        }

        // Retour de la liste
        return eventList;
    }

    // Méthode qui récupère l'id de l'utilisateur connecté
    private int getUserIdByMail() {

        // Déclaration et affectation de db
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Appel de la fonction pour récupérer l'utilisateur connecté
        readCacheFile();

        String sql = "SELECT " + UsersContract.UserEntry.KEY_ID
                + " FROM " + UsersContract.UserEntry.TABLE_NAME
                + " WHERE " + UsersContract.UserEntry.KEY_EMAIL + " = '" + mail
                + "';";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int idUser = c.getInt(0);

        // Retour de l'id de l'utilisateur connecté
        return idUser;
    }
}