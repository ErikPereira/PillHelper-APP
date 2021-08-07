package com.example.pillhelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
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
import com.example.pillhelper.dataBaseBulla.DataBaseBullaHelper;
import com.example.pillhelper.item.BoxItem;
import com.example.pillhelper.item.BullaItem;
import com.example.pillhelper.services.JsonPlaceHolderApi;
import com.example.pillhelper.singleton.SupervisorIdSingleton;
import com.example.pillhelper.singleton.UserIdSingleton;
import com.example.pillhelper.utils.Constants;
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

public class BullaListAdapter extends ArrayAdapter<BullaItem> {

    private static final String TAG = "BullaListAdapter";

    private Context mContext;
    private int mResource;
    private DataBaseBullaHelper mDataBaseBullaHelper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private EditText editTextName;
    private Cursor data;
    ArrayList<BullaItem> bullas;

    public BullaListAdapter(Context context, int resource, ArrayList<BullaItem> objects) {
        super(context, resource, objects);
        this.bullas = objects;
        mContext = context;
        mResource = resource;
        mDataBaseBullaHelper = new DataBaseBullaHelper(context);

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

        ConstraintLayout constraintLayout = convertView.findViewById(R.id.bulla_list_layout);

        constraintLayout.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater insideInflater = LayoutInflater.from(getContext());

            View view = insideInflater.inflate(R.layout.layout_dialog_box, parent, false);

            builder.setView(view)
                    .setTitle(R.string.dialog_change_name_title)
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss())
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        data = mDataBaseBullaHelper.getData();
                        data.move(position + 1);

                        String newName = editTextName.getText().toString();
                    });

            editTextName = view.findViewById(R.id.edit_box_name);

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        ImageView imageView = convertView.findViewById(R.id.bulla_list_image);
        imageView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title)
                    .setPositiveButton(R.string.ok, (dialog, id) -> createPostRemoveBulla(getItem(position).getNameBulla()))
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return convertView;
    }

    private void loadDataToView(View view, int position){
        data = mDataBaseBullaHelper.getData();
        data.move(position + 1);

        TextView nameView = view.findViewById(R.id.name_bulla);
        nameView.setText(data.getString(0));
    }

    private void createPostRemoveBulla(String nameBulla) {
        String requestStr = formatJSONRemoveBulla(nameBulla);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postUpdateBox(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                mDataBaseBullaHelper.removeData(nameBulla);

                notifyDataSetChanged();

                Log.e(TAG, "onResponse: " + response);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure:" + t);
            }
        });
    }

    private String formatJSONRemoveBulla(String nameBulla) {
        final JSONObject root = new JSONObject();

        try {
            String uuid = SupervisorIdSingleton.getInstance().getSupervisorId();
            if (TextUtils.isEmpty(uuid)) {
                uuid = UserIdSingleton.getInstance().getUserId();
            }
            root.put("uuid", uuid);
            root.put("nameBulla", String.valueOf(nameBulla));

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
