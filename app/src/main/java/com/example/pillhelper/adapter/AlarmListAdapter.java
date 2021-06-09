package com.example.pillhelper.adapter;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.pillhelper.receiver.AlarmReceiver;
import com.example.pillhelper.R;
import com.example.pillhelper.singleton.UserIdSingleton;
import com.example.pillhelper.dataBase.DataBaseAlarmsHelper;
import com.example.pillhelper.item.AlarmItem;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.utils.Constants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

public class AlarmListAdapter extends ArrayAdapter<AlarmItem> {

    private static final String TAG = "AlarmListAdapter";

    private Context mContext;
    private int mResource;
    private DataBaseAlarmsHelper mDataBaseAlarmsHelper;
    private String name;
    private int hours;
    private int min;
    private int notificationId;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    public AlarmListAdapter(Context context, int resource, ArrayList<AlarmItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(context);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AlarmItem item = getItem(position);

        name = item.getName();
        hours = item.getHours();
        min = item.getMin();
        notificationId = item.getNotificationId();

        String hourString = hours < 10 ? "0" + hours : String.valueOf(hours);
        String minString = min < 10 ? "0" + min : String.valueOf(min);

        String totalTime = hourString + ":" + minString;

        int isActive = item.getStatus();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        ImageView imageViewStatus = convertView.findViewById(R.id.adapter_image);
        TextView timeView = convertView.findViewById(R.id.adapter_time);
        TextView textView = convertView.findViewById(R.id.adapter_text);
        ImageView imageViewDelete = convertView.findViewById(R.id.alarm_list_image);

        imageViewStatus.setImageResource(
                isActive == 1 ? R.drawable.ic_alarm_on_black_24dp : R.drawable.ic_alarm_off_black_24dp);
        timeView.setText(totalTime);
        textView.setText(name);

        View finalConvertView = convertView;
        imageViewStatus.setOnClickListener(v -> {

            Cursor data = mDataBaseAlarmsHelper.getData();
            data.move(position + 1);

            int[] days = new int[7];
            days[0] = data.getInt(10);
            days[1] = data.getInt(11);
            days[2] = data.getInt(12);
            days[3] = data.getInt(13);
            days[4] = data.getInt(14);
            days[5] = data.getInt(15);
            days[6] = data.getInt(16);

            int active = isActive == 1 ? 0 : 1;

            createPostUpdateAlarm(finalConvertView, position, data.getString(0), data.getInt(1), data.getInt(2), active,
                    data.getString(4), data.getString(4), data.getInt(5), data.getInt(6), data.getInt(7),
                    data.getInt(8), data.getInt(9), data.getInt(8), data.getInt(9), days, data.getInt(17),
                    data.getInt(18), data.getInt(19), data.getInt(20), data.getInt(21), data.getInt(22),
                    data.getInt(23));
        });

        imageViewDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);

            builder.setPositiveButton(R.string.ok,
                    (dialog, id) -> createPostDeleteAlarm(position, item.getUuidAlarm()));

            builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
        return convertView;
    }

    private void createPostUpdateAlarm(View convertView, int position, String uuidAlarm, int alarmType,
            int medicineType, int active, String oldName, String name, int dosage, int qtd, int qtdBox, int oldHour,
            int oldMinute, int hour, int min, int[] days, int times_day, int period_hour, int period_min,
            int notificationId, int luminous, int sound, int posBox) {

        String requestStr = formatJSONupdateAlarm(uuidAlarm, alarmType, medicineType, active, oldName, name, dosage,
                qtd, qtdBox, oldHour, oldMinute, hour, min, days, times_day, period_hour, period_min, notificationId,
                luminous, sound, posBox);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postModifyAlarm(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Um erro ocorreu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response);
                    return;
                }

                mDataBaseAlarmsHelper.updateData(String.valueOf(position + 1), alarmType, medicineType, active, name,
                        dosage, qtd, qtdBox, hour, min, days, times_day, period_hour, period_min, notificationId,
                        luminous, sound, posBox);

                ImageView imageViewStatus = convertView.findViewById(R.id.adapter_image);
                imageViewStatus.setImageResource(
                        active == 1 ? R.drawable.ic_alarm_on_black_24dp : R.drawable.ic_alarm_off_black_24dp);
                AlarmItem item = getItem(position);
                item.setStatus(item.getStatus() == 1 ? 0 : 1);
                notifyDataSetChanged();

                if (active == 1) {
                    if (alarmType == 1) {
                        createAlarmIntent(hour, min, days, notificationId);
                    } else {
                        createAlarmIntent(hour, min, notificationId, times_day, period_hour, period_min);
                    }
                } else {
                    cancelAlarmIntent();
                }

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private void createAlarmIntent(int hours, int min, int[] days, int notificationId) {

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

        if (hours < hourCurrent) {
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
        } else if (hours == hourCurrent) {
            if (min <= minCurrent) {
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

        calendar.set(yearCurrent, monthCurrent, dayCurrent, hours, min, 0);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("ALARM_TYPE", 1);
        intent.putExtra("ALARM_HOUR", hours);
        intent.putExtra("ALARM_MINUTES", min);
        intent.putExtra("ALARM_DAYS", days);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), notificationId,
                intent, 0);
        alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
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

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("ALARM_TYPE", 2);
        intent.putExtra("ALARM_HOUR", hour_start);
        intent.putExtra("ALARM_MINUTES", min_start);
        intent.putExtra("ALARM_TIMES_DAY", times_day);
        intent.putExtra("ALARM_TIMES_DAY_MISSING", times_day);
        intent.putExtra("ALARM_PERIOD_HOUR", period_hour);
        intent.putExtra("ALARM_PERIOD_MINUTE", period_min);
        intent.putExtra("ALARM_INTERVAL_FIRST_CALL", true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), notificationId,
                intent, 0);
        alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void createPostDeleteAlarm(int position, String uuidAlarm) {
        String requestStr = formatJSON(uuidAlarm);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postDeleteAlarm(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }
                Cursor data = mDataBaseAlarmsHelper.getData();
                data.move(position + 1);
                int isDeleted = mDataBaseAlarmsHelper.removeData(uuidAlarm);

                if (isDeleted > 0) {
                    cancelAlarmIntent();
                    AlarmListAdapter.this.remove(getItem(position));
                    AlarmListAdapter.this.notifyDataSetChanged();
                } else
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSON(String uuidAlarm) {
        final JSONObject root = new JSONObject();

        try {
            root.put("uuidUser", UserIdSingleton.getInstance().getUserId());
            root.put("uuidAlarm", uuidAlarm);
            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String formatJSONupdateAlarm(String uuidAlarm, int alarmType, int medicineType, int active, String oldName,
            String name, int dosage, int qtd, int qtdBox, int velhaHora, int velhoMinuto, int hour, int min, int[] days,
            int times_day, int period_hour, int period_min, int notificationId, int luminous, int sound, int posBox) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject updateAlarm = new JSONObject();
            updateAlarm.put(ID_ALARME, String.valueOf(uuidAlarm));
            updateAlarm.put(ALARM_TYPE, String.valueOf(alarmType));
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

    private void cancelAlarmIntent() {
        Intent intent = new Intent(getContext().getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), notificationId,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
    }
}
