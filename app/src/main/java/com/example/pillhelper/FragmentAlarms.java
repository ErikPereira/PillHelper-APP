package com.example.pillhelper;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pillhelper.databinding.FragmentAlarmesBinding;

import java.util.ArrayList;

public class FragmentAlarms extends Fragment {

    private static final String TAG = "FragmentAlarms";
    private FragmentAlarmesBinding binding;
    private DataBaseAlarmsHelper mDataBaseAlarmsHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentAlarmesBinding.inflate(getLayoutInflater());
        mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(getActivity());

        Cursor data = mDataBaseAlarmsHelper.getData();
        ArrayList<AlarmeItem> alarmes = new ArrayList<>();

        while (data.moveToNext()) {
            String uuidAlarm = data.getString(0);
            int status = data.getInt(3);
            String nome = data.getString(4);
            int horas = data.getInt(8);
            int minutos = data.getInt(9);
            int notificationId = data.getInt(20);

            AlarmeItem alarme;
            alarme = new AlarmeItem(uuidAlarm, status, nome, horas, minutos, notificationId);
            alarmes.add(alarme);
        }

        AlarmeListAdapter adapter = new AlarmeListAdapter(getContext(), R.layout.alarmes_list_item, alarmes);
        binding.alarmesListView.setAdapter(adapter);

        binding.alarmesListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getContext(), CadastrarAlarmeActivity.class);
            intent.putExtra("IS_EDIT", true);
            intent.putExtra("POSITION", position);
            startActivity(intent);
        });

        binding.fabFragment.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AllMedicinesActivity.class);
            ArrayList<String> listaNomeMedicamentos = new ArrayList<>();

            for (AlarmeItem alarme : alarmes) {
                listaNomeMedicamentos.add(alarme.getNome());
            }

            intent.putStringArrayListExtra("MEDICINE_NAME_LIST", listaNomeMedicamentos);
            startActivity(intent);
        });

        return binding.getRoot();
    }
}
