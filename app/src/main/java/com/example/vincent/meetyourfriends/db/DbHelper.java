package com.example.vincent.meetyourfriends.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vincent on 15.03.2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    // Informations de la base de données
    private static final String DATABASE_NAME = "meetyourfriends.db";
    private static final int DATABASE_VESRION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VESRION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(UsersContract.UserEntry.SQL_DELETE_USERS);
        db.execSQL(UsersContract.UserEntry.CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(UsersContract.UserEntry.SQL_DELETE_USERS);
        db.execSQL(UsersContract.UserEntry.CREATE_TABLE_USERS);
    }
}
