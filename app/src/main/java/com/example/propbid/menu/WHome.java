package com.example.propbid.menu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.propbid.Model.Products;
import com.example.propbid.ProductDetails;
import com.example.propbid.R;
import com.example.propbid.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class WHome extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private ImageButton homeButtons, messageButtons, transactionButtons, notificationButtons, profileButtons, reportButton;
    private String productRandomKey;
    private FirebaseUser User;
    private FirebaseAuth Auth;
    private DatabaseReference databaseReference, ProductsRef;
    View view;
    private boolean isBackPressedOnce = false;

    private TextView  sortButton;
    private String userId = "";
    ProductViewHolder holder;
    private ImageView notify;
    private ImageButton searchButton;
    private EditText searchTXT;
    private String searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whome);

        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //menu
        Spinner categorySpinner = findViewById(R.id.categorySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle the selected category here
                String selectedCategory = parentView.getItemAtPosition(position).toString();
                // Perform actions based on the selected category (e.g., filter data)
                handleCategorySelection(selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

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



        messageButtons = (ImageButton) findViewById(R.id.messageButton);
        messageButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WHome.this, WMessages.class);
                startActivity(intent);
            }
        });
        notificationButtons = (ImageButton) findViewById(R.id.fab);
        notificationButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WHome.this, WNotification.class);
                startActivity(intent);
            }
        });
        transactionButtons = (ImageButton) findViewById(R.id.transactionButton);
        transactionButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WHome.this, WTransaction.class);
                startActivity(intent);
            }
        });
        profileButtons = (ImageButton) findViewById(R.id.profileButton);
        profileButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WHome.this, WProfiles.class);
                startActivity(intent);
            }
        });


        searchTXT = (EditText) findViewById(R.id.homesearchItemsText);
        searchButton = (ImageButton) findViewById(R.id.homesearchItemsButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput = searchTXT.getText().toString().toLowerCase();
                searchItems();
            }
        });

        sortButton = (TextView) findViewById(R.id.homesortItemsButton);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortDialog();
            }
        });

    }
    private void handleCategorySelection(String selectedCategory) {
        // Update the layout manager based on the selected category
        if (selectedCategory.equals("List View")) {
            setRecyclerViewLayoutManager(new LinearLayoutManager(this));
        } else if (selectedCategory.equals("Card View")) {
            setRecyclerViewLayoutManager(new GridLayoutManager(this, 2));
        }
    }

    private void setRecyclerViewLayoutManager(RecyclerView.LayoutManager layoutManager) {
        recyclerView.setLayoutManager(layoutManager);
    }

    private void showSortDialog() {
        String[] sortOptions = {"Newest", "Oldest (Default)", "Commercial",
                "Land", "Residential","Multi Family"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter By:");
        builder.setIcon(R.drawable.sortsortsort);
        builder.setSingleChoiceItems(sortOptions, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    dialog.dismiss();
                    Toast.makeText(WHome.this, "Newest", Toast.LENGTH_SHORT).show();
                    newest();
                } else if (which == 1) {
                    dialog.dismiss();
                    Toast.makeText(WHome.this, "Oldest", Toast.LENGTH_SHORT).show();
                    oldest();
                } else if (which == 2) {
                    dialog.dismiss();
                    Toast.makeText(WHome.this, "Commercial", Toast.LENGTH_SHORT).show();
                    pestControl();
                } else if (which == 3) {
                    dialog.dismiss();
                    Toast.makeText(WHome.this, "Land", Toast.LENGTH_SHORT).show();
                    generalCleaning();
                } else if (which == 4) {
                    dialog.dismiss();
                    Toast.makeText(WHome.this, "Residential", Toast.LENGTH_SHORT).show();
                    mattressCleaning();
                } else if (which == 5) {
                    dialog.dismiss();
                    Toast.makeText(WHome.this, "Multi Family", Toast.LENGTH_SHORT).show();
                    sofaSteam();
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

    private void searchItems() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Requests");


        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(reference.orderByChild("product_name").startAt(searchInput), Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {


                        holder.txtProductName.setText(model.getProduct_location());
                        Picasso.get().load(model.getImage()).resize(300, 150).into(holder.imageView);
                        if (model.getPid() != null) {
                            checkForApplicants(model.getPid(), holder.textOngoing);
                        } else {
                            holder.textOngoing.setVisibility(View.GONE);
                        }
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(WHome.this, ProductDetails.class);
                                intent.putExtra("pid", model.getPid());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings)
//        {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Products products;

        ProductsRef = FirebaseDatabase.getInstance().getReference("Requests");


        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("verified").equalTo("yes"), Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {


                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {


                        productViewHolder.txtProductName.setText(products.getProduct_location());
                        Picasso.get().load(products.getImage()).resize(300, 150).into(productViewHolder.imageView);
                        if (products.getPid() != null) {
                            checkForApplicants(products.getPid(), productViewHolder.textOngoing);
                        } else {
                            productViewHolder.textOngoing.setVisibility(View.GONE);
                        }
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(WHome.this, ProductDetails.class);
                                intent.putExtra("userId", products.getUserId());
                                intent.putExtra("pid", products.getPid());
                                startActivity(intent);

                            }
                        });
                    }


                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    //

    public void onBackPressed(){
        Intent backs = new Intent(this, WHome.class);
        startActivity(backs);

    }
    public void  pestControl() {


        ProductsRef = FirebaseDatabase.getInstance().getReference("Requests");


        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("category").equalTo("verifiedpestControl"), Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtProductName.setText(products.getProduct_location());
                        Picasso.get().load(products.getImage()).resize(300, 150).into(productViewHolder.imageView);
                        if (products.getPid() != null) {
                            checkForApplicants(products.getPid(), productViewHolder.textOngoing);
                        } else {
                            productViewHolder.textOngoing.setVisibility(View.GONE);
                        }
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(WHome.this, ProductDetails.class);
                                intent.putExtra("userId", products.getUserId());
                                intent.putExtra("pid", products.getPid());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WHome.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    public void generalCleaning() {


        ProductsRef = FirebaseDatabase.getInstance().getReference("Requests");


        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("category").equalTo("verifiedgeneralCleaning"), Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtProductName.setText(products.getProduct_location());
                        Picasso.get().load(products.getImage()).resize(300, 150).into(productViewHolder.imageView);
                        if (products.getPid() != null) {
                            checkForApplicants(products.getPid(), productViewHolder.textOngoing);
                        } else {
                            productViewHolder.textOngoing.setVisibility(View.GONE);
                        }
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(WHome.this, ProductDetails.class);
                                intent.putExtra("userId", products.getUserId());
                                intent.putExtra("pid", products.getPid());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WHome.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void sofaSteam() {


        ProductsRef = FirebaseDatabase.getInstance().getReference("Requests");


        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("category").equalTo("verifiedsofaSteam"), Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {

                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtProductName.setText(products.getProduct_location());
                        Picasso.get().load(products.getImage()).resize(300, 150).into(productViewHolder.imageView);
                        if (products.getPid() != null) {
                            checkForApplicants(products.getPid(), productViewHolder.textOngoing);
                        } else {
                            productViewHolder.textOngoing.setVisibility(View.GONE);
                        }
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(WHome.this, ProductDetails.class);
                                intent.putExtra("userId", products.getUserId());
                                intent.putExtra("pid", products.getPid());
                                startActivity(intent);
                            }   
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WHome.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    public void mattressCleaning() {


        ProductsRef = FirebaseDatabase.getInstance().getReference("Requests");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("category").equalTo("verifiedmattressCleaning"), Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtProductName.setText(products.getProduct_location());
                        Picasso.get().load(products.getImage()).resize(300, 150).into(productViewHolder.imageView);

                        if (products.getPid() != null) {
                            checkForApplicants(products.getPid(), productViewHolder.textOngoing);
                        } else {
                            productViewHolder.textOngoing.setVisibility(View.GONE);
                        }
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(WHome.this, ProductDetails.class);
                                intent.putExtra("userId", products.getUserId());
                                intent.putExtra("pid", products.getPid());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WHome.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

    }


    public void newest() {


        ProductsRef = FirebaseDatabase.getInstance().getReference("Requests");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("verified").equalTo("yes"), Products.class).build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtProductName.setText(products.getProduct_location());
                        Picasso.get().load(products.getImage()).resize(300, 150).into(productViewHolder.imageView);
                        if (products.getPid() != null) {
                            checkForApplicants(products.getPid(), productViewHolder.textOngoing);
                        } else {
                            productViewHolder.textOngoing.setVisibility(View.GONE);
                        }
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(WHome.this, ProductDetails.class);
                                intent.putExtra("userId", products.getUserId());
                                intent.putExtra("pid", products.getPid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WHome.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void oldest() {


        ProductsRef = FirebaseDatabase.getInstance().getReference("Requests");

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("verified").equalTo("yes"), Products.class).build();
        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull Products products) {
                        productViewHolder.txtProductName.setText(products.getProduct_location());
                        Picasso.get().load(products.getImage()).resize(300, 150).into(productViewHolder.imageView);
                        if (products.getPid() != null) {
                            checkForApplicants(products.getPid(), productViewHolder.textOngoing);
                        } else {
                            productViewHolder.textOngoing.setVisibility(View.GONE);
                        }
                        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent intent = new Intent(WHome.this, ProductDetails.class);
                                intent.putExtra("userId", products.getUserId());
                                intent.putExtra("pid", products.getPid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);

                        holder = new ProductViewHolder(view);
                        return holder;
                    }
                };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(WHome.this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);


    }
    private void checkForApplicants(String productId, TextView textOngoing) {
        DatabaseReference applicantsRef = FirebaseDatabase.getInstance().getReference().child("Applicants");
        applicantsRef.orderByChild("requestId").equalTo(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    textOngoing.setVisibility(View.VISIBLE);
                } else {
                    textOngoing.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors or fallback logic here
            }
        });
    }
}

