package com.example.pillhelper.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pillhelper.adapter.AlarmListedAdapter;
import com.example.pillhelper.R;
import com.example.pillhelper.databinding.ActivityAllMedicinesBinding;

import java.util.ArrayList;

public class AllMedicinesActivity extends AppCompatActivity {
    private ActivityAllMedicinesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllMedicinesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ArrayList<String> allMedicinesNames = getIntent().getStringArrayListExtra("MEDICINE_NAME_LIST");

        AlarmListedAdapter arrayAdapter = new AlarmListedAdapter(this, R.layout.list_item, allMedicinesNames);
        binding.allMedListView.setAdapter(arrayAdapter);
    }
}
