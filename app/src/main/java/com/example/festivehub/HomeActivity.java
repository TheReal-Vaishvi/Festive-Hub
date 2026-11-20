package com.example.festivehub;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class HomeActivity extends AppCompatActivity {
    TextView txtall, txtmusic, txtfood, txtcelebration;
    View lnrgrandfestival, lnrmusicalnight, lnrfoodfestival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtall = findViewById(R.id.txtall);
        txtmusic = findViewById(R.id.txtmusic);
        txtfood = findViewById(R.id.txtfood);
        txtcelebration = findViewById(R.id.txtcelebration);

        lnrgrandfestival = findViewById(R.id.lnrgrandfestival);
        lnrmusicalnight= findViewById(R.id.lnrmusicalnight);
        lnrfoodfestival = findViewById(R.id.lnrfoodfestival);

        TextView[] categories = {txtall,txtmusic,txtfood,txtcelebration};

        for (TextView category : categories){
            category.setOnClickListener(v ->{
                for (TextView cat : categories){
                    cat.setBackgroundResource(R.drawable.unselected_category);
                    cat.setTextColor(getColor(R.color.purple));
                }
                category.setBackgroundResource(R.drawable.selected_category);
                category.setTextColor(getColor(R.color.white));

                filterEvents(category);
            });
        }

    }

    private void filterEvents(TextView category) {
        String selected = category.getText().toString();

        //if all is selected
        if (selected.equals("All")){
            lnrgrandfestival.setVisibility(View.VISIBLE);
            lnrmusicalnight.setVisibility(View.VISIBLE);
            lnrfoodfestival.setVisibility(View.VISIBLE);
        }
        //if music is selected
        else if (selected.equals("Music")) {
            lnrgrandfestival.setVisibility(View.GONE);
            lnrmusicalnight.setVisibility(View.VISIBLE);
            lnrfoodfestival.setVisibility(View.GONE);
        }
        //if food is selected
        else if (selected.equals("Food")) {
            lnrgrandfestival.setVisibility(View.GONE);
            lnrmusicalnight.setVisibility(View.GONE);
            lnrfoodfestival.setVisibility(View.VISIBLE);
        }
        //if celebration is selected
        else if (selected.equals("Celebration")) {
            lnrgrandfestival.setVisibility(View.VISIBLE);
            lnrmusicalnight.setVisibility(View.GONE);
            lnrfoodfestival.setVisibility(View.GONE);
        }
    }
}