package com.example.pillhelper.activity;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.pillhelper.R;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pillhelper.databinding.ActivityBullaInformationBinding;
import com.miguelcatalan.materialsearchview.MaterialSearchView;


public class BullaInformationActivity extends AppCompatActivity {
    private ActivityBullaInformationBinding binding;
    MaterialSearchView searchView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBullaInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        /*androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Bula");
        
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));*/

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        ScrollView textViewWrapper = (ScrollView) findViewById(R.id.textViewWrapper);

        String informationBulla = getIntent().getStringExtra("BULLA_INFORMATION");
        String nameBulla = getIntent().getStringExtra("NAME_BULLA");
        binding.infoTextBulla.setText(Html.fromHtml(informationBulla));
        binding.nameBulla.setText(nameBulla);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                binding.infoTextBulla.setText(Html.fromHtml(informationBulla));
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String criteria = newText;
                String fullText = informationBulla;
                if (fullText.contains(criteria) && !criteria.equals("") && !criteria.equals("p")) {
                    int indexOfCriteria = fullText.indexOf(criteria);
                    int lineNumber = binding.infoTextBulla.getLayout().getLineForOffset(indexOfCriteria) - 15;
                    lineNumber = lineNumber < 0 ? 0 : lineNumber;
                    String highlighted = "<font color='red'>"+criteria+"</font>";
                    fullText = fullText.replace(criteria, highlighted);
                    binding.infoTextBulla.setText(Html.fromHtml(fullText));

                    textViewWrapper.scrollTo(0, binding.infoTextBulla.getLayout().getLineTop(lineNumber));
                }
                else if (criteria.equals("")){
                    binding.infoTextBulla.setText(Html.fromHtml(informationBulla));
                }
                return true;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_search_menu, menu);
        MenuItem Item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(Item);
        return true;
    }
}
