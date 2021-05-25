package com.example.pillhelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.dataBase.DataBaseUserHelper;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.utils.MaskEditUtil;
import com.example.pillhelper.databinding.ActivityRegisterBinding;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.pillhelper.utils.Constants.BASE_URL;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private ActivityRegisterBinding binding;
    DataBaseUserHelper mDataBaseUserHelper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mDataBaseUserHelper = new DataBaseUserHelper(this);

        binding.telephoneLayout.addTextChangedListener(MaskEditUtil.mask(binding.telephoneLayout, MaskEditUtil.FORMAT_FONE));

        binding.registerButtonConfirm.setOnClickListener(v -> addDataDB());
        binding.backButton.setOnClickListener(v -> finish());

        binding.emailRadioButton.setOnClickListener(v -> {
            binding.emailLayout.setVisibility(View.VISIBLE);
            binding.telephoneLayout.setVisibility(View.GONE);
            binding.telephoneLayout.setText("");
        });

        binding.telefoneRadioButton.setOnClickListener(v -> {
            binding.telephoneLayout.setVisibility(View.VISIBLE);
            binding.emailLayout.setVisibility(View.GONE);
            binding.emailLayout.setText("");
        });
    }

    private void addDataDB() {
        String fone = MaskEditUtil.unmask(binding.telephoneLayout.getText().toString());
        String email = binding.emailLayout.getText().toString();
        String senha = binding.senhaLayout.getText().toString();
        int tipo = 0; //1 = login por email  2 = login por telefone

        if (binding.emailRadioButton.isChecked()) {
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Dados incompletos", Toast.LENGTH_SHORT).show();
                return;
            }

            tipo = 1;
        } else if (binding.telefoneRadioButton.isChecked()) {
            if (fone.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Dados incompletos", Toast.LENGTH_SHORT).show();
                return;
            }
            //verifica se a string do telefone possui somente numeros
            if (!fone.matches("\\d+") || fone.length() != 11) return;

            tipo = 2;
        }

        createPost(email, senha, fone);
    }

    private void createPost(String email, String senha, String celular) {
        binding.progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSON(email, senha, celular);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postCreateUser(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                binding.progressBar.setVisibility(View.GONE);
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Dados já utilizados", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t);
            }
        });
    }

    private String formatJSON(String email, String senha, String celular) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject login = new JSONObject();

            login.put("email", email);
            login.put("cell", celular);
            login.put("password", senha);

            root.put("login", login);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
