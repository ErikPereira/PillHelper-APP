package com.example.pillhelper.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.pillhelper.R;

import java.util.ArrayList;

public class AlarmListedAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int mResource;

    public AlarmListedAdapter(@NonNull Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String item = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView nameTextView = convertView.findViewById(R.id.adapter_text);

        nameTextView.setText(item);

        return convertView;
    }
}
