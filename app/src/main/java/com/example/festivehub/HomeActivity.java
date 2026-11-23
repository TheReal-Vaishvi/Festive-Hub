package com.example.festivehub;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    TextView txtall, txtmusic, txtfood, txtcelebration ,txtProfile;
    View lnrgrandfestival, lnrmusicalnight, lnrfoodfestival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

       txtProfile = findViewById(R.id.txtprofile);
       txtProfile.setOnClickListener(v->{
           Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
           startActivity(intent);
       });

        txtall = findViewById(R.id.txtall);
        txtmusic = findViewById(R.id.txtmusic);
        txtfood = findViewById(R.id.txtfood);
        txtcelebration = findViewById(R.id.txtcelebration);

        lnrgrandfestival = findViewById(R.id.lnrgrandfestival);
        lnrmusicalnight = findViewById(R.id.lnrmusicalnight);
        lnrfoodfestival = findViewById(R.id.lnrfoodfestival);

        //To display banner(carousel code)
        ViewPager2 viewPagerBanners = findViewById(R.id.viewPager);

        ArrayList<EventBanner> bannerList = new ArrayList<>();

        bannerList.add(new EventBanner(R.drawable.grand_festival, "Grand Festival Celebration", "Music | Food | Celebration"));

        bannerList.add(new EventBanner(R.drawable.music_night, "Live Musical Night", "Enjoy live music and performances"));

        bannerList.add(new EventBanner(R.drawable.food_stall, "Food Festival", "Discover delicious food"));

        EventBannerAdapter bannerAdapter = new EventBannerAdapter(bannerList);
        viewPagerBanners.setAdapter(bannerAdapter);

        // For indicating the dots after carousel

        LinearLayout bannerdots = findViewById(R.id.lnrdots);

        TextView[] dots = new TextView[bannerList.size()];

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(this);
            dots[i].setText("●");
            dots[i].setTextSize(10);
            if (i == 0) {
                dots[i].setTextColor(Color.parseColor("#6D3BA5"));
            } else {
                dots[i].setTextColor(Color.parseColor("#D6C9E5"));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(5, 0, 5, 0);
            bannerdots.addView(dots[i], params);
        }
        //banner swipe dots color change
        viewPagerBanners.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        if (position < 0 || position >= dots.length) {
                            return;
                        }
                        for (int i = 0; i < dots.length; i++) {
                            if (i == position) {
                                dots[i].setTextColor(Color.parseColor("#6D3BA5"));
                            } else {
                                dots[i].setTextColor(Color.parseColor("#D6C9E5"));
                            }
                        }
                    }
                }
        );
        //selected and unselected category color
        TextView[] categories = {txtall, txtmusic, txtfood, txtcelebration};

        for (TextView category : categories) {
            category.setOnClickListener(v -> {
                for (TextView cat : categories) {
                    cat.setBackgroundResource(R.drawable.unselected_category);
                    cat.setTextColor(getColor(R.color.purple));
                }
                category.setBackgroundResource(R.drawable.selected_category);
                category.setTextColor(getColor(R.color.white));

                filterEvents(category);
            });
        }

    }

    //For filter the category
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