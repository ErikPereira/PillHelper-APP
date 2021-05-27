package com.example.pillhelper.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.example.pillhelper.activity.MainActivity;
import com.example.pillhelper.databinding.ActivityAlarmActiveBinding;

import java.util.Calendar;

public class AlarmActiveActivity extends AppCompatActivity {

    ActivityAlarmActiveBinding binding;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlarmActiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setTitle("Alarme");

        Calendar rightNow = Calendar.getInstance();
        int intCurrentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        int intCurrentMinute = rightNow.get(Calendar.MINUTE);
        String strCurrentHour = "";
        String strCurrentMinute = "";

        if (intCurrentHour < 10) strCurrentHour = "0" + intCurrentHour;
        else strCurrentHour = String.valueOf(intCurrentHour);

        if (intCurrentMinute < 10) strCurrentMinute = "0" + intCurrentMinute;
        else strCurrentMinute = String.valueOf(intCurrentMinute);

        String time = strCurrentHour + ":" + strCurrentMinute;
        binding.scheduleAlarm.setText(time);

        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();

        binding.buttonAlarmConfirm.setOnClickListener(v -> {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.cancel(getIntent().getIntExtra("NOTIFICATION_ID", 0));
            mediaPlayer.stop();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}