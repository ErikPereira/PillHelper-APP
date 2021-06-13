package com.example.pillhelper.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pillhelper.adapter.BoxListAdapter;
import com.example.pillhelper.R;
import com.example.pillhelper.dataBaseUser.DataBaseBoxHelper;
import com.example.pillhelper.databinding.FragmentBoxesBinding;
import com.example.pillhelper.item.BoxItem;

import java.util.ArrayList;

public class FragmentBoxes extends Fragment {

    private FragmentBoxesBinding binding;
    private DataBaseBoxHelper mDataBaseBoxHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        binding = FragmentBoxesBinding.inflate(getLayoutInflater());

        mDataBaseBoxHelper = new DataBaseBoxHelper(getContext());

        Cursor data = mDataBaseBoxHelper.getData();
        ArrayList<BoxItem> caixas = new ArrayList<>();

        while (data.moveToNext()) {
            String uuidBox = data.getString(0);
            String name = data.getString(1);

            BoxItem box = new BoxItem(uuidBox, name);
            caixas.add(box);
        }

        BoxListAdapter adapter = new BoxListAdapter(getContext(), R.layout.boxs_list_item, caixas);
        binding.caixasListView.setAdapter(adapter);

        return binding.getRoot();
    }
}
