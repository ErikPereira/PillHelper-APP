package com.example.pillhelper.dataBaseSupervisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.pillhelper.utils.Constants.NOME_USER;
import static com.example.pillhelper.utils.Constants.REGISTRADO_POR;
import static com.example.pillhelper.utils.Constants.VINCULO;

public class DataBaseBoundUserHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "bound_user_table";
    private static final String COL0 = "uuidUser";
    private static final String COL1 = REGISTRADO_POR;
    private static final String COL2 = VINCULO;
    private static final String COL3 = NOME_USER;

    public DataBaseBoundUserHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL0 + " TEXT PRIMARY KEY," +
                COL1 + " TEXT," +
                COL2 + " TEXT," +
                COL3 + " TEXT)";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public boolean addData(String uuidUser, String registeredBy, String bond,  String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, uuidUser);
        contentValues.put(COL1, registeredBy);
        contentValues.put(COL2, bond);
        contentValues.put(COL3, name);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }



    public boolean updateData(String uuidUser, String registeredBy, String bond,  String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, uuidUser);
        contentValues.put(COL1, registeredBy);
        contentValues.put(COL2, bond);
        contentValues.put(COL3, name);

        db.update(TABLE_NAME, contentValues, "uuidUser = ?", new String[] { uuidUser });
        return true;
    }

    public Integer removeData(String uuidUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "uuidUser = ?", new String[] { uuidUser });
    }
}
