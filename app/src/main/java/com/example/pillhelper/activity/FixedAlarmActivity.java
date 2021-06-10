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

import com.example.pillhelper.receiver.AlarmReceiver;
import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.dataBaseUser.DataBaseAlarmsHelper;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.R;
import com.example.pillhelper.singleton.UserIdSingleton;
import com.example.pillhelper.databinding.ActivityFixedAlarmBinding;
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

public class FixedAlarmActivity extends AppCompatActivity {

    private static final String TAG = "FixedAlarmActivity";
    private ActivityFixedAlarmBinding binding;
    private DataBaseAlarmsHelper mDataBaseAlarmsHelper;
    private boolean isEdit;
    private int alarmEditPosition;
    private Cursor data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFixedAlarmBinding.inflate(getLayoutInflater());
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

            int medType = getIntent().getIntExtra("MEDICINE_TYPE", 0);
            String name = getIntent().getStringExtra("MEDICINE_NAME");
            int notificationId = getIntent().getIntExtra("NOTIFICATION_ID", 0);
            int luminous = getIntent().getIntExtra("LUMINOSO", 0);
            int sound = getIntent().getIntExtra("SONORO", 0);

            if (medType == 1) {
                int qtd = getIntent().getIntExtra("MEDICINE_QUANTITY", 0);
                int qtdBox = getIntent().getIntExtra("MEDICINE_BOX_QUANTITY", 0);
                int posBox = getIntent().getIntExtra("BOX_POSITION", 0);
                addDataDB(medType, name, 0, qtd, qtdBox, notificationId, luminous, sound, posBox);
            } else if (medType == 2) {
                int dosage = getIntent().getIntExtra("MEDICINE_DOSAGE", 0);
                addDataDB(medType, name, dosage, 0, 0, notificationId, luminous, sound, 0);
            }
        });
    }

    private void addDataDB(int medType, String name, int dosage, int qtd, int qtdBox, int notificationId, int luminous,
            int sound, int posBox) {
        int hour = binding.idClockSchedule.getHour();
        int minute = binding.idClockSchedule.getMinute();

        int[] days = new int[7];
        days[0] = binding.sundayDay.isChecked() ? 1 : 0;
        days[1] = binding.mondayDay.isChecked() ? 1 : 0;
        days[2] = binding.tuesdayDay.isChecked() ? 1 : 0;
        days[3] = binding.wednesdayDay.isChecked() ? 1 : 0;
        days[4] = binding.thursdayDay.isChecked() ? 1 : 0;
        days[5] = binding.fridayDay.isChecked() ? 1 : 0;
        days[6] = binding.saturdayDay.isChecked() ? 1 : 0;

        if (isEdit) {
            int active = data.getInt(3);
            String uuidAlarm = data.getString(0);

            createPostUpdateAlarm(uuidAlarm, medType, active, name, dosage, qtd, qtdBox, hour, minute, days, 0, 0, 0,
                    notificationId, luminous, sound, posBox);
        } else {
            createPostCreateAlarm(medType, name, dosage, qtd, qtdBox, hour, minute, days, 0, 0, 0, notificationId,
                    luminous, sound, posBox);
        }
    }

    private void createPostUpdateAlarm(String uuidAlarm, int medType, int active, String name, int dosage, int qtd,
            int qtdBox, int hour, int minute, int[] days, int times_day, int period_hour, int period_min,
            int notificationId, int luminous, int sound, int posBox) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSONupdateAlarm(uuidAlarm, medType, active, name, dosage, qtd, qtdBox, hour, minute,
                days, times_day, period_hour, period_min, notificationId, luminous, sound, posBox);
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

                boolean confirmation = mDataBaseAlarmsHelper.updateData(uuidAlarm, 1, medType, active, name, dosage,
                        qtd, qtdBox, hour, minute, days, 0, 0, 0, notificationId, luminous, sound, posBox);

                if (confirmation) {
                    if (active == 1) {
                        cancelAlarmIntent(notificationId);
                        createAlarmIntent(hour, minute, days, notificationId);
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

    private void createPostCreateAlarm(int medType, String name, int dosage, int qtd, int qtdBox, int hour, int minute,
            int[] days, int times_day, int period_hour, int period_min, int notificationId, int luminous, int sound,
            int posBox) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSONnewAlarm(medType, name, dosage, qtd, qtdBox, hour, minute, days, times_day,
                period_hour, period_min, notificationId, luminous, sound, posBox);
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

                boolean confirmation = mDataBaseAlarmsHelper.addData(uuidAlarm, 1, medType, 1, name, dosage, qtd,
                        qtdBox, hour, minute, days, 0, 0, 0, notificationId, luminous, sound, posBox);

                if (confirmation) {
                    createAlarmIntent(hour, minute, days, notificationId);
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

    private String formatJSONupdateAlarm(String uuidAlarm, int medType, int active, String name, int dosage, int qtd,
            int qtdBox, int hour, int minute, int[] days, int times_day, int period_hour, int period_min,
            int notificationId, int luminous, int sound, int posBox) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject updateAlarm = new JSONObject();
            updateAlarm.put(ID_ALARME, String.valueOf(uuidAlarm));
            updateAlarm.put(ALARM_TYPE, String.valueOf(1));
            updateAlarm.put(MEDICINE_TYPE, String.valueOf(medType));
            updateAlarm.put(ATIVO, String.valueOf(active));
            updateAlarm.put(NOME_REMEDIO, String.valueOf(name));
            updateAlarm.put(DOSAGEM, String.valueOf(dosage));
            updateAlarm.put(QUANTIDADE, String.valueOf(qtd));
            updateAlarm.put(QUANTIDADE_BOX, String.valueOf(qtdBox));
            updateAlarm.put(HORA, String.valueOf(hour));
            updateAlarm.put(MINUTO, String.valueOf(minute));
            updateAlarm.put(DOMINGO, String.valueOf(days[0]));
            updateAlarm.put(SEGUNDA, String.valueOf(days[1]));
            updateAlarm.put(TERCA, String.valueOf(days[2]));
            updateAlarm.put(QUARTA, String.valueOf(days[3]));
            updateAlarm.put(QUINTA, String.valueOf(days[4]));
            updateAlarm.put(SEXTA, String.valueOf(days[5]));
            updateAlarm.put(SABADO, String.valueOf(days[6]));
            updateAlarm.put(VEZES_DIA, String.valueOf(times_day));
            updateAlarm.put(PERIODO_HORA, String.valueOf(period_hour));
            updateAlarm.put(PERIODO_MIN, String.valueOf(period_min));
            updateAlarm.put(NOTIFICATION_ID, String.valueOf(notificationId));
            updateAlarm.put(LUMINOSO, String.valueOf(luminous));
            updateAlarm.put(SONORO, String.valueOf(sound));
            updateAlarm.put(BOX_POSITION, String.valueOf(posBox));

            root.put(ID_USUARIO, UserIdSingleton.getInstance().getUserId());
            root.put("updateAlarm", updateAlarm);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String formatJSONnewAlarm(int medType, String name, int dosage, int qtd, int qtdBox, int hour, int minute,
            int[] days, int times_day, int period_hour, int period_min, int notificationId, int luminous, int sound,
            int posBox) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject newAlarm = new JSONObject();

            newAlarm.put(ALARM_TYPE, String.valueOf(1));
            newAlarm.put(MEDICINE_TYPE, String.valueOf(medType));
            newAlarm.put(ATIVO, String.valueOf(1));
            newAlarm.put(NOME_REMEDIO, String.valueOf(name));
            newAlarm.put(DOSAGEM, String.valueOf(dosage));
            newAlarm.put(QUANTIDADE, String.valueOf(qtd));
            newAlarm.put(QUANTIDADE_BOX, String.valueOf(qtdBox));
            newAlarm.put(HORA, String.valueOf(hour));
            newAlarm.put(MINUTO, String.valueOf(minute));
            newAlarm.put(DOMINGO, String.valueOf(days[0]));
            newAlarm.put(SEGUNDA, String.valueOf(days[1]));
            newAlarm.put(TERCA, String.valueOf(days[2]));
            newAlarm.put(QUARTA, String.valueOf(days[3]));
            newAlarm.put(QUINTA, String.valueOf(days[4]));
            newAlarm.put(SEXTA, String.valueOf(days[5]));
            newAlarm.put(SABADO, String.valueOf(days[6]));
            newAlarm.put(VEZES_DIA, String.valueOf(times_day));
            newAlarm.put(PERIODO_HORA, String.valueOf(period_hour));
            newAlarm.put(PERIODO_MIN, String.valueOf(period_min));
            newAlarm.put(NOTIFICATION_ID, String.valueOf(notificationId));
            newAlarm.put(LUMINOSO, String.valueOf(luminous));
            newAlarm.put(SONORO, String.valueOf(sound));
            newAlarm.put(BOX_POSITION, String.valueOf(posBox));

            root.put(ID_USUARIO, UserIdSingleton.getInstance().getUserId());
            root.put("newAlarm", newAlarm);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createAlarmIntent(int hour, int minute, int[] days, int notificationId) {

        Calendar calendar = Calendar.getInstance();

        int hourCurrent = calendar.get(Calendar.HOUR_OF_DAY);
        int minuteCurrent = calendar.get(Calendar.MINUTE);
        int dayCurrent = calendar.get(Calendar.DAY_OF_MONTH);
        int monthCurrent = calendar.get(Calendar.MONTH);
        int yearCurrent = calendar.get(Calendar.YEAR);

        Calendar nextNotifTime = Calendar.getInstance();
        nextNotifTime.add(Calendar.MONTH, 1);
        nextNotifTime.set(Calendar.DATE, 1);
        nextNotifTime.add(Calendar.DATE, -1);

        if (hour < hourCurrent) {
            if (dayCurrent == nextNotifTime.get(Calendar.DAY_OF_MONTH)) {
                if (monthCurrent == 11) {
                    yearCurrent = yearCurrent + 1;
                    monthCurrent = 0;
                } else {
                    dayCurrent = 1;
                    monthCurrent = monthCurrent + 1;
                }
            } else {
                dayCurrent = dayCurrent + 1;
            }
        } else if (hour == hourCurrent) {
            if (minute <= minuteCurrent) {
                if (dayCurrent == nextNotifTime.get(Calendar.DAY_OF_MONTH)) {
                    if (monthCurrent == 11) {
                        yearCurrent = yearCurrent + 1;
                        monthCurrent = 0;
                    } else {
                        dayCurrent = 1;
                        monthCurrent = monthCurrent + 1;
                    }
                } else {
                    dayCurrent = dayCurrent + 1;
                }
            }
        }

        calendar.set(yearCurrent, monthCurrent, dayCurrent, hour, minute, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("ALARM_TYPE", 1);
        intent.putExtra("ALARM_HOUR", hour);
        intent.putExtra("ALARM_MINUTES", minute);
        intent.putExtra("ALARM_DAYS", days);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, intent, 0);
        alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarmIntent(int notificationId) {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
    }
}
