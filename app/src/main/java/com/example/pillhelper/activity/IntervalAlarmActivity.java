package com.example.pillhelper.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pillhelper.receiver.AlarmReceiver;
import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.dataBaseUser.DataBaseAlarmsHelper;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.utils.MaskEditUtil;
import com.example.pillhelper.R;
import com.example.pillhelper.singleton.UserIdSingleton;
import com.example.pillhelper.databinding.ActivityIntervalAlarmBinding;
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

public class IntervalAlarmActivity extends AppCompatActivity {

    private static final String TAG = "IntervalAlarmActivity";

    private ActivityIntervalAlarmBinding binding;
    private DataBaseAlarmsHelper mDataBaseAlarmsHelper;
    private boolean isEdit;
    private int alarmEditPosition;
    private Cursor data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntervalAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle(R.string.action_button_interval);

        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
        mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(this);
        data = mDataBaseAlarmsHelper.getData();

        binding.startTimeIntervalText
                .addTextChangedListener(MaskEditUtil.mask(binding.startTimeIntervalText, MaskEditUtil.FORMAT_HOUR));
        binding.timeIntervalText
                .addTextChangedListener(MaskEditUtil.mask(binding.timeIntervalText, MaskEditUtil.FORMAT_HOUR));

        if (isEdit) {
            alarmEditPosition = getIntent().getIntExtra("POSITION", -1);
            data.move(alarmEditPosition + 1);

            String hour_start = String.valueOf(data.getInt(8));
            String min_start = String.valueOf(data.getInt(9));

            if (hour_start.length() == 1)
                hour_start = "0" + hour_start;
            if (min_start.length() == 1)
                min_start = "0" + min_start;

            binding.startTimeIntervalText.setText(String.format("%s%s", hour_start, min_start));

            String period_hour = String.valueOf(data.getInt(18));
            String period_min = String.valueOf(data.getInt(19));

            if (period_hour.length() == 1)
                period_hour = "0" + period_hour;
            if (period_min.length() == 1)
                period_min = "0" + period_min;
            binding.timeIntervalText.setText(String.format("%s%s", period_hour, period_min));

            String times_day = String.valueOf(data.getInt(17));
            binding.howManyTimesIntervalText.setText(times_day);
        }

        binding.startTimeIntervalTextImage.setOnClickListener(v -> imageInfoClick(binding.startTimeIntervalTextImage));
        binding.howManyTimesIntervalTextImage
                .setOnClickListener(v -> imageInfoClick(binding.howManyTimesIntervalTextImage));
        binding.timeIntervalTextImage.setOnClickListener(v -> imageInfoClick(binding.timeIntervalTextImage));

        binding.backButtonIntervalClock.setOnClickListener(v -> finish());
        binding.nextButtonIntervalClock.setOnClickListener(v -> {
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

        if (MaskEditUtil.unmask(binding.startTimeIntervalText.getText().toString()).length() == 4
                && MaskEditUtil.unmask(binding.timeIntervalText.getText().toString()).length() == 4) {
            int hour_start = Integer.parseInt(binding.startTimeIntervalText.getText().toString().substring(0, 2));
            int min_start = Integer.parseInt(binding.startTimeIntervalText.getText().toString().substring(3, 5));

            int period_hour = Integer.parseInt(binding.timeIntervalText.getText().toString().substring(0, 2));
            int period_min = Integer.parseInt(binding.timeIntervalText.getText().toString().substring(3, 5));

            String times_day_str = binding.howManyTimesIntervalText.getText().toString();

            if (hour_start < 24 && min_start < 60 && period_hour < 24 && period_min < 60) {

                if (times_day_str.length() < 10) {
                    int times_day = Integer.parseInt(times_day_str);

                    if (isEdit) {
                        int active = data.getInt(3);
                        int oldHour = data.getInt(8);
                        int oldMin = data.getInt(9);
                        String oldName = data.getString(4);
                        String uuidAlarm = data.getString(0);
                        createPostUpdateAlarm(uuidAlarm, medType, active, oldName, name, dosage, qtd, qtdBox, oldHour,
                                oldMin, hour_start, min_start, new int[7], times_day, period_hour, period_min,
                                notificationId, luminous, sound, posBox);
                    } else {
                        createPostCreateAlarm(medType, name, dosage, qtd, qtdBox, hour_start, min_start, new int[7],
                                times_day, period_hour, period_min, notificationId, luminous, sound, posBox);
                    }
                } else
                    Toast.makeText(this, "Número muito grande", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "Insira os dados corretamente", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Insira os dados corretamente", Toast.LENGTH_SHORT).show();
    }

    private void createPostUpdateAlarm(String uuidAlarm, int medicineType, int active, String oldName, String name,
            int dosage, int qtd, int qtdBox, int oldHour, int oldMinute, int hour, int min, int[] days, int times_day,
            int period_hour, int period_min, int notificationId, int luminous, int sound, int posBox) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSONupdateAlarm(uuidAlarm, medicineType, active, oldName, name, dosage, qtd, qtdBox,
                oldHour, oldMinute, hour, min, days, times_day, period_hour, period_min, notificationId, luminous,
                sound, posBox);
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

                boolean confirmation = mDataBaseAlarmsHelper.updateData(uuidAlarm, 2, medicineType, active, name,
                        dosage, qtd, qtdBox, hour, min, new int[7], times_day, period_hour, period_min, notificationId,
                        luminous, sound, posBox);

                if (confirmation) {
                    if (active == 1) {
                        cancelAlarmIntent(notificationId);
                        createAlarmIntent(hour, min, notificationId, times_day, period_hour, period_min);
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

    private void createPostCreateAlarm(int medicineType, String name, int dosage, int qtd, int qtdBox, int hour,
            int min, int[] days, int times_day, int period_hour, int period_min, int notificationId, int luminous,
            int sound, int posBox) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSONnewAlarm(medicineType, name, dosage, qtd, qtdBox, hour, min, days, times_day,
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

                boolean confirmation = mDataBaseAlarmsHelper.addData(uuidAlarm, 2, medicineType, 1, name, dosage, qtd,
                        qtdBox, hour, min, new int[7], times_day, period_hour, period_min, notificationId, luminous,
                        sound, posBox);

                if (confirmation) {
                    createAlarmIntent(hour, min, notificationId, times_day, period_hour, period_min);
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

    private String formatJSONupdateAlarm(String uuidAlarm, int medicineType, int active, String oldName, String name,
            int dosage, int qtd, int qtdBox, int oldHour, int oldMin, int hour, int min, int[] days, int times_day,
            int period_hour, int period_min, int notificationId, int luminous, int sound, int posBox) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject updateAlarm = new JSONObject();
            updateAlarm.put(ID_ALARME, String.valueOf(uuidAlarm));
            updateAlarm.put(ALARM_TYPE, String.valueOf(1));
            updateAlarm.put(MEDICINE_TYPE, String.valueOf(medicineType));
            updateAlarm.put(ATIVO, String.valueOf(active));
            updateAlarm.put(NOME_REMEDIO, String.valueOf(name));
            updateAlarm.put(DOSAGEM, String.valueOf(dosage));
            updateAlarm.put(QUANTIDADE, String.valueOf(qtd));
            updateAlarm.put(QUANTIDADE_BOX, String.valueOf(qtdBox));
            updateAlarm.put(HORA, String.valueOf(hour));
            updateAlarm.put(MINUTO, String.valueOf(min));
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

    private String formatJSONnewAlarm(int medicineType, String name, int dosage, int qtd, int qtdBox, int hour, int min,
            int[] days, int times_day, int period_hour, int period_min, int notificationId, int luminous, int sound,
            int posBox) {

        final JSONObject root = new JSONObject();

        try {
            JSONObject newAlarm = new JSONObject();

            newAlarm.put(ALARM_TYPE, String.valueOf(2));
            newAlarm.put(MEDICINE_TYPE, String.valueOf(medicineType));
            newAlarm.put(ATIVO, String.valueOf(1));
            newAlarm.put(NOME_REMEDIO, String.valueOf(name));
            newAlarm.put(DOSAGEM, String.valueOf(dosage));
            newAlarm.put(QUANTIDADE, String.valueOf(qtd));
            newAlarm.put(QUANTIDADE_BOX, String.valueOf(qtdBox));
            newAlarm.put(HORA, String.valueOf(hour));
            newAlarm.put(MINUTO, String.valueOf(min));
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

    public void imageInfoClick(ImageView imageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.info_dialog_title_text);

        if (binding.startTimeIntervalTextImage.equals(imageView)) {
            builder.setMessage(R.string.dialog_text_start_time_info);
        } else if (binding.howManyTimesIntervalTextImage.equals(imageView)) {
            builder.setMessage(R.string.dialog_text_how_many_times_info);
        } else if (binding.timeIntervalTextImage.equals(imageView)) {
            builder.setMessage(R.string.dialog_text_time_interval_info);
        }

        builder.setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());

        builder.create().show();
    }

    private void createAlarmIntent(int hour_start, int min_start, int notificationId, int times_day, int period_hour,
            int period_min) {

        Calendar calendar = Calendar.getInstance();

        int hourCurrent = calendar.get(Calendar.HOUR_OF_DAY);
        int minCurrent = calendar.get(Calendar.MINUTE);
        int dayCurrent = calendar.get(Calendar.DAY_OF_MONTH);
        int monthCurrent = calendar.get(Calendar.MONTH);
        int yearCurrent = calendar.get(Calendar.YEAR);

        Calendar nextNotifTime = Calendar.getInstance();
        nextNotifTime.add(Calendar.MONTH, 1);
        nextNotifTime.set(Calendar.DATE, 1);
        nextNotifTime.add(Calendar.DATE, -1);

        if (hour_start < hourCurrent) {
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
        } else if (hour_start == hourCurrent) {
            if (min_start <= minCurrent) {
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

        calendar.set(yearCurrent, monthCurrent, dayCurrent, hour_start, min_start, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("ALARM_TYPE", 2);
        intent.putExtra("ALARM_HOUR", hour_start);
        intent.putExtra("ALARM_MINUTES", min_start);
        intent.putExtra("ALARM_TIMES_DAY", times_day);
        intent.putExtra("ALARM_TIMES_DAY_MISSING", times_day);
        intent.putExtra("ALARM_PERIOD_HOUR", period_hour);
        intent.putExtra("ALARM_PERIOD_MINUTE", period_min);
        intent.putExtra("ALARM_INTERVAL_FIRST_CALL", true);

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