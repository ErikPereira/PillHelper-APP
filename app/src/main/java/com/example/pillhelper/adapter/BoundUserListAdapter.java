package com.example.pillhelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
import com.example.pillhelper.activity.FragmentsActivity;
import com.example.pillhelper.activity.LoginActivity;
import com.example.pillhelper.dataBaseSupervisor.DataBaseBoundUserHelper;
import com.example.pillhelper.dataBaseUser.DataBaseAlarmsHelper;
import com.example.pillhelper.dataBaseUser.DataBaseBoundSupervisorHelper;
import com.example.pillhelper.dataBaseUser.DataBaseBoxHelper;
import com.example.pillhelper.dataBaseUser.DataBaseClinicalDataHelper;
import com.example.pillhelper.item.BoundItem;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.singleton.SupervisorIdSingleton;
import com.example.pillhelper.singleton.UserIdSingleton;
import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.utils.LoadDataBase;
import com.google.gson.JsonArray;
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
import static com.example.pillhelper.utils.Constants.ID_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.NOME_USER;
import static com.example.pillhelper.utils.Constants.REGISTRADO_POR;
import static com.example.pillhelper.utils.Constants.VINCULO;
import static com.example.pillhelper.utils.Constants.OPEN_BOX_FRAG;
import static com.example.pillhelper.utils.Constants.WHO_USER_FRAG;

public class BoundUserListAdapter extends ArrayAdapter<BoundItem> {

    private static final String TAG = "BoundSupervisorListAdapter";

    private Context mContext;
    private int mResource;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private EditText editTextName;
    private Cursor data;
    DataBaseAlarmsHelper mDataBaseAlarmsHelper;
    DataBaseBoxHelper mDataBaseBoxHelper;
    DataBaseBoundSupervisorHelper mDataBaseBoundSupervisorHelper;
    DataBaseBoundUserHelper mDataBaseBoundUserHelper;
    DataBaseClinicalDataHelper mDataBaseClinicalDataHelper;

    public BoundUserListAdapter(Context context, int resource, ArrayList<BoundItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mDataBaseBoundUserHelper = new DataBaseBoundUserHelper(context);

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

        ConstraintLayout constraintLayout = convertView.findViewById(R.id.supervisor_list_layout);

        constraintLayout.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater insideInflater = LayoutInflater.from(getContext());

            View view = insideInflater.inflate(R.layout.layout_dialog_supervisor, parent, false);

            builder.setView(view)
                    .setTitle(R.string.dialog_change_name_title_user)
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss())
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        data = mDataBaseBoundUserHelper.getData();
                        data.move(position + 1);

                        String uuidUser = data.getString(0);
                        String registeredBy = data.getString(1);
                        String bond = data.getString(2);
                        String newName = editTextName.getText().toString();

                        if (!newName.isEmpty()) {
                            createPostUpdateUser(uuidUser, registeredBy, bond, newName);
                        } else {
                            Toast.makeText(getContext(), "Nome inválido", Toast.LENGTH_LONG).show();
                        }
                    });

            editTextName = view.findViewById(R.id.edit_supervisor_name);

            TextView statusView = view.findViewById(R.id.string_status_supervisor);
            statusView.setText("Status do vinculo: ");

            String stringBondView = "";
            TextView bondView = view.findViewById(R.id.edit_status_supervisor);

            data = mDataBaseBoundUserHelper.getData();
            data.move(position + 1);

            String bond = data.getString(2);

            switch (bond.toLowerCase()){
                case "wait":
                    stringBondView = "Aguardando confirmação";
                    bondView.setTextColor(Color.GRAY);
                    break;
                case "refused":
                    stringBondView = "Vinculo Recusado";
                    bondView.setTextColor(Color.RED);
                    break;
                case "accepted":
                    stringBondView = "Vinculo Aceito";
                    bondView.setTextColor(Color.GREEN);
                    break;
                default:
                    stringBondView = "Vinculo Deletado";
                    bondView.setTextColor(Color.RED);
            }
            bondView.setText(stringBondView);

            AlertDialog dialog = builder.create();
            dialog.show();

        });

        ImageView imageView = convertView.findViewById(R.id.supervisor_list_image);
        imageView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title)
                    .setPositiveButton(R.string.ok, (dialog, id) -> createPostDeleteUser(position, getItem(position).getUuid()))
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        ImageView bondImage = convertView.findViewById(R.id.adapter_image_bound);
        bondImage.setOnClickListener(u -> {
            Intent intent = new Intent(mContext, FragmentsActivity.class);
            intent.putExtra(OPEN_BOX_FRAG, false);
            intent.putExtra(WHO_USER_FRAG, "user");
            UserIdSingleton.getInstance().setUserId(getItem(position).getUuid());

            getContext().deleteDatabase("alarms_table");
            getContext().deleteDatabase("boxes_table");
            getContext().deleteDatabase("bound_supervisors_table");
            getContext().deleteDatabase("clinical_data_table");

            postGetUser(getItem(position).getUuid());

            mContext.startActivity(intent);
            intent.putExtra(WHO_USER_FRAG, "supervisor");

            getContext().deleteDatabase("alarms_table");
            getContext().deleteDatabase("boxes_table");
            getContext().deleteDatabase("bound_supervisors_table");
            getContext().deleteDatabase("clinical_data_table");
        });

        return convertView;
    }

    private void loadDataToView(View view, int position){
        data = mDataBaseBoundUserHelper.getData();
        data.move(position + 1);

        TextView nameView = view.findViewById(R.id.supervisor_name);
        nameView.setText(data.getString(3));

        ImageView bondImage = view.findViewById(R.id.adapter_image_bound);

        String bond = data.getString(2);

        switch (bond.toLowerCase()){
            case "wait":
                bondImage.setImageResource(R.drawable.ic_supervisor_gray_48dp);
                break;
            case "refused":
                bondImage.setImageResource(R.drawable.ic_supervisor_red_48dp);
                break;
            case "accepted":
                bondImage.setImageResource(R.drawable.ic_supervisor_green_48dp);
                break;
            default:
                bondImage.setImageResource(R.drawable.ic_supervisor_48dp);
        }

    }

    private void createPostUpdateUser(String uuidUser, String registeredBy, String bond, String newName) {
        String requestStr = formatJSONUpdateUser(uuidUser, registeredBy, bond, newName);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postUpdateUserInSupervisor(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                mDataBaseBoundUserHelper.updateData(
                        uuidUser,
                        registeredBy,
                        bond,
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

    private void createPostDeleteUser(int position, String uuidUser){
        String requestStr = formatJSONDeleteUser(uuidUser);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postDeleteUserInSupervisor(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                Cursor data = mDataBaseBoundUserHelper.getData();
                data.move(position + 1);

                int isDeleted = mDataBaseBoundUserHelper.removeData(uuidUser);

                if (isDeleted > 0) {
                    BoundUserListAdapter.this.remove(getItem(position));
                    BoundUserListAdapter.this.notifyDataSetChanged();
                } else Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSONUpdateUser(String uuidSupervisor, String registeredBy, String bond, String newName) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject updateSupervisor = new JSONObject();
            updateSupervisor.put("uuidUser", String.valueOf(uuidSupervisor));
            updateSupervisor.put(REGISTRADO_POR, String.valueOf(registeredBy));
            updateSupervisor.put(VINCULO, String.valueOf(bond));
            updateSupervisor.put(NOME_USER, String.valueOf(newName));


            root.put(ID_SUPERVISOR, SupervisorIdSingleton.getInstance().getSupervisorId());
            root.put("user", updateSupervisor);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String formatJSONDeleteUser(String uuidUser) {
        final JSONObject root = new JSONObject();

        try {
            root.put(ID_SUPERVISOR, SupervisorIdSingleton.getInstance().getSupervisorId());
            root.put("uuidUser", uuidUser);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void postGetUser(String uuidUser) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<JsonObject> call = jsonPlaceHolderApi.postUserData(Constants.TOKEN_ACCESS,
                uuidUser);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Um erro ocorreu", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onResponse: " + response);
                    return;
                }

                JsonObject jsonObject = response.body();
                JsonArray alarmsArray = jsonObject.getAsJsonObject("response").getAsJsonArray("alarms");
                JsonArray boxArray = jsonObject.getAsJsonObject("response").getAsJsonArray("box");
                JsonArray supervisorArray = jsonObject.getAsJsonObject("response").getAsJsonArray("supervisors");
                JsonObject clinicalDataObject = jsonObject.getAsJsonObject("response").getAsJsonObject("clinicalData");

                mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(getContext());
                mDataBaseBoxHelper = new DataBaseBoxHelper(getContext());
                mDataBaseBoundSupervisorHelper = new DataBaseBoundSupervisorHelper(getContext());
                mDataBaseClinicalDataHelper = new DataBaseClinicalDataHelper(getContext());

                LoadDataBase loadDataBase = new LoadDataBase();
                loadDataBase.loadDataBaseUser(
                        alarmsArray,
                        boxArray,
                        supervisorArray,
                        clinicalDataObject,
                        mDataBaseAlarmsHelper,
                        mDataBaseBoxHelper,
                        mDataBaseClinicalDataHelper,
                        mDataBaseBoundSupervisorHelper);

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

}

