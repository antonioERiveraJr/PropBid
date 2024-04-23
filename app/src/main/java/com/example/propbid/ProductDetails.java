package com.example.propbid;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.propbid.Model.Applicants;
import com.example.propbid.Model.ChatUsers;
import com.example.propbid.Model.Products;
import com.example.propbid.Model.Workers;
import com.example.propbid.ViewHolder.ApplicantsViewHolder;
import com.example.propbid.menu.WHome;
import com.example.propbid.menu.WTransaction;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ProductDetails extends AppCompatActivity {

    private ImageButton arrowBack;
    private EditText productName, productPrice, producDetails, productLocation, productPhoneNumber, postedDate, postedTime, worker1, worker2,
            listersName, bidPrices,details;
    private ImageView ProductPicture, ProductPicture2, ProductPicture3, ProductPicture4, ProductPicture5;
    private String productID = "";
    private String theuserID = "";
    private String date;
    private Button messageBTN, startButton, stopButton;

    private String applicantDates = "";
    private String trry;
    private String productRandomKey, saveCurrentDate, saveCurrentTime;
    private FirebaseUser User;
    private FirebaseAuth Auth;
    private DatabaseReference databaseReference, ProductsRef;
    private DatabaseReference checkWorker;
    private DatabaseReference checkCLientID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        arrowBack = (ImageButton) findViewById(R.id.arrowback_ProductDetails);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetails.this, WHome.class);
                startActivity(intent);
            }
        });

        details = (EditText) findViewById(R.id.details);
        bidPrices = (EditText) findViewById(R.id.bidPrice);
        productName = (EditText) findViewById(R.id.product_name_details);
        productPrice = (EditText) findViewById(R.id.product_price_details);
        producDetails = (EditText) findViewById(R.id.product_description_details);
        productLocation = (EditText) findViewById(R.id.product_location_details);
        productPhoneNumber = (EditText) findViewById(R.id.sellers_phoneNumber_details);
        postedDate = (EditText) findViewById(R.id.sellers_posted_date);
        listersName = (EditText) findViewById(R.id.ListerName);

        ProductPicture = (ImageView) findViewById(R.id.select_product_image);
        ProductPicture2 = (ImageView) findViewById(R.id.select_product_image2);
        ProductPicture3 = (ImageView) findViewById(R.id.select_product_image3);
        ProductPicture4 = (ImageView) findViewById(R.id.select_product_image4);
        ProductPicture5 = (ImageView) findViewById(R.id.select_product_image5);

        applicantDates = getIntent().getStringExtra("applicantDate");
        productID = getIntent().getStringExtra("pid");
        theuserID = getIntent().getStringExtra("userId");


        messageBTN = (Button) findViewById(R.id.message_seller_btn);

        getProductDetails(productID, theuserID);

        DatabaseReference removeButton = FirebaseDatabase.getInstance().getReference("Applicants");
        removeButton.orderByChild("Togetter2").equalTo(User.getUid() + productID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // messageBTN.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    private void getProductDetails(String productID/*, String theuserID*/, String theuserID) {
        // Toast.makeText(this, "Request ID: "+productID, Toast.LENGTH_SHORT).show();
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        productsRef.child(/*theuserID + " " + */productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Products products = snapshot.getValue(Products.class);

                    details.setText(products.getDetails());
                    productName.setText(products.getProduct_name());
                    productPrice.setText("₱ " + products.getProduct_price());
                    producDetails.setText(products.getProduct_details());
                    productLocation.setText("Location: " + products.getProduct_location());
                    productPhoneNumber.setText("Contact # " + products.getSellers_contact());
                    productRandomKey = products.getDate() + products.getTime();
                    postedDate.setText("Date Listed: " + products.getDate());
                    listersName.setText(products.getLister_shopName());


                    String detailsText = products.getDetails();

                    if (detailsText != null && !detailsText.isEmpty()) {
                        details.setText(detailsText);
                    } else {
                        details.setVisibility(View.GONE);
                    }


                    listersName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ProductDetails.this, ChatsActivity.class);
                            //note Sure with seller's id name on firebase
                            intent.putExtra("userId", products.getUserId());
                            startActivity(intent);
                        }
                    });
                    messageBTN.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String bidPriceList = bidPrices.getText().toString();

                            if (bidPriceList.isEmpty()) {
                                bidPrices.setError("Bid price is required");
                            } else {
                                int bidPrice = Integer.parseInt(bidPriceList);

                                if (bidPrice < 1) {
                                    bidPrices.setError("Bid price must be at least 1");
                                } else if (bidPrice > 9999) {
                                    bidPrices.setError("The maximum bid is 9999");
                                } else {
                                    Toast.makeText(ProductDetails.this, "BID SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                    DatabaseReference requestName = FirebaseDatabase.getInstance().getReference("Requests").child(productID);
                                    requestName.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                Calendar calendar = Calendar.getInstance();

                                                SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                                                String saveCurrentDate = currentDate.format(calendar.getTime());

                                                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                                                String saveCurrentTime = currentTime.format(calendar.getTime());

                                                String productRandomKey = saveCurrentDate + saveCurrentTime;
                                                HashMap<String, Object> productMaps = new HashMap<>();
                                                productMaps.put("Users", User.getUid());
                                                productMaps.put("Date", productRandomKey);
                                                String nameOfRequest = snapshot.child("product_name").getValue().toString();
                                                productMaps.put("Topic", nameOfRequest);
                                                productMaps.put("Action", "You accepted the request " + nameOfRequest + ".");
                                                DatabaseReference logActivity = FirebaseDatabase.getInstance().getReference("Log").child(User.getUid() + productRandomKey);
                                                logActivity.updateChildren(productMaps);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    DatabaseReference getProfilePic = FirebaseDatabase.getInstance().getReference("Workers").child(User.getUid()).child("profileImage");
                                    getProfilePic.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {


                                                String profilePic = snapshot.getValue().toString();

                                                updateWorkerAcceptedRequest(String.valueOf(products.getProduct_name()), productID, profilePic, theuserID);
                                                Intent intent = new Intent(ProductDetails.this, WTransaction.class);
                                                intent.putExtra("userId", theuserID);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            }
                        }
                    });

                    Picasso.get().load(products.getImage()).resize(300, 150).into(ProductPicture);

                    if (products.getImage2() != null) {

                        Picasso.get().load(products.getImage2()).resize(300, 150).into(ProductPicture2);
                        ProductPicture2.setVisibility(View.VISIBLE);
                    }
                    if (products.getImage3() != null) {

                        Picasso.get().load(products.getImage3()).resize(300, 150).into(ProductPicture3);
                        ProductPicture3.setVisibility(View.VISIBLE);
                    }
                    if (products.getImage4() != null) {

                        Picasso.get().load(products.getImage4()).resize(300, 150).into(ProductPicture4);
                        ProductPicture4.setVisibility(View.VISIBLE);
                    }
                    if (products.getImage5() != null) {

                        Picasso.get().load(products.getImage5()).resize(300, 150).into(ProductPicture5);
                        ProductPicture5.setVisibility(View.VISIBLE);
                    }

                    if (products.getUserId().equals(User.getUid())) {
                        //  messageBTN.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onStart() {
        super.onStart();

        RecyclerView applicantsRecyclerView;
        applicantsRecyclerView = findViewById(R.id.applicantsRecyclerView);
        applicantsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        applicantsRecyclerView.setLayoutManager(layoutManager);
        DatabaseReference bid = FirebaseDatabase.getInstance().getReference("Applicants");

        FirebaseRecyclerOptions<Applicants> options = new FirebaseRecyclerOptions.Builder<Applicants>()
                .setQuery(bid.orderByChild("requestId").equalTo(productID), Applicants.class).build();

        FirebaseRecyclerAdapter<Applicants, ApplicantsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Applicants, ApplicantsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ApplicantsViewHolder productViewHolder, int i, @NonNull Applicants applicants) {
                        // Bind data to ViewHolder
                        productViewHolder.applicantNames.setText(applicants.getApplicantName());
                        productViewHolder.applicantBid.setText(applicants.getBidPrice());
                        productViewHolder.applicantDate.setText(applicants.getApplicantDate());

                        // Calculate bid counts based on ranges
                        DatabaseReference bidRef = FirebaseDatabase.getInstance().getReference("Applicants");
                        bidRef.orderByChild("requestId").equalTo(productID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int count1_3000 = 0;
                                int count3001_6000 = 0;
                                int count6001_9000 = 0;
                                int count9001_above = 0;

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Applicants applicant = snapshot.getValue(Applicants.class);
                                    if (applicant != null) {
                                        String bidPrice = applicant.getBidPrice();
                                        if (!TextUtils.isEmpty(bidPrice)) {
                                            int bid = Integer.parseInt(bidPrice);
                                            if (bid >= 1 && bid <= 3000) {
                                                count1_3000++;
                                            } else if (bid >= 3001 && bid <= 6000) {
                                                count3001_6000++;
                                            } else if (bid >= 6001 && bid <= 9000) {
                                                count6001_9000++;
                                            } else {
                                                count9001_above++;
                                            }
                                        }
                                    }
                                }

                                // Update chart with bid counts
                                updateChart(count1_3000, count3001_6000, count6001_9000, count9001_above);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(TAG, "Error fetching bids: " + databaseError.getMessage());
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ApplicantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        // Create ViewHolder
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.applicant_item, parent, false);
                        return new ApplicantsViewHolder(view);
                    }
                };

        applicantsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    void updateChart(int count1_3000, int count3001_6000, int count6001_9000, int count9001_above) {
        BarChart barChart = findViewById(R.id.barChart);
        barChart.getDescription().setEnabled(false); // Disable description
        barChart.setFitBars(true); // Set bars to fit the width of the chart

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, count1_3000));
        entries.add(new BarEntry(1, count3001_6000));
        entries.add(new BarEntry(2, count6001_9000));
        entries.add(new BarEntry(3, count9001_above));

        BarDataSet dataSet = new BarDataSet(entries, "Number of Applicants");
        BarData barData = new BarData(dataSet);

        barChart.setData(barData);
        barChart.invalidate();

        // Configure X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f); // Adjust granularity as needed
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Define bid ranges for X-axis labels
                switch ((int) value) {
                    case 0:
                        return "1 - 3000";
                    case 1:
                        return "3001 - 6000";
                    case 2:
                        return "6001 - 9000";
                    case 3:
                        return "9001 >";
                    default:
                        return "";
                }
            }
        }); // Optional: format the labels as needed
    }


// dacanay.aj.06@gmail.com


/*
 if (value <= 3000) {
                    bidRange = "1 - 3000";
                } else if (value <= 6000) {
                    bidRange = "3001 - 6000";
                } else if (value <= 9000) {
                    bidRange = "6001 - 9000";
                } else {
                    bidRange = "More than 9000";
                }
                return bidRange;
 */

    private void updateWorkerAcceptedRequest(String productName, String productID, String profilePic, String theuserID) {

        Calendar calendar = Calendar.getInstance();


        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        date = saveCurrentDate + saveCurrentTime;


        //int bidPriceList = Integer.parseInt(bidPrice.getText().toString());
        String bidPriceList = bidPrices.getText().toString();

        if (Integer.parseInt(bidPriceList) < 1 || bidPriceList.isEmpty()) {
            bidPrices.setError("Bid price is required");
        } else if (Integer.parseInt(bidPriceList) > 9999) {
            bidPrices.setError("The maximum bid is 9999");
        } else {
            HashMap<String, Object> notify = new HashMap<>();
            notify.put("To", theuserID);
            notify.put("Message", " raised the bid on your property to " + bidPriceList);
            notify.put("Id", productID);
            notify.put("From", User.getUid());
            notify.put("FromPicture", profilePic);
            notify.put("Date", date);
            notify.put("Clicked", "no");
            notify.put("Clicked" + theuserID, "no");
            notify.put("Request", productName);
            notify.put("bidPrice", bidPriceList);
            DatabaseReference toNotification = FirebaseDatabase.getInstance().getReference("Notification").child(date);
            toNotification.updateChildren(notify);


            DatabaseReference getProfilePic = FirebaseDatabase.getInstance().getReference("Workers");
            getProfilePic.child(User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()) {

                        Workers workers = snapshot.getValue(Workers.class);
                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                        String saveCurrentDate = currentDate.format(calendar.getTime());

                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                        String saveCurrentTime = currentTime.format(calendar.getTime());

                        float finalRating = workers.getRating();
                        int finalCount = workers.getRatingCount();
                        float rating = finalRating / finalCount;
                        // int bidPriceList = Integer.parseInt(bidPrice.getText().toString());
                        String bidPriceList = bidPrices.getText().toString();
                        String date = saveCurrentDate + saveCurrentTime;
                        HashMap<String, Object> hashMapxs = new HashMap<>();
                        hashMapxs.put("applicantId", User.getUid());
                        hashMapxs.put("Togetter", theuserID + productID);
                        hashMapxs.put("Togetter2", User.getUid() + productID);
                        hashMapxs.put("To", theuserID);
                        hashMapxs.put("requestId", productID);
                        hashMapxs.put("applicantDate", date);
                        hashMapxs.put("applicantImage", workers.getProfileImage());
                        hashMapxs.put("applicantName", workers.getName());
                        hashMapxs.put("applicantRatings", workers.getRating());
                        hashMapxs.put("applicantEmail", workers.getEmail());
                        hashMapxs.put("applicantAddress", workers.getAddress());
                        hashMapxs.put("number", workers.getPhoneNumber());
                        hashMapxs.put("bidPrice", bidPriceList);

                        DatabaseReference updateListofWorker = FirebaseDatabase.getInstance().getReference("Applicants").child(User.getUid() + date);
                        updateListofWorker.updateChildren(hashMapxs);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }
}