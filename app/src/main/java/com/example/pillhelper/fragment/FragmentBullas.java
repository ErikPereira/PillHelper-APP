package com.example.pillhelper.fragment;

import static com.example.pillhelper.utils.Constants.WHO_USER_FRAG;

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
import com.example.pillhelper.dataBaseBulla.DataBaseBullaUserHelper;
import com.example.pillhelper.databinding.FragmentBullasBinding;
import com.example.pillhelper.item.BullaItem;

import java.util.ArrayList;

public class FragmentBullas extends Fragment {
    private FragmentBullasBinding binding;
    private DataBaseBullaHelper mDataBaseBullaHelper;
    private DataBaseBullaUserHelper mDataBaseBullaUserHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String whoUserFrag = getActivity().getIntent().getStringExtra(WHO_USER_FRAG);
        binding = FragmentBullasBinding.inflate(getLayoutInflater());

        mDataBaseBullaHelper = new DataBaseBullaHelper(getContext());
        mDataBaseBullaUserHelper = new DataBaseBullaUserHelper(getContext());
        Cursor data;
        String who;

        if (whoUserFrag.equals("user")) {
            data = mDataBaseBullaUserHelper.getData();
            who = "user";
        }
        else {
            data = mDataBaseBullaHelper.getData();
            who = "supervisor";
        }


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

            if(!newNameBulla.equals(nameBulla) && !nameBulla.equals("")) {
                BullaItem bulla = new BullaItem(nameBulla, title, description, information, who);
                bullas.add(bulla);
                title = new ArrayList<>();
                description = new ArrayList<>();
                information = new ArrayList<>();
            }
            nameBulla = newNameBulla;
            title.add(newTitle);
            description.add(newDescription);
            information.add(newInformation);

        }
        BullaItem bulla = new BullaItem(nameBulla, title, description, information, who);
        bullas.add(bulla);

        if(bullas.get(0).getNameBulla().equals("")){
            bullas = new ArrayList<>();
        }

        BullaListAdapter adapter = new BullaListAdapter(getContext(), R.layout.bulla_list_item, bullas);
        binding.bullasListView.setAdapter(adapter);

        return binding.getRoot();
    }
}
