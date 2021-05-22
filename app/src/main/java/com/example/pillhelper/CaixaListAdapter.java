package com.example.pillhelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.pillhelper.Constants.BASE_URL;
import static com.example.pillhelper.Constants.ID_CAIXA;
import static com.example.pillhelper.Constants.ID_USUARIO;
import static com.example.pillhelper.Constants.MUDAR_USUARIO;
import static com.example.pillhelper.Constants.NOME_CAIXA;

public class CaixaListAdapter extends ArrayAdapter<CaixaItem> {

    private static final String TAG = "CaixaListAdapter";

    private Context mContext;
    private int mResource;
    private DataBaseBoxHelper mDataBaseBoxHelper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private EditText editTextName;
    private Cursor data;

    CaixaListAdapter(Context context, int resource, ArrayList<CaixaItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mDataBaseBoxHelper = new DataBaseBoxHelper(context);

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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        loadDataToView(convertView, position);

        ConstraintLayout constraintLayout = convertView.findViewById(R.id.caixa_list_layout);

        View finalConvertView = convertView;
        constraintLayout.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater insideInflater = LayoutInflater.from(getContext());

            View view = insideInflater.inflate(R.layout.layout_dialog, parent, false);

            builder.setView(view)
                    .setTitle(R.string.dialog_change_name_title)
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss())
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        data = mDataBaseBoxHelper.getData();
                        data.move(position + 1);

                        String newName = editTextName.getText().toString();

                        if (!newName.isEmpty()) {
                            createPostUpdateBox(finalConvertView, position, data.getString(0), newName);
                        } else {
                            Toast.makeText(getContext(), "Nome inválido", Toast.LENGTH_LONG).show();
                        }
                    });

            editTextName = view.findViewById(R.id.edit_box_name);

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        ImageView imageView = convertView.findViewById(R.id.caixa_list_image);
        imageView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title)
                    .setPositiveButton(R.string.ok, (dialog, id) -> createPostDeleteBox(position, getItem(position).getUuidBox()))
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return convertView;
    }

    private void loadDataToView(View view, int position){
        data = mDataBaseBoxHelper.getData();
        data.move(position + 1);

        TextView nameView = view.findViewById(R.id.box_name);
        nameView.setText(data.getString(1));
    }

    private void createPostUpdateBox(View convertView, int position, String uuidBox, String newName) {
        String requestStr = formatJSONUpdateBox(uuidBox, newName);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postUpdateBox(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                mDataBaseBoxHelper.updateData(
                        uuidBox,
                        newName);

                notifyDataSetChanged();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSONUpdateBox(String uuidBox, String nameBox) {
        final JSONObject root = new JSONObject();

        try {
            root.put("uuidUser", UserIdSingleton.getInstance().getUserId());
            root.put(ID_CAIXA, String.valueOf(uuidBox));
            root.put("newNameBox", String.valueOf(nameBox));

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void createPostDeleteBox(int position, String uuidBox){
        String requestStr = formatJSONDeleteBox(uuidBox);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postDeleteBox(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                Cursor data = mDataBaseBoxHelper.getData();
                data.move(position + 1);

                int isDeleted = mDataBaseBoxHelper.removeData(uuidBox);

                if (isDeleted > 0) {
                    CaixaListAdapter.this.remove(getItem(position));
                    CaixaListAdapter.this.notifyDataSetChanged();
                } else Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSONDeleteBox(String uuidBox) {
        final JSONObject root = new JSONObject();

        try {
            root.put("uuidUser", UserIdSingleton.getInstance().getUserId());
            root.put(ID_CAIXA, uuidBox);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}