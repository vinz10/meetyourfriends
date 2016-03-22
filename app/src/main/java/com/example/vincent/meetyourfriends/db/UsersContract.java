package com.example.vincent.meetyourfriends.db;

import android.provider.BaseColumns;

/**
 * Created by Vincent on 15.03.2016.
 */
public final class UsersContract {

    private UsersContract() {

    }

    public static abstract class UserEntry implements BaseColumns {

        // Nom de la table
        public static final String TABLE_NAME = "users";


        // Nom des colonnes de la table Users
        public static final String KEY_ID = "id";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_PASSWORD = "password";
        public static final String KEY_FIRSTNAME = "firstname";
        public static final String KEY_LASTNAME = "lastname";
        public static final String KEY_SEXE = "sexe";
        public static final String KEY_BIRTHDATE = "birthdate";

        // Requête de création de la table
        public static final String CREATE_TABLE_USERS = "CREATE TABLE "
                + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_EMAIL + " TEXT NOT NULL, "
                + KEY_PASSWORD + " TEXT NOT NULL, "
                + KEY_FIRSTNAME + " TEXT NOT NULL, "
                + KEY_LASTNAME + " TEXT NOT NULL, "
                + KEY_SEXE + " TEXT NOT NULL, "
                + KEY_BIRTHDATE + " TEXT NOT NULL);";

        public static final String SQL_DELETE_USERS =
                "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME + ";";
    }
}
