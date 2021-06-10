package com.example.pillhelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pillhelper.R;
import com.example.pillhelper.dataBaseUser.DataBaseClinicalDataHelper;
import com.example.pillhelper.databinding.ActivityRegisterClinicalDataBinding;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.singleton.UserIdSingleton;
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
import static com.example.pillhelper.utils.Constants.OPEN_BOX_FRAG;

public class RegisterClinicalDataActivity extends AppCompatActivity {
    private static final String TAG = "RegisterClinicalDataActivity";

    private ActivityRegisterClinicalDataBinding binding;
    private DataBaseClinicalDataHelper mDataBaseClinicalDataHelper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterClinicalDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.register_clinical_data_title);

        mDataBaseClinicalDataHelper = new DataBaseClinicalDataHelper(this);

        binding.backButtonRegisterClinicalData.setOnClickListener(v -> finish());

        binding.buttonRegisterClinicalData.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);

            String name = binding.nameInfClinicalData.getText().toString();
            String value = binding.valueInfClinicalData.getText().toString();

            createPostCreateClinicalData(name, value);

        });

        binding.infValueClinicalData.setOnClickListener(v -> imageInfoClick(binding.infValueClinicalData));
        binding.infNameClinicalData.setOnClickListener(v -> imageInfoClick(binding.infNameClinicalData));
    }

    public void imageInfoClick(ImageView imageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.info_dialog_title_text);

        if (binding.infNameClinicalData.equals(imageView)) {
            builder.setMessage(R.string.dialog_text_clinical_data_name_info);
        } else if (binding.infValueClinicalData.equals(imageView)) {
            builder.setMessage(R.string.dialog_text_clinical_data_value_info);
        }

        builder.setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());

        builder.create().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createPostCreateClinicalData(String nameClinicalData, String value) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSONCreateClinicalData(nameClinicalData, value);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postAddClinicalData(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()){
                    if (response.code() == 409) {
                        Toast.makeText(getBaseContext(), "Dado Clínico já cadastrado!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getBaseContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean confirmation = mDataBaseClinicalDataHelper.addData(
                        nameClinicalData,
                        value);

                if (confirmation){
                    Intent intent = new Intent(getBaseContext(), FragmentsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(OPEN_BOX_FRAG, false);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(getBaseContext(), "Não foi possível salvar o Supervisor", Toast.LENGTH_LONG).show();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSONCreateClinicalData(String nameClinicalData, String newValue) {
        final JSONObject root = new JSONObject();

        try {
            root.put("uuidUser", UserIdSingleton.getInstance().getUserId());
            root.put("nameClinicalData", nameClinicalData);
            root.put("valueClinicalData", newValue);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
