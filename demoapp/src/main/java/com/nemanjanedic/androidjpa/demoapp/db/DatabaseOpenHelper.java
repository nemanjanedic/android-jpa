package com.nemanjanedic.androidjpa.demoapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "Notepad.db";

    public static final String CREATE_SQL =
            "CREATE TABLE \"notepad\" (\"id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "\"name\" VARCHAR NOT NULL, \"created\" INTEGER NOT NULL, \"modified\" INTEGER NOT NULL)";

    public static final String DROP_SQL = "DROP TABLE \"notepad\"";

    public DatabaseOpenHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL(DROP_SQL);
        onCreate(db);
    }

}
