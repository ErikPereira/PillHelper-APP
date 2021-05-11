package com.example.projetointegrado;

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
import static com.example.projetointegrado.Constants.ALARM_TYPE;
import static com.example.projetointegrado.Constants.ATIVO;
import static com.example.projetointegrado.Constants.BASE_URL;
import static com.example.projetointegrado.Constants.BOX_POSITION;
import static com.example.projetointegrado.Constants.DOMINGO;
import static com.example.projetointegrado.Constants.DOSAGEM;
import static com.example.projetointegrado.Constants.HORA;
import static com.example.projetointegrado.Constants.ID_USUARIO;
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

public class AlarmeListAdapter extends ArrayAdapter<AlarmeItem> {

    private static final String TAG = "AlarmListAdapter";

    private Context mContext;
    private int mResource;
    private DataBaseAlarmsHelper mDataBaseAlarmsHelper;
    private String nome;
    private int horas;
    private int minutos;
    private int notificationId;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    AlarmeListAdapter(Context context, int resource, ArrayList<AlarmeItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(context);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AlarmeItem item = getItem(position);

        nome = item.getNome();
        horas = item.getHora();
        minutos = item.getMinuto();
        notificationId = item.getNotificationId();

        String horaString = horas < 10 ? "0" + horas : String.valueOf(horas);
        String minutoString = minutos < 10 ? "0" + minutos : String.valueOf(minutos);

        String horaTotal = horaString + ":" + minutoString;

        int isActive = item.getStatus();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        ImageView imageViewStatus = convertView.findViewById(R.id.adapter_image);
        TextView timeView = convertView.findViewById(R.id.adapter_time);
        TextView textView = convertView.findViewById(R.id.adapter_text);
        ImageView imageViewDelete = convertView.findViewById(R.id.alarm_list_image);

        imageViewStatus.setImageResource(isActive == 1 ? R.drawable.ic_alarm_on_black_24dp : R.drawable.ic_alarm_off_black_24dp);
        timeView.setText(horaTotal);
        textView.setText(nome);

        View finalConvertView = convertView;
        imageViewStatus.setOnClickListener(v -> {

            Cursor data = mDataBaseAlarmsHelper.getData();
            data.move(position + 1);

            int[] dias = new int[7];
            dias[0] = data.getInt(10);
            dias[1] = data.getInt(11);
            dias[2] = data.getInt(12);
            dias[3] = data.getInt(13);
            dias[4] = data.getInt(14);
            dias[5] = data.getInt(15);
            dias[6] = data.getInt(16);

            createPostUpdateAlarm(finalConvertView, position,
                    data.getInt(1),
                    data.getInt(2),
                    data.getInt(3) == 1 ? 0 : 1,
                    data.getString(4),
                    data.getString(4),
                    data.getInt(5),
                    data.getInt(6),
                    data.getInt(7),
                    data.getInt(8),
                    data.getInt(9),
                    data.getInt(8),
                    data.getInt(9),
                    dias, data.getInt(17),
                    data.getInt(18),
                    data.getInt(19),
                    data.getInt(20),
                    data.getInt(21),
                    data.getInt(22),
                    data.getInt(23));
        });

        imageViewDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title);

            builder.setPositiveButton(R.string.ok, (dialog, id) -> createPostDeleteAlarm(position, item.getNome(), item.getHora(), item.getMinuto()));

            builder.setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
        return convertView;
    }

    private void createPostUpdateAlarm(View convertView, int position, int alarmType, int medicineType, int ativo, String velhoNome, String nome, int dosagem, int quantidade, int quantidadeBox, int oldHour, int oldMinute, int hora, int minuto, int[] dias, int vezes_dia, int periodo_hora, int periodo_minuto, int notificationId, int luminoso, int sonoro, int posCaixa) {

        String requestStr = formatJSONupdateAlarm(alarmType, medicineType, ativo, velhoNome, nome, dosagem, quantidade, quantidadeBox, oldHour, oldMinute, hora, minuto, dias, vezes_dia, periodo_hora, periodo_minuto, notificationId, luminoso, sonoro, posCaixa);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postModifyAlarm(request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Um erro ocorreu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response);
                    return;
                }

                mDataBaseAlarmsHelper.updateData(
                        String.valueOf(position + 1),
                        alarmType,
                        medicineType,
                        ativo,
                        nome,
                        dosagem,
                        quantidade,
                        quantidadeBox,
                        hora,
                        minuto,
                        dias,
                        vezes_dia,
                        periodo_hora,
                        periodo_minuto,
                        notificationId,
                        luminoso,
                        sonoro,
                        posCaixa);

                ImageView imageViewStatus = convertView.findViewById(R.id.adapter_image);
                imageViewStatus.setImageResource(ativo == 1 ? R.drawable.ic_alarm_on_black_24dp : R.drawable.ic_alarm_off_black_24dp);
                AlarmeItem item = getItem(position);
                item.setStatus(item.getStatus() == 1 ? 0 : 1);
                notifyDataSetChanged();

                if (ativo == 1) {
                    if (alarmType == 1) {
                        createAlarmIntent(hora, minuto, dias, notificationId);
                    } else {
                        createAlarmIntent(hora, minuto, notificationId, vezes_dia, periodo_hora, periodo_minuto);
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

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmeReceiver.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("ALARM_TYPE", 1);
        intent.putExtra("ALARM_HOUR", horas);
        intent.putExtra("ALARM_MINUTES", minutos);
        intent.putExtra("ALARM_DAYS", dias);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), notificationId, intent, 0);
        alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void createAlarmIntent(int hora_inicio, int min_inicio, int notificationId, int vezes_dia, int periodo_hora, int periodo_minuto) {

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

        if (hora_inicio < horaAtual) {
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
        } else if (hora_inicio == horaAtual) {
            if (min_inicio <= minutoAtual) {
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

        calendar.set(anoAtual, mesAtual, diaAtual, hora_inicio, min_inicio, 0);

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmeReceiver.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("ALARM_TYPE", 2);
        intent.putExtra("ALARM_HOUR", hora_inicio);
        intent.putExtra("ALARM_MINUTES", min_inicio);
        intent.putExtra("ALARM_TIMES_DAY", vezes_dia);
        intent.putExtra("ALARM_TIMES_DAY_MISSING", vezes_dia);
        intent.putExtra("ALARM_PERIOD_HOUR", periodo_hora);
        intent.putExtra("ALARM_PERIOD_MINUTE", periodo_minuto);
        intent.putExtra("ALARM_INTERVAL_FIRST_CALL", true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), notificationId, intent, 0);
        alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void createPostDeleteAlarm(int position, String nome, int horas, int minutos) {
        String requestStr = formatJSON(nome, horas, minutos);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postDeleteAlarm(request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }
                Cursor data = mDataBaseAlarmsHelper.getData();
                data.move(position + 1);
                int isDeleted = mDataBaseAlarmsHelper.removeData(String.valueOf(data.getInt(0)));

                if (isDeleted > 0) {
                    cancelAlarmIntent();
                    AlarmeListAdapter.this.remove(getItem(position));
                    AlarmeListAdapter.this.notifyDataSetChanged();
                } else Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSON(String velhoNome, int velhaHora, int velhoMinuto) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject velhoAlarme = new JSONObject();
            velhoAlarme.put(NOME_REMEDIO, velhoNome);
            velhoAlarme.put(HORA, String.valueOf(velhaHora));
            velhoAlarme.put(MINUTO, String.valueOf(velhoMinuto));

            root.put(ID_USUARIO, UserIdSingleton.getInstance().getUserId());
            root.put("velhoAlarme", velhoAlarme);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String formatJSONupdateAlarm(int alarmType, int medicineType, int ativo, String velhoNome, String nome, int dosagem, int quantidade, int quantidadeBox, int velhaHora, int velhoMinuto, int hora, int minuto, int[] dias, int vezes_dia, int periodo_hora, int periodo_minuto, int notificationId, int luminoso, int sonoro, int posCaixa) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject velhoAlarme = new JSONObject();
            velhoAlarme.put(NOME_REMEDIO, velhoNome);
            velhoAlarme.put(HORA, String.valueOf(velhaHora));
            velhoAlarme.put(MINUTO, String.valueOf(velhoMinuto));

            JSONObject novoAlarme = new JSONObject();
            novoAlarme.put(ALARM_TYPE, String.valueOf(alarmType));
            novoAlarme.put(MEDICINE_TYPE, String.valueOf(medicineType));
            novoAlarme.put(ATIVO, String.valueOf(ativo));
            novoAlarme.put(NOME_REMEDIO, String.valueOf(nome));
            novoAlarme.put(DOSAGEM, String.valueOf(dosagem));
            novoAlarme.put(QUANTIDADE, String.valueOf(quantidade));
            novoAlarme.put(QUANTIDADE_BOX, String.valueOf(quantidadeBox));
            novoAlarme.put(HORA, String.valueOf(hora));
            novoAlarme.put(MINUTO, String.valueOf(minuto));
            novoAlarme.put(DOMINGO, String.valueOf(dias[0]));
            novoAlarme.put(SEGUNDA, String.valueOf(dias[1]));
            novoAlarme.put(TERCA, String.valueOf(dias[2]));
            novoAlarme.put(QUARTA, String.valueOf(dias[3]));
            novoAlarme.put(QUINTA, String.valueOf(dias[4]));
            novoAlarme.put(SEXTA, String.valueOf(dias[5]));
            novoAlarme.put(SABADO, String.valueOf(dias[6]));
            novoAlarme.put(VEZES_DIA, String.valueOf(vezes_dia));
            novoAlarme.put(PERIODO_HORA, String.valueOf(periodo_hora));
            novoAlarme.put(PERIODO_MIN, String.valueOf(periodo_minuto));
            novoAlarme.put(NOTIFICATION_ID, String.valueOf(notificationId));
            novoAlarme.put(LUMINOSO, String.valueOf(luminoso));
            novoAlarme.put(SONORO, String.valueOf(sonoro));
            novoAlarme.put(BOX_POSITION, String.valueOf(posCaixa));

            root.put(ID_USUARIO, UserIdSingleton.getInstance().getUserId());
            root.put("velhoAlarme", velhoAlarme);
            root.put("novoAlarme", novoAlarme);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cancelAlarmIntent() {
        Intent intent = new Intent(getContext().getApplicationContext(), AlarmeReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(), notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
    }
}
