
package com.example.festivehub;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    TextView txtProfile;
    RecyclerView recyclerviewhomeevents;
    FirebaseFirestore db;
    ArrayList<Event> eventList;
    ArrayList<Event> filteredEventList;
    HomeEventsAdapter homeEventsAdapter;
    ViewPager2 viewPagerBanners;
    LinearLayout categoryContainer;
    LinearLayout bannerdots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize RecyclerView
        recyclerviewhomeevents = findViewById(R.id.recyclerviewhomeevents);
        recyclerviewhomeevents.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();
        filteredEventList = new ArrayList<>();

        homeEventsAdapter = new HomeEventsAdapter(this, filteredEventList);
        recyclerviewhomeevents.setAdapter(homeEventsAdapter);

        // Profile navigation
        txtProfile = findViewById(R.id.txtprofile);
        txtProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Initialize category container
        categoryContainer = findViewById(R.id.categoryContainer);

        // Initialize banner views
        viewPagerBanners = findViewById(R.id.viewPager);
        bannerdots = findViewById(R.id.lnrdots);
        // Load events from Firestore
        loadEvents();
    }

    // Fetch events from Firestore
    private void loadEvents() {

        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    ArrayList<EventBanner> bannerList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();

                        String title = document.getString("title");
                        String category = document.getString("category");
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String location = document.getString("location");
                        String description = document.getString("description");

                        int image = 0;
                        if (id.equals("grand_festival")) {
                            image = R.drawable.grand_festival;
                        } else if (id.equals("musical_event")) {
                            image = R.drawable.music_night;
                        } else if (id.equals("food_stall")) {
                            image = R.drawable.food_stall;
                        }
                        Event event = new Event(
                                id,
                                image,
                                title,
                                category,
                                date,
                                time,
                                location,
                                description
                        );
                        eventList.add(event);
                        // Add event to carousel
                        bannerList.add(new EventBanner(
                                image,
                                title,
                                category
                        ));
                    }
                    // Setup dynamic categories
                    setupCategories();
                    // Setup dynamic carousel
                    setupCarousel(bannerList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // Setup dynamic carousel and dots
    private void setupCarousel(ArrayList<EventBanner> bannerList) {
        EventBannerAdapter bannerAdapter = new EventBannerAdapter(bannerList);
        viewPagerBanners.setAdapter(bannerAdapter);
        // Clear previous dots
        bannerdots.removeAllViews();
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

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            bannerdots.addView(dots[i], params);
        }

        // Change dot color when swiped
        viewPagerBanners.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
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
    }

    // Create categories dynamically from Firestore
    private void setupCategories() {
        categoryContainer.removeAllViews();
        ArrayList<String> categories = new ArrayList<>();
        // Add All category first
        categories.add("All");
        // Get unique categories from events
        for (Event event : eventList) {
            if (event.category != null && !event.category.trim().isEmpty()) {
                String[] eventCategories = event.category.split("\\|");
                for (String category : eventCategories) {
                    String cleanCategory = category.trim();
                    if (!cleanCategory.isEmpty() &&
                            !categories.contains(cleanCategory)) {
                        categories.add(cleanCategory);
                    }
                }
            }
        }

        // Create category buttons
        for (int i = 0; i < categories.size(); i++) {
            String categoryName = categories.get(i);
            TextView categoryButton = new TextView(this);

            categoryButton.setText(categoryName);
            categoryButton.setTextSize(14);
            categoryButton.setTypeface(null, android.graphics.Typeface.NORMAL);
            categoryButton.setGravity(android.view.Gravity.CENTER);
            categoryButton.setPadding(dp(16), 0, dp(16), 0);
            categoryButton.setIncludeFontPadding(true);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            dp(42)
                    );
            if (i > 0) {params.setMargins(10, 0, 0, 0);}
            categoryButton.setLayoutParams(params);
            if (i == 0) {
                categoryButton.setBackgroundResource(R.drawable.selected_category);
                categoryButton.setTextColor(Color.WHITE);
            } else {
                categoryButton.setBackgroundResource(R.drawable.unselected_category);
                categoryButton.setTextColor(Color.parseColor("#8743BE"));
            }
            categoryButton.setOnClickListener(v -> {
                // Reset all category buttons
                for (int j = 0; j < categoryContainer.getChildCount(); j++) {

                    TextView button = (TextView) categoryContainer.getChildAt(j);
                    button.setBackgroundResource(R.drawable.unselected_category);
                    button.setTextColor(Color.parseColor("#8743BE"));
                }
                // Highlight selected category
                categoryButton.setBackgroundResource(R.drawable.selected_category);
                categoryButton.setTextColor(Color.WHITE);
                filterEvents(categoryName);
            });
            categoryContainer.addView(categoryButton);
        }

        // Display all events initially
        filterEvents("All");
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    // Filter events by selected category
    private void filterEvents(String selectedCategory) {
        filteredEventList.clear();
        for (Event event : eventList) {
            if (selectedCategory.equalsIgnoreCase("All")) {
                filteredEventList.add(event);
            } else if (event.category != null) {
                String[] eventCategories = event.category.split("\\|");
                for (String category : eventCategories) {
                    if (category.trim().equalsIgnoreCase(selectedCategory)) {
                        filteredEventList.add(event);
                        break;
                    }
                }
            }
        }
        homeEventsAdapter.notifyDataSetChanged();
    }
}
