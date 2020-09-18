package com.example.android.heartratemonitor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditProfile extends AppCompatActivity {

    private static final int RC_UPDATE_IMAGE = 1;


    String name, age, weight, height, emergencyContact;


    private EditText mName;
    private EditText mAge;
    private EditText mWeight;
    private EditText mHeight;
    private EditText mEmergencyContact;
    private ImageView mPrescription;
    private Button mSave;


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Uri prescUri;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == RC_UPDATE_IMAGE && resultCode == RESULT_OK) {
            prescUri = data.getData();
            if (prescUri != null) {
                Glide.with(mPrescription).load(prescUri).into(mPrescription);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setupViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();


        storageReference.child(firebaseAuth.getUid()).child("prescription").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Glide.with(mPrescription.getContext()).load(uri).into(mPrescription);
                    }
                });

        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
//                    Toast.makeText(EditProfile.this, "Inside onDataChange", Toast.LENGTH_SHORT).show();
                    mName.setText(user.getUserName());
                    mAge.setText("" + user.getUserAge());
                    mHeight.setText(user.getUserHeight());
                    mWeight.setText(user.getUserWeight());
                    mEmergencyContact.setText(user.getEmergencyContactNumber());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(EditProfile.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save data to database
                saveData();
                finish();
            }
        });

        mPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_UPDATE_IMAGE);
            }
        });
    }
    private void setupViews() {
        mName = findViewById(R.id.et_update_user_name);
        mAge = findViewById(R.id.et_update_user_age);
        mWeight = findViewById(R.id.et_update_user_weight);
        mHeight = findViewById(R.id.et_update_user_height);
        mEmergencyContact = findViewById(R.id.et_update_user_emergency_contact);
        mPrescription = findViewById(R.id.iv_update_prescription);
        mSave = findViewById(R.id.bt_save);
    }

    void saveData() {
        name = mName.getText().toString();
        age = mAge.getText().toString();
        weight = mWeight.getText().toString();
        height = mHeight.getText().toString();
        emergencyContact = mEmergencyContact.getText().toString();


        StorageReference imageReference = storageReference.child(firebaseAuth.getUid()).child("prescription");
        if (prescUri != null) {
            imageReference.putFile(prescUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(EditProfile.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfile.this, "upload failed", Toast.LENGTH_SHORT).show();
                }

            });
        }
        User user = new User(name, Integer.parseInt(age), weight, height, emergencyContact);

        databaseReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (prescUri == null)
                    Toast.makeText(EditProfile.this, "Information updated", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
