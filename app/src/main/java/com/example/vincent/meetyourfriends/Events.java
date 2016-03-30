package com.example.vincent.meetyourfriends;

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
import android.view.MenuInflater;
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

public class Events extends AppCompatActivity {

    private DbHelper mDbHelper;

    private String mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String colorAB = sharedPreferences.getString(SettingsActivity.KEY_PREF_COLORAB, "#0A0A2A");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_action_android);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorAB)));
        boolean logo = sharedPreferences.getBoolean(SettingsActivity.KEY_PREF_LOGOAB, true);
        actionBar.setDisplayUseLogoEnabled(logo);
        actionBar.setDisplayShowHomeEnabled(true);

        mDbHelper = new DbHelper(this);

        final ListView listview = (ListView) findViewById(R.id.listview);

        final ArrayList<String> guestList = getEventList();

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, guestList);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {

                /*final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                list.remove(item);
                                adapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });*/

                Intent intent = new Intent(view.getContext(), ShowEvent.class);
                //Intent intent = getIntent();
                // Mettre le nom de l'event dans l'intent
                String eventName = parent.getItemAtPosition(position).toString();
                intent.putExtra("eventName", eventName);

                startActivity(intent);
            }

        });

        // Show the Up button in the action bar.
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //new MenuInflater(getApplication()).inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //

                //add the infos about the apartment to the intent -> so we can show the last intruduced apartment

                Intent intent = new Intent(this, CreateEvent.class);

                Events.this.startActivity(intent);
                return true;
            case R.id.action_user:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //

                //add the infos about the apartment to the intent -> so we can show the last intruduced apartment

                intent = new Intent(this, ModifyAccount.class);

                Events.this.startActivity(intent);
                return true;
            case R.id.action_settings:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //

                //add the infos about the apartment to the intent -> so we can show the last intruduced apartment

                intent = new Intent(this, SettingsActivity.class);

                Events.this.startActivity(intent);
                return true;
            case R.id.action_logout:

                // Suppression du fichier cache
                this.deleteFile("cache.txt");

                intent = new Intent(this, Login.class);

                Events.this.startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> getEventList() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int idUser = getUserIdByMail();

        String sql = "SELECT " + EventsContract.EventEntry.KEY_NAME
                + " FROM " + EventsContract.EventEntry.TABLE_NAME + " E, " + UsersInEventContract.UsersInEventEntry.TABLE_NAME + " UE"
                + " WHERE E." + EventsContract.EventEntry.KEY_ID + " = UE." + UsersInEventContract.UsersInEventEntry.KEY_ID_EVENT
                + " AND (E." + EventsContract.EventEntry.KEY_ID_USER + " = " + idUser
                + " OR UE." + UsersInEventContract.UsersInEventEntry.KEY_ID_USER + " = " + idUser + ")"
                + " ORDER BY " + EventsContract.EventEntry.KEY_NAME
                + ";";

        Cursor c = db.rawQuery(sql, null);

        ArrayList<String> eventList = new ArrayList<String>();

        while(c.moveToNext()) {
            eventList.add(c.getString(0));
        }

        return eventList;
    }

    private int getUserIdByMail() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        readCacheFile();

        String sql = "SELECT " + UsersContract.UserEntry.KEY_ID
                + " FROM " + UsersContract.UserEntry.TABLE_NAME
                + " WHERE " + UsersContract.UserEntry.KEY_EMAIL + " = '" + mail
                + "';";

        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int idUser = c.getInt(0);

        return idUser;
    }
}
