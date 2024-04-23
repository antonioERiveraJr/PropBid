package com.example.propbid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.propbid.Model.Products;
import com.example.propbid.menu.WTransaction;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ViewRequestDetails extends AppCompatActivity {
    private ImageButton arrowBack;
    private EditText productName, productPrice, producDetails, productLocation, productPhoneNumber, postedDate, postedTime, requester, request_worker, request_worker1;
    private TextInputLayout worker1input;
    private TextInputLayout worker2input;
    private TextView workerInformation, counters;
    private ImageView ProductPicture,ProductPicture2,ProductPicture3,ProductPicture4,ProductPicture5;
    private ProgressDialog loadingBar;
    private String chosenWorker = "";
    private TextView txtclose, totalPrice;
    private RelativeLayout completed;
    private ImageView beforeImage, afterImage, before, after;
    private String downloadAfterUrl, downloadBeforeUrl;
    private Button workerButton1, startButton, stopButton;
    private Button workerButton2, transactionCompleteLayout;
    private FirebaseUser User;
    private FirebaseAuth Auth;
    boolean hasWorker = false;
    private DatabaseReference databaseReference, databaseReference2, databaseReference3, chosenworker, jobIsDone;
    private String chosen_worker1;
    private String productID = "";
    private String Date;
    private String worker = "";
    private Uri beforeUri, afterUri;
    private ScrollView scrollView;
    private StorageReference requestBeforeloc, requestAfterloc;
    private String workers = "";
    public int counter, toMinute;
    Dialog myDialog, myDialogs;
    View v;

    private static final int GalleryPick = 1;
    ProgressDialog loadBar;

    HashMap<String, Object> productMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request_details);

        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        arrowBack = (ImageButton) findViewById(R.id.arrowback_View);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewRequestDetails.this, WTransaction.class);
                startActivity(intent);
            }
        });
        requestBeforeloc = FirebaseStorage.getInstance().getReference("Request Before");
        requestAfterloc = FirebaseStorage.getInstance().getReference("Request After");

        //counters = (TextView) findViewById(R.id.textCounter);
        scrollView = (ScrollView) findViewById(R.id.scroller);
        Boolean ButtonClicked = false;
        loadBar = new ProgressDialog(this);
        Boolean startClicked = false;
        //startButton = (Button) findViewById(R.id.startButton);


        stopButton = (Button) findViewById(R.id.stopButton);
        requester = (EditText) findViewById(R.id.product_name_lister);
        productName = (EditText) findViewById(R.id.product_name_Viewdetails);
        productPrice = (EditText) findViewById(R.id.product_price_Viewdetails);
        producDetails = (EditText) findViewById(R.id.product_description_Viewdetails);
        productLocation = (EditText) findViewById(R.id.product_location_Viewdetails);
        productPhoneNumber = (EditText) findViewById(R.id.sellers_phoneNumber_Viewdetails);
        postedDate = (EditText) findViewById(R.id.sellers_posted_Viewdate);
        postedTime = (EditText) findViewById(R.id.sellers_posted_Viewtime);


        ProductPicture = (ImageView) findViewById(R.id.select_product_image);
        ProductPicture2 = (ImageView) findViewById(R.id.select_product_image2);
        ProductPicture3 = (ImageView) findViewById(R.id.select_product_image3);
        ProductPicture4 = (ImageView) findViewById(R.id.select_product_image4);
        ProductPicture5 = (ImageView) findViewById(R.id.select_product_image5);

        totalPrice = (TextView) findViewById(R.id.totalPrice);
        before = (ImageView) findViewById(R.id.before);
        after = (ImageView) findViewById(R.id.after);
        completed = (RelativeLayout) findViewById(R.id.completed);

        Date = getIntent().getStringExtra("Date");
        productID = getIntent().getStringExtra("pid");


        getProductDetails(productID);

        loadingBar = new ProgressDialog(this);


    }

    private void getProductDetails(String productID) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Requests").child(productID);


        databaseReference.child("chosenWorker").child(User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //divider
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        Products products = snapshot.getValue(Products.class);
                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
                        String saveCurrentDate = currentDate.format(calendar.getTime());

                        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                        String saveCurrentTime = currentTime.format(calendar.getTime());

                        String date = saveCurrentDate + saveCurrentTime;


                        DatabaseReference getProfilePic = FirebaseDatabase.getInstance().getReference("Workers").child(User.getUid()).child("profileImage");
                        getProfilePic.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String profilePic = snapshot.getValue().toString();
                                    HashMap<String, Object> notify = new HashMap<>();
                                    notify.put("To", products.getUserId());
                                    notify.put("Message", " has already paid your lot");
                                    notify.put("Id", productID);
                                    notify.put("From", User.getUid());
                                    notify.put("Date", date);
                                    notify.put("FromPicture", profilePic);
                                    notify.put("Clicked", "no");
                                    notify.put("Clicked" + products.getUserId(), "no");
                                    notify.put("Request", products.getProduct_name());
                                    DatabaseReference toNotification = FirebaseDatabase.getInstance().getReference("Notification").child(date);
                                    toNotification.updateChildren(notify);


                                    if (snapshot.exists()) {
                                        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("Notification");
                                        Query query = notificationRef.orderByChild("Id").equalTo(productID);

                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot notificationSnapshot) {
                                                if (notificationSnapshot.exists()) {
                                                    for (DataSnapshot childSnapshot : notificationSnapshot.getChildren()) {
                                                        String fromUser = childSnapshot.child("From").getValue(String.class);
                                                        if (fromUser != null && fromUser.equals(User.getUid())) {
                                                            // The notification is from the current user, so get bidPrice

                                                            String bidPriceString = childSnapshot.child("bidPrice").getValue(String.class);

                                                              if (bidPriceString != null) {
                                                                  int bidPrice = Integer.parseInt(bidPriceString);
                                                                HashMap<String, Object> time = new HashMap<>();
                                                                time.put("workerIsDone", bidPrice);
                                                                HashMap<String, Object> times = new HashMap<>();
                                                                times.put("taposNa", "oo");
                                                                databaseReference.updateChildren(times);
                                                                databaseReference.updateChildren(time);
                                                                Toast.makeText(ViewRequestDetails.this, "Bid: " + bidPrice, Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ViewRequestDetails.this, finalTransaction.class);
                                                                intent.putExtra("pid", productID);
                                                                intent.putExtra("workerIsDone", bidPrice);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                // Handle the error
                                            }
                                        });
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //divider
                //popUP
                // myDialog = new Dialog(ViewRequestDetails.this);
                //CreatepupUpWindow(v, productID);
                //


            }
        });



        databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange (@NonNull DataSnapshot snapshot){

            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            if (snapshot.hasChild("taposNa")) {
               // startButton.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
            }
            if (snapshot.hasChild("taposNaWorker")) {

                completed.setVisibility(View.VISIBLE);
            }


            Products products = snapshot.getValue(Products.class);
            productName.setText(products.getProduct_name());
            requester.setText(products.getRequester());
            productPrice.setText("₱ " + products.getProduct_price());
            producDetails.setText(products.getProduct_details());
            productLocation.setText("Location: " + products.getProduct_location());
            productPhoneNumber.setText("Contact # " + products.getSellers_contact());
            postedDate.setText("Date Listed: " + products.getDate() + products.getTime());
            postedTime.setText("Request Date: " + products.getRequestdate());
            //  hasWorker.Valeu(products.isWorkerAvailable());

            int totalPrices = products.getWorkerIsDone() + Integer.parseInt(products.getProduct_price());
            totalPrice.setText("TOTAL PRICE: ₱ " + totalPrices);

            // getProductDetailsListofWorkers(productID);

            Picasso.get().load(products.getBeforeImage()).into(before);
            Picasso.get().load(products.getAfterImage()).into(after);
            int width = 300;
            int height = 300;

            //  hasWorker.Valeu(products.isWorkerAvailable());
            Picasso.get().load(products.getImage()).resize(width, height).into(ProductPicture);
// Additional images
            if (products.getImage2() != null) {
                Picasso.get().load(products.getImage2()).resize(width, height).into(ProductPicture2);
                ProductPicture2.setVisibility(View.VISIBLE);
            }
            if (products.getImage3() != null) {
                Picasso.get().load(products.getImage3()).resize(width, height).into(ProductPicture3);
                ProductPicture3.setVisibility(View.VISIBLE);
            }
            if (products.getImage4() != null) {
                Picasso.get().load(products.getImage4()).resize(width, height).into(ProductPicture4);
                ProductPicture4.setVisibility(View.VISIBLE);
            }
            if (products.getImage5() != null) {
                Picasso.get().load(products.getImage5()).resize(width, height).into(ProductPicture5);
                ProductPicture5.setVisibility(View.VISIBLE);
            }


            //


        }

        @Override
        public void onCancelled (@NonNull DatabaseError error){
            Toast.makeText(ViewRequestDetails.this, "Error, please report this bug!", Toast.LENGTH_SHORT).show();
        }
    });

}

}

