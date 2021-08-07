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
import com.example.pillhelper.adapter.BullaListAdapter;
import com.example.pillhelper.dataBaseBulla.DataBaseBullaHelper;
import com.example.pillhelper.databinding.FragmentBullasBinding;
import com.example.pillhelper.item.BullaItem;

import java.util.ArrayList;

public class FragmentBullas extends Fragment {
    private FragmentBullasBinding binding;
    private DataBaseBullaHelper mDataBaseBullaHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentBullasBinding.inflate(getLayoutInflater());

        mDataBaseBullaHelper = new DataBaseBullaHelper(getContext());

        Cursor data = mDataBaseBullaHelper.getData();
        ArrayList<BullaItem> bullas = new ArrayList<>();
        String nameBulla = "";
        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> description = new ArrayList<>();
        ArrayList<String> information = new ArrayList<>();

        while (data.moveToNext()) {
            String newNameBulla = data.getString(0);
            String newTitle = data.getString(1);
            String newDescription = data.getString(2);
            String newInformation = data.getString(3);

            if(!newNameBulla.equals(nameBulla)) {
                title.add(newTitle);
                description.add(newDescription);
                information.add(newInformation);
                if(!nameBulla.equals("")) {
                    nameBulla = newNameBulla;
                    BullaItem bulla = new BullaItem(nameBulla, title, description, information);
                    bullas.add(bulla);
                    title = new ArrayList<>();
                    description = new ArrayList<>();
                    information = new ArrayList<>();
                }
            }
        }

        BullaListAdapter adapter = new BullaListAdapter(getContext(), R.layout.bulla_list_item, bullas);
        binding.bullasListView.setAdapter(adapter);

        return binding.getRoot();
    }
}
