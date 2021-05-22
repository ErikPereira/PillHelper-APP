package com.example.pillhelper;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class TimePickerActivity extends Activity implements TimePicker.OnTimeChangedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_fix);

        TimePicker picker = findViewById(R.id.id_clock_schedule);

        picker.setOnTimeChangedListener(this);
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        Calendar then = Calendar.getInstance();

        then.set(Calendar.HOUR_OF_DAY, hourOfDay);
        then.set(Calendar.MINUTE, minute);
        then.set(Calendar.SECOND, 0);

        Toast.makeText(this, then.getTime().toString(), Toast.LENGTH_SHORT).show();
    }
}