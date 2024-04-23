package com.example.propbid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.propbid.ADMIN.ADMIN;
import com.example.propbid.menu.WHome;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkerLogIn extends AppCompatActivity {

    private Button loginButton, clientButton;
    private EditText emailEditText, passwordEditText;
    private TextView register;
    private ImageButton arrowBack;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog loadBar;

    FirebaseAuth Auth;
    FirebaseUser User, SellerAcc;
    // ads
    private AdView mAdView;

    private String parentDbName = "Workers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_login_panel);
        // Banner Ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        arrowBack = (ImageButton) findViewById(R.id.arrowback_message);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkerLogIn.this, logInOption.class);
                startActivity(intent);
            }
        });

        // Initialize firebase
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        SellerAcc = Auth.getCurrentUser();
        // Progress Dialog
        loadBar = new ProgressDialog(this);
        // Button
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        // EditText
        emailEditText = (EditText) findViewById(R.id.input_username);
        passwordEditText = (EditText) findViewById(R.id.input_password);
        // TextViews

        register = (TextView) findViewById(R.id.CreateAccountTextView);
        // Register Panel On click Listener
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkerLogIn.this, workerRegistration.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String pass = passwordEditText.getText().toString();

        DatabaseReference clientRef = FirebaseDatabase.getInstance().getReference("Client");
        clientRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // This email exists in the "Client" database
                    Toast.makeText(WorkerLogIn.this, "This account is registered as a Seller.", Toast.LENGTH_SHORT).show();
                } else {
                    // Continue with the login process
                    if (!email.matches(emailPattern)) {
                        emailEditText.setError("Enter the correct E-mail!");
                    } else if (pass.isEmpty() || pass.length() < 6) {
                        passwordEditText.setError("Invalid Password!");
                    } else {
                        // Create and show a ProgressDialog
                        ProgressDialog loadingDialog = new ProgressDialog(WorkerLogIn.this);
                        loadingDialog.setTitle("Logging In...");
                        loadingDialog.setMessage("Please wait, we are currently checking your credentials.");
                        loadingDialog.setCanceledOnTouchOutside(false);
                        loadingDialog.show();

                        Auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User = FirebaseAuth.getInstance().getCurrentUser();
                                    if (User.isEmailVerified()) {
                                        // Check if the account is verified by an admin
                                        DatabaseReference isVerifiedAdmin = FirebaseDatabase.getInstance().getReference("Workers").child(User.getUid()).child("workerNotVerified");
                                        isVerifiedAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                loadingDialog.dismiss();
                                                if (!snapshot.exists()) {
                                                    Intent intent = new Intent(WorkerLogIn.this, WHome.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(WorkerLogIn.this, "Please wait for an admin to verify your account.", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    } else {
                                        User.sendEmailVerification();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(WorkerLogIn.this);
                                        builder.setTitle("E-mail Verification")
                                                .setMessage("Check your email to verify your account before logging in.")
                                                .setCancelable(true)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    }
                                } else {
                                    loadingDialog.dismiss();
                                    Toast.makeText(WorkerLogIn.this, "E-mail / Password is incorrect!", Toast.LENGTH_SHORT).show();
                                }
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
}
