package com.example.pillhelper.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pillhelper.fragment.FragmentAlarms;
import com.example.pillhelper.fragment.FragmentBoxes;
import com.example.pillhelper.R;
import com.example.pillhelper.databinding.ActivityFragmentsBinding;
import com.example.pillhelper.fragment.FragmentClinicalData;
import com.example.pillhelper.fragment.FragmentSupervisors;

import static com.example.pillhelper.utils.Constants.OPEN_BOX_FRAG;

public class FragmentsActivity extends AppCompatActivity {

    private ActivityFragmentsBinding binding;
    private static final String TAG = "AlarmeActivity";
    private Fragment actualFragment;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFragmentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_alarmes:
                    binding.fabFragment.setImageDrawable(getBaseContext().getDrawable(R.drawable.ic_alarm_add_white_24dp));
                    actualFragment = new FragmentAlarms();
                    loadFragment(actualFragment);
                    break;
                case R.id.nav_caixas:
                    binding.fabFragment.setImageDrawable(getBaseContext().getDrawable(R.drawable.ic_add_box_white_24dp));
                    actualFragment = new FragmentBoxes();
                    loadFragment(actualFragment);
                    break;
                case R.id.nav_supervisor:
                    binding.fabFragment.setImageDrawable(getBaseContext().getDrawable(R.drawable.ic_add_supervisor_white_24dp));
                    actualFragment = new FragmentSupervisors();
                    loadFragment(actualFragment);
                    break;
                case R.id.nav_dados_clinicos:
                    binding.fabFragment.setImageDrawable(getBaseContext().getDrawable(R.drawable.ic_add_dadosclinicos));
                    actualFragment = new FragmentClinicalData();
                    loadFragment(actualFragment);
                    break;
            }

            return true;
        });

        binding.fabFragment.setOnClickListener(v -> {
            if (actualFragment instanceof FragmentAlarms) {
                Intent intent = new Intent(this, RegisterAlarmActivity.class);
                startActivity(intent);
            }

            if (actualFragment instanceof FragmentSupervisors) {
                Intent intent = new Intent(this, RegisterSupervisorActivity.class);
                startActivity(intent);
            }

            if (actualFragment instanceof FragmentClinicalData) {
                Intent intent = new Intent(this, RegisterClinicalDataActivity.class);
                startActivity(intent);
            }

            if (actualFragment instanceof FragmentBoxes) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    Intent intent = new Intent(this, RegisterBoxActivity.class);
                    startActivityForResult(intent, 1);
                }
            }

        });
        // aki pode ocorrer algum problema
        binding.bottomNavigation.setSelectedItemId(getIntent().getBooleanExtra(OPEN_BOX_FRAG, false) ? R.id.nav_caixas : R.id.nav_alarmes);
        binding.bottomNavigation.performClick();
    }

    public void loadFragment(Fragment fragment) {
        if (fragment instanceof FragmentAlarms)
            getSupportActionBar().setTitle(R.string.menu_alarme);

        else if (fragment instanceof FragmentSupervisors)
            getSupportActionBar().setTitle(R.string.menu_supervisor);

        else if (fragment instanceof FragmentClinicalData)
            getSupportActionBar().setTitle(R.string.menu_dados_clinicos);

        else getSupportActionBar().setTitle(R.string.menu_caixas);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    public void reloadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.detach(fragment);
        transaction.attach(fragment);
        transaction.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra("DB_RESULT", false)) {
                    reloadFragment(actualFragment);
                }
            }
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * this method request to permission asked.
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
        } else {
            Log.i(TAG, "Requesting permission");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(this, RegisterBoxActivity.class);
                startActivityForResult(intent, 1);
            }
        }
    }
}
