package com.example.vincent.meetyourfriends.db;

import android.provider.BaseColumns;

/**
 * Created by acer-oem on 24.03.2016.
 */
public final class CommentairesContract {
    private CommentairesContract() {

    }

    public static abstract class CommentairesEntry implements BaseColumns {
        // Nom de la table
        public static final String TABLE_NAME = "commentaires";

        // Nom des colonnes
        public static final String KEY_ID = "id";
        public static final String KEY_COMMENTAIRE = "commentaire";
        public static final String KEY_ID_USER = "idUser";
        public static final String KEY_ID_EVENT = "idEvent";

        // Nom des Foreign Key
        public static final String FK_ID_USER = "fk_idUser";
        public static final String FK_ID_EVENT = "fk_idEvent";

        // Requête de création de la table
        public static final String CREATE_TABLE_COMMENTAIRES = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_COMMENTAIRE + " STRING NOT NULL, "
                + KEY_ID_USER + " INTEGER NOT NULL, "
                + KEY_ID_EVENT + " INTEGER NOT NULL, "
                + " CONSTRAINT " + FK_ID_USER
                + " FOREIGN KEY(" + KEY_ID_USER + ") "
                + " REFERENCES " + UsersContract.UserEntry.TABLE_NAME + "(" + UsersContract.UserEntry.KEY_ID + "), "
                + " CONSTRAINT " + FK_ID_EVENT
                + " FOREIGN KEY(" + KEY_ID_EVENT + ") "
                + " REFERENCES " + EventsContract.EventEntry.TABLE_NAME + "(" + EventsContract.EventEntry.KEY_ID + ") "
                + " ON DELETE CASCADE "
                + ");";

        public static final String SQL_DELETE_COMMENTAIRES =
                "DROP TABLE IF EXISTS " + CommentairesEntry.TABLE_NAME + ";";
    }
}
