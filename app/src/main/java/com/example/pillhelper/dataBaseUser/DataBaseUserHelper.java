package com.example.pillhelper.dataBaseUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseUserHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "users_table";
    private static final String COL0 = "ID";
    private static final String COL1 = "type";
    private static final String COL2 = "cell";
    private static final String COL3 = "email";
    private static final String COL4 = "password";

    public DataBaseUserHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL0 + " INTEGER PRIMARY KEY," + COL1 + " INTEGER,"
                + COL2 + " TEXT," + COL3 + " TEXT," + COL4 + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(int type, String email, String cell, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, type);
        if (type == 1) {
            contentValues.put(COL3, email);
        } else {
            contentValues.put(COL2, cell);
        }
        contentValues.put(COL4, password);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }
}
