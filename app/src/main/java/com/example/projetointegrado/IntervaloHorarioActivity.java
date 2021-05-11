package com.example.projetointegrado;

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

import com.example.projetointegrado.databinding.ActivityIntervaloHorarioBinding;
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
import static com.example.projetointegrado.Constants.OPEN_BOX_FRAG;
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

public class IntervaloHorarioActivity extends AppCompatActivity {

    private static final String TAG = "IntervaloHorarioActivity";

    private ActivityIntervaloHorarioBinding binding;
    private DataBaseAlarmsHelper mDataBaseAlarmsHelper;
    private boolean isEdit;
    private int alarmEditPosition;
    private Cursor data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntervaloHorarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle(R.string.action_button_interval);

        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
        mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(this);
        data = mDataBaseAlarmsHelper.getData();

        binding.startTimeIntervalText.addTextChangedListener(MaskEditUtil.mask(binding.startTimeIntervalText, MaskEditUtil.FORMAT_HOUR));
        binding.timeIntervalText.addTextChangedListener(MaskEditUtil.mask(binding.timeIntervalText, MaskEditUtil.FORMAT_HOUR));

        if (isEdit) {
            alarmEditPosition = getIntent().getIntExtra("POSITION", -1);
            data.move(alarmEditPosition + 1);

            String hora_inicio = String.valueOf(data.getInt(8));
            String min_inicio = String.valueOf(data.getInt(9));

            if (hora_inicio.length() == 1) hora_inicio = "0" + hora_inicio;
            if (min_inicio.length() == 1) min_inicio = "0" + min_inicio;

            binding.startTimeIntervalText.setText(String.format("%s%s", hora_inicio, min_inicio));

            String hora_periodo = String.valueOf(data.getInt(18));
            String min_periodo = String.valueOf(data.getInt(19));

            if (hora_periodo.length() == 1) hora_periodo = "0" + hora_periodo;
            if (min_periodo.length() == 1) min_periodo = "0" + min_periodo;
            binding.timeIntervalText.setText(String.format("%s%s", hora_periodo, min_periodo));

            String vezes_dia = String.valueOf(data.getInt(17));
            binding.howManyTimesIntervalText.setText(vezes_dia);
        }

        binding.startTimeIntervalTextImage.setOnClickListener(v -> imageInfoClick(binding.startTimeIntervalTextImage));
        binding.howManyTimesIntervalTextImage.setOnClickListener(v -> imageInfoClick(binding.howManyTimesIntervalTextImage));
        binding.timeIntervalTextImage.setOnClickListener(v -> imageInfoClick(binding.timeIntervalTextImage));

        binding.backButtonIntervalClock.setOnClickListener(v -> finish());
        binding.nextButtonIntervalClock.setOnClickListener(v -> {
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

        if (MaskEditUtil.unmask(binding.startTimeIntervalText.getText().toString()).length() == 4 && MaskEditUtil.unmask(binding.timeIntervalText.getText().toString()).length() == 4) {
            int hora_inicio = Integer.parseInt(binding.startTimeIntervalText.getText().toString().substring(0, 2));
            int min_inicio = Integer.parseInt(binding.startTimeIntervalText.getText().toString().substring(3, 5));

            int hora_periodo = Integer.parseInt(binding.timeIntervalText.getText().toString().substring(0, 2));
            int min_periodo = Integer.parseInt(binding.timeIntervalText.getText().toString().substring(3, 5));

            String vezes_dia_str = binding.howManyTimesIntervalText.getText().toString();

            if (hora_inicio < 24 && min_inicio < 60 && hora_periodo < 24 && min_periodo < 60) {

                if (vezes_dia_str.length() < 10) {
                    int vezes_dia = Integer.parseInt(vezes_dia_str);

                    if (isEdit) {
                        int ativo = data.getInt(3);
                        int velhaHora = data.getInt(8);
                        int velhoMinuto = data.getInt(9);
                        String velhoNome = data.getString(4);

                        createPostUpdateAlarm(
                                tipoRemedio,
                                ativo,
                                velhoNome,
                                nome,
                                dosagem,
                                quantidade,
                                quantidadeCaixa,
                                velhaHora,
                                velhoMinuto,
                                hora_inicio,
                                min_inicio,
                                new int[7],
                                vezes_dia,
                                hora_periodo,
                                min_periodo,
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
                                hora_inicio,
                                min_inicio,
                                new int[7],
                                vezes_dia,
                                hora_periodo,
                                min_periodo,
                                notificationId,
                                luminoso,
                                sonoro,
                                posCaixa);
                    }
                } else Toast.makeText(this, "Número muito grande", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Insira os dados corretamente", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "Insira os dados corretamente", Toast.LENGTH_SHORT).show();
    }

    private void createPostUpdateAlarm(int medicineType, int ativo, String velhoNome, String nome, int dosagem, int quantidade, int quantidadeBox, int oldHour, int oldMinute, int hora, int minuto, int[] dias, int vezes_dia, int periodo_hora, int periodo_minuto, int notificationId, int luminoso, int sonoro, int posCaixa) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSONupdateAlarm(medicineType, ativo, velhoNome, nome, dosagem, quantidade, quantidadeBox, oldHour, oldMinute, hora, minuto, dias, vezes_dia, periodo_hora, periodo_minuto, notificationId, luminoso, sonoro, posCaixa);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postModifyAlarm(request);

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
                        String.valueOf(alarmEditPosition + 1),
                        2,
                        medicineType,
                        ativo,
                        nome,
                        dosagem,
                        quantidade,
                        quantidadeBox,
                        hora,
                        minuto,
                        new int[7],
                        vezes_dia,
                        periodo_hora,
                        periodo_minuto,
                        notificationId,
                        luminoso,
                        sonoro,
                        posCaixa);

                if (confirmation) {
                    if (ativo == 1) {
                        cancelAlarmIntent(notificationId);
                        createAlarmIntent(hora, minuto, notificationId, vezes_dia, periodo_hora, periodo_minuto);
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

        Call<JsonObject> call = jsonPlaceHolderApi.postCreateAlarm(request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Um erro ocorreu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response);
                    return;
                }

                boolean confirmation = mDataBaseAlarmsHelper.addData(
                        2,
                        medicineType,
                        1,
                        nome,
                        dosagem,
                        quantidade,
                        quantidade,
                        hora,
                        minuto,
                        new int[7],
                        vezes_dia,
                        periodo_hora,
                        periodo_minuto,
                        notificationId,
                        luminoso,
                        sonoro,
                        posCaixa);

                if (confirmation) {
                    createAlarmIntent(hora, minuto, notificationId, vezes_dia, periodo_hora, periodo_minuto);
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

    private String formatJSONupdateAlarm(int medicineType, int ativo, String velhoNome, String nome, int dosagem, int quantidade, int quantidadeBox, int velhaHora, int velhoMinuto, int hora, int minuto, int[] dias, int vezes_dia, int periodo_hora, int periodo_minuto, int notificationId, int luminoso, int sonoro, int posCaixa) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject velhoAlarme = new JSONObject();
            velhoAlarme.put(NOME_REMEDIO, velhoNome);
            velhoAlarme.put(HORA, String.valueOf(velhaHora));
            velhoAlarme.put(MINUTO, String.valueOf(velhoMinuto));

            JSONObject novoAlarme = new JSONObject();
            novoAlarme.put(ALARM_TYPE, String.valueOf(2));
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

    private String formatJSONnewAlarm(int medicineType, String nome, int dosagem, int quantidade, int quantidadeBox, int hora, int minuto, int[] dias, int vezes_dia, int periodo_hora, int periodo_minuto, int notificationId, int luminoso, int sonoro, int posCaixa) {

        final JSONObject root = new JSONObject();

        try {
            JSONObject novoAlarme = new JSONObject();

            novoAlarme.put(ALARM_TYPE, String.valueOf(2));
            novoAlarme.put(MEDICINE_TYPE, String.valueOf(medicineType));
            novoAlarme.put(ATIVO, String.valueOf(1));
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
            root.put("novoAlarme", novoAlarme);

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

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmeReceiver.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("ALARM_TYPE", 2);
        intent.putExtra("ALARM_HOUR", hora_inicio);
        intent.putExtra("ALARM_MINUTES", min_inicio);
        intent.putExtra("ALARM_TIMES_DAY", vezes_dia);
        intent.putExtra("ALARM_TIMES_DAY_MISSING", vezes_dia);
        intent.putExtra("ALARM_PERIOD_HOUR", periodo_hora);
        intent.putExtra("ALARM_PERIOD_MINUTE", periodo_minuto);
        intent.putExtra("ALARM_INTERVAL_FIRST_CALL", true);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, intent, 0);
        alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarmIntent(int notificationId) {
        Intent intent = new Intent(getApplicationContext(), AlarmeReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent.cancel();
    }
}