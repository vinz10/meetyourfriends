package com.example.vincent.meetyourfriends.db;

// IMPORTATIONS
import android.provider.BaseColumns;

// Classe pour la gestion de la table des événements
public final class EventsContract {

    // Constructeur
    private EventsContract() {}

    public static abstract class EventEntry implements BaseColumns {
        // Nom de la table
        public static final String TABLE_NAME = "events";

        // Nom des colonnes de la table Event
        public static final String KEY_ID = "id";
        public static final String KEY_NAME = "name";
        public static final String KEY_DESCRIPTION = "description";
        public static final String KEY_DATE = "date";
        public static final String KEY_TIME = "time";
        public static final String KEY_LONGITUDE = "longitude";
        public static final String KEY_LATITUDE = "latitude";
        public static final String KEY_ID_USER = "idUser";

        // Nom de clé étrangère
        public static final String FK_ID_USER = "fk_idUser";

        // Requête de création de table
        public static final String CREATE_TABLE_EVENTS = "CREATE TABLE "
                + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT NOT NULL, "
                + KEY_DESCRIPTION + " TEXT NOT NULL, "
                + KEY_DATE + " TEXT NOT NULL, "
                + KEY_TIME + " TEXT NOT NULL, "
                + KEY_LONGITUDE + " TEXT NOT NULL, "
                + KEY_LATITUDE + " TEXT NOT NULL, "
                + KEY_ID_USER + " INTEGER NOT NULL, "
                + " CONSTRAINT " + FK_ID_USER
                + " FOREIGN KEY(" + KEY_ID_USER + ") "
                + " REFERENCES " + UsersContract.UserEntry.TABLE_NAME + "(" + UsersContract.UserEntry.KEY_ID + ") "
                + " ON DELETE CASCADE "
                + ");";

        // Requête de suppression de la table
        public static final String SQL_DELETE_EVENTS =
                "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME + ";";
    }
}