package com.example.pillhelper.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pillhelper.dataBaseUser.DataBaseAlarmsHelper;
import com.example.pillhelper.R;
import com.example.pillhelper.databinding.ActivityRegisterAlarmBinding;

import java.util.Random;

public class RegisterAlarmActivity extends AppCompatActivity {

    private ActivityRegisterAlarmBinding binding;
    private DataBaseAlarmsHelper mDataBaseAlarmsHelper;
    private boolean isEdit;
    private int alarmEditPosition;
    private Cursor data;
    private int validNotificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterAlarmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.register_alarm_title);

        mDataBaseAlarmsHelper = new DataBaseAlarmsHelper(this);
        data = mDataBaseAlarmsHelper.getData();

        isEdit = getIntent().getBooleanExtra("IS_EDIT", false);

        if (isEdit) {
            alarmEditPosition = getIntent().getIntExtra("POSITION", -1);
            data.move(alarmEditPosition + 1);
            binding.nameInfMedicine.setText(data.getString(4));
            validNotificationId = data.getInt(20);
            binding.luminosoCheckbox.setChecked(data.getInt(21) == 1);
            binding.sonoroCheckbox.setChecked(data.getInt(22) == 1);
            binding.infBoxPosition.setText(String.valueOf(data.getInt(23)));

            if (data.getInt(2) == 1) {
                binding.radioButtonMedicineTypePill.setChecked(true);
                binding.radioButtonMedicineTypeLiquid.setChecked(false);
                binding.infDosage.setVisibility(View.GONE);
                binding.infQuantity.setVisibility(View.VISIBLE);
                binding.infBoxQuantityLayout.setVisibility(View.VISIBLE);
                binding.infQuantity.setText(String.valueOf(data.getInt(6)));
                binding.infBoxQuantity.setText(String.valueOf(data.getInt(7)));
            } else if (data.getInt(2) == 2) {
                binding.radioButtonMedicineTypeLiquid.setChecked(true);
                binding.radioButtonMedicineTypePill.setChecked(false);
                binding.infDosage.setVisibility(View.VISIBLE);
                binding.infQuantity.setVisibility(View.GONE);
                binding.infBoxQuantityLayout.setVisibility(View.GONE);
                binding.infDosage.setText(String.valueOf(data.getInt(5)));
            }

            if (data.getInt(1) == 1) {
                binding.radioButtonRegisterMedicineFixtime.setChecked(true);
                binding.radioButtonRegisterMedicineInterval.setChecked(false);
            } else if (data.getInt(1) == 2) {
                binding.radioButtonRegisterMedicineInterval.setChecked(true);
                binding.radioButtonRegisterMedicineFixtime.setChecked(false);
            }
        } else {
            binding.radioButtonMedicineTypePill.setChecked(true);
            validNotificationId = getValidNotificationId();
        }

        binding.backButtonRegisterMedicine.setOnClickListener(v -> finish());

        binding.nextButtonRegisterMedicine.setOnClickListener(v -> {
            Class activity = binding.radioButtonRegisterMedicineFixtime.isChecked() ? FixedAlarmActivity.class
                    : binding.radioButtonRegisterMedicineInterval.isChecked() ? IntervalAlarmActivity.class : null;

            if (activity != null) {
                callActivity(activity);
            }
        });

        binding.infNameInfo.setOnClickListener(v -> imageInfoClick(binding.infNameInfo));
        binding.infBoxQuantityInfo.setOnClickListener(v -> imageInfoClick(binding.infBoxQuantityInfo));
        binding.infQuantityInfo.setOnClickListener(v -> imageInfoClick(binding.infQuantityInfo));
        binding.infBoxPositionInfo.setOnClickListener(v -> imageInfoClick(binding.infBoxPositionInfo));

        binding.radioButtonMedicineTypePill.setOnClickListener(v -> {
            binding.infDosage.setVisibility(View.GONE);
            binding.infQuantity.setVisibility(View.VISIBLE);
            binding.infBoxQuantityLayout.setVisibility(View.VISIBLE);
            binding.infBoxPositionLayout.setVisibility(View.VISIBLE);
            binding.radioButtonMedicineTypeLiquid.setChecked(false);
        });

        binding.radioButtonMedicineTypeLiquid.setOnClickListener(v -> {
            binding.infDosage.setVisibility(View.VISIBLE);
            binding.infQuantity.setVisibility(View.GONE);
            binding.infBoxQuantityLayout.setVisibility(View.GONE);
            binding.infBoxPositionLayout.setVisibility(View.GONE);
            binding.radioButtonMedicineTypePill.setChecked(false);
        });

        binding.radioButtonRegisterMedicineInterval.setOnClickListener(v -> {
            binding.radioButtonRegisterMedicineInterval.setChecked(true);
            binding.radioButtonRegisterMedicineFixtime.setChecked(false);
        });

        binding.radioButtonRegisterMedicineFixtime.setOnClickListener(v -> {
            binding.radioButtonRegisterMedicineFixtime.setChecked(true);
            binding.radioButtonRegisterMedicineInterval.setChecked(false);
        });
    }

    private void callActivity(Class activity) {
        String name = binding.nameInfMedicine.getText().toString();
        boolean isLuminousChecked = binding.luminosoCheckbox.isChecked();
        boolean isSoundChecked = binding.sonoroCheckbox.isChecked();

        if (binding.radioButtonMedicineTypePill.isChecked()) {
            String qtd = binding.infQuantity.getText().toString();
            String qtdBox = binding.infBoxQuantity.getText().toString();
            String posBox = binding.infBoxPosition.getText().toString();

            if (qtd.length() < 10 && qtdBox.length() < 10) {
                if (!name.isEmpty() && !qtd.isEmpty() && !qtdBox.isEmpty() && !posBox.isEmpty()) {
                    Intent intent = new Intent(this, activity);
                    if (isEdit) {
                        intent.putExtra("IS_EDIT", true);
                        intent.putExtra("POSITION", alarmEditPosition);
                        intent.putExtra("MEDICINE_HORA", data.getInt(8));
                        intent.putExtra("MEDICINE_MINUTO", data.getInt(9));
                    }

                    intent.putExtra("MEDICINE_TYPE", 1);
                    intent.putExtra("MEDICINE_QUANTITY", Integer.parseInt(qtd));
                    intent.putExtra("MEDICINE_BOX_QUANTITY", Integer.parseInt(qtdBox));
                    intent.putExtra("MEDICINE_NAME", name);
                    intent.putExtra("NOTIFICATION_ID", validNotificationId);
                    intent.putExtra("LUMINOSO", isLuminousChecked ? 1 : 0);
                    intent.putExtra("SONORO", isSoundChecked ? 1 : 0);
                    intent.putExtra("BOX_POSITION", Integer.parseInt(posBox));
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Número muito grande", Toast.LENGTH_SHORT).show();
            }
        } else if (binding.radioButtonMedicineTypeLiquid.isChecked()) {
            String dosage = binding.infDosage.getText().toString();

            if (dosage.length() < 10) {
                if (!name.isEmpty() && !dosage.isEmpty()) {
                    Intent intent = new Intent(this, activity);
                    if (isEdit) {
                        intent.putExtra("IS_EDIT", true);
                        intent.putExtra("POSITION", alarmEditPosition);
                        intent.putExtra("MEDICINE_HORA", data.getInt(8));
                        intent.putExtra("MEDICINE_MINUTO", data.getInt(9));
                    }

                    intent.putExtra("MEDICINE_TYPE", 2);
                    intent.putExtra("MEDICINE_NAME", name);
                    intent.putExtra("MEDICINE_DOSAGE", Integer.parseInt(dosage));
                    intent.putExtra("NOTIFICATION_ID", validNotificationId);
                    intent.putExtra("LUMINOSO", isLuminousChecked ? 1 : 0);
                    intent.putExtra("SONORO", isSoundChecked ? 1 : 0);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Número muito grande", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void imageInfoClick(ImageView imageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.info_dialog_title_text);

        if (binding.infNameInfo.equals(imageView)) {
            builder.setMessage(R.string.dialog_text_medicine_name_info);
        } else if (binding.infQuantityInfo.equals(imageView)) {
            if (binding.radioButtonMedicineTypePill.isChecked()) {
                builder.setMessage(R.string.dialog_text_medicine_dosage_info);
            } else {
                builder.setMessage(R.string.dialog_text_medicine_quantity_info);
            }
        } else if (binding.infBoxQuantityInfo.equals(imageView)) {
            builder.setMessage(R.string.dialog_text_medicine_box_quantity_info);
        } else if (binding.infBoxPositionInfo.equals(imageView)) {
            builder.setMessage(R.string.dialog_text_medicine_box_position_info);
        }

        builder.setPositiveButton(R.string.ok, (dialog, id) -> dialog.dismiss());

        builder.create().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private int getValidNotificationId() {
        Cursor data = mDataBaseAlarmsHelper.getData();

        Random rand = new Random();
        boolean isInvalid = true;
        int randomNumber = 0;

        while (isInvalid) {
            randomNumber = rand.nextInt(1000);

            if (data.moveToFirst()) {
                do {
                    if (randomNumber == data.getInt(20)) {
                        isInvalid = true;
                    }

                    if (data.isLast() && isInvalid)
                        isInvalid = false;
                } while (data.moveToNext());
            } else
                isInvalid = false;
        }

        return randomNumber;
    }
}
