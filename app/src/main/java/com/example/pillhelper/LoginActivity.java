package com.example.pillhelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pillhelper.databinding.ActivityLoginBinding;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.pillhelper.Constants.ALARM_TYPE;
import static com.example.pillhelper.Constants.ATIVO;
import static com.example.pillhelper.Constants.BASE_URL;
import static com.example.pillhelper.Constants.BOX_POSITION;
import static com.example.pillhelper.Constants.DOMINGO;
import static com.example.pillhelper.Constants.DOSAGEM;
import static com.example.pillhelper.Constants.HORA;
import static com.example.pillhelper.Constants.ID_CAIXA;
import static com.example.pillhelper.Constants.LUMINOSO;
import static com.example.pillhelper.Constants.MEDICINE_TYPE;
import static com.example.pillhelper.Constants.MINUTO;
import static com.example.pillhelper.Constants.NOME_CAIXA;
import static com.example.pillhelper.Constants.NOME_REMEDIO;
import static com.example.pillhelper.Constants.NOTIFICATION_ID;
import static com.example.pillhelper.Constants.OPEN_BOX_FRAG;
import static com.example.pillhelper.Constants.PERIODO_HORA;
import static com.example.pillhelper.Constants.PERIODO_MIN;
import static com.example.pillhelper.Constants.QUANTIDADE;
import static com.example.pillhelper.Constants.QUANTIDADE_BOX;
import static com.example.pillhelper.Constants.QUARTA;
import static com.example.pillhelper.Constants.QUINTA;
import static com.example.pillhelper.Constants.SABADO;
import static com.example.pillhelper.Constants.SEGUNDA;
import static com.example.pillhelper.Constants.SEXTA;
import static com.example.pillhelper.Constants.SONORO;
import static com.example.pillhelper.Constants.TERCA;
import static com.example.pillhelper.Constants.VEZES_DIA;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    DataBaseUserHelper mDataBaseUserHelper;
    private String loginType = "1";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    DataBaseAlarmsHelper mDataBaseAlarmsHelper;
    DataBaseBoxHelper mDataBaseBoxHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPreferences = getSharedPreferences("com.example.pillhelper", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();

        mDataBaseUserHelper = new DataBaseUserHelper(this);

        bindAll();

        checkSharedPref();
    }

    private void bindAll() {
        binding.emailRadioButton.setOnClickListener((View v) -> {
            binding.emailEditText.setVisibility(View.VISIBLE);
            binding.phoneEditText.setVisibility(View.GONE);
            binding.emailEditText.setText("");
        });

        binding.telefoneRadioButton.setOnClickListener((View v) -> {
            binding.phoneEditText.setVisibility(View.VISIBLE);
            binding.emailEditText.setVisibility(View.GONE);
            binding.phoneEditText.setText("");
        });

        binding.loginButton.setOnClickListener(v -> login());
        binding.backButton.setOnClickListener(v -> finish());

        binding.phoneEditText.addTextChangedListener(MaskEditUtil.mask(binding.phoneEditText, MaskEditUtil.FORMAT_FONE));
    }

    private void checkSharedPref() {
        String checkbox = mPreferences.getString(getString(R.string.checkboxKey), "False");
        String mainValue = mPreferences.getString(getString(R.string.mainValueKey), "");
        String password = mPreferences.getString(getString(R.string.passwordKey), "");
        String loginType = mPreferences.getString(getString(R.string.loginTypeKey), "1");

        if (loginType.equals("1")) {
            binding.emailRadioButton.performClick();
            binding.emailEditText.setText(mainValue);
        } else {
            binding.telefoneRadioButton.performClick();
            binding.phoneEditText.setText(mainValue);
        }

        binding.senhaEditText.setText(password);
        binding.rememberMeCheckbox.setChecked(checkbox.equals("True"));
    }

    private void login() {

        String mainChoiceString = "";

        if (binding.emailRadioButton.isChecked()) {
            mainChoiceString = binding.emailEditText.getText().toString();
            loginType = "1";
        } else if (binding.telefoneRadioButton.isChecked()) {
            mainChoiceString = binding.phoneEditText.getText().toString();
            mainChoiceString = MaskEditUtil.unmask(mainChoiceString);
            loginType = "2";
        }

        String password = binding.senhaEditText.getText().toString();

        if (mainChoiceString.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Dados incompletos", Toast.LENGTH_SHORT).show();
        } else {
            createPost(mainChoiceString, password, loginType);
        }
    }

    private void createPost(String mainString, String senha, String tipo) {

        binding.progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String email = "";
        String celular = "";

        if (tipo.equals("1")) {
            email = mainString;
        } else if (tipo.equals("2")) {
            celular = mainString;
        }

        Map<String, String> fields = new HashMap<>();
        fields.put("email", email);
        fields.put("celular", celular);
        fields.put("senha", senha);

        Call<JsonObject> call = jsonPlaceHolderApi.postLogin(fields);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "Um erro ocorreu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response);
                    return;
                }

                JsonObject postResponse = response.body();

                if (postResponse.get("response").getAsBoolean()) {
                    String userId = postResponse.get("msg").getAsString();
                    UserIdSingleton.getInstance().setUserId(userId);

                    if (binding.rememberMeCheckbox.isChecked()) {
                        mEditor.putString(getString(R.string.checkboxKey), "True");
                        mEditor.commit();

                        mEditor.putString(getString(R.string.mainValueKey), mainString);
                        mEditor.commit();

                        mEditor.putString(getString(R.string.passwordKey), senha);
                        mEditor.commit();

                        mEditor.putString(getString(R.string.loginTypeKey), loginType);
                        mEditor.commit();
                    } else {
                        mEditor.putString(getString(R.string.checkboxKey), "False");
                        mEditor.commit();

                        mEditor.putString(getString(R.string.mainValueKey), "");
                        mEditor.commit();

                        mEditor.putString(getString(R.string.passwordKey), "");
                        mEditor.commit();

                        mEditor.putString(getString(R.string.loginTypeKey), "");
                        mEditor.commit();
                    }

                    loadDataBase();
                    return;
                }

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private void loadDataBase() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<JsonObject> call = jsonPlaceHolderApi.postUserData(UserIdSingleton.getInstance().getUserId());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), "Um erro ocorreu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response);
                    return;
                }

                JsonObject jsonObject = response.body();
                JsonArray alarmsArray = jsonObject.getAsJsonObject("msg").getAsJsonArray("alarmes");
                JsonArray boxArray = jsonObject.getAsJsonObject("msg").getAsJsonArray("caixas");

                getBaseContext().deleteDatabase("alarms_table");
                getBaseContext().deleteDatabase("boxes_table");

                if (alarmsArray != null) {
                    mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(getBaseContext());

                    for (int i = 0; i < alarmsArray.size(); i++) {
                        JsonElement jsonElement = alarmsArray.get(i);
                        JsonObject jsonAlarm = jsonElement.getAsJsonObject();

                        int[] dias = new int[7];
                        dias[0] = jsonAlarm.get(DOMINGO).getAsInt();
                        dias[1] = jsonAlarm.get(SEGUNDA).getAsInt();
                        dias[2] = jsonAlarm.get(TERCA).getAsInt();
                        dias[3] = jsonAlarm.get(QUARTA).getAsInt();
                        dias[4] = jsonAlarm.get(QUINTA).getAsInt();
                        dias[5] = jsonAlarm.get(SEXTA).getAsInt();
                        dias[6] = jsonAlarm.get(SABADO).getAsInt();

                        mDataBaseAlarmsHelper.addData(
                                jsonAlarm.get(ALARM_TYPE).getAsInt(),
                                jsonAlarm.get(MEDICINE_TYPE).getAsInt(),
                                jsonAlarm.get(ATIVO).getAsInt(),
                                jsonAlarm.get(NOME_REMEDIO).getAsString(),
                                jsonAlarm.get(DOSAGEM).getAsInt(),
                                jsonAlarm.get(QUANTIDADE).getAsInt(),
                                jsonAlarm.get(QUANTIDADE_BOX).getAsInt(),
                                jsonAlarm.get(HORA).getAsInt(),
                                jsonAlarm.get(MINUTO).getAsInt(),
                                dias,
                                jsonAlarm.get(VEZES_DIA).getAsInt(),
                                jsonAlarm.get(PERIODO_HORA).getAsInt(),
                                jsonAlarm.get(PERIODO_MIN).getAsInt(),
                                jsonAlarm.get(NOTIFICATION_ID).getAsInt(),
                                jsonAlarm.get(LUMINOSO).getAsInt(),
                                jsonAlarm.get(SONORO).getAsInt(),
                                jsonAlarm.get(BOX_POSITION).getAsInt());
                    }
                }

                if (boxArray != null) {
                    mDataBaseBoxHelper = new DataBaseBoxHelper(getBaseContext());

                    for (int i = 0; i < boxArray.size(); i++) {
                        JsonElement jsonElement = boxArray.get(i);
                        JsonObject jsonBox = jsonElement.getAsJsonObject();

                        mDataBaseBoxHelper.addData(
                                jsonBox.get(ID_CAIXA).getAsString(),
                                jsonBox.get(NOME_CAIXA).getAsString());
                    }
                }

                Intent intent = new Intent(LoginActivity.this, FragmentsActivity.class);
                intent.putExtra(OPEN_BOX_FRAG, false);
                startActivity(intent);
                finish();

                binding.progressBar.setVisibility(View.GONE);
                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }
}