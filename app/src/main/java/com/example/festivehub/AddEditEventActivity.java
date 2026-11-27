package com.example.festivehub;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEditEventActivity extends AppCompatActivity {
    EditText edteventtitle, edteventcategory, edteventdate;
    EditText edteventtime, edteventlocation, edteventdescription;
    AppCompatButton btnsaveevent, btnchooseimage;
    ImageView imgeventpreview;
    ProgressBar progressimageupload;
    TextView txtimageuploadstatus;
    FirebaseFirestore db;
    String eventId;
    String imageUrl = "";
    Uri selectedImageUri;
    ActivityResultLauncher<String> imagePicker;
    String uploadPreset = "festivehub_events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_event);

        db = FirebaseFirestore.getInstance();

        edteventtitle = findViewById(R.id.edteventtitle);
        edteventcategory = findViewById(R.id.edteventcategory);
        edteventdate = findViewById(R.id.edteventdate);
        edteventtime = findViewById(R.id.edteventtime);
        edteventlocation = findViewById(R.id.edteventlocation);
        edteventdescription = findViewById(R.id.edteventdescription);

        btnsaveevent = findViewById(R.id.btnsaveevent);
        btnchooseimage = findViewById(R.id.btnchooseimage);

        imgeventpreview = findViewById(R.id.imgeventpreview);
        progressimageupload = findViewById(R.id.progressimageupload);
        txtimageuploadstatus = findViewById(R.id.txtimageuploadstatus);

        // Choose Image then it will store the location and display the preview
        imagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        imgeventpreview.setImageURI(uri);
                        txtimageuploadstatus.setText("Image selected");
                    }
                }
        );
        btnchooseimage.setOnClickListener(v ->
                imagePicker.launch("image/*")
        );
        // Add / Edit / Update Event
        eventId = getIntent().getStringExtra("eventId");

        if (eventId != null && !eventId.isEmpty()) {
            loadEventDetails();
            btnsaveevent.setText("Update Event");
        } else {
            btnsaveevent.setText("Save Event");
        }
        btnsaveevent.setOnClickListener(v -> saveEvent());

        // Keyboard handling for scrollview
        ScrollView scrollView = findViewById(R.id.addEventsScrollView);
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (view, insets) -> {
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets system = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    Math.max(ime.bottom, system.bottom)
            );
            return insets;
        });
        ViewCompat.requestApplyInsets(scrollView);
    }
    // Load event details
    private void loadEventDetails() {

        edteventtitle.setText(getIntent().getStringExtra("eventTitle"));
        edteventcategory.setText(getIntent().getStringExtra("eventCategory"));
        edteventdate.setText(getIntent().getStringExtra("eventDate"));
        edteventtime.setText(getIntent().getStringExtra("eventTime"));
        edteventlocation.setText(getIntent().getStringExtra("eventLocation"));
        edteventdescription.setText(getIntent().getStringExtra("eventDescription"));

        imageUrl = getIntent().getStringExtra("eventImageUrl");

        if (imageUrl != null && !imageUrl.isEmpty()) {

            com.bumptech.glide.Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgeventpreview);
        }
    }
    // Save event
    private void saveEvent() {

        String title = edteventtitle.getText().toString().trim();
        String category = edteventcategory.getText().toString().trim();
        String date = edteventdate.getText().toString().trim();
        String time = edteventtime.getText().toString().trim();
        String location = edteventlocation.getText().toString().trim();
        String description = edteventdescription.getText().toString().trim();

        if (title.isEmpty() || category.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnsaveevent.setEnabled(false);

        // If image selected, upload it first
        if (selectedImageUri != null) {
            uploadImage();
        } else {
            saveEventToFirestore();
        }
    }

    // Upload image to Cloudinary
    private void uploadImage() {
        progressimageupload.setVisibility(View.VISIBLE);
        txtimageuploadstatus.setText("Uploading image...");

        //it will inform cloudinary to upload the selected image in upload Present created in cloudinary
        MediaManager.get().upload(selectedImageUri)
                .unsigned(uploadPreset)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                    }
                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }
                    //it will generate safe and secure URL
                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        imageUrl = resultData.get("secure_url").toString();

                        runOnUiThread(() -> {
                            txtimageuploadstatus.setText("Image uploaded");
                            progressimageupload.setVisibility(View.GONE);

                            saveEventToFirestore();
                        });
                    }
                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        runOnUiThread(() -> {
                            progressimageupload.setVisibility(View.GONE);
                            btnsaveevent.setEnabled(true);

                            Toast.makeText(AddEditEventActivity.this, "Image upload failed: "
                                    + error.getDescription(), Toast.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                    }

                })
                .dispatch();
    }
    // Save event details in Firestore
    private void saveEventToFirestore() {
        Map<String, Object> event = new HashMap<>();

        event.put("title", edteventtitle.getText().toString().trim());
        event.put("category", edteventcategory.getText().toString().trim());
        event.put("date", edteventdate.getText().toString().trim());
        event.put("time", edteventtime.getText().toString().trim());
        event.put("location", edteventlocation.getText().toString().trim());
        event.put("description", edteventdescription.getText().toString().trim());

        //it will save img url in firestore
        if (imageUrl != null && !imageUrl.isEmpty()) {
            event.put("imageUrl", imageUrl);
        }

        if (eventId != null && !eventId.isEmpty()) {
            db.collection("Events").document(eventId)
                    .update(event)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Event updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnsaveevent.setEnabled(true);
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } else {
            db.collection("Events")
                    .add(event)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnsaveevent.setEnabled(true);
                        Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}
