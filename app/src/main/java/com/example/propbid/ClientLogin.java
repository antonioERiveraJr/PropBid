package com.example.propbid;

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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.propbid.ADMIN.ADMIN;
import com.example.propbid.R;
import com.example.propbid.clientRegistration;
import com.example.propbid.logInOption;
import com.example.propbid.menu.CHome;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClientLogin extends AppCompatActivity {

    private Button loginButton, switchWorker, adminButton;
    private EditText emailEditText, passwordEditText;
    private TextView register;
    private DatabaseReference clients = FirebaseDatabase.getInstance().getReference("Client");
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private ImageButton arrowBack;
    ProgressDialog loadBar;
    FirebaseAuth Auth;
    FirebaseUser User, SellerAcc;
    // ads
    private AdView mAdView;

    private ProgressDialog loadingDialog; // Loading dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_login_panel);

        // Initialize Firebase
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        SellerAcc = Auth.getCurrentUser();

        // Progress Dialog
        loadBar = new ProgressDialog(this);

        // Button
        loginButton = (Button) findViewById(R.id.sellerloginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // EditText
        emailEditText = (EditText) findViewById(R.id.seller_email);
        passwordEditText = (EditText) findViewById(R.id.seller_password);

        // TextViews
        register = (TextView) findViewById(R.id.sellerCreateAccount);

        arrowBack = (ImageButton) findViewById(R.id.arrowback_message);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientLogin.this, logInOption.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientLogin.this, clientRegistration.class);
                startActivity(intent);
            }
        });

        // Initialize the loading dialog
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("Logging In...");
        loadingDialog.setMessage("Please wait, we are currently checking your credentials.");
        loadingDialog.setCanceledOnTouchOutside(false);
    }

    private void loginUser() {
        String email = emailEditText.getText().toString();
        String pass = passwordEditText.getText().toString();

        // Show the loading dialog
        loadingDialog.show();

        // Check if the email is signed up as a worker
        DatabaseReference checkIfWorker = FirebaseDatabase.getInstance().getReference("Workers");
        checkIfWorker.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Dismiss the loading dialog
                    loadingDialog.dismiss();
                    Toast.makeText(ClientLogin.this, "This account is registered as a Worker.", Toast.LENGTH_SHORT).show();
                } else {
                    // Continue with client login
                    if (email.equals("admin") && pass.equals("admin")) {
                        Intent intent = new Intent(ClientLogin.this, ADMIN.class);
                        startActivity(intent);
                    } else if (!email.matches(emailPattern)) {
                        emailEditText.setError("Enter correct E-mail!");
                        // Dismiss the loading dialog
                        loadingDialog.dismiss();
                    } else if (pass.isEmpty() || pass.length() < 6) {
                        passwordEditText.setError("Enter Password!");
                        // Dismiss the loading dialog
                        loadingDialog.dismiss();
                    } else {
                        Auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Dismiss the loading dialog
                                loadingDialog.dismiss();
                                if (task.isSuccessful()) {
                                    User = FirebaseAuth.getInstance().getCurrentUser();
                                    if (User.isEmailVerified()) {
                                        DatabaseReference isVerifiedAdmin = FirebaseDatabase.getInstance().getReference("Client").child(User.getUid()).child("clientNotVerified");
                                        isVerifiedAdmin.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (!snapshot.exists()) {
                                                    Intent intent = new Intent(ClientLogin.this, CHome.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(ClientLogin.this, "Please wait for admin to verify your account..", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else {
                                        User.sendEmailVerification();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ClientLogin.this);
                                        builder.setTitle("E-mail Verification")
                                                .setMessage("Check your email to verify your account before Logging in.")
                                                .setCancelable(true)
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                }).show();
                                    }
                                } else {
                                    Toast.makeText(ClientLogin.this, "E-mail / Password is incorrect!", Toast.LENGTH_SHORT).show();
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