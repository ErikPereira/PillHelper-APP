package com.example.pillhelper.activity;

import static android.os.Environment.getExternalStoragePublicDirectory;

import static com.example.pillhelper.utils.Constants.BASE_URL;
import static com.example.pillhelper.utils.Constants.OPEN_BOX_FRAG;
import static com.example.pillhelper.utils.Constants.WHO_USER_FRAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.pillhelper.R;
import com.example.pillhelper.adapter.BullaListAdapter;
import com.example.pillhelper.dataBaseBulla.DataBaseBullaHelper;
import com.example.pillhelper.dataBaseSupervisor.DataBaseBoundUserHelper;
import com.example.pillhelper.databinding.ActivitySearchBullasBinding;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.singleton.SupervisorIdSingleton;
import com.example.pillhelper.singleton.UserIdSingleton;
import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.utils.LoadDataBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchBullaActivity extends AppCompatActivity {
    private static final String TAG = "SearchBullaActivity";
    static final int REQUEST_TAKE_PHOTO = 4;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PERMISSION_REQUEST = 2;
    private ActivitySearchBullasBinding binding;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private DataBaseBullaHelper mDataBaseBullaHelper;
    private String who = "supervisor";

    private String currentPhotoPath;
    private File photoFile = null;
    private Object lock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBullasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Novas Bulas");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST);
            }
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST);
            }
        }

        binding.backButton.setOnClickListener(v -> finish());

        binding.takeAPictureButton.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.pillhelper.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        });

        binding.saveImageButton.setOnClickListener(v -> {
            if(photoFile == null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Por favor, tire uma foto antes.")
                        .setTitle("Ops, faltou algo!")
                        .setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
            File image = photoFile.getAbsoluteFile();
            createTextRecognizer(image, this);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater insideInflater = LayoutInflater.from(this);

            View view = insideInflater.inflate(R.layout.layout_dialog_wait_bulla, binding.getRoot(), false);

            builder.setView(view)
                    .setTitle("Processando!")
                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(photoFile))
            );
            showImage();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG" + timeStamp + "";
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void showImage() {
        int targetW = binding.bullaImage.getWidth();
        int targetH = binding.bullaImage.getHeight();
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), bmOptions);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        binding.bullaImage.setImageBitmap(rotated);
    }

    private void createTextRecognizer(File image, Context context) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String uuid = SupervisorIdSingleton.getInstance().getSupervisorId();
        if (TextUtils.isEmpty(uuid)) {
            uuid = UserIdSingleton.getInstance().getUserId();
            who = "user";
        }
        RequestBody requestUuid = RequestBody.create(MediaType.parse("multipart/form-data"), uuid);
        MultipartBody.Part requestImage = null;

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), image);
        requestImage = MultipartBody.Part.createFormData("image", image.getName(), requestFile);

        Call<JsonObject> call = jsonPlaceHolderApi.postTextRecognizer(Constants.TOKEN_ACCESS, requestImage, requestUuid);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    if (response.code() == 404) {
                        builder.setMessage("Não foi possível identificar qual é o medicamento ou não possuimos a bula para esse medicamento")
                                .setTitle("Bula não encontrada")
                                .setPositiveButton(R.string.ok, (dialog, id) -> {
                                    dialog.dismiss();
                                    finish();
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return;
                    }
                    builder.setMessage("Por favor, tente novamente!")
                            .setTitle("Algo deu errado")
                            .setPositiveButton(R.string.ok, (dialog, id) -> {
                                dialog.dismiss();
                                finish();
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                Log.e(TAG, "onResponse1: " + response);

                JsonObject postResponse = response.body();
                boolean error = postResponse.get("error").getAsBoolean();

                if(error) {
                    String msg = postResponse.get("response").getAsString();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setMessage("A bula identificada já esta cadastrada!")
                            .setTitle(msg)
                            .setPositiveButton(R.string.ok, (dialog, id) -> {
                                dialog.dismiss();
                                finish();
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    Toast.makeText(getBaseContext(), "Usuário não encontrado", Toast.LENGTH_SHORT).show();
                    return;
                }

                JsonArray bullasArray = postResponse.get("response").getAsJsonArray();

                mDataBaseBullaHelper = new DataBaseBullaHelper(getBaseContext());

                LoadDataBase loadDataBase = new LoadDataBase();
                loadDataBase.loadDataBaseSupervisor(null, bullasArray, null, mDataBaseBullaHelper);

                Intent intent = new Intent(getBaseContext(), FragmentsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(OPEN_BOX_FRAG, false);
                intent.putExtra(WHO_USER_FRAG, who);
                startActivity(intent);
                finish();

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }
}
