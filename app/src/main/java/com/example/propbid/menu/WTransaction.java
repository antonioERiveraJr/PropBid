package com.example.propbid.menu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.propbid.Model.Products;
import com.example.propbid.R;
import com.example.propbid.ViewHolder.SellersProductViewHolder;
import com.example.propbid.ViewRequestDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WTransaction extends AppCompatActivity {
    private ImageButton ArrowBack;
    private RecyclerView myitemList;
    private ImageButton homeButtons, messageButtons, transactionButtons, notificationButtons, profileButtons;

    private ImageView notify;
    public int verifychecker = 1;
    private FirebaseUser User;
    private FirebaseAuth Auth;
    private Button allButton,onGoingButton,doneButton;
    private DatabaseReference databaseReference, ProductsRef;

    private String productID = "";
    private String theuserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wtransaction);

        //spinner
        Spinner dropdownSpinner = (Spinner) findViewById(R.id.dropdownSpinner);
        dropdownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                switch (selectedItem) {
                    case "All":
                        all();
                        break;
                    case "In Progress":
                        onGoing();
                        break;
                    case "Done":
                        done();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                all();
            }
        });

        //endSpinner



        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        notify = (ImageView) findViewById(R.id.red_notification);
        DatabaseReference ProductsRefs = FirebaseDatabase.getInstance().getReference().child("Notification");
        ProductsRefs.orderByChild("Clicked" + User.getUid()).equalTo("no").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    notify.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //menu

        allButton = (Button)findViewById(R.id.allButton);
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                all();
            }
        });
        onGoingButton = (Button)findViewById(R.id.onGoingButton);
        onGoingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoing();
            }
        });
        doneButton = (Button) findViewById(R.id.completedButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
            }
        });
        homeButtons = (ImageButton) findViewById(R.id.homeButton);
        homeButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WTransaction.this, WHome.class);
                startActivity(intent);
            }
        });
        messageButtons = (ImageButton) findViewById(R.id.messageButton);
        messageButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WTransaction.this, WMessages.class);
                startActivity(intent);
            }
        });

        notificationButtons = (ImageButton) findViewById(R.id.fab);
        notificationButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WTransaction.this, WNotification.class);
                startActivity(intent);
            }
        });
        profileButtons = (ImageButton) findViewById(R.id.profileButton);
        profileButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WTransaction.this, WProfiles.class);
                startActivity(intent);
            }
        });



        myitemList = (RecyclerView) findViewById(R.id.myitemsrecyclerview);
        myitemList.setLayoutManager(new LinearLayoutManager(WTransaction.this));
    }

    private void all() {
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();

        productID = getIntent().getStringExtra("pid");
        theuserID = getIntent().getStringExtra("userId");

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Requests");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("chosenWorker").equalTo(User.getUid()), Products.class).build();

        FirebaseRecyclerAdapter<Products, SellersProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, SellersProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SellersProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtProductName.setText(products.getProduct_name());

                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(products.getPid());
                        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {


                                if (snapshot.hasChild("workerIsDone")) {

                                    productViewHolder.txtRemarks.setText("Successfully Completed");
                                } else if (snapshot.hasChild("verifiedtxt") ) {
                                    // run some code
                                    verifychecker = 2;
                                    productViewHolder.txtRemarks.setText("VERIFIED");
                                } else {

                                    productViewHolder.txtRemarks.setText("NOT VERIFIED");
                                }

                                if (snapshot.hasChild("chosenWorker")) {

                                    productViewHolder.txtStatus.setText("Worker is Available");


                                } else {

                                    productViewHolder.txtStatus.setText(products.getStatus());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] sortOptions = {"View Item", "Delete Item"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(WTransaction.this);
                                builder.setTitle(products.getProduct_name());
                                builder.setIcon(R.drawable.ic_mylistings);
                                builder.setSingleChoiceItems(sortOptions, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(WTransaction.this, ViewRequestDetails.class);
                                            intent.putExtra("userId", products.getUserId());
                                            intent.putExtra("pid", products.getPid());
                                            startActivity(intent);
                                        } else if (which == 1) {
                                            dialog.dismiss();
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WTransaction.this);
                                            builder.setTitle("")
                                                    .setMessage("Are you sure you want to delete " + products.getProduct_name() + " ?")
                                                    .setCancelable(true)
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            databaseReference = FirebaseDatabase.getInstance().getReference("Requests").child(products.getPid());
                                                            databaseReference.removeValue();
                                                        }
                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    }).show();
                                        }
                                    }
                                });
                                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public SellersProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_items_layout, parent, false);
                        SellersProductViewHolder holder = new SellersProductViewHolder(view);
                        return holder;
                    }
                };


        myitemList.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WTransaction.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myitemList.setLayoutManager(linearLayoutManager);


    }
    private void onGoing() {
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();

        productID = getIntent().getStringExtra("pid");
        theuserID = getIntent().getStringExtra("userId");

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Requests");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("onGoing"+User.getUid()).equalTo("yes"), Products.class).build();

        FirebaseRecyclerAdapter<Products, SellersProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, SellersProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SellersProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtProductName.setText(products.getProduct_name());

                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(products.getPid());
                        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {


                                if (snapshot.hasChild("workerIsDone")) {

                                    productViewHolder.txtRemarks.setText("Successfully Completed");
                                } else if (snapshot.hasChild("verified") ) {
                                    // run some code
                                    verifychecker = 2;
                                    productViewHolder.txtRemarks.setText("VERIFIED");
                                } else {

                                    productViewHolder.txtRemarks.setText("NOT VERIFIED");
                                }

                                if (snapshot.hasChild("chosenWorker")) {

                                    productViewHolder.txtStatus.setText("Worker is Available");


                                } else {

                                    productViewHolder.txtStatus.setText(products.getStatus());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] sortOptions = {"View Item", "Delete Item"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(WTransaction.this);
                                builder.setTitle(products.getProduct_name());
                                builder.setIcon(R.drawable.ic_mylistings);
                                builder.setSingleChoiceItems(sortOptions, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(WTransaction.this, ViewRequestDetails.class);
                                            intent.putExtra("userId", products.getUserId());
                                            intent.putExtra("pid", products.getPid());
                                            startActivity(intent);
                                        } else if (which == 1) {
                                            dialog.dismiss();
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WTransaction.this);
                                            builder.setTitle("")
                                                    .setMessage("Are you sure you want to delete " + products.getProduct_name() + " ?")
                                                    .setCancelable(true)
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            databaseReference = FirebaseDatabase.getInstance().getReference("Requests").child(products.getPid());
                                                            databaseReference.removeValue();
                                                        }
                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    }).show();
                                        }
                                    }
                                });
                                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public SellersProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_items_layout, parent, false);
                        SellersProductViewHolder holder = new SellersProductViewHolder(view);
                        return holder;
                    }
                };


        myitemList.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WTransaction.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myitemList.setLayoutManager(linearLayoutManager);


    }
    private void done() {
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();

        productID = getIntent().getStringExtra("pid");
        theuserID = getIntent().getStringExtra("userId");

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Requests");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("done"+User.getUid()).equalTo("yes"), Products.class).build();

        FirebaseRecyclerAdapter<Products, SellersProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, SellersProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SellersProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtProductName.setText(products.getProduct_name());

                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(products.getPid());
                        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {


                                if (snapshot.hasChild("workerIsDone")) {

                                    productViewHolder.txtRemarks.setText("Successfully Completed");
                                } else if (snapshot.hasChild("verified") ) {
                                    // run some code
                                    verifychecker = 2;
                                    productViewHolder.txtRemarks.setText("VERIFIED");
                                } else {

                                    productViewHolder.txtRemarks.setText("NOT VERIFIED");
                                }

                                if (snapshot.hasChild("chosenWorker")) {

                                    productViewHolder.txtStatus.setText("Worker is Available");


                                } else {

                                    productViewHolder.txtStatus.setText(products.getStatus());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] sortOptions = {"View Item", "Delete Item"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(WTransaction.this);
                                builder.setTitle(products.getProduct_name());
                                builder.setIcon(R.drawable.ic_mylistings);
                                builder.setSingleChoiceItems(sortOptions, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(WTransaction.this, ViewRequestDetails.class);
                                            intent.putExtra("userId", products.getUserId());
                                            intent.putExtra("pid", products.getPid());
                                            startActivity(intent);
                                        } else if (which == 1) {
                                            dialog.dismiss();
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WTransaction.this);
                                            builder.setTitle("")
                                                    .setMessage("Are you sure you want to delete " + products.getProduct_name() + " ?")
                                                    .setCancelable(true)
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            databaseReference = FirebaseDatabase.getInstance().getReference("Requests").child(products.getPid());
                                                            databaseReference.removeValue();
                                                        }
                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    }).show();
                                        }
                                    }
                                });
                                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public SellersProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_items_layout, parent, false);
                        SellersProductViewHolder holder = new SellersProductViewHolder(view);
                        return holder;
                    }
                };


        myitemList.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WTransaction.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myitemList.setLayoutManager(linearLayoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();

        productID = getIntent().getStringExtra("pid");
        theuserID = getIntent().getStringExtra("userId");

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Requests");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("chosenWorker").equalTo(User.getUid()), Products.class).build();

        FirebaseRecyclerAdapter<Products, SellersProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, SellersProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SellersProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtProductName.setText(products.getProduct_name());

                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Requests").child(products.getPid());
                        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {


                                if (snapshot.hasChild("workerIsDone")) {

                                    productViewHolder.txtRemarks.setText("Successfully Completed");
                                } else if (snapshot.hasChild("verified") ) {
                                    // run some code
                                    verifychecker = 2;
                                    productViewHolder.txtRemarks.setText("VERIFIED");
                                } else {

                                    productViewHolder.txtRemarks.setText("NOT VERIFIED");
                                }

                                if (snapshot.hasChild("chosenWorker")) {

                                    productViewHolder.txtStatus.setText("Worker is Available");


                                } else {

                                    productViewHolder.txtStatus.setText(products.getStatus());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String[] sortOptions = {"View Item", "Delete Item"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(WTransaction.this);
                                builder.setTitle(products.getProduct_name());
                                builder.setIcon(R.drawable.ic_mylistings);
                                builder.setSingleChoiceItems(sortOptions, -1, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(WTransaction.this, ViewRequestDetails.class);
                                            intent.putExtra("userId", products.getUserId());
                                            intent.putExtra("pid", products.getPid());
                                            startActivity(intent);
                                        } else if (which == 1) {
                                            dialog.dismiss();
                                            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(WTransaction.this);
                                            builder.setTitle("")
                                                    .setMessage("Are you sure you want to delete " + products.getProduct_name() + " ?")
                                                    .setCancelable(true)
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            databaseReference = FirebaseDatabase.getInstance().getReference("Requests").child(products.getPid());
                                                            databaseReference.removeValue();
                                                        }
                                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    }).show();
                                        }
                                    }
                                });
                                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public SellersProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client_items_layout, parent, false);
                        SellersProductViewHolder holder = new SellersProductViewHolder(view);
                        return holder;
                    }
                };


        myitemList.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WTransaction.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myitemList.setLayoutManager(linearLayoutManager);


    }
}

