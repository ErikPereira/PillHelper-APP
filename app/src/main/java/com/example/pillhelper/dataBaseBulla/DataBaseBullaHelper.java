package com.example.pillhelper.dataBaseBulla;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.pillhelper.utils.Constants.NAME_BULLA;
import static com.example.pillhelper.utils.Constants.TITLE_BULLA;
import static com.example.pillhelper.utils.Constants.DESCRIPTION_BULLA;
import static com.example.pillhelper.utils.Constants.INFORMATION_BULLA;

public class DataBaseBullaHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "bulla_table";
    private static final String COL0 = NAME_BULLA;
    private static final String COL1 = TITLE_BULLA;
    private static final String COL2 = DESCRIPTION_BULLA;
    private static final String COL3 = INFORMATION_BULLA;

    public DataBaseBullaHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL0 + " TEXT, " +
                COL1 + " TEXT, " +
                COL2 + " TEXT, " +
                COL3 + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String nameBulla, String title, String description, String information) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, nameBulla);
        contentValues.put(COL1, title);
        contentValues.put(COL2, description);
        contentValues.put(COL3, information);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public Integer removeData(String nameBulla) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "nameBulla = ?", new String[] { nameBulla });
    }
}
