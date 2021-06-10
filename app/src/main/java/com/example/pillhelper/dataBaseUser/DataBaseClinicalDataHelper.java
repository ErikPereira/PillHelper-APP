package com.example.pillhelper.dataBaseUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.pillhelper.utils.Constants.NAME_CLINICAL_DATA;
import static com.example.pillhelper.utils.Constants.VALUE_CLINICAL_DATA;

public class DataBaseClinicalDataHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "clinical_data_table";
    private static final String COL0 = NAME_CLINICAL_DATA;
    private static final String COL1 = VALUE_CLINICAL_DATA;

    public DataBaseClinicalDataHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL0 + " TEXT PRIMARY KEY," +
                COL1 + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String name, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, name);
        contentValues.put(COL1, value);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public boolean updateData(String name, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, name);
        contentValues.put(COL1, value);

        db.update(TABLE_NAME, contentValues, "name = ?", new String[] { name });
        return true;
    }

    public Integer removeData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "name = ?", new String[] { name });
    }
}
