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
import com.example.pillhelper.adapter.BoundUserListAdapter;
import com.example.pillhelper.dataBaseSupervisor.DataBaseBoundUserHelper;
import com.example.pillhelper.databinding.FragmentSupervisorsBinding;
import com.example.pillhelper.item.BoundItem;

import java.util.ArrayList;

public class FragmentBoundUsers extends Fragment {
    private FragmentSupervisorsBinding binding;
    private DataBaseBoundUserHelper mDataBaseBoundUserHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSupervisorsBinding.inflate(getLayoutInflater());

        mDataBaseBoundUserHelper = new DataBaseBoundUserHelper(getContext());

        Cursor data = mDataBaseBoundUserHelper.getData();
        ArrayList<BoundItem> user = new ArrayList<>();

        while (data.moveToNext()) {
            String uuidUser = data.getString(0);
            String registeredBy = data.getString(1);
            String bond = data.getString(2);
            String name = data.getString(3);

            BoundItem u = new BoundItem(uuidUser, registeredBy, bond, name);
            user.add(u);
        }

        BoundUserListAdapter adapter = new BoundUserListAdapter(getContext(), R.layout.supervisors_list_item, user);
        binding.supervisorListView.setAdapter(adapter);

        return binding.getRoot();
    }
}
