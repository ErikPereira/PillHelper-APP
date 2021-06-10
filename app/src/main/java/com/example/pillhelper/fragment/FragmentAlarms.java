package com.example.pillhelper.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pillhelper.adapter.AlarmListAdapter;
import com.example.pillhelper.R;
import com.example.pillhelper.activity.AllMedicinesActivity;
import com.example.pillhelper.activity.RegisterAlarmActivity;
import com.example.pillhelper.dataBaseUser.DataBaseAlarmsHelper;
import com.example.pillhelper.databinding.FragmentAlarmsBinding;
import com.example.pillhelper.item.AlarmItem;

import java.util.ArrayList;

public class FragmentAlarms extends Fragment {

    private static final String TAG = "FragmentAlarms";
    private FragmentAlarmsBinding binding;
    private DataBaseAlarmsHelper mDataBaseAlarmsHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        binding = FragmentAlarmsBinding.inflate(getLayoutInflater());
        mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(getActivity());

        Cursor data = mDataBaseAlarmsHelper.getData();
        ArrayList<AlarmItem> alarmes = new ArrayList<>();

        while (data.moveToNext()) {
            String uuidAlarm = data.getString(0);
            int status = data.getInt(3);
            String name = data.getString(4);
            int hours = data.getInt(8);
            int min = data.getInt(9);
            int notificationId = data.getInt(20);

            AlarmItem alarm;
            alarm = new AlarmItem(uuidAlarm, status, name, hours, min, notificationId);
            alarmes.add(alarm);
        }

        AlarmListAdapter adapter = new AlarmListAdapter(getContext(), R.layout.alarms_list_item, alarmes);
        binding.alarmesListView.setAdapter(adapter);

        binding.alarmesListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getContext(), RegisterAlarmActivity.class);
            intent.putExtra("IS_EDIT", true);
            intent.putExtra("POSITION", position);
            startActivity(intent);
        });

        binding.fabFragment.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AllMedicinesActivity.class);
            ArrayList<String> listaNomeMedicamentos = new ArrayList<>();

            for (AlarmItem alarm : alarmes) {
                listaNomeMedicamentos.add(alarm.getName());
            }

            intent.putStringArrayListExtra("MEDICINE_NAME_LIST", listaNomeMedicamentos);
            startActivity(intent);
        });

        return binding.getRoot();
    }
}
