package com.example.propbid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.propbid.ADMIN.ADMIN;

public class logInOption extends AppCompatActivity {

    private Button buyerButton, sellerButton, adminButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_option);



        buyerButton = (Button) findViewById(R.id.Buyer);
        buyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logInOption.this, WorkerLogIn.class);
                startActivity(intent);
            }
        });
        sellerButton = (Button) findViewById(R.id.Seller);
        sellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logInOption.this, ClientLogin.class);
                startActivity(intent);
            }
        });
        adminButton = (Button) findViewById(R.id.Admin);
        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(logInOption.this, ADMIN.class);
                startActivity(intent);
            }
        });


    }
}