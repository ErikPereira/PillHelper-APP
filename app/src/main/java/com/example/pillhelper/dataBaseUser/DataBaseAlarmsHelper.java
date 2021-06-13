package com.example.pillhelper.dataBaseUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.pillhelper.utils.Constants.ID_ALARME;
import static com.example.pillhelper.utils.Constants.ALARM_TYPE;
import static com.example.pillhelper.utils.Constants.ATIVO;
import static com.example.pillhelper.utils.Constants.BOX_POSITION;
import static com.example.pillhelper.utils.Constants.DOMINGO;
import static com.example.pillhelper.utils.Constants.DOSAGEM;
import static com.example.pillhelper.utils.Constants.HORA;
import static com.example.pillhelper.utils.Constants.LUMINOSO;
import static com.example.pillhelper.utils.Constants.MEDICINE_TYPE;
import static com.example.pillhelper.utils.Constants.MINUTO;
import static com.example.pillhelper.utils.Constants.NOME_REMEDIO;
import static com.example.pillhelper.utils.Constants.NOTIFICATION_ID;
import static com.example.pillhelper.utils.Constants.PERIODO_HORA;
import static com.example.pillhelper.utils.Constants.PERIODO_MIN;
import static com.example.pillhelper.utils.Constants.QUANTIDADE;
import static com.example.pillhelper.utils.Constants.QUANTIDADE_BOX;
import static com.example.pillhelper.utils.Constants.QUARTA;
import static com.example.pillhelper.utils.Constants.QUINTA;
import static com.example.pillhelper.utils.Constants.SABADO;
import static com.example.pillhelper.utils.Constants.SEGUNDA;
import static com.example.pillhelper.utils.Constants.SEXTA;
import static com.example.pillhelper.utils.Constants.SONORO;
import static com.example.pillhelper.utils.Constants.TERCA;
import static com.example.pillhelper.utils.Constants.VEZES_DIA;

public class DataBaseAlarmsHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "alarms_table";
    private static final String COL0 = ID_ALARME;
    private static final String COL1 = ALARM_TYPE;// 1 == fixo 2 == intervalo
    private static final String COL2 = MEDICINE_TYPE;// 1 == pilula 2 == liquid
    private static final String COL3 = ATIVO;// 1 == active 0 == inativo
    private static final String COL4 = NOME_REMEDIO;
    private static final String COL5 = DOSAGEM;
    private static final String COL6 = QUANTIDADE;
    private static final String COL7 = QUANTIDADE_BOX;
    private static final String COL8 = HORA;
    private static final String COL9 = MINUTO;
    private static final String COL10 = DOMINGO;
    private static final String COL11 = SEGUNDA;
    private static final String COL12 = TERCA;
    private static final String COL13 = QUARTA;
    private static final String COL14 = QUINTA;
    private static final String COL15 = SEXTA;
    private static final String COL16 = SABADO;
    private static final String COL17 = VEZES_DIA;
    private static final String COL18 = PERIODO_HORA;
    private static final String COL19 = PERIODO_MIN;
    private static final String COL20 = NOTIFICATION_ID;
    private static final String COL21 = LUMINOSO;
    private static final String COL22 = SONORO;
    private static final String COL23 = BOX_POSITION;

    public DataBaseAlarmsHelper(@Nullable Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + COL0 + " TEXT PRIMARY KEY," + COL1 + " INTEGER,"
                + COL2 + " INTEGER," + COL3 + " INTEGER," + COL4 + " TEXT," + COL5 + " INTEGER," + COL6 + " INTEGER,"
                + COL7 + " INTEGER," + COL8 + " INTEGER," + COL9 + " INTEGER," + COL10 + " INTEGER," + COL11
                + " INTEGER," + COL12 + " INTEGER," + COL13 + " INTEGER," + COL14 + " INTEGER," + COL15 + " INTEGER,"
                + COL16 + " INTEGER," + COL17 + " INTEGER," + COL18 + " INTEGER," + COL19 + " INTEGER," + COL20
                + " INTEGER," + COL21 + " INTEGER," + COL22 + " INTEGER," + COL23 + " INTEGER)";

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

    public boolean addData(String uuidAlarm, int alarmType, int medicineType, int active, String name, int dosage,
            int qtd, int qtdBox, int hour, int min, int[] days, int times_day, int period_hour, int period_min,
            int notificationId, int luminous, int sound, int posBox) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, uuidAlarm);
        contentValues.put(COL1, alarmType);
        contentValues.put(COL2, medicineType);
        contentValues.put(COL3, active);
        contentValues.put(COL4, name);
        contentValues.put(COL5, dosage);
        contentValues.put(COL6, qtd);
        contentValues.put(COL7, qtdBox);
        contentValues.put(COL8, hour);
        contentValues.put(COL9, min);
        contentValues.put(COL10, days[0]);
        contentValues.put(COL11, days[1]);
        contentValues.put(COL12, days[2]);
        contentValues.put(COL13, days[3]);
        contentValues.put(COL14, days[4]);
        contentValues.put(COL15, days[5]);
        contentValues.put(COL16, days[6]);
        contentValues.put(COL17, times_day);
        contentValues.put(COL18, period_hour);
        contentValues.put(COL19, period_min);
        contentValues.put(COL20, notificationId);
        contentValues.put(COL21, luminous);
        contentValues.put(COL22, sound);
        contentValues.put(COL23, posBox);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public boolean updateData(String uuidAlarm, int alarmType, int medicineType, int active, String name, int dosage,
            int qtd, int qtdBox, int hour, int min, int[] days, int times_day, int period_hour, int period_min,
            int notificationId, int luminous, int sound, int posBox) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, uuidAlarm);
        contentValues.put(COL1, alarmType);
        contentValues.put(COL2, medicineType);
        contentValues.put(COL3, active);
        contentValues.put(COL4, name);
        contentValues.put(COL5, dosage);
        contentValues.put(COL6, qtd);
        contentValues.put(COL7, qtdBox);
        contentValues.put(COL8, hour);
        contentValues.put(COL9, min);
        contentValues.put(COL10, days[0]);
        contentValues.put(COL11, days[1]);
        contentValues.put(COL12, days[2]);
        contentValues.put(COL13, days[3]);
        contentValues.put(COL14, days[4]);
        contentValues.put(COL15, days[5]);
        contentValues.put(COL16, days[6]);
        contentValues.put(COL17, times_day);
        contentValues.put(COL18, period_hour);
        contentValues.put(COL19, period_min);
        contentValues.put(COL20, notificationId);
        contentValues.put(COL21, luminous);
        contentValues.put(COL22, sound);
        contentValues.put(COL23, posBox);

        db.update(TABLE_NAME, contentValues, "uuidAlarm = ?", new String[] { uuidAlarm });
        return true;
    }

    public Integer removeData(String uuidAlarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "uuidAlarm = ?", new String[] { uuidAlarm });
    }
}
