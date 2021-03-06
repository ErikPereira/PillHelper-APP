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
import com.example.pillhelper.adapter.BoundSupervisorListAdapter;
import com.example.pillhelper.dataBaseSupervisor.DataBaseBoundUserHelper;
import com.example.pillhelper.dataBaseUser.DataBaseBoundSupervisorHelper;
import com.example.pillhelper.databinding.FragmentSupervisorsBinding;
import com.example.pillhelper.item.BoundItem;

import java.util.ArrayList;

public class FragmentBoundSupervisors extends Fragment {
    private FragmentSupervisorsBinding binding;
    private DataBaseBoundSupervisorHelper mDataBaseBoundSupervisorHelper;
    private DataBaseBoundUserHelper mDataBaseBoundUserHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSupervisorsBinding.inflate(getLayoutInflater());

        mDataBaseBoundSupervisorHelper = new DataBaseBoundSupervisorHelper(getContext());

        Cursor data = mDataBaseBoundSupervisorHelper.getData();
        ArrayList<BoundItem> supervisors = new ArrayList<>();

        while (data.moveToNext()) {
            String uuidSupervisor = data.getString(0);
            String registeredBy = data.getString(1);
            String bond = data.getString(2);
            String name = data.getString(3);

            BoundItem supervisor = new BoundItem(uuidSupervisor, registeredBy, bond, name);
            supervisors.add(supervisor);
        }

        BoundSupervisorListAdapter adapter = new BoundSupervisorListAdapter(getContext(), R.layout.supervisors_list_item, supervisors);
        binding.supervisorListView.setAdapter(adapter);

        return binding.getRoot();
    }
}
