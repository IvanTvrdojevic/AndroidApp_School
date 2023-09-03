package com.example.projekt;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final int databaseVersion = 1;
    private static final String databaseName = "UsersDatabase.db";
    static final String tableName = "users";
    static final String columnID = "id";
    static final String columnUsername = "username";
    static final String columnPassword = "password";

    private static final String createTable = "CREATE TABLE " + tableName + " ("
            + columnID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + columnUsername + " TEXT, "
            + columnPassword + " TEXT)";

    public DBHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
        onCreate(db);
    }
}
