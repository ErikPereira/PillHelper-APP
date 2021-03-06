package com.example.pillhelper.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pillhelper.dataBaseBulla.DataBaseBullaHelper;
import com.example.pillhelper.dataBaseBulla.DataBaseBullaUserHelper;
import com.example.pillhelper.dataBaseSupervisor.DataBaseBoundUserHelper;
import com.example.pillhelper.dataBaseUser.DataBaseClinicalDataHelper;
import com.example.pillhelper.dataBaseUser.DataBaseBoundSupervisorHelper;
import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.dataBaseUser.DataBaseAlarmsHelper;
import com.example.pillhelper.dataBaseUser.DataBaseBoxHelper;
import com.example.pillhelper.dataBaseUser.DataBaseUserHelper;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.utils.LoadDataBase;
import com.example.pillhelper.utils.MaskEditUtil;
import com.example.pillhelper.R;
import com.example.pillhelper.singleton.UserIdSingleton;
import com.example.pillhelper.singleton.SupervisorIdSingleton;
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

import static com.example.pillhelper.utils.Constants.ID_ALARME;
import static com.example.pillhelper.utils.Constants.ALARM_TYPE;
import static com.example.pillhelper.utils.Constants.ATIVO;
import static com.example.pillhelper.utils.Constants.BASE_URL;
import static com.example.pillhelper.utils.Constants.BOX_POSITION;
import static com.example.pillhelper.utils.Constants.DOMINGO;
import static com.example.pillhelper.utils.Constants.DOSAGEM;
import static com.example.pillhelper.utils.Constants.HORA;
import static com.example.pillhelper.utils.Constants.ID_CAIXA;
import static com.example.pillhelper.utils.Constants.ID_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.LUMINOSO;
import static com.example.pillhelper.utils.Constants.MEDICINE_TYPE;
import static com.example.pillhelper.utils.Constants.MINUTO;
import static com.example.pillhelper.utils.Constants.NOME_CAIXA;
import static com.example.pillhelper.utils.Constants.NOME_REMEDIO;
import static com.example.pillhelper.utils.Constants.NOME_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.NOME_USER;
import static com.example.pillhelper.utils.Constants.NOTIFICATION_ID;
import static com.example.pillhelper.utils.Constants.OPEN_BOX_FRAG;
import static com.example.pillhelper.utils.Constants.WHO_USER_FRAG;
import static com.example.pillhelper.utils.Constants.PERIODO_HORA;
import static com.example.pillhelper.utils.Constants.PERIODO_MIN;
import static com.example.pillhelper.utils.Constants.QUANTIDADE;
import static com.example.pillhelper.utils.Constants.QUANTIDADE_BOX;
import static com.example.pillhelper.utils.Constants.QUARTA;
import static com.example.pillhelper.utils.Constants.QUINTA;
import static com.example.pillhelper.utils.Constants.REGISTRADO_POR;
import static com.example.pillhelper.utils.Constants.SABADO;
import static com.example.pillhelper.utils.Constants.SEGUNDA;
import static com.example.pillhelper.utils.Constants.SEXTA;
import static com.example.pillhelper.utils.Constants.SONORO;
import static com.example.pillhelper.utils.Constants.TERCA;
import static com.example.pillhelper.utils.Constants.VEZES_DIA;
import static com.example.pillhelper.utils.Constants.VINCULO;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;
    DataBaseUserHelper mDataBaseUserHelper;
    private String loginType = "1";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    DataBaseAlarmsHelper mDataBaseAlarmsHelper;
    DataBaseBoxHelper mDataBaseBoxHelper;
    DataBaseBoundSupervisorHelper mDataBaseBoundSupervisorHelper;
    DataBaseBoundUserHelper mDataBaseBoundUserHelper;
    DataBaseClinicalDataHelper mDataBaseClinicalDataHelper;
    DataBaseBullaHelper mDataBaseBullaHelper;
    DataBaseBullaUserHelper mDataBaseBullaUserHelper;

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

        binding.phoneEditText
                .addTextChangedListener(MaskEditUtil.mask(binding.phoneEditText, MaskEditUtil.FORMAT_FONE));
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

    private void createPost(String mainString, String password, String tipo) {

        binding.progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String email = "";
        String cell = "";

        if (tipo.equals("1")) {
            email = mainString;
        } else if (tipo.equals("2")) {
            cell = mainString;
        }

        Map<String, String> fields = new HashMap<>();
        fields.put("email", email);
        fields.put("cell", cell);
        fields.put("password", password);

        Call<JsonObject> call = jsonPlaceHolderApi.postLogin(Constants.TOKEN_ACCESS, fields);

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
                Boolean error = postResponse.get("error").getAsBoolean();
                if (error.equals(false)) {

                    JsonObject responseObject = postResponse.get("response").getAsJsonObject();
                    String uuid = responseObject.get("uuid").getAsString();
                    String who = responseObject.get("who").getAsString();

                    if (binding.rememberMeCheckbox.isChecked()) {
                        mEditor.putString(getString(R.string.checkboxKey), "True");
                        mEditor.commit();

                        mEditor.putString(getString(R.string.mainValueKey), mainString);
                        mEditor.commit();

                        mEditor.putString(getString(R.string.passwordKey), password);
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

                    if (who.equals("user")) {
                        UserIdSingleton.getInstance().setUserId(uuid);
                        loadDataBaseUser();
                    }
                    else {
                        SupervisorIdSingleton.getInstance().setSupervisorId(uuid);
                        loadDataBaseSupervisor();
                    }
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

    private void loadDataBaseUser() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<JsonObject> call = jsonPlaceHolderApi.postUserData(Constants.TOKEN_ACCESS,
                UserIdSingleton.getInstance().getUserId());

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
                JsonArray alarmsArray = jsonObject.getAsJsonObject("response").getAsJsonArray("alarms");
                JsonArray boxArray = jsonObject.getAsJsonObject("response").getAsJsonArray("box");
                JsonArray supervisorArray = jsonObject.getAsJsonObject("response").getAsJsonArray("supervisors");
                JsonObject clinicalDataObject = jsonObject.getAsJsonObject("response").getAsJsonObject("clinicalData");
                JsonArray bullasArray = jsonObject.getAsJsonObject("response").getAsJsonArray("bulla");

                getBaseContext().deleteDatabase("alarms_table");
                getBaseContext().deleteDatabase("boxes_table");
                getBaseContext().deleteDatabase("bound_supervisors_table");
                getBaseContext().deleteDatabase("clinical_data_table");
                getBaseContext().deleteDatabase("bulla_table");
                getBaseContext().deleteDatabase("bulla_table_user");
                getBaseContext().deleteDatabase("bound_user_table");

                mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(getBaseContext());
                mDataBaseBoxHelper = new DataBaseBoxHelper(getBaseContext());
                mDataBaseBoundSupervisorHelper = new DataBaseBoundSupervisorHelper(getBaseContext());
                mDataBaseClinicalDataHelper = new DataBaseClinicalDataHelper(getBaseContext());
                mDataBaseBullaUserHelper = new DataBaseBullaUserHelper(getBaseContext());


                LoadDataBase loadDataBase = new LoadDataBase();
                loadDataBase.loadDataBaseUser(
                        alarmsArray,
                        boxArray,
                        supervisorArray,
                        clinicalDataObject,
                        bullasArray,
                        mDataBaseAlarmsHelper,
                        mDataBaseBoxHelper,
                        mDataBaseClinicalDataHelper,
                        mDataBaseBoundSupervisorHelper,
                        mDataBaseBullaUserHelper);

                Intent intent = new Intent(LoginActivity.this, FragmentsActivity.class);
                intent.putExtra(OPEN_BOX_FRAG, false);
                intent.putExtra(WHO_USER_FRAG, "user");
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

    private void loadDataBaseSupervisor() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<JsonObject> call = jsonPlaceHolderApi.postSupervisorData(Constants.TOKEN_ACCESS,
                SupervisorIdSingleton.getInstance().getSupervisorId());

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
                JsonArray usersArray = jsonObject.getAsJsonObject("response").getAsJsonArray("users");
                JsonArray bullasArray = jsonObject.getAsJsonObject("response").getAsJsonArray("bulla");

                getBaseContext().deleteDatabase("alarms_table");
                getBaseContext().deleteDatabase("boxes_table");
                getBaseContext().deleteDatabase("bound_supervisors_table");
                getBaseContext().deleteDatabase("clinical_data_table");
                getBaseContext().deleteDatabase("bulla_table");
                getBaseContext().deleteDatabase("bulla_table_user");
                getBaseContext().deleteDatabase("bound_user_table");

                mDataBaseBoundUserHelper = new DataBaseBoundUserHelper(getBaseContext());
                mDataBaseBullaHelper = new DataBaseBullaHelper(getBaseContext());

                LoadDataBase loadDataBase = new LoadDataBase();
                loadDataBase.loadDataBaseSupervisor(
                        usersArray,
                        bullasArray,
                        mDataBaseBoundUserHelper,
                        mDataBaseBullaHelper
                );


                Intent intent = new Intent(LoginActivity.this, FragmentsActivity.class);
                intent.putExtra(OPEN_BOX_FRAG, false);
                intent.putExtra(WHO_USER_FRAG, "supervisor");
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
