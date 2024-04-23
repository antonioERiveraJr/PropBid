package com.example.propbid.ADMIN;

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

import com.example.propbid.Model.Reports;
import com.example.propbid.R;
import com.example.propbid.ViewHolder.ReportViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class adminMessage extends AppCompatActivity {

    private FirebaseUser User;
    private FirebaseAuth Auth;
    private DatabaseReference databaseReference, ProductsRef;
    private ImageButton ArrowBack;
    private String productID = "";
    private String theuserID = "";

    private ImageView arrowBack;
    RecyclerView myitemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_message);

        arrowBack = (ImageButton) findViewById(R.id.arrowback_message);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(adminMessage.this, ADMIN.class);
                startActivity(intent);
            }
        });
        myitemList = (RecyclerView) findViewById(R.id.myitemsrecyclerview);
        myitemList.setHasFixedSize(true);
        myitemList.setLayoutManager(new LinearLayoutManager(adminMessage.this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Reports");


        FirebaseRecyclerOptions<Reports> options = new FirebaseRecyclerOptions.Builder<Reports>()
                .setQuery(ProductsRef.orderByChild("report").equalTo("yes"), Reports.class).build();

        FirebaseRecyclerAdapter<Reports, ReportViewHolder> adapter =
                new FirebaseRecyclerAdapter<Reports, ReportViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ReportViewHolder reportViewHolder, int i, @NonNull Reports reports) {
                        reportViewHolder.ReportSubjects.setText(reports.getReportSubject());
                        reportViewHolder.ReportSender.setText(reports.getUserId());

                        reportViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(adminMessage.this, messageDetails.class);
                                intent.putExtra("reportId", reports.getReportId());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_admin_layout, parent, false);
                        ReportViewHolder holder = new ReportViewHolder(view);
                        return holder;
                    }
                };
        myitemList.setAdapter(adapter);
        adapter.startListening();

    }
}
