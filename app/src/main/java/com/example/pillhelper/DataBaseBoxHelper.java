package com.example.pillhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.pillhelper.Constants.ID_CAIXA;
import static com.example.pillhelper.Constants.ID_USUARIO;
import static com.example.pillhelper.Constants.NOME_CAIXA;

import androidx.annotation.Nullable;

public class DataBaseBoxHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "boxes_table";
    private static final String COL0 = ID_CAIXA;
    private static final String COL1 = ID_USUARIO;
    private static final String COL2 = NOME_CAIXA;

    DataBaseBoxHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL0 + " TEXT PRIMARY KEY," +
                COL1 + " TEXT," +
                COL2 + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    boolean addData(String uuidBox, String nome) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, uuidBox);
        contentValues.put(COL2, nome);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public boolean updateData(String uuidBox, String uuidUser, String nome) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, uuidBox);
        contentValues.put(COL1, uuidUser);
        contentValues.put(COL2, nome);

        db.update(TABLE_NAME, contentValues, "uuidBox = ?", new String[]{uuidBox});
        return true;
    }

    Integer removeData(String uuidBox) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "uuidBox = ?", new String[]{uuidBox});
    }
}
