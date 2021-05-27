package com.example.pillhelper.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.pillhelper.utils.Constants.ID_CAIXA;
import static com.example.pillhelper.utils.Constants.NOME_CAIXA;

import androidx.annotation.Nullable;

public class DataBaseBoxHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "boxes_table";
    private static final String COL0 = ID_CAIXA;
    private static final String COL1 = NOME_CAIXA;

    public DataBaseBoxHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL0 + " TEXT PRIMARY KEY," + COL1 + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String uuidBox, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, uuidBox);
        contentValues.put(COL1, name);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public boolean updateData(String uuidBox, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, uuidBox);
        contentValues.put(COL1, name);

        db.update(TABLE_NAME, contentValues, "uuidBox = ?", new String[] { uuidBox });
        return true;
    }

    public Integer removeData(String uuidBox) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "uuidBox = ?", new String[] { uuidBox });
    }
}
