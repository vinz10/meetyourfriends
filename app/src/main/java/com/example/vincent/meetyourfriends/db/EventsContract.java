package com.example.vincent.meetyourfriends.db;

import android.provider.BaseColumns;

/**
 * Created by acer-oem on 24.03.2016.
 */
public final class EventsContract {

    private EventsContract() {

    }

    public static abstract class EventEntry implements BaseColumns {
        // nom de la table
        public static final String TABLE_NAME = "events";

        // nom des colonnes de la table Event
        public static final String KEY_ID = "id";


        // requête de création de table
        public static final String CREATE_TABLE_USERS = "CREATE TABLE "
                + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                + ");";

    }
}
