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
import com.example.pillhelper.dataBaseUser.DataBaseBoundSupervisorHelper;
import com.example.pillhelper.item.BoundItem;
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
import static com.example.pillhelper.utils.Constants.ID_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.NOME_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.OPEN_BOX_FRAG;
import static com.example.pillhelper.utils.Constants.REGISTRADO_POR;
import static com.example.pillhelper.utils.Constants.VINCULO;
import static com.example.pillhelper.utils.Constants.WHO_USER_FRAG;

public class BoundSupervisorListAdapter extends ArrayAdapter<BoundItem> {

    private static final String TAG = "BoundSupervisorListAdapter";

    private Context mContext;
    private int mResource;
    private DataBaseBoundSupervisorHelper mDataBaseBoundSupervisorHelper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private EditText editTextName;
    private Cursor data;

    public BoundSupervisorListAdapter(Context context, int resource, ArrayList<BoundItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        mDataBaseBoundSupervisorHelper = new DataBaseBoundSupervisorHelper(context);

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
                        data = mDataBaseBoundSupervisorHelper.getData();
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

            data = mDataBaseBoundSupervisorHelper.getData();
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
                    .setPositiveButton(R.string.ok, (dialog, id) -> createPostDeleteSupervisor(position, getItem(position).getUuid(), "deleted"))
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        ImageView bondImage = convertView.findViewById(R.id.adapter_image_bound);
        bondImage.setOnClickListener(u -> {
            data = mDataBaseBoundSupervisorHelper.getData();
            data.move(position + 1);

            String bond = data.getString(2);
            String getRegisteredBy = getItem(position).getRegisteredBy();
            String name = getItem(position).getName();
            String uuidSupervisor = getItem(position).getUuid();

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            String msg;

            switch (bond.toLowerCase()){
                case "accepted":
                    msg = "Vínculo permitido!";
                    builder.setMessage(msg)
                            .setTitle("Status do vínculo")
                            .setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());
                    break;
                case "wait":
                    if (getRegisteredBy.toLowerCase().equals("user")){
                        msg = "Vínculo aguardando resposta!\n\nPor favor, aguarde o supervisor " + name + " aceitar o vínculo.";
                        builder.setMessage(msg)
                                .setTitle("Status do vínculo")
                                .setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());

                    }
                    else {
                        msg = "Vínculo Solicitado!\n\nDeseja aceitar o vínculo do supervisor " + name + " ?";
                        builder.setMessage(msg)
                                .setTitle("Status do vínculo")
                                .setPositiveButton("Aceitar", (dialog, id) -> createPostUpdateSupervisor(uuidSupervisor, getRegisteredBy, "accepted", name))
                                .setNegativeButton("Negar", (dialog, id) -> {
                                    createPostUpdateSupervisor(uuidSupervisor, getRegisteredBy, "refused", name);
                                    createPostDeleteSupervisor(position, uuidSupervisor, "refused");
                                });

                    }
                    break;

                case "refused":
                    msg = "Vínculo Recusado!\n\nEsta tentativa de vínculo será excluido\n\nPor favor, entre e contato com o usuário " + name + " e tente novamente.";
                    builder.setMessage(msg)
                            .setTitle("Status do vínculo")
                            .setPositiveButton(R.string.ok, (dialog, id) -> createPostDeleteSupervisor(position, uuidSupervisor, "deleted"));
                    break;
                case "deleted":
                    msg = "Vínculo Deletado!\n\nEste vínculo será excluido\n\nPor favor, entre e contato com o usuário " + name + " e tente novamente.";
                    builder.setMessage(msg)
                            .setTitle("Status do vínculo")
                            .setPositiveButton(R.string.ok, (dialog, id) -> createPostDeleteSupervisor(position, uuidSupervisor, "deleted"));
                    break;
                default:

            }

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return convertView;
    }

    private void loadDataToView(View view, int position){
        data = mDataBaseBoundSupervisorHelper.getData();
        data.move(position + 1);

        TextView nameView = view.findViewById(R.id.supervisor_name);
        nameView.setText(data.getString(3));

        ImageView bondImage = view.findViewById(R.id.adapter_image_bound);

        String bond = data.getString(2);

        switch (bond.toLowerCase()){
            case "wait":
                bondImage.setImageResource(R.drawable.ic_supervisor_gray_48dp);
                break;
            case "accepted":
                bondImage.setImageResource(R.drawable.ic_supervisor_green_48dp);
                break;
            default:
                bondImage.setImageResource(R.drawable.ic_supervisor_red_48dp);
        }
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

                mDataBaseBoundSupervisorHelper.updateData(
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

    private void createPostDeleteSupervisor(int position, String uuidSupervisor, String bond){
        String requestStr = formatJSONDeleteSupervisor(uuidSupervisor, bond);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postDeleteSupervisorInUser(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (!response.isSuccessful()) {
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                Cursor data = mDataBaseBoundSupervisorHelper.getData();
                data.move(position + 1);

                int isDeleted = mDataBaseBoundSupervisorHelper.removeData(uuidSupervisor);

                if (isDeleted > 0) {
                    BoundSupervisorListAdapter.this.remove(getItem(position));
                    BoundSupervisorListAdapter.this.notifyDataSetChanged();
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

    private String formatJSONDeleteSupervisor(String uuidSupervisor, String bond) {
        final JSONObject root = new JSONObject();

        try {
            root.put("uuidUser", UserIdSingleton.getInstance().getUserId());
            root.put(ID_SUPERVISOR, uuidSupervisor);
            root.put("bondSupervisor", bond);

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}

