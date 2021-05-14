package com.example.pillhelper;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pillhelper.databinding.FragmentCaixasBinding;

import java.util.ArrayList;

public class FragmentCaixas extends Fragment {

    private FragmentCaixasBinding binding;
    private DataBaseBoxHelper mDataBaseBoxHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentCaixasBinding.inflate(getLayoutInflater());

        mDataBaseBoxHelper = new DataBaseBoxHelper(getContext());

        Cursor data = mDataBaseBoxHelper.getData();
        ArrayList<CaixaItem> caixas = new ArrayList<>();

        while (data.moveToNext()) {
            String idCaixa = data.getString(1);
            String nome = data.getString(2);

            CaixaItem caixa = new CaixaItem(idCaixa, nome);
            caixas.add(caixa);
        }

        CaixaListAdapter adapter = new CaixaListAdapter(getContext(), R.layout.caixas_list_item, caixas);
        binding.caixasListView.setAdapter(adapter);

        return binding.getRoot();
    }
}
