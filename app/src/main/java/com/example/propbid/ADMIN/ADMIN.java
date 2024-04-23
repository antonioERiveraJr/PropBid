package com.example.propbid.ADMIN;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.propbid.R;
import com.example.propbid.logInOption;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ADMIN extends AppCompatActivity {

    private Button adminCLient,adminRequest,adminWorker,adminMessage;
    private ImageButton arrowBack;
    private Button clientCounters;
    private Button buyerCounter, reportCounter, requestCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        arrowBack = (ImageButton) findViewById(R.id.arrowback_message);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ADMIN.this, logInOption.class);
                startActivity(intent);
            }
        });


        adminCLient = (Button) findViewById(R.id.clientButton);
        adminCLient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ADMIN.this, adminClient.class);
                startActivity(intent);
            }
        });
        clientCounters= findViewById(R.id.clientCounter);  // Get the TextView inside the button

        // Query to count items in the database
        Query query = FirebaseDatabase.getInstance().getReference().child("Client").orderByChild("clientNotVerified").equalTo("yes");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                if (count > 0) {
                    // If there are items, set the count as text and make the badge visible
                    clientCounters.setVisibility(View.VISIBLE);
                    clientCounters.setText(String.valueOf(count));
                } else {
                    // If there are no items, hide the badge
                    clientCounters.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });


        adminMessage = (Button) findViewById(R.id.messageButton);
        adminMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ADMIN.this, adminMessage.class);
                startActivity(intent);
            }
        });
        // Report Counter
        reportCounter = findViewById(R.id.reportCounter);
        Query reportQuery = FirebaseDatabase.getInstance().getReference().child("Reports").orderByChild("report").equalTo("yes");
        reportQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                if (count > 0) {
                    reportCounter.setVisibility(View.VISIBLE);
                    reportCounter.setText(String.valueOf(count));
                } else {
                    reportCounter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        adminWorker = (Button) findViewById(R.id.workerButton);
        adminWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ADMIN.this, adminWorker.class);
                startActivity(intent);
            }
        });
        // Buyer Counter
        buyerCounter = findViewById(R.id.workerCounter);
        Query clientQuery = FirebaseDatabase.getInstance().getReference().child("Workers").orderByChild("workerNotVerified").equalTo("yes");
        clientQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                if (count > 0) {
                    buyerCounter.setVisibility(View.VISIBLE);
                    buyerCounter.setText(String.valueOf(count));
                } else {
                    buyerCounter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });


        adminRequest = (Button) findViewById(R.id.requestButton);
        adminRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ADMIN.this, adminRequest.class);
                startActivity(intent);
            }
        });
        // Request Counter
        requestCounter = findViewById(R.id.requestCounter);
        Query requestQuery = FirebaseDatabase.getInstance().getReference().child("Requests").orderByChild("check if request is not verified").equalTo("yes");
        requestQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                if (count > 0) {
                    requestCounter.setVisibility(View.VISIBLE);
                    requestCounter.setText(String.valueOf(count));
                } else {
                    requestCounter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

    }
}