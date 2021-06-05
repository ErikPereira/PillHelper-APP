package com.example.pillhelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.pillhelper.dataBase.DataBaseSupervisorHelper;
import com.example.pillhelper.item.SupervisorItem;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.utils.Constants;
import com.example.pillhelper.utils.UserIdSingleton;
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
import static com.example.pillhelper.utils.Constants.NOME_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.REGISTRADO_POR;
import static com.example.pillhelper.utils.Constants.VINCULO;

public class SupervisorListAdapter extends ArrayAdapter<SupervisorItem> {

    private static final String TAG = "SupervisorListAdapter";

    private Context mContext;
    private int mResource;
    private DataBaseSupervisorHelper mDataBaseSupervisorHelper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private EditText editTextName;
    private Cursor data;

    public SupervisorListAdapter(Context context, int resource, ArrayList<SupervisorItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mDataBaseSupervisorHelper = new DataBaseSupervisorHelper(context);

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
                    .setTitle(R.string.dialog_change_name_title_supervisor)
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss())
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        data = mDataBaseSupervisorHelper.getData();
                        data.move(position + 1);

                        String uuidSupervisor = data.getString(0);
                        String registeredBy = data.getString(1);
                        String bond = data.getString(2);
                        String newName = editTextName.getText().toString();

                        if (!newName.isEmpty()) {
                            createPostUpdateSupervisor(uuidSupervisor, registeredBy, bond, newName);
                        } else {
                            Toast.makeText(getContext(), "Nome inválido", Toast.LENGTH_LONG).show();
                        }
                    });

            editTextName = view.findViewById(R.id.edit_supervisor_name);

            TextView statusView = view.findViewById(R.id.string_status_supervisor);
            statusView.setText("Status do vinculo: ");

            String stringBondView = "";
            TextView bondView = view.findViewById(R.id.edit_status_supervisor);

            data = mDataBaseSupervisorHelper.getData();
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
                    .setPositiveButton(R.string.ok, (dialog, id) -> createPostDeleteSupervisor(position, getItem(position).getUuidSupervisor()))
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return convertView;
    }

    private void loadDataToView(View view, int position){
        data = mDataBaseSupervisorHelper.getData();
        data.move(position + 1);

        TextView nameView = view.findViewById(R.id.supervisor_name);
        nameView.setText(data.getString(3));
    }

    private void createPostUpdateSupervisor(String uuidSupervisor, String registeredBy, String bond, String newName) {
        String requestStr = formatJSONUpdateSupervisor(uuidSupervisor, registeredBy, bond, newName);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postUpdateSupervisorInUser(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                mDataBaseSupervisorHelper.updateData(
                        uuidSupervisor,
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

    private void createPostDeleteSupervisor(int position, String uuidSupervisor){
        String requestStr = formatJSONDeleteSupervisor(uuidSupervisor);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postDeleteSupervisorInUser(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                Cursor data = mDataBaseSupervisorHelper.getData();
                data.move(position + 1);

                int isDeleted = mDataBaseSupervisorHelper.removeData(uuidSupervisor);

                if (isDeleted > 0) {
                    SupervisorListAdapter.this.remove(getItem(position));
                    SupervisorListAdapter.this.notifyDataSetChanged();
                } else Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSONUpdateSupervisor(String uuidSupervisor, String registeredBy, String bond, String newName) {
        final JSONObject root = new JSONObject();

        try {
            JSONObject updateSupervisor = new JSONObject();
            updateSupervisor.put(ID_SUPERVISOR, String.valueOf(uuidSupervisor));
            updateSupervisor.put(REGISTRADO_POR, String.valueOf(registeredBy));
            updateSupervisor.put(VINCULO, String.valueOf(bond));
            updateSupervisor.put(NOME_SUPERVISOR, String.valueOf(newName));


            root.put("uuidUser", UserIdSingleton.getInstance().getUserId());
            root.put("supervisor", updateSupervisor);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String formatJSONDeleteSupervisor(String uuidSupervisor) {
        final JSONObject root = new JSONObject();

        try {
            root.put("uuidUser", UserIdSingleton.getInstance().getUserId());
            root.put(ID_SUPERVISOR, uuidSupervisor);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}

