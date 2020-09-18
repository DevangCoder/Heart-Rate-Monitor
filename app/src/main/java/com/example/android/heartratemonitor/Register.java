package com.example.android.heartratemonitor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Register extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 1;


    // firebase instance
    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mAge;
    private EditText mWeight;
    private EditText mHeight;
    private EditText mEmergencyContact;
    private ImageView mPrescription;
    private Button mRegister;
    private TextView mLogin;


    private String name;
    private String email;
    private String password;
    private String age;
    private String weight;
    private String height;
    private String emergencyContact;
    private Uri prescUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            prescUri = data.getData();

            if (prescUri != null) {
                Glide.with(mPrescription.getContext()).load(prescUri).into(mPrescription);
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //linking views with layout file
        setupViews();

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseStorage = FirebaseStorage.getInstance();


        storageReference = firebaseStorage.getReference();

        //login text view click
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                startActivity(new Intent(Register.this, MainActivity.class));

                finish();
            }
        });

        //image view click
        mPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                intent.setType("image/*");

                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

                startActivityForResult(Intent.createChooser(intent, "Select Image"),
                        RC_PHOTO_PICKER);

            }
        });

        // register button click
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("signing in");
                progressDialog.show();

                /**check user has entered details */
                if (validate()) {
                    String user_email = mEmail.getText().toString().trim();
                    String user_password = mPassword.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        //verify user by sending email verification and add the data to database
                                        sendEmailVerification();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(Register.this, "Registration Unsuccessful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void setupViews() {
        mName = findViewById(R.id.et_user_name);
        mEmail = findViewById(R.id.et_user_email);
        mPassword = findViewById(R.id.et_user_password);
        mAge = findViewById(R.id.et_user_age);
        mWeight = findViewById(R.id.et_user_weight);
        mHeight = findViewById(R.id.et_user_height);
        mEmergencyContact = findViewById(R.id.et_user_emergency_contact);
        mPrescription = findViewById(R.id.iv_prescription);
        mRegister = findViewById(R.id.bt_register);
        mLogin = findViewById(R.id.tv_login);
        progressDialog = new ProgressDialog(this);
    }

    private boolean validate() {
        name = mName.getText().toString();
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        age = mAge.getText().toString();
        weight = mWeight.getText().toString();
        height = mHeight.getText().toString();
        emergencyContact = mEmergencyContact.getText().toString();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || age.isEmpty() ||
                weight.isEmpty() || height.isEmpty() || emergencyContact.isEmpty() ||
                prescUri == null) {
            Toast.makeText(this, "Please Enter all the details", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void sendEmailVerification() {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //send data to database
                        sendData();
                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                    } else {
                        Toast.makeText(Register.this, "Verification mail has not been sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendData() {
        StorageReference prescriptionReference = storageReference.child(firebaseAuth.getUid()).child("prescription");

        prescriptionReference.putFile(prescUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Register.this, "Upload Successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Register.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        name = mName.getText().toString();
        age = mAge.getText().toString();
        weight = mWeight.getText().toString();
        height = mHeight.getText().toString();
        emergencyContact = mEmergencyContact.getText().toString();
        User user = new User(name, Integer.parseInt(age), weight,
                height, emergencyContact);

        myRef.setValue(user);


    }
}
