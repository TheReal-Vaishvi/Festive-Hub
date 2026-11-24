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
    Event grandFestival, musicalNight, foodFestival;
    TextView txtgrandtitle, txtgrandcategory, txtgranddate;
    TextView txtmusictitle, txtmusiccategory, txtmusicdate;
    TextView txtfoodtitle, txtfoodcategory, txtfooddate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        txtgrandtitle = findViewById(R.id.txtgrandtitle);
        txtgrandcategory = findViewById(R.id.txtgrandcategory);
        txtgranddate = findViewById(R.id.txtgranddate);

        txtmusictitle = findViewById(R.id.txtmusictitle);
        txtmusiccategory = findViewById(R.id.txtmusiccategory);
        txtmusicdate = findViewById(R.id.txtmusicdate);

        txtfoodtitle = findViewById(R.id.txtfoodtitle);
        txtfoodcategory = findViewById(R.id.txtfoodcategory);
        txtfooddate = findViewById(R.id.txtfooddate);

        //sync home and event page detail
        grandFestival = new Event(
                "grand_festival",
                R.drawable.grand_festival,
                "Grand Festival Celebration",
                "Music | Food | Celebration",
                "26 December 2026",
                "6:00 PM – 10:00 PM",
                "Rajkot",
                "Celebrate together with music, food, entertainment and exciting activities. " +
                        "Join us for an unforgettable festival experience!");

        musicalNight = new Event(
                "musical_event",
                R.drawable.music_night,
                "Live Musical Night",
                "Music",
                "31 December 2026",
                "8:00 PM – 11:59 PM",
                "Rajkot",
                "Enjoy an unforgettable musical evening with live performances, "
                        + "amazing melodies and an energetic atmosphere. Join us for a night filled with music and entertainment!");

        foodFestival = new Event(
                "food_stall",
                R.drawable.food_stall,
                "Food Festival",
                "Food",
                "27 December 2026",
                "11:00 AM – 10:00 PM",
                "Rajkot",
                "Explore a variety of delicious foods, discover new flavors and enjoy a wonderful food experience. "
                        + "A perfect destination for food lovers!");

        // Grand Festival
        txtgrandtitle.setText(grandFestival.title);
        txtgrandcategory.setText(grandFestival.category);
        txtgranddate.setText(grandFestival.date + " | " + grandFestival.location);

        // Musical Night
        txtmusictitle.setText(musicalNight.title);
        txtmusiccategory.setText(musicalNight.category);
        txtmusicdate.setText(musicalNight.date + " | " + musicalNight.location);

        // Food Festival
        txtfoodtitle.setText(foodFestival.title);
        txtfoodcategory.setText(foodFestival.category);
        txtfooddate.setText(foodFestival.date + " | " + foodFestival.location);

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

        //open card and view details
        //grand festival event
        lnrgrandfestival.setOnClickListener(v -> {
            openEventDetails(grandFestival);
        });

        //musical night event
        lnrmusicalnight.setOnClickListener(v -> {
            openEventDetails(musicalNight);
        });

        //food stall
        lnrfoodfestival.setOnClickListener(v -> {
            openEventDetails(foodFestival);
        });

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

    //class to fetch details
    private void openEventDetails(Event event) {

        Intent intent = new Intent(HomeActivity.this,
                EventDetailActivity.class);

        intent.putExtra("eventId",event.id);
        intent.putExtra("eventTitle", event.title);
        intent.putExtra("eventCategory", event.category);
        intent.putExtra("eventDate", event.date);
        intent.putExtra("eventTime", event.time);
        intent.putExtra("eventLocation", event.location);
        intent.putExtra("eventDescription", event.description);
        intent.putExtra("eventImage", event.image);

        startActivity(intent);
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