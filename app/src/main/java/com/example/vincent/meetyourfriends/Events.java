package com.example.vincent.meetyourfriends;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Events extends AppCompatActivity {

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

        final ListView listview = (ListView) findViewById(R.id.listview);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
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
}
