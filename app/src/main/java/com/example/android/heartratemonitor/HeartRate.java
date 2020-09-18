package com.example.android.heartratemonitor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HeartRate extends AppCompatActivity {
    private Button mConnect;
    private Button mDisconnect;

    private BluetoothAdapter bluetoothAdapter;
    private static final int RC_BLUETOOTH = 100;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == RC_BLUETOOTH) {
            if (resultCode == RC_BLUETOOTH) {
                Toast.makeText(this, "Bluetooth Turned On", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        setupViews();

        firebaseAuth = FirebaseAuth.getInstance();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, RC_BLUETOOTH);
                } else {
                    Toast.makeText(HeartRate.this, "Already On", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HeartRate.this, "Disconnected", Toast.LENGTH_SHORT).show();
                bluetoothAdapter.disable();
            }
        });
    }
    private void setupViews() {
        mConnect = findViewById(R.id.device_connect);
        mDisconnect = findViewById(R.id.device_disconnect);

    }

    private void logout() {

        firebaseAuth.signOut();

        finish();

        startActivity(new Intent(HeartRate.this, MainActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.heart_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_history:
                startActivity(new Intent(HeartRate.this, History.class));
                break;
            case R.id.m_edit_profile:
                startActivity(new Intent(HeartRate.this, EditProfile.class));
                break;
            case R.id.m_sign_out:
                logout();
                break;


        }
        return super.onOptionsItemSelected(item);

    }
}
