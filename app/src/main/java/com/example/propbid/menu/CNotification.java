package com.example.propbid.menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.propbid.Model.Notifcation;
import com.example.propbid.R;
import com.example.propbid.ViewHolder.NotificationViewHolder;
import com.example.propbid.ViewYourProductDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class CNotification extends AppCompatActivity {

    private FirebaseUser User;
    private FirebaseAuth Auth;
    private DatabaseReference databaseReference, ProductsRef;
    private String message;
    private ImageView arrowBack;
    private String workersName;
    RecyclerView myitemList;

    private ImageButton homeButtons, messageButtons, transactionButtons, notificationButtons, profileButtons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnotifications);


        homeButtons = (ImageButton) findViewById(R.id.homeButton);
        homeButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CNotification.this, CHome.class);
                startActivity(intent);
            }
        });
        messageButtons = (ImageButton) findViewById(R.id.messageButton);
        messageButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CNotification.this, CMessages.class);
                startActivity(intent);
            }
        });
        transactionButtons = (ImageButton) findViewById(R.id.transactionButton);
        transactionButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CNotification.this, CTransaction.class);

                startActivity(intent);
            }
        });

        profileButtons = (ImageButton) findViewById(R.id.profileButton);
        profileButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CNotification.this, CProfiles.class);
                startActivity(intent);
            }
        });


        myitemList = (RecyclerView) findViewById(R.id.myitemsrecyclerview);
        myitemList.setHasFixedSize(true);
       // myitemList.setLayoutManager(new LinearLayoutManager(CNotification.this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CNotification.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myitemList.setLayoutManager(linearLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Notification");
        DatabaseReference getName = FirebaseDatabase.getInstance().getReference("Workers");
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        FirebaseRecyclerOptions<Notifcation> options = new FirebaseRecyclerOptions.Builder<Notifcation>()
                .setQuery(ProductsRef.orderByChild("To").equalTo(User.getUid()), Notifcation.class).build();

        FirebaseRecyclerAdapter<Notifcation, NotificationViewHolder> adapter =
                new FirebaseRecyclerAdapter<Notifcation, NotificationViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull NotificationViewHolder notificationViewHolder, int i, @NonNull Notifcation notifcation) {

                        if (notifcation.getClicked().equals("no")) {
                            notificationViewHolder.boxColorChanger.setBackgroundResource(R.color.magma_red);
                        }

                        if (notifcation.getFrom().equals("Admin")) {

                            workersName = "Admin";

                            message = workersName + notifcation.getMessage() + "\n" + notifcation.getRequest() + ".\n" + notifcation.getDate();
                            notificationViewHolder.notificationMessage.setText(message);
                            if (Objects.equals(workersName, "Admin")) {
                                notificationViewHolder.profileImage.setImageResource(R.drawable.logo);
                            } else {
                                Picasso.get().load(notifcation.getFromPicture()).into(notificationViewHolder.profileImage);
                            }
                            notificationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("Clicked" + User.getUid(), "yes");
                                    hashMap.put("Clicked", "yes");
                                    ProductsRef.child(notifcation.getDate()).updateChildren(hashMap);


                                    Toast.makeText(CNotification.this, "Date: " + notifcation.getDate(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CNotification.this, ViewYourProductDetails.class);
                                    intent.putExtra("chosenWorker", notifcation.getTo());
                                    intent.putExtra("userId", notifcation.getFrom());
                                    intent.putExtra("pid", notifcation.getId());
                                    intent.putExtra("Date", notifcation.getDate());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            getName.child(notifcation.getFrom()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {


                                    workersName = snapshot.getValue().toString();

                                    message = workersName + notifcation.getMessage() + "\n" + notifcation.getRequest() + ".\n" + notifcation.getDate();
                                    notificationViewHolder.notificationMessage.setText(message);
                                    if (workersName.equals("Admin")) {
                                        notificationViewHolder.profileImage.setImageResource(R.drawable.logo);
                                    } else {
                                        Picasso.get().load(notifcation.getFromPicture()).into(notificationViewHolder.profileImage);
                                    }

                                    notificationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("Clicked" + User.getUid(), "yes");
                                            hashMap.put("Clicked", "yes");
                                            ProductsRef.child(notifcation.getDate()).updateChildren(hashMap);


                                            Toast.makeText(CNotification.this, "Date: " + notifcation.getDate(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(CNotification.this, ViewYourProductDetails.class);
                                            intent.putExtra("chosenWorker", notifcation.getTo());
                                            intent.putExtra("userId", notifcation.getFrom());
                                            intent.putExtra("pid", notifcation.getId());
                                            intent.putExtra("Date", notifcation.getDate());
                                            startActivity(intent);
                                        }
                                    });
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }


                    }

                    @NonNull
                    @Override
                    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifcation_client_layout, parent, false);
                        NotificationViewHolder holder = new NotificationViewHolder(view);
                        return holder;
                    }
                };
        myitemList.setAdapter(adapter);
        adapter.startListening();

    }
}
