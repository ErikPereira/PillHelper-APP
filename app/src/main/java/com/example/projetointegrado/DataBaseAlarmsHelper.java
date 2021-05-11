package com.example.projetointegrado;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.projetointegrado.Constants.ALARM_TYPE;
import static com.example.projetointegrado.Constants.ATIVO;
import static com.example.projetointegrado.Constants.BOX_POSITION;
import static com.example.projetointegrado.Constants.DOMINGO;
import static com.example.projetointegrado.Constants.DOSAGEM;
import static com.example.projetointegrado.Constants.HORA;
import static com.example.projetointegrado.Constants.LUMINOSO;
import static com.example.projetointegrado.Constants.MEDICINE_TYPE;
import static com.example.projetointegrado.Constants.MINUTO;
import static com.example.projetointegrado.Constants.NOME_REMEDIO;
import static com.example.projetointegrado.Constants.NOTIFICATION_ID;
import static com.example.projetointegrado.Constants.PERIODO_HORA;
import static com.example.projetointegrado.Constants.PERIODO_MIN;
import static com.example.projetointegrado.Constants.QUANTIDADE;
import static com.example.projetointegrado.Constants.QUANTIDADE_BOX;
import static com.example.projetointegrado.Constants.QUARTA;
import static com.example.projetointegrado.Constants.QUINTA;
import static com.example.projetointegrado.Constants.SABADO;
import static com.example.projetointegrado.Constants.SEGUNDA;
import static com.example.projetointegrado.Constants.SEXTA;
import static com.example.projetointegrado.Constants.SONORO;
import static com.example.projetointegrado.Constants.TERCA;
import static com.example.projetointegrado.Constants.VEZES_DIA;

public class DataBaseAlarmsHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "alarms_table";
    private static final String COL0 = "ID";
    private static final String COL1 = ALARM_TYPE;//1 == fixo 2 == intervalo
    private static final String COL2 = MEDICINE_TYPE;//1 == pilula 2 == liquid
    private static final String COL3 = ATIVO;// 1 == ativo 0 == inativo
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
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL0 + " INTEGER PRIMARY KEY," +
                COL1 + " INTEGER," +
                COL2 + " INTEGER," +
                COL3 + " INTEGER," +
                COL4 + " TEXT," +
                COL5 + " INTEGER," +
                COL6 + " INTEGER," +
                COL7 + " INTEGER," +
                COL8 + " INTEGER," +
                COL9 + " INTEGER," +
                COL10 + " INTEGER," +
                COL11 + " INTEGER," +
                COL12 + " INTEGER," +
                COL13 + " INTEGER," +
                COL14 + " INTEGER," +
                COL15 + " INTEGER," +
                COL16 + " INTEGER," +
                COL17 + " INTEGER," +
                COL18 + " INTEGER," +
                COL19 + " INTEGER," +
                COL20 + " INTEGER," +
                COL21 + " INTEGER," +
                COL22 + " INTEGER," +
                COL23 + " INTEGER)";

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

    public boolean addData(int alarmType, int medicineType,
                           int ativo, String nome, int dosagem,
                           int quantidade, int quantidadeBox,
                           int hora, int minuto, int[] dias, int vezes_dia,
                           int periodo_hora, int periodo_minuto, int notificationId,
                           int luminoso, int sonoro, int pos_caixa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, alarmType);
        contentValues.put(COL2, medicineType);
        contentValues.put(COL3, ativo);
        contentValues.put(COL4, nome);
        contentValues.put(COL5, dosagem);
        contentValues.put(COL6, quantidade);
        contentValues.put(COL7, quantidadeBox);
        contentValues.put(COL8, hora);
        contentValues.put(COL9, minuto);
        contentValues.put(COL10, dias[0]);
        contentValues.put(COL11, dias[1]);
        contentValues.put(COL12, dias[2]);
        contentValues.put(COL13, dias[3]);
        contentValues.put(COL14, dias[4]);
        contentValues.put(COL15, dias[5]);
        contentValues.put(COL16, dias[6]);
        contentValues.put(COL17, vezes_dia);
        contentValues.put(COL18, periodo_hora);
        contentValues.put(COL19, periodo_minuto);
        contentValues.put(COL20, notificationId);
        contentValues.put(COL21, luminoso);
        contentValues.put(COL22, sonoro);
        contentValues.put(COL23, pos_caixa);

        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;
    }

    public boolean updateData(String id, int alarmType,
                              int medicineType, int ativo,
                              String nome, int dosagem, int quantidade,
                              int quantidadeBox, int hora, int minuto, int[] dias,
                              int vezes_dia, int periodo_hora, int periodo_minuto,
                              int notificationId, int luminoso, int sonoro, int pos_caixa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL0, id);
        contentValues.put(COL1, alarmType);
        contentValues.put(COL2, medicineType);
        contentValues.put(COL3, ativo);
        contentValues.put(COL4, nome);
        contentValues.put(COL5, dosagem);
        contentValues.put(COL6, quantidade);
        contentValues.put(COL7, quantidadeBox);
        contentValues.put(COL8, hora);
        contentValues.put(COL9, minuto);
        contentValues.put(COL10, dias[0]);
        contentValues.put(COL11, dias[1]);
        contentValues.put(COL12, dias[2]);
        contentValues.put(COL13, dias[3]);
        contentValues.put(COL14, dias[4]);
        contentValues.put(COL15, dias[5]);
        contentValues.put(COL16, dias[6]);
        contentValues.put(COL17, vezes_dia);
        contentValues.put(COL18, periodo_hora);
        contentValues.put(COL19, periodo_minuto);
        contentValues.put(COL20, notificationId);
        contentValues.put(COL21, luminoso);
        contentValues.put(COL22, sonoro);
        contentValues.put(COL23, pos_caixa);

        db.update(TABLE_NAME, contentValues, "ID = ?", new String[]{id});
        return true;
    }

    Integer removeData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }
}
