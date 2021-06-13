package com.example.pillhelper.adapter;

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

import com.example.pillhelper.R;
import com.example.pillhelper.dataBaseUser.DataBaseClinicalDataHelper;
import com.example.pillhelper.item.ClinicalDataItem;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.singleton.UserIdSingleton;
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

import static com.example.pillhelper.utils.Constants.BASE_URL;

public class ClinicalDataListAdapter extends ArrayAdapter<ClinicalDataItem> {
    private static final String TAG = "ClinicalDataListAdapter";

    private Context mContext;
    private int mResource;
    private DataBaseClinicalDataHelper mDataBaseClinicalDataHelper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private EditText editTextValue;
    private Cursor data;

    public ClinicalDataListAdapter(Context context, int resource, ArrayList<ClinicalDataItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mDataBaseClinicalDataHelper = new DataBaseClinicalDataHelper(context);

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

        ConstraintLayout constraintLayout = convertView.findViewById(R.id.clinical_data_list_layout);

        constraintLayout.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater insideInflater = LayoutInflater.from(getContext());

            View view = insideInflater.inflate(R.layout.layout_dialog_clinical_data, parent, false);

            builder.setView(view)
                    .setTitle(R.string.dialog_change_name_title_clinical_data)
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss())
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        data = mDataBaseClinicalDataHelper.getData();
                        data.move(position + 1);

                        String nameClinicalData = data.getString(0);
                        String newValue = editTextValue.getText().toString();

                        if (!newValue.isEmpty()) {
                            createPostUpdateClinicalData(nameClinicalData, newValue);
                        } else {
                            Toast.makeText(getContext(), "Nome inválido", Toast.LENGTH_LONG).show();
                        }
                    });

            editTextValue = view.findViewById(R.id.edit_clinical_data_value);

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        ImageView imageView = convertView.findViewById(R.id.clinical_data_list_image);
        imageView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title)
                    .setPositiveButton(R.string.ok, (dialog, id) -> createPostDeleteClinicalData(position, getItem(position).getName()))
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return convertView;
    }

    private void loadDataToView(View view, int position){
        data = mDataBaseClinicalDataHelper.getData();
        data.move(position + 1);

        TextView nameView = view.findViewById(R.id.clinical_data_name);
        TextView valueView = view.findViewById(R.id.clinical_data_value);

        nameView.setText(data.getString(0).replaceAll(" ","\n"));
        valueView.setText(data.getString(1));
    }

    private void createPostUpdateClinicalData(String nameClinicalData, String newValue) {
        String requestStr = formatJSONUpdateClinicalData(nameClinicalData, newValue);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postUpdateClinicalData(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                mDataBaseClinicalDataHelper.updateData(
                        nameClinicalData,
                        newValue);

                notifyDataSetChanged();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private void createPostDeleteClinicalData(int position, String nameClinicalData){
        String requestStr = formatJSONDeleteClinicalData(nameClinicalData);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postDeleteClinicalData(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                Cursor data = mDataBaseClinicalDataHelper.getData();
                data.move(position + 1);

                int isDeleted = mDataBaseClinicalDataHelper.removeData(nameClinicalData);

                if (isDeleted > 0) {
                    ClinicalDataListAdapter.this.remove(getItem(position));
                    ClinicalDataListAdapter.this.notifyDataSetChanged();
                } else Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSONUpdateClinicalData(String nameClinicalData, String newValue) {
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

    private String formatJSONDeleteClinicalData(String nameClinicalData) {
        final JSONObject root = new JSONObject();

        try {
            root.put("uuidUser", UserIdSingleton.getInstance().getUserId());
            root.put("nameClinicalData", nameClinicalData);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
