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
import com.example.pillhelper.dataBaseUser.DataBaseBoundSupervisorHelper;
import com.example.pillhelper.databinding.ActivityRegisterSupervisorBinding;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.utils.MaskEditUtil;
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
import static com.example.pillhelper.utils.Constants.CELL_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.NOME_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.EMAIL_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.OPEN_BOX_FRAG;

public class RegisterSupervisorActivity extends AppCompatActivity {
    private static final String TAG = "RegisterSupervisorActivity";

    private ActivityRegisterSupervisorBinding binding;
    private DataBaseBoundSupervisorHelper mDataBaseBoundSupervisorHelper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterSupervisorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.register_supervisor_title);

        mDataBaseBoundSupervisorHelper = new DataBaseBoundSupervisorHelper(this);

        binding.emailRadioButton.setOnClickListener((View v) -> {
            binding.emailInfSupervisor.setVisibility(View.VISIBLE);
            binding.cellInfSupervisor.setVisibility(View.GONE);
            binding.cellInfSupervisor.setText("");
            binding.nameInfSupervisor.setText("");
            binding.telefoneRadioButton.setChecked(false);
        });

        binding.telefoneRadioButton.setOnClickListener((View v) -> {
            binding.cellInfSupervisor.setVisibility(View.VISIBLE);
            binding.emailInfSupervisor.setVisibility(View.GONE);
            binding.emailInfSupervisor.setText("");
            binding.nameInfSupervisor.setText("");
            binding.emailRadioButton.setChecked(false);
        });


        binding.backButtonRegisterSupervisor.setOnClickListener(v -> finish());

        binding.buttonRegisterSupervisor.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);

            String name = binding.nameInfSupervisor.getText().toString();

            if(binding.emailRadioButton.isChecked()) {
                String emailSupervisor = binding.emailInfSupervisor.getText().toString();
                createPostRegisterSupervisor(emailSupervisor, "", name);
            }
            else {
                String cellSupervisor = binding.cellInfSupervisor.getText().toString();
                cellSupervisor = MaskEditUtil.unmask(cellSupervisor);
                createPostRegisterSupervisor("", cellSupervisor, name);
            }

        });

        binding.infEmailSupervisor.setOnClickListener(v -> imageInfoClick(binding.infEmailSupervisor));
        binding.infNameSupervisor.setOnClickListener(v -> imageInfoClick(binding.infNameSupervisor));

        binding.cellInfSupervisor
                .addTextChangedListener(MaskEditUtil.mask(binding.cellInfSupervisor, MaskEditUtil.FORMAT_FONE));
    }

    public void imageInfoClick(ImageView imageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.info_dialog_title_text);

        if (binding.infNameSupervisor.equals(imageView)) {
            builder.setMessage(R.string.dialog_text_supervisor_name_info);
        } else if (binding.infEmailSupervisor.equals(imageView)) {
            builder.setMessage(R.string.dialog_text_supervisor_email_info);
        }

        builder.setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());

        builder.create().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void createPostRegisterSupervisor(String emailSupervisor, String cellSupervisor, String nameSupervisor) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSONRegisterSupervisor(emailSupervisor, cellSupervisor, nameSupervisor);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postRegisterSupervisor(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful()) {
                    if (response.code() == 404) {
                        Toast.makeText(getBaseContext(), "Supervisor não encontrado", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(getBaseContext(), "Um erro ocorreu", Toast.LENGTH_SHORT).show();
                    return;
                }
                JsonObject jsonObject = response.body();
                boolean error = jsonObject.get("error").getAsBoolean();

                if (error) {
                    String msgError = jsonObject.get("msgError").getAsString();
                    Toast.makeText(getBaseContext(), msgError, Toast.LENGTH_LONG).show();
                    return;
                }

                JsonObject postResponse = jsonObject.getAsJsonObject("response");

                String uuidSupervisor = postResponse.get("uuidSupervisor").getAsString();
                String registeredBy = postResponse.get("registeredBy").getAsString();
                String bond = postResponse.get("bond").getAsString();
                String name = postResponse.get("name").getAsString();

                boolean confirmation = mDataBaseBoundSupervisorHelper.addData(
                        uuidSupervisor,
                        registeredBy,
                        bond,
                        name);

                if (confirmation){
                    Intent intent = new Intent(getBaseContext(), FragmentsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(OPEN_BOX_FRAG, false);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(getBaseContext(), "Não foi possível salvar o Supervisor", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSONRegisterSupervisor(String emailSupervisor, String cellSupervisor, String nameSupervisor) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject loginSupervisor = new JSONObject();
            loginSupervisor.put(CELL_SUPERVISOR, String.valueOf(cellSupervisor));
            loginSupervisor.put(EMAIL_SUPERVISOR, String.valueOf(emailSupervisor));
            loginSupervisor.put(NOME_SUPERVISOR, String.valueOf(nameSupervisor));


            root.put("uuidUser", UserIdSingleton.getInstance().getUserId());
            root.put("loginSupervisor", loginSupervisor);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
