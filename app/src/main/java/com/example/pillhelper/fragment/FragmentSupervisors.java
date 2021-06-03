package com.example.pillhelper.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pillhelper.R;
import com.example.pillhelper.adapter.SupervisorListAdapter;
import com.example.pillhelper.dataBase.DataBaseSupervisorHelper;
import com.example.pillhelper.databinding.FragmentSupervisorsBinding;
import com.example.pillhelper.item.SupervisorItem;

import java.util.ArrayList;

public class FragmentSupervisors extends Fragment {
    private FragmentSupervisorsBinding binding;
    private DataBaseSupervisorHelper mDataBaseSupervisorHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentSupervisorsBinding.inflate(getLayoutInflater());

        mDataBaseSupervisorHelper = new DataBaseSupervisorHelper(getContext());

        Cursor data = mDataBaseSupervisorHelper.getData();
        ArrayList<SupervisorItem> supervisors = new ArrayList<>();

        while (data.moveToNext()) {
            String uuidSupervisor = data.getString(0);
            String registeredBy = data.getString(1);
            String bond = data.getString(2);
            String name = data.getString(3);

            SupervisorItem supervisor = new SupervisorItem(uuidSupervisor, registeredBy, bond, name);
            supervisors.add(supervisor);
        }

        SupervisorListAdapter adapter = new SupervisorListAdapter(getContext(), R.layout.supervisors_list_item, supervisors);
        binding.supervisorListView.setAdapter(adapter);

        return binding.getRoot();
    }
}
