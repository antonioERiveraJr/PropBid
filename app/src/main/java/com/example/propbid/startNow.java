package com.example.propbid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class startNow extends AppCompatActivity {

    private TextView startNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_now);
        startNow=(TextView)findViewById(R.id.startNowButton);
        startNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(startNow.this,logInOption.class);
                startActivity(intent);
            }
        });
    }
}