package com.example.pillhelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.pillhelper.activity.BullaInformationActivity;
import com.example.pillhelper.dataBaseBulla.DataBaseBullaHelper;
import com.example.pillhelper.dataBaseBulla.DataBaseBullaUserHelper;
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
import static com.example.pillhelper.utils.Constants.WHO_USER_FRAG;

public class BullaListAdapter extends ArrayAdapter<BullaItem> {

    private static final String TAG = "BullaListAdapter";

    private Context mContext;
    private int mResource;
    private DataBaseBullaHelper mDataBaseBullaHelper;
    private DataBaseBullaUserHelper mDataBaseBullaUserHelper;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    ArrayList<BullaItem> bullas;

    public BullaListAdapter(Context context, int resource, ArrayList<BullaItem> objects) {
        super(context, resource, objects);
        this.bullas = objects;
        mContext = context;
        mResource = resource;
        mDataBaseBullaHelper = new DataBaseBullaHelper(context);
        mDataBaseBullaUserHelper = new DataBaseBullaUserHelper(context);

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

            Intent intent = new Intent(getContext(), BullaInformationActivity.class);
            String bullaInformation = "<resource>\n<string name=\"my_string\">";
            ArrayList<String> titles = getItem(position).getTitle();
            ArrayList<String> descriptions = getItem(position).getDescription();
            ArrayList<String> informations = getItem(position).getInformation();

            for (int i = 0; i < titles.size(); i++) {
                String title = titles.get(i);
                String description = descriptions.get(i);
                String information = informations.get(i);

                bullaInformation += "<big><b>" + title +"</b></big>"
                                + "<br><br><b>" + description +"</b>"
                                + "<br><br><p align=\"justify\">\t\t" + information + "</p><br>";
            }

            bullaInformation = bullaInformation.replaceAll(". <br> ",".<br>\t\t");
            bullaInformation = bullaInformation.replaceAll("____","_");
            bullaInformation += "</string>\n </resources>\n";

            intent.putExtra("BULLA_INFORMATION", bullaInformation);
            intent.putExtra("NAME_BULLA", getItem(position).getNameBulla());

            mContext.startActivity(intent);
        });

        ImageView imageView = convertView.findViewById(R.id.bulla_list_image);
        imageView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setMessage(R.string.dialog_message)
                    .setTitle(R.string.dialog_title)
                    .setPositiveButton(R.string.ok, (dialog, id) -> createPostRemoveBulla(position, getItem(position).getNameBulla()))
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return convertView;
    }

    private void loadDataToView(View view, int position){
        BullaItem bulla = bullas.get(position);

        TextView nameView = view.findViewById(R.id.name_bulla);
        nameView.setText(bulla.getNameBulla());
    }

    private void createPostRemoveBulla(int position, String nameBulla) {
        String requestStr = formatJSONRemoveBulla(nameBulla);
        JsonObject request = JsonParser.parseString(requestStr).getAsJsonObject();

        Call<JsonObject> call = jsonPlaceHolderApi.postRemoveBulla(Constants.TOKEN_ACCESS, request);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();
                    return;
                }

                Log.e(TAG, "onResponse: " + response);
                int isDeleted;
                if (bullas.get(position).getWho().equals("user")) {
                    isDeleted = mDataBaseBullaUserHelper.removeData(nameBulla);
                }
                else {
                    isDeleted = mDataBaseBullaHelper.removeData(nameBulla);
                }


                if (isDeleted > 0) {
                    BullaListAdapter.this.remove(getItem(position));
                    BullaListAdapter.this.notifyDataSetChanged();
                } else Toast.makeText(getContext(), "Algo deu errado", Toast.LENGTH_LONG).show();

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
            root.put("nameBulla", String.valueOf(nameBulla).toLowerCase());

            return root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
