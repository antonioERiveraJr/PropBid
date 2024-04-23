package com.example.propbid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.propbid.Model.Applicants;
import com.example.propbid.Model.Products;
import com.example.propbid.ViewHolder.ApplicantsViewHolder;
import com.example.propbid.menu.CTransaction;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewYourProductDetails extends AppCompatActivity {

    private ImageButton arrowBack;
    private EditText productName, productPrice, producDetails, productLocation, productPhoneNumber, postedDate, postedTime, request_worker, request_worker1;
    private TextInputLayout worker1input;
    private TextInputLayout worker2input;
    private TextView workerInformation;
    private ImageView ProductPicture,before,after;
    private ImageView ProductPicture2,ProductPicture3,ProductPicture4,ProductPicture5;
    private ProgressDialog loadingBar;
    private String chosenWorker = "";
    private FrameLayout frame1;
    private Button workerButton1;
    private Button workerButton2, transactionCompleteLayout;
    private FirebaseUser User;
    private FirebaseAuth Auth;
    boolean hasWorker = false;
    private RelativeLayout completed;
    private TextView textView;
    private EditText inputLayoutWorker, inputLayoutWorker2;
    private DatabaseReference databaseReference, databaseReference2, databaseReference3, chosenworker, jobIsDone;
    private String chosen_worker1;
    private String productID = "";
    private String Date = "";
    private TextView workerChosen;
    private TextView workerTxt,totalPrice;
    private String worker;
    private String workers;
    private String daterrss;
    private ScrollView scrollView;
    Dialog myDialog;
    RecyclerView myitemList;


    private DatabaseReference databaseReference4 = FirebaseDatabase.getInstance().getReference().child("Requests").child(productID).child("worker1");
    ProgressDialog loadBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_your_product_details);
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        scrollView = (ScrollView) findViewById(R.id.scroller);
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        totalPrice = (TextView)findViewById(R.id.totalPrice);
        arrowBack = (ImageButton) findViewById(R.id.arrowback_View);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewYourProductDetails.this, CTransaction.class);
                startActivity(intent);
            }
        });
        before = (ImageView) findViewById(R.id.before);
        after  = (ImageView) findViewById(R.id.after);
        completed = (RelativeLayout) findViewById(R.id.completed);
        productID = getIntent().getStringExtra("pid");
        daterrss = getIntent().getStringExtra("applicantDate");
        Date = getIntent().getStringExtra("Date");
        workerTxt = (TextView) findViewById(R.id.chosenWorker);
        workerChosen = (TextView)findViewById(R.id.txtworker);

        frame1 = (FrameLayout) findViewById(R.id.frame1);
        textView = (TextView) findViewById(R.id.textView);
        transactionCompleteLayout = (Button) findViewById(R.id.workIsDone);
        transactionCompleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadBar.show();
                myDialog = new Dialog(ViewYourProductDetails.this);
              CreatepupUpWindow(v,productID,chosenWorker);
            }
        });


        loadBar = new ProgressDialog(this);
        productName = (EditText) findViewById(R.id.product_name_Viewdetails);
        productPrice = (EditText) findViewById(R.id.product_price_Viewdetails);
        producDetails = (EditText) findViewById(R.id.product_description_Viewdetails);
        productLocation = (EditText) findViewById(R.id.product_location_Viewdetails);
        productPhoneNumber = (EditText) findViewById(R.id.sellers_phoneNumber_Viewdetails);
        postedDate = (EditText) findViewById(R.id.sellers_posted_Viewdate);


        ProductPicture = (ImageView) findViewById(R.id.select_product_image);
        ProductPicture2 = (ImageView) findViewById(R.id.select_product_image2);
        ProductPicture3 = (ImageView) findViewById(R.id.select_product_image3);
        ProductPicture4 = (ImageView) findViewById(R.id.select_product_image4);
        ProductPicture5 = (ImageView) findViewById(R.id.select_product_image5);



        workerInformation = (TextView) findViewById(R.id.textView);


        transactionCompleteLayout = (Button) findViewById(R.id.workIsDone);


        DatabaseReference Gone = FirebaseDatabase.getInstance().getReference("Applicants");
        Gone.orderByChild("requestId").equalTo(productID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){

                    workerInformation.setVisibility(View.GONE);
                    myitemList.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        loadingBar = new ProgressDialog(this);

        myitemList = (RecyclerView) findViewById(R.id.listofWorkers);
        myitemList.setHasFixedSize(true);
        myitemList.setLayoutManager(new LinearLayoutManager(ViewYourProductDetails.this,LinearLayoutManager.HORIZONTAL,true));
        getProductDetails(productID);

        databaseReference3 = FirebaseDatabase.getInstance().getReference().child("Requests").child(productID);
        databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("chosenWorker")) {
                    String workersss = snapshot.child("chosenWorker").getValue().toString();
                    DatabaseReference getName = FirebaseDatabase.getInstance().getReference("Workers").child(workersss).child("name");
                    getName.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                String workName = snapshot.getValue().toString();
                                workerTxt.setText(workName);
                                workerTxt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ViewYourProductDetails.this,checkProfile.class);
                                        intent.putExtra("applicantId",workersss);
                                        intent.putExtra("pid", productID);
                                        intent.putExtra("applicantDate", daterrss);
                                        intent.putExtra("chosenWorker", workersss);
                                        startActivity(intent);
                                    }
                                });
                                myitemList.setVisibility(View.GONE);
                                workerTxt.setVisibility(View.VISIBLE);
                                workerChosen.setVisibility(View.VISIBLE);
                                textView.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    DatabaseReference workerIsDone = FirebaseDatabase.getInstance().getReference("Requests").child(productID);

                    workerIsDone.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild("workerIsDone") && !snapshot.hasChild("taposNaWorker" )&& snapshot.hasChild("beforeImage")) {


                                frame1.setVisibility(View.VISIBLE);
                                transactionCompleteLayout.setVisibility(View.VISIBLE);


                            }
                            if(snapshot.hasChild("completed")){
                                completed.setVisibility(View.VISIBLE);
                            }

                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getProductDetails(String productID) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Requests");
        databaseReference.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    Products products = snapshot.getValue(Products.class);
                    productName.setText(products.getProduct_name());
                    productPrice.setText("₱ " + products.getProduct_price());
                    producDetails.setText(products.getProduct_details());
                    productLocation.setText("Location: " + products.getProduct_location());
                    productPhoneNumber.setText("Contact # " + products.getSellers_contact());
                    postedDate.setText("Date Listed: " + products.getDate());
                            //totalPrice.setText("TOTAL PRICE: ₱ "+products.getProduct_price());
                    totalPrice.setText("");


                    int width = 300;
                    int height = 300;

                    //  hasWorker.Valeu(products.isWorkerAvailable());
                    Picasso.get().load(products.getImage()).resize(width, height).into(ProductPicture);
// Additional images
                    if (products.getImage() != null) {
                        ProductPicture2.setVisibility(View.VISIBLE);
                    }if (products.getImage2() != null) {
                        Picasso.get().load(products.getImage2()).resize(width, height).into(ProductPicture2);
                        ProductPicture3.setVisibility(View.VISIBLE);
                    }
                    if (products.getImage3() != null) {
                        Picasso.get().load(products.getImage3()).resize(width, height).into(ProductPicture3);
                        ProductPicture4.setVisibility(View.VISIBLE);
                    }
                    if (products.getImage4() != null) {
                        Picasso.get().load(products.getImage4()).resize(width, height).into(ProductPicture4);
                        ProductPicture5.setVisibility(View.VISIBLE);
                    }
                    if (products.getImage5() != null) {
                        Picasso.get().load(products.getImage5()).resize(width, height).into(ProductPicture5);
                    }


                    //


                    Picasso.get().load(products.getBeforeImage()).into(before);
                    Picasso.get().load(products.getAfterImage()).into(after);

                    DatabaseReference getProfilePic = FirebaseDatabase.getInstance().getReference("Client").child(User.getUid()).child("profileImage");
                    getProfilePic.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String profilePic = snapshot.getValue().toString();


                                getProductDetailsListofWorkers(productID, profilePic);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewYourProductDetails.this, "Error, please report this bug!", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void getProductDetailsListofWorkers(String productID, String profilePic) {
        DatabaseReference ProductsRef = FirebaseDatabase.getInstance().getReference().child("Applicants");


        FirebaseRecyclerOptions<Applicants> options = new FirebaseRecyclerOptions.Builder<Applicants>()
                .setQuery(ProductsRef.orderByChild("Togetter").equalTo(User.getUid()+productID), Applicants.class).build();

        FirebaseRecyclerAdapter<Applicants, ApplicantsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Applicants, ApplicantsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ApplicantsViewHolder applicantsViewHolder, int i, @NonNull Applicants applicants) {


                        String id = applicants.getApplicantId();
                        String daters = applicants.getApplicantDate();
                        String workersid = applicants.getApplicantId();
                        //n


                        //

                        applicantsViewHolder.applicantName.setText(applicants.getApplicantName());
                        applicantsViewHolder.bidPrice.setText(applicants.getBidPrice());
//                        applicantsViewHolder.applicantRating.setRating(applicants.getApplicantRatings());

                        Picasso.get().load(applicants.getApplicantImage()).into(applicantsViewHolder.applicantImage);

                        applicantsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ViewYourProductDetails.this, checkProfile.class);
                                intent.putExtra("applicantId", applicants.getApplicantId());
                                intent.putExtra("pid", productID);
                                intent.putExtra("applicantDate", applicants.getApplicantDate());
                                intent.putExtra("chosenWorker", applicants.getApplicantId());
                                intent.putExtra("bidPrice", applicants.getBidPrice());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ApplicantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.applicant_items_layout, parent, false);
                        ApplicantsViewHolder holder = new ApplicantsViewHolder(view);
                        return holder;
                    }
                };
        myitemList.setAdapter(adapter);
        adapter.startListening();


    }



    private void CreatepupUpWindow(View v,String productID,String chosenWorker) {

        Toast.makeText(this, "id"+productID, Toast.LENGTH_SHORT).show();
        TextView txtclose,price;
        ImageView before,after;
        myDialog.setContentView(R.layout.final_transaction_show_client);
        txtclose = (TextView) myDialog.findViewById(R.id.Gotit);
        txtclose.setText("GOT IT!");
        price  = (TextView) myDialog.findViewById(R.id.totalPrice);
        before = (ImageView) myDialog.findViewById(R.id.before);
        after = (ImageView) myDialog.findViewById(R.id.after);
        DatabaseReference getInfo = FirebaseDatabase.getInstance().getReference("Requests").child(productID);
        getInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Products products = snapshot.getValue(Products.class);

                    int totalPrice = products.getWorkerIsDone() + Integer.parseInt(products.getProduct_price());

                    Glide.with(getApplicationContext()).load(products.getBeforeImage()).into(before);
                    Glide.with(getApplicationContext()).load(products.getAfterImage()).into(after);
                    price.setText("Price: ₱"+totalPrice);
                    loadBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                Intent intent = new Intent(ViewYourProductDetails.this, jobIsDone.class);
                intent.putExtra("chosenWorker", chosenWorker);
                intent.putExtra("pid", productID);
                startActivity(intent);

            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }




}