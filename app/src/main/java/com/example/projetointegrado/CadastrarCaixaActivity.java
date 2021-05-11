package com.example.projetointegrado;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.projetointegrado.databinding.ActivityCadastrarCaixaBinding;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.projetointegrado.Constants.BASE_URL;
import static com.example.projetointegrado.Constants.ID_CAIXA;
import static com.example.projetointegrado.Constants.ID_USUARIO;
import static com.example.projetointegrado.Constants.MUDAR_USUARIO;
import static com.example.projetointegrado.Constants.NOME_CAIXA;
import static com.example.projetointegrado.Constants.OPEN_BOX_FRAG;

public class CadastrarCaixaActivity extends AppCompatActivity {

    private static String TAG = CadastrarCaixaActivity.class.getSimpleName();
    private ActivityCadastrarCaixaBinding binding;

    DataBaseBoxHelper mDataBaseBoxHelper;

    SurfaceView surfaceView;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    boolean notRead = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastrarCaixaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("QR Code");

        mDataBaseBoxHelper = new DataBaseBoxHelper(this);

        surfaceView = binding.cameraPreview;

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480).build();

        binding.cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    if (ActivityCompat.checkSelfPermission(CadastrarCaixaActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if (qrCodes.size() != 0) {
                    binding.warningTextView.post(() -> {
                        if (notRead) {
                            notRead = false;
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(200);

                            binding.progressBar.setVisibility(View.VISIBLE);

                            String readCode = qrCodes.valueAt(0).displayValue;
                            addDataDB(readCode, readCode);
                        }
                    });
                }
            }
        });
    }

    private void addDataDB(String idCaixa, String nome) {
        createPostCreateBox(idCaixa, nome);
    }

    private void createPostCreateBox(String idCaixa, String nome) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String requestStr = formatJSONCreateAlarm(idCaixa, nome);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postCreateUpdateBox(request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), "Um erro ocorreu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response);
                    finish();
                    return;
                } else {
                    boolean confirmation = mDataBaseBoxHelper.addData(idCaixa, nome);

                    if (confirmation) {
                        Intent intent = new Intent(getBaseContext(), FragmentsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(OPEN_BOX_FRAG, true);
                        startActivity(intent);
                        finish();
                    } else
                        Toast.makeText(getBaseContext(), "Algo deu errado", Toast.LENGTH_LONG).show();

                    Log.e(TAG, "onResponse: " + response);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSONCreateAlarm(String idCaixa, String nome) {
        final JSONObject root = new JSONObject();

        try {
            root.put(ID_USUARIO, UserIdSingleton.getInstance().getUserId());
            root.put(ID_CAIXA, String.valueOf(idCaixa));
            root.put(NOME_CAIXA, String.valueOf(nome));
            root.put(MUDAR_USUARIO, String.valueOf(false));

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
