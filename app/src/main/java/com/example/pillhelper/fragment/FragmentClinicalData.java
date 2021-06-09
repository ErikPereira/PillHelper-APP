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
import com.example.pillhelper.adapter.ClinicalDataListAdapter;
import com.example.pillhelper.dataBase.DataBaseClinicalDataHelper;
import com.example.pillhelper.databinding.FragmentClinicalDataBinding;
import com.example.pillhelper.item.ClinicalDataItem;

import java.util.ArrayList;

public class FragmentClinicalData extends Fragment {

    private FragmentClinicalDataBinding binding;
    private DataBaseClinicalDataHelper mDataBaseClinicalDataHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentClinicalDataBinding.inflate(getLayoutInflater());

        mDataBaseClinicalDataHelper = new DataBaseClinicalDataHelper(getContext());

        Cursor data = mDataBaseClinicalDataHelper.getData();
        ArrayList<ClinicalDataItem> clinicalData = new ArrayList<>();

        while (data.moveToNext()) {
            String name = data.getString(0);
            String value = data.getString(1);

            ClinicalDataItem cd = new ClinicalDataItem(name, value);
            clinicalData.add(cd);
        }

        ClinicalDataListAdapter adapter = new ClinicalDataListAdapter(getContext(), R.layout.clinical_data_list_item, clinicalData);
        binding.clinicalDataListView.setAdapter(adapter);

        return binding.getRoot();
    }
}
