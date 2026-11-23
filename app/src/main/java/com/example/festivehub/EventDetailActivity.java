package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class EventDetailActivity extends AppCompatActivity {
    ImageView imgeventdetail;
    TextView txtbackevent, txteventtitle, txteventcategory, txteventdate, txteventtime, txteventlocation;
    TextView txtdescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        imgeventdetail = findViewById(R.id.imgeventdetail);
        txtbackevent = findViewById(R.id.txtbackevent);
        txteventtitle = findViewById(R.id.txteventtitle);
        txteventcategory = findViewById(R.id.txteventcategory);
        txteventdate = findViewById(R.id.txteventdate);
        txteventtime = findViewById(R.id.txteventtime);
        txteventlocation = findViewById(R.id.txteventlocation);
        txtdescription = findViewById(R.id.txteventdescription);

        Intent intent = getIntent();

        String title = intent.getStringExtra("eventTitle");
        String category = intent.getStringExtra("eventCategory");
        String date = intent.getStringExtra("eventDate");
        String time = intent.getStringExtra("eventTime");
        String location = intent.getStringExtra("eventLocation");
        String description = intent.getStringExtra("eventDescription");

        int image = intent.getIntExtra("eventImage", R.drawable.grand_festival);

        txteventtitle.setText(title);
        txteventcategory.setText(category);
        txteventdate.setText(date);
        txteventtime.setText(time);
        txteventlocation.setText(location);
        txtdescription.setText(description);

        imgeventdetail.setImageResource(image);

        txtbackevent.setOnClickListener(v->{
            finish();
        });

    }
}