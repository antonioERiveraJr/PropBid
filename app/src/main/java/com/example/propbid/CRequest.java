package com.example.propbid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.propbid.Model.Client;
import com.example.propbid.menu.CHome;
import com.example.propbid.menu.CNotification;
import com.example.propbid.menu.CTransaction;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;


public class CRequest extends AppCompatActivity {

    private MapView mapView;
    private ImageButton arrowBack;
    private ImageView addProductPicture, addProductPicture2, addProductPicture3, addProductPicture4, addProductPicture5;
    private Boolean approved = false;

    private Boolean hasWorker = false;
    private String detailss;
    private EditText details;
    private Button addProductBTN;
    private EditText productName, productPrice, producDetails, productLocation, ListerPhoneNumber, listerShop;

    private String listers_shopName, saveCurrentDate, saveCurrentTime, product_name,
            product_price, product_details, product_location, product_number, productCategory, approvedOrNot , requestDatetobedone,requester, request_worker1,request_worker;

    private String hasWorkerLoad = "waiting for worker";
    private static final int GalleryPick = 1;
    private Uri ImageUri, ImageUri2, ImageUri3, ImageUri4, ImageUri5;
    private String productRandomKey, downloadImageUrl, downloadImageUrl2, downloadImageUrl3, downloadImageUrl4, downloadImageUrl5;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef, databaseReference;
    private ProgressDialog loadingBar;
    private int limitPrice;

    private EditText requestDate;

    final Calendar myCalendar = Calendar.getInstance();
    private FirebaseUser User;
    private FirebaseAuth Auth;

    //  String[] category = {"Pest Control", "General Cleaning", "Mattress Cleaning", "Sofa Steam"};
    String[] category = {"Commercial", "Land", "Residential", "Multi Family"};
    AutoCompleteTextView categortSelector;
    ArrayAdapter<String> adapterItems;
    String categoryItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crequest);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        details = findViewById(R.id.more_details);
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        arrowBack = (ImageButton) findViewById(R.id.arrowback_SellersAddNewProduct);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( CRequest.this, CHome.class);
                startActivity(intent);
            }
        });

        addProductPicture = (ImageView) findViewById(R.id.select_product_image);
        addProductPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        addProductPicture2 = (ImageView) findViewById(R.id.select_product_image2);
        addProductPicture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery2();
            }
        });
        addProductPicture3 = (ImageView) findViewById(R.id.select_product_image3);
        addProductPicture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery3();
            }
        });
        addProductPicture4 = (ImageView) findViewById(R.id.select_product_image4);
        addProductPicture4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery4();
            }
        });
        addProductPicture5 = (ImageView) findViewById(R.id.select_product_image5);
        addProductPicture5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery5();
            }
        });

        addProductBTN = (Button) findViewById(R.id.add_new_product);
        addProductBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRequest();
            }
        });

        loadingBar = new ProgressDialog(this);
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Request Images");

        categortSelector = findViewById(R.id.itemCategory);
        adapterItems = new ArrayAdapter<>(this, R.layout.categort_list, category);
        categortSelector.setAdapter(adapterItems);

        categortSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                categoryItem = parent.getItemAtPosition(position).toString();
            }
        });

        productName = (EditText) findViewById(R.id.product_name);
        productPrice = (EditText) findViewById(R.id.product_price);
        producDetails = (EditText) findViewById(R.id.product_description);
        productLocation = (EditText) findViewById(R.id.product_location);
        ListerPhoneNumber = (EditText) findViewById(R.id.sellers_phoneNumber);
        listerShop = (EditText) findViewById(R.id.add_shop_name);
        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference("Client").child(User.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Client client = snapshot.getValue(Client.class);
                ListerPhoneNumber.setText(client.getPhoneNumber());
                listerShop.setText(client.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CRequest.this, "Error, please report this bug!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void addRequest() {
        product_name = productName.getText().toString();
        productCategory = categortSelector.getText().toString();
        product_price = productPrice.getText().toString();
        limitPrice = Integer.parseInt(product_price);
        product_details = producDetails.getText().toString();
        product_location = productLocation.getText().toString();
        product_number = ListerPhoneNumber.getText().toString();
        listers_shopName = listerShop.getText().toString();
        detailss = details.getText().toString();

        if (ImageUri == null) {
            Toast.makeText(this, "Product image is needed...", Toast.LENGTH_SHORT).show();
            productPrice.setError("Please input Image");
        } else if (limitPrice < 1) {
            Toast.makeText(this, "Price must be not less than 1...", Toast.LENGTH_SHORT).show();
            productPrice.setError("Price must be not less than 1...");
        } else if (TextUtils.isEmpty(productCategory)) {
            Toast.makeText(this, "Please select category...", Toast.LENGTH_SHORT).show();
            categortSelector.setError("Please select category");
        } else if (TextUtils.isEmpty(product_name)) {
            Toast.makeText(this, "Please input product name...", Toast.LENGTH_SHORT).show();
            productName.setError("Enter Request Name");
        } else if (TextUtils.isEmpty(product_price)) {
            Toast.makeText(this, "Please input product price...", Toast.LENGTH_SHORT).show();
            productPrice.setError("Enter Price");
        } else if (TextUtils.isEmpty(product_details)) {
            Toast.makeText(this, "Please input product details...", Toast.LENGTH_SHORT).show();
            producDetails.setError("Enter Details");
        } else if (TextUtils.isEmpty(product_location)) {
            Toast.makeText(this, "Please input product location...", Toast.LENGTH_SHORT).show();
            productLocation.setError("Enter Location");
        }  else if (  TextUtils.isEmpty(product_price)){
            Toast.makeText(this, "Please input price...", Toast.LENGTH_SHORT).show();
            productLocation.setError("Enter Price");

        }else {
            StoreProductInformation();
        }
    }



    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        requestDate.setText(dateFormat.format(myCalendar.getTime()));
    }


    private void StoreProductInformation() {
        loadingBar.setTitle("Adding New Request");
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(CRequest.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                /*Toast.makeText(AddNewItem.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();*/
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();
                            SaveProductInfoToDatabase();

                        }

                    }
                });
            }
        });

        //2

        if(ImageUri2!=null){
            final StorageReference filePath2 = ProductImagesRef.child(ImageUri2.getLastPathSegment() + productRandomKey + ".jpg");
            final UploadTask uploadTask2 = filePath2.putFile(ImageUri2);

            uploadTask2.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(CRequest.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    /*Toast.makeText(AddNewItem.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();*/
                    Task<Uri> urlTask2 = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            downloadImageUrl2 = filePath2.getDownloadUrl().toString();
                            return filePath2.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadImageUrl2 = task.getResult().toString();
                                SaveProductInfoToDatabase();

                            }

                        }
                    });
                }
            });
        }

        //3

        if(ImageUri3!=null){
            final StorageReference filePath3 = ProductImagesRef.child(ImageUri3.getLastPathSegment() + productRandomKey + ".jpg");
            final UploadTask uploadTask3 = filePath3.putFile(ImageUri3);

            uploadTask3.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(CRequest.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    /*Toast.makeText(AddNewItem.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();*/
                    Task<Uri> urlTask3 = uploadTask3.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            downloadImageUrl3 = filePath3.getDownloadUrl().toString();
                            return filePath3.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadImageUrl3 = task.getResult().toString();
                                SaveProductInfoToDatabase();

                            }

                        }
                    });
                }
            });
        }

        //4

        if(ImageUri4!=null){
            final StorageReference filePath4 = ProductImagesRef.child(ImageUri4.getLastPathSegment() + productRandomKey + ".jpg");
            final UploadTask uploadTask4 = filePath4.putFile(ImageUri4);

            uploadTask4.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(CRequest.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    /*Toast.makeText(AddNewItem.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();*/
                    Task<Uri> urlTask4 = uploadTask4.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            downloadImageUrl4 = filePath4.getDownloadUrl().toString();
                            return filePath4.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadImageUrl4 = task.getResult().toString();
                                SaveProductInfoToDatabase();

                            }

                        }
                    });
                }
            });
        }

        //5

        if(ImageUri5!=null){
            final StorageReference filePath5 = ProductImagesRef.child(ImageUri5.getLastPathSegment() + productRandomKey + ".jpg");
            final UploadTask uploadTask5 = filePath5.putFile(ImageUri5);

            uploadTask5.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(CRequest.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    /*Toast.makeText(AddNewItem.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();*/
                    Task<Uri> urlTask5 = uploadTask5.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            downloadImageUrl5 = filePath5.getDownloadUrl().toString();
                            return filePath5.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadImageUrl5 = task.getResult().toString();
                                SaveProductInfoToDatabase();

                            }

                        }
                    });
                }
            });
        }

    }

    private void SaveProductInfoToDatabase() {



        //admin
        if (!approved) {
            approvedOrNot = "waiting for approval";
        } else if (approved) {
            approvedOrNot = "approved";
        }
        //if worker is available
        //  if(!hasWorker){
        //      hasWorkerload = "waiting";
        //  }else if (hasWorker){
        //     hasWorkerload = "worker is available";
        //  }



        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("check if request is not verified", "yes");
        productMap.put("category", productCategory);
        productMap.put("status", hasWorkerLoad);
        productMap.put("remarks", approvedOrNot);
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("worker available", hasWorkerLoad);
        productMap.put("image", downloadImageUrl);
        productMap.put("details", detailss);

        //additional image
        if (downloadImageUrl2 != null) {
            productMap.put("image2", downloadImageUrl2);
        }
        if (downloadImageUrl3 != null) {
            productMap.put("image3", downloadImageUrl3);
        }
        if (downloadImageUrl4 != null) {
            productMap.put("image4", downloadImageUrl4);
        }
        if (downloadImageUrl5 != null) {
            productMap.put("image5", downloadImageUrl5);
        }

        //
        productMap.put("product_name", product_name);
        productMap.put("product_price", product_price);
        productMap.put("product_details", product_details);
        productMap.put("product_location", product_location);
        productMap.put("sellers_contact", product_number);
        productMap.put("requester", listers_shopName);
        productMap.put("userId", User.getUid());
        productMap.put("pending"+User.getUid(),"yes");
        productMap.put("search", product_name.toLowerCase());
        productMap.put("requestdate", requestDatetobedone);
        productMap.put("worker", request_worker);
        productMap.put("worker1", request_worker1);

        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();
        HashMap<String, Object> productMaps = new HashMap<>();
        productMaps.put("Users",User.getUid());
        productMaps.put("Date",saveCurrentDate+" "+saveCurrentTime);
        productMaps.put("Topic",product_name);
        productMaps.put("Action","You've made a request: "+product_name);

        DatabaseReference logActivity = FirebaseDatabase.getInstance().getReference("Log").child(User.getUid()+productRandomKey);
        logActivity.updateChildren(productMaps);


        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        ProductsRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(CRequest.this, CTransaction.class);
                    intent.putExtra("userId", User.getUid());
                    intent.putExtra("pid", productRandomKey);
                    showProductToWorkers();

                    loadingBar.dismiss();
                    Toast.makeText(CRequest.this, "Request has been added!", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(CRequest.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void showProductToWorkers() {

        if (!approved) {
            approvedOrNot = "waiting";
        } else {
            approvedOrNot = "approved";
        }

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("category", productCategory);
        productMap.put("status", hasWorkerLoad);
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("image", downloadImageUrl);
        productMap.put("details", detailss);
        //additional image
        if (downloadImageUrl2 != null) {
            productMap.put("image2", downloadImageUrl2);
        }
        if (downloadImageUrl3 != null) {
            productMap.put("image3", downloadImageUrl3);
        }
        if (downloadImageUrl4 != null) {
            productMap.put("image4", downloadImageUrl4);
        }
        if (downloadImageUrl5 != null) {
            productMap.put("image5", downloadImageUrl5);
        }

        productMap.put("product_name", product_name);
        productMap.put("product_price", product_price);
        productMap.put("product_details", product_details);
        productMap.put("product_location", product_location);
        productMap.put("sellers_contact", product_number);
        productMap.put("lister_shopName", listers_shopName);
        productMap.put("userId", User.getUid());
        productMap.put("search", product_name.toLowerCase());


        Auth = FirebaseAuth.getInstance();
        User = Auth.getCurrentUser();


        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        ProductsRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(CRequest.this, CTransaction.class);
                    Intent toNotify = new Intent(CRequest.this, CNotification.class);
                    toNotify.putExtra("userId", User.getUid());
                    toNotify.putExtra("pid", productRandomKey);
                    intent.putExtra("userId", User.getUid());
                    intent.putExtra("pid", productRandomKey);
                    sendBroadcast(toNotify);
                    startActivity(intent);
                    finish();

                    loadingBar.dismiss();
                    Toast.makeText(CRequest.this, "Request has been added!", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(CRequest.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    } private void openGallery2() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 2);
    } private void openGallery3() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 3);
    } private void openGallery4() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 4);
    } private void openGallery5() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            try {
                Bitmap bitmap = decodeSampledBitmapFromUri(ImageUri, 300, 300); // Adjust the desired width and height
                addProductPicture.setImageBitmap(bitmap);

                if (ImageUri != null) {
                    findViewById(R.id.select_product_image2).setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else if(requestCode == 2 && resultCode == RESULT_OK && data != null) {
            ImageUri2 = data.getData();
            try {
                Bitmap bitmap = decodeSampledBitmapFromUri(ImageUri2, 300, 300); // Adjust the desired width and height
                addProductPicture2.setImageBitmap(bitmap);

                if (ImageUri2 != null) {
                    findViewById(R.id.select_product_image3).setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(requestCode == 3 && resultCode == RESULT_OK && data != null) {
            ImageUri3 = data.getData();
            try {
                Bitmap bitmap = decodeSampledBitmapFromUri(ImageUri3, 300, 300); // Adjust the desired width and height
                addProductPicture3.setImageBitmap(bitmap);

                if (ImageUri3 != null) {
                    findViewById(R.id.select_product_image4).setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(requestCode == 4 && resultCode == RESULT_OK && data != null) {
            ImageUri4 = data.getData();
            addProductPicture4.setImageURI(ImageUri4);
            try {
                Bitmap bitmap = decodeSampledBitmapFromUri(ImageUri4, 300, 300); // Adjust the desired width and height
                addProductPicture4.setImageBitmap(bitmap);

                if (ImageUri4 != null) {
                    findViewById(R.id.select_product_image5).setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(requestCode == 5 && resultCode == RESULT_OK && data != null) {
            ImageUri5 = data.getData();
            try {
                Bitmap bitmap = decodeSampledBitmapFromUri(ImageUri5, 300, 300); // Adjust the desired width and height
                addProductPicture5.setImageBitmap(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }
    private Bitmap decodeSampledBitmapFromUri(Uri uri, int reqWidth, int reqHeight) throws FileNotFoundException {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
    }

    // Calculate the inSampleSize value
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}