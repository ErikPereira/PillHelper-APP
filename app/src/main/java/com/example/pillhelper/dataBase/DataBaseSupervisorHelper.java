package com.example.pillhelper.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.pillhelper.utils.Constants.ID_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.REGISTRADO_POR;
import static com.example.pillhelper.utils.Constants.VINCULO;
import static com.example.pillhelper.utils.Constants.NOME_SUPERVISOR;

import androidx.annotation.Nullable;

public class DataBaseSupervisorHelper extends SQLiteOpenHelper {
    private static final String TABLE_NAME = "supervisors_table";
    private static final String COL0 = ID_SUPERVISOR;
    private static final String COL1 = REGISTRADO_POR;
    private static final String COL2 = VINCULO;
    private static final String COL3 = NOME_SUPERVISOR;

    public DataBaseSupervisorHelper(@Nullable Context context) {
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

    public boolean addData(String uuidSupervisor, String registeredBy, String bond,  String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, uuidSupervisor);
        contentValues.put(COL1, registeredBy);
        contentValues.put(COL2, bond);
        contentValues.put(COL3, name);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public boolean updateData(String uuidSupervisor, String registeredBy, String bond,  String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, uuidSupervisor);
        contentValues.put(COL1, registeredBy);
        contentValues.put(COL2, bond);
        contentValues.put(COL3, name);

        db.update(TABLE_NAME, contentValues, "uuidSupervisor = ?", new String[] { uuidSupervisor });
        return true;
    }

    public Integer removeData(String uuidSupervisor) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "uuidSupervisor = ?", new String[] { uuidSupervisor });
    }
}
