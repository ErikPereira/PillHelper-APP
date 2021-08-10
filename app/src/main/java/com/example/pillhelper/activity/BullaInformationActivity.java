package com.example.pillhelper.activity;

import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pillhelper.databinding.ActivityBullaInformationBinding;


public class BullaInformationActivity extends AppCompatActivity {
    private ActivityBullaInformationBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBullaInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        String informationBulla = getIntent().getStringExtra("BULLA_INFORMATION");
        String nameBulla = getIntent().getStringExtra("NAME_BULLA");
        binding.infoTextBulla.setText(Html.fromHtml(informationBulla));
        binding.nameBulla.setText(nameBulla);
    }
}
