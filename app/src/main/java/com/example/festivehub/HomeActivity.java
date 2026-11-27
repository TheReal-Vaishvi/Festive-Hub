package com.example.festivehub;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    TextView txtProfile;
    RecyclerView recyclerviewhomeevents;
    FirebaseFirestore db;
    ArrayList<Event> eventList;
    ArrayList<Event> filteredEventList;
    AppCompatButton btnmyassignedevents;
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
        // Initialize Volunteer Assigned Events Button
        btnmyassignedevents = findViewById(R.id.btnmyassignedevents);
        btnmyassignedevents.setVisibility(View.GONE);

        // Check whether the logged-in user is a volunteer
        if (FirebaseAuth.getInstance().getCurrentUser() != null
                && FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {

            String email = FirebaseAuth.getInstance()
                    .getCurrentUser()
                    .getEmail();

            FirebaseFirestore.getInstance()
                    .collection("Volunteers")
                    .whereEqualTo("email", email)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            btnmyassignedevents.setVisibility(View.VISIBLE);
                        }
                    });
        }
        // Open Volunteer Assigned Events
        btnmyassignedevents.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, VolunteerAssignedEventsActivity.class);
            startActivity(intent);
        });
        // Initialize RecyclerView
        recyclerviewhomeevents = findViewById(R.id.recyclerviewhomeevents);

        recyclerviewhomeevents.setLayoutManager(
                new LinearLayoutManager(this)
        );
        eventList = new ArrayList<>();
        filteredEventList = new ArrayList<>();

        homeEventsAdapter = new HomeEventsAdapter(this, filteredEventList);
        recyclerviewhomeevents.setAdapter(homeEventsAdapter);
        // Profile Navigation
        txtProfile = findViewById(R.id.txtprofile);

        txtProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        // Initialize Category Container
        categoryContainer = findViewById(R.id.categoryContainer);
        // Initialize Banner Views
        viewPagerBanners = findViewById(R.id.viewPager);

        bannerdots = findViewById(R.id.lnrdots);
        // Load Events
        loadEvents();
    }
    // Fetch Events from Firestore
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
                        // Get Local Event Image
                        int image = 0;

                        if (id.equals("grand_festival")) {
                            image = R.drawable.grand_festival;
                        } else if (id.equals("musical_event")) {
                            image = R.drawable.music_night;
                        } else if (id.equals("food_stall")) {
                            image = R.drawable.food_stall;
                        }
                        // Create Event Object
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
                        // Get Image URL from Firestore
                        event.imageUrl = document.getString("imageUrl");
                        eventList.add(event);
                        // Add Event to Carousel
                        String imageUrl = document.getString("imageUrl");

                        bannerList.add(new EventBanner(
                                image,
                                imageUrl,
                                title,
                                category
                        ));
                    }
                    // Setup Dynamic Categories
                    setupCategories();
                    // Setup Dynamic Carousel
                    setupCarousel(bannerList);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(HomeActivity.this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    // Carousel and Dots
    private void setupCarousel(ArrayList<EventBanner> bannerList) {
        EventBannerAdapter bannerAdapter = new EventBannerAdapter(bannerList);
        viewPagerBanners.setAdapter(bannerAdapter);
        // Clear Previous Dots
        bannerdots.removeAllViews();
        TextView[] dots = new TextView[bannerList.size()];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("●");
            dots[i].setTextSize(10);
            if (i == 0) {
                dots[i].setTextColor(Color.parseColor("#6D3BA5")
                );
            } else {
                dots[i].setTextColor(
                        Color.parseColor("#D6C9E5")
                );
            }
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
            params.setMargins(5, 0, 5, 0);
            bannerdots.addView(dots[i], params);
        }
        // Change Dot Color When Swiped
        viewPagerBanners.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        if (position < 0 || position >= dots.length) {
                            return;
                        }
                        for (int i = 0; i < dots.length; i++) {
                            if (i == position) {
                                dots[i].setTextColor(Color.parseColor("#6D3BA5")
                                );
                            } else {
                                dots[i].setTextColor(Color.parseColor("#D6C9E5")
                                );
                            }
                        }
                    }
                }
        );
    }
    // Create Categories Dynamically from Firestore
    private void setupCategories() {
        categoryContainer.removeAllViews();
        ArrayList<String> categories = new ArrayList<>();
        // Add All Category First
        categories.add("All");

        // Get Unique Categories from Events
        for (Event event : eventList) {
            if (event.category != null && !event.category.trim().isEmpty()) {
                String[] eventCategories = event.category.split("\\|");
                for (String category : eventCategories) {
                    String cleanCategory = category.trim();
                    if (!cleanCategory.isEmpty()
                            && !categories.contains(cleanCategory)) {
                        categories.add(cleanCategory);
                    }
                }
            }
        }
        // Create Category Buttons
        for (int i = 0; i < categories.size(); i++) {
            String categoryName = categories.get(i);
            TextView categoryButton = new TextView(this);

            categoryButton.setText(categoryName);
            categoryButton.setTextSize(14);

            categoryButton.setTypeface(null, android.graphics.Typeface.NORMAL);

            categoryButton.setGravity(Gravity.CENTER);
            categoryButton.setPadding(
                    dp(16),
                    0,
                    dp(16),
                    0
            );
            categoryButton.setIncludeFontPadding(true);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(42));
            if (i > 0) {
                params.setMargins(10, 0, 0, 0);
            }
            categoryButton.setLayoutParams(params);
            // Set Initial Category Design
            if (i == 0) {
                categoryButton.setBackgroundResource(R.drawable.selected_category);
                categoryButton.setTextColor(Color.WHITE);
            }
            else {
                categoryButton.setBackgroundResource(R.drawable.unselected_category);
                categoryButton.setTextColor(Color.parseColor("#8743BE"));
            }

            // Category Click Listener
            categoryButton.setOnClickListener(v -> {

                // Reset All Category Buttons
                for (int j = 0;
                     j < categoryContainer.getChildCount();
                     j++) {
                    TextView button = (TextView) categoryContainer.getChildAt(j);
                    button.setBackgroundResource(R.drawable.unselected_category);
                    button.setTextColor(Color.parseColor("#8743BE"));
                }
                // Highlight Selected Category
                categoryButton.setBackgroundResource(R.drawable.selected_category);
                categoryButton.setTextColor(Color.WHITE);
                // Filter Events
                filterEvents(categoryName);
            });
            categoryContainer.addView(categoryButton);
        }
        // Display All Events Initially
        filterEvents("All");
    }
    // Convert DP to Pixels
    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }
    // Filter Events by Selected Category
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
