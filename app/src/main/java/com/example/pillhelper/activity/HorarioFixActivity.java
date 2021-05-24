package com.example.pillhelper.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pillhelper.AlarmeReceiver;
import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.dataBase.DataBaseAlarmsHelper;
import com.example.pillhelper.JsonPlaceHolderApi;
import com.example.pillhelper.R;
import com.example.pillhelper.UserIdSingleton;
import com.example.pillhelper.databinding.ActivityHorarioFixBinding;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.AlarmManager.RTC_WAKEUP;
import static com.example.pillhelper.utils.Constants.ALARM_TYPE;
import static com.example.pillhelper.utils.Constants.ATIVO;
import static com.example.pillhelper.utils.Constants.BASE_URL;
import static com.example.pillhelper.utils.Constants.BOX_POSITION;
import static com.example.pillhelper.utils.Constants.DOMINGO;
import static com.example.pillhelper.utils.Constants.DOSAGEM;
import static com.example.pillhelper.utils.Constants.HORA;
import static com.example.pillhelper.utils.Constants.ID_ALARME;
import static com.example.pillhelper.utils.Constants.ID_USUARIO;
import static com.example.pillhelper.utils.Constants.LUMINOSO;
import static com.example.pillhelper.utils.Constants.MEDICINE_TYPE;
import static com.example.pillhelper.utils.Constants.MINUTO;
import static com.example.pillhelper.utils.Constants.NOME_REMEDIO;
import static com.example.pillhelper.utils.Constants.NOTIFICATION_ID;
import static com.example.pillhelper.utils.Constants.OPEN_BOX_FRAG;
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

public class HorarioFixActivity extends AppCompatActivity {

    private static final String TAG = "HorarioFixActivity";
    private ActivityHorarioFixBinding binding;
    private DataBaseAlarmsHelper mDataBaseAlarmsHelper;
    private boolean isEdit;
    private int alarmEditPosition;
    private Cursor data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHorarioFixBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle(R.string.action_button_fix_time);

        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
        mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(this);
        data = mDataBaseAlarmsHelper.getData();

        if (isEdit) {
            alarmEditPosition = getIntent().getIntExtra("POSITION", -1);
            data.move(alarmEditPosition + 1);

            binding.sundayDay.setChecked(data.getInt(10) == 1);
            binding.mondayDay.setChecked(data.getInt(11) == 1);
            binding.tuesdayDay.setChecked(data.getInt(12) == 1);
            binding.wednesdayDay.setChecked(data.getInt(13) == 1);
            binding.thursdayDay.setChecked(data.getInt(14) == 1);
            binding.fridayDay.setChecked(data.getInt(15) == 1);
            binding.saturdayDay.setChecked(data.getInt(16) == 1);
            binding.idClockSchedule.setHour(data.getInt(8));
            binding.idClockSchedule.setMinute(data.getInt(9));
        }

        binding.backButtonRegisterMedicine.setOnClickListener(v -> finish());

        binding.nextButtonRegisterMedicine.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);

            int medTipo = getIntent().getIntExtra("MEDICINE_TYPE", 0);
            String nome = getIntent().getStringExtra("MEDICINE_NAME");
            int notificationId = getIntent().getIntExtra("NOTIFICATION_ID", 0);
            int luminoso = getIntent().getIntExtra("LUMINOSO", 0);
            int sonoro = getIntent().getIntExtra("SONORO", 0);

            if (medTipo == 1) {
                int quantidade = getIntent().getIntExtra("MEDICINE_QUANTITY", 0);
                int quantidadeCaixa = getIntent().getIntExtra("MEDICINE_BOX_QUANTITY", 0);
                int posCaixa = getIntent().getIntExtra("BOX_POSITION", 0);
                addDataDB(medTipo, nome, 0, quantidade, quantidadeCaixa, notificationId, luminoso, sonoro, posCaixa);
            } else if (medTipo == 2) {
                int dosagem = getIntent().getIntExtra("MEDICINE_DOSAGE", 0);
                addDataDB(medTipo, nome, dosagem, 0, 0, notificationId, luminoso, sonoro, 0);
            }
        });
    }

    private void addDataDB(int tipoRemedio, String nome, int dosagem, int quantidade, int quantidadeCaixa, int notificationId, int luminoso, int sonoro, int posCaixa) {
        int horas = binding.idClockSchedule.getHour();
        int minutos = binding.idClockSchedule.getMinute();

        int[] dias = new int[7];
        dias[0] = binding.sundayDay.isChecked() ? 1 : 0;
        dias[1] = binding.mondayDay.isChecked() ? 1 : 0;
        dias[2] = binding.tuesdayDay.isChecked() ? 1 : 0;
        dias[3] = binding.wednesdayDay.isChecked() ? 1 : 0;
        dias[4] = binding.thursdayDay.isChecked() ? 1 : 0;
        dias[5] = binding.fridayDay.isChecked() ? 1 : 0;
        dias[6] = binding.saturdayDay.isChecked() ? 1 : 0;

        if (isEdit) {
            int ativo = data.getInt(3);
            int velhaHora = data.getInt(8);
            int velhoMinuto = data.getInt(9);
            String velhoNome = data.getString(4);
            String uuidAlarm = data.getString(0);

            createPostUpdateAlarm(
                    uuidAlarm,
                    tipoRemedio,
                    ativo,
                    velhoNome,
                    nome,
                    dosagem,
                    quantidade,
                    quantidadeCaixa,
                    velhaHora,
                    velhoMinuto,
                    horas,
                    minutos,
                    dias,
                    0,
                    0,
                    0,
                    notificationId,
                    luminoso,
                    sonoro,
                    posCaixa);
        } else {
            createPostCreateAlarm(
                    tipoRemedio,
                    nome,
                    dosagem,
                    quantidade,
                    quantidadeCaixa,
                    horas,
                    minutos,
                    dias,
                    0,
                    0,
                    0,
                    notificationId,
                    luminoso,
                    sonoro,
                    posCaixa);
        }
    }

    private void createPostUpdateAlarm(String uuidAlarm, int medicineType, int ativo, String velhoNome, String nome, int dosagem, int quantidade, int quantidadeBox, int oldHour, int oldMinute, int hora, int minuto, int[] dias, int vezes_dia, int periodo_hora, int periodo_minuto, int notificationId, int luminoso, int sonoro, int posCaixa) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSONupdateAlarm(uuidAlarm, medicineType, ativo, velhoNome, nome, dosagem, quantidade, quantidadeBox, oldHour, oldMinute, hora, minuto, dias, vezes_dia, periodo_hora, periodo_minuto, notificationId, luminoso, sonoro, posCaixa);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postModifyAlarm(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Um erro ocorreu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response);
                    return;
                }

                boolean confirmation = mDataBaseAlarmsHelper.updateData(
                        uuidAlarm,
                        1,
                        medicineType,
                        ativo,
                        nome,
                        dosagem,
                        quantidade,
                        quantidadeBox,
                        hora,
                        minuto,
                        dias,
                        0,
                        0,
                        0,
                        notificationId,
                        luminoso,
                        sonoro,
                        posCaixa);

                if (confirmation) {
                    if (ativo == 1) {
                        cancelAlarmIntent(notificationId);
                        createAlarmIntent(hora, minuto, dias, notificationId);
                    }

                    Intent intent = new Intent(getBaseContext(), FragmentsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(OPEN_BOX_FRAG, false);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(getBaseContext(), "Algo deu errado", Toast.LENGTH_LONG).show();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private void createPostCreateAlarm(int medicineType, String nome, int dosagem, int quantidade, int quantidadeBox, int hora, int minuto, int[] dias, int vezes_dia, int periodo_hora, int periodo_minuto, int notificationId, int luminoso, int sonoro, int posCaixa) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSONnewAlarm(medicineType, nome, dosagem, quantidade, quantidadeBox, hora, minuto, dias, vezes_dia, periodo_hora, periodo_minuto, notificationId, luminoso, sonoro, posCaixa);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postCreateAlarm(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Um erro ocorreu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response);
                    return;
                }

                JsonObject postResponse = response.body();
                String uuidAlarm = postResponse.get("response").getAsString();

                boolean confirmation = mDataBaseAlarmsHelper.addData(
                        uuidAlarm,
                        1,
                        medicineType,
                        1,
                        nome,
                        dosagem,
                        quantidade,
                        quantidadeBox,
                        hora,
                        minuto,
                        dias,
                        0,
                        0,
                        0,
                        notificationId,
                        luminoso,
                        sonoro,
                        posCaixa);


                if (confirmation) {
                    createAlarmIntent(hora, minuto, dias, notificationId);
                    Intent intent = new Intent(getBaseContext(), FragmentsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(OPEN_BOX_FRAG, false);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(getBaseContext(), "Algo deu errado", Toast.LENGTH_LONG).show();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSONupdateAlarm(String uuidAlarm,int medicineType, int ativo, String velhoNome, String nome, int dosagem, int quantidade, int quantidadeBox, int velhaHora, int velhoMinuto, int hora, int minuto, int[] dias, int vezes_dia, int periodo_hora, int periodo_minuto, int notificationId, int luminoso, int sonoro, int posCaixa) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject updateAlarm = new JSONObject();
            updateAlarm.put(ID_ALARME, String.valueOf(uuidAlarm));
            updateAlarm.put(ALARM_TYPE, String.valueOf(1));
            updateAlarm.put(MEDICINE_TYPE, String.valueOf(medicineType));
            updateAlarm.put(ATIVO, String.valueOf(ativo));
            updateAlarm.put(NOME_REMEDIO, String.valueOf(nome));
            updateAlarm.put(DOSAGEM, String.valueOf(dosagem));
            updateAlarm.put(QUANTIDADE, String.valueOf(quantidade));
            updateAlarm.put(QUANTIDADE_BOX, String.valueOf(quantidadeBox));
            updateAlarm.put(HORA, String.valueOf(hora));
            updateAlarm.put(MINUTO, String.valueOf(minuto));
            updateAlarm.put(DOMINGO, String.valueOf(dias[0]));
            updateAlarm.put(SEGUNDA, String.valueOf(dias[1]));
            updateAlarm.put(TERCA, String.valueOf(dias[2]));
            updateAlarm.put(QUARTA, String.valueOf(dias[3]));
            updateAlarm.put(QUINTA, String.valueOf(dias[4]));
            updateAlarm.put(SEXTA, String.valueOf(dias[5]));
            updateAlarm.put(SABADO, String.valueOf(dias[6]));
            updateAlarm.put(VEZES_DIA, String.valueOf(vezes_dia));
            updateAlarm.put(PERIODO_HORA, String.valueOf(periodo_hora));
            updateAlarm.put(PERIODO_MIN, String.valueOf(periodo_minuto));
            updateAlarm.put(NOTIFICATION_ID, String.valueOf(notificationId));
            updateAlarm.put(LUMINOSO, String.valueOf(luminoso));
            updateAlarm.put(SONORO, String.valueOf(sonoro));
            updateAlarm.put(BOX_POSITION, String.valueOf(posCaixa));

            root.put(ID_USUARIO, UserIdSingleton.getInstance().getUserId());
            root.put("updateAlarm", updateAlarm);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String formatJSONnewAlarm(int medicineType, String nome, int dosagem, int quantidade, int quantidadeBox, int hora, int minuto, int[] dias, int vezes_dia, int periodo_hora, int periodo_minuto, int notificationId, int luminoso, int sonoro, int posCaixa) {

        final JSONObject root = new JSONObject();

        try {
            JSONObject newAlarm = new JSONObject();

            newAlarm.put(ALARM_TYPE, String.valueOf(1));
            newAlarm.put(MEDICINE_TYPE, String.valueOf(medicineType));
            newAlarm.put(ATIVO, String.valueOf(1));
            newAlarm.put(NOME_REMEDIO, String.valueOf(nome));
            newAlarm.put(DOSAGEM, String.valueOf(dosagem));
            newAlarm.put(QUANTIDADE, String.valueOf(quantidade));
            newAlarm.put(QUANTIDADE_BOX, String.valueOf(quantidadeBox));
            newAlarm.put(HORA, String.valueOf(hora));
            newAlarm.put(MINUTO, String.valueOf(minuto));
            newAlarm.put(DOMINGO, String.valueOf(dias[0]));
            newAlarm.put(SEGUNDA, String.valueOf(dias[1]));
            newAlarm.put(TERCA, String.valueOf(dias[2]));
            newAlarm.put(QUARTA, String.valueOf(dias[3]));
            newAlarm.put(QUINTA, String.valueOf(dias[4]));
            newAlarm.put(SEXTA, String.valueOf(dias[5]));
            newAlarm.put(SABADO, String.valueOf(dias[6]));
            newAlarm.put(VEZES_DIA, String.valueOf(vezes_dia));
            newAlarm.put(PERIODO_HORA, String.valueOf(periodo_hora));
            newAlarm.put(PERIODO_MIN, String.valueOf(periodo_minuto));
            newAlarm.put(NOTIFICATION_ID, String.valueOf(notificationId));
            newAlarm.put(LUMINOSO, String.valueOf(luminoso));
            newAlarm.put(SONORO, String.valueOf(sonoro));
            newAlarm.put(BOX_POSITION, String.valueOf(posCaixa));

            root.put(ID_USUARIO, UserIdSingleton.getInstance().getUserId());
            root.put("newAlarm", newAlarm);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createAlarmIntent(int horas, int minutos, int[] dias, int notificationId) {

        Calendar calendar = Calendar.getInstance();

        int horaAtual = calendar.get(Calendar.HOUR_OF_DAY);
        int minutoAtual = calendar.get(Calendar.MINUTE);
        int diaAtual = calendar.get(Calendar.DAY_OF_MONTH);
        int mesAtual = calendar.get(Calendar.MONTH);
        int anoAtual = calendar.get(Calendar.YEAR);

        Calendar nextNotifTime = Calendar.getInstance();
        nextNotifTime.add(Calendar.MONTH, 1);
        nextNotifTime.set(Calendar.DATE, 1);
        nextNotifTime.add(Calendar.DATE, -1);

        if (horas < horaAtual) {
            if (diaAtual == nextNotifTime.get(Calendar.DAY_OF_MONTH)) {
                if (mesAtual == 11) {
                    anoAtual = anoAtual + 1;
                    mesAtual = 0;
                } else {
                    diaAtual = 1;
                    mesAtual = mesAtual + 1;
                }
            } else {
                diaAtual = diaAtual + 1;
            }
        } else if (horas == horaAtual) {
            if (minutos <= minutoAtual) {
                if (diaAtual == nextNotifTime.get(Calendar.DAY_OF_MONTH)) {
                    if (mesAtual == 11) {
                        anoAtual = anoAtual + 1;
                        mesAtual = 0;
                    } else {
                        diaAtual = 1;
                        mesAtual = mesAtual + 1;
                    }
                } else {
                    diaAtual = diaAtual + 1;
                }
            }
        }

        calendar.set(anoAtual, mesAtual, diaAtual, horas, minutos, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmeReceiver.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("ALARM_TYPE", 1);
        intent.putExtra("ALARM_HOUR", horas);
        intent.putExtra("ALARM_MINUTES", minutos);
        intent.putExtra("ALARM_DAYS", dias);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, intent, 0);
        alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarmIntent(int notificationId) {
        Intent intent = new Intent(getApplicationContext(), AlarmeReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
    }
}
