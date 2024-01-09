package com.google.android.gms.samples.vision.ocrreader;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DriverClass extends AppCompatActivity {

    EditText et1;
    DatabaseReference myFirebase, myFirebase2;
    TextView tvname, tvnameshow, tvphone, tvphoneShow,logText;
    ProgressDialog progressDialog;
    String myChildphone;
    String myChildname;
    String number;
    int flag = 0;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_class);
        et1 = findViewById(R.id.editTextd);
        tvname = findViewById(R.id.textViewname);
        tvnameshow = findViewById(R.id.textViewnameShoe);
        tvphone = findViewById(R.id.textViewphone);
        tvphoneShow = findViewById(R.id.textViewphoneShow);
        logText = findViewById(R.id.logTextView);
        progressDialog = new ProgressDialog(this);

        Intent intent2 = getIntent();
        String receive = intent2.getStringExtra("5555");
        et1.setText(receive);

        // Initialize Firebase
        myFirebase = FirebaseDatabase.getInstance().getReference();
    }

    public void btn_fetch(View v) {
        number = et1.getText().toString().trim();
        if (number.isEmpty()) {
            et1.setError("Vehicle number missing");
            et1.requestFocus();
            return;
        }
        progressDialog.setMessage("Fetching data");
        progressDialog.show();

        String path = "Registered-users/" + number;
        myFirebase.child(path).child("name").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myChildname = dataSnapshot.getValue(String.class);
                progressDialog.dismiss();
                tvname.setVisibility(View.VISIBLE);
                tvnameshow.setText(myChildname);
                flag = 3;
                if (tvnameshow.getText().length() == 0) {
                    flag = 1;
                    Toast.makeText(DriverClass.this, "Vehicle is not registered", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
                flag = 2;
            }
        });

        myFirebase.child(path).child("phoneNo").addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myChildphone = dataSnapshot.getValue(String.class);
                tvphone.setVisibility(View.VISIBLE);
                tvphoneShow.setText(myChildphone);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                flag = 2;
                Toast.makeText(getApplicationContext(), "Error retrieving value", Toast.LENGTH_SHORT).show();
            }
        });

        myFirebase.child(path).child("logs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder logs = new StringBuilder();

                for (DataSnapshot logSnapshot : dataSnapshot.getChildren()) {
                    String logType = logSnapshot.child("logType").getValue(String.class);
                    Long timestamp = logSnapshot.child("timestamp").getValue(Long.class);

                    if (timestamp != null) {
                        // Format timestamp to display date and time
                        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .format(new Date(timestamp));

                        logs.append(logType).append(" at ").append(dateTime).append("\n");
                    }
                }


                // Display the logs in logsTextView
                logText.setText(logs.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }

    public void btn_send(View v) {
        if (flag == 1) {
            et1.setError("No records found for this");
            et1.requestFocus();
            return;
        }
        if (flag == 2) {
            et1.setError("Please try again");
            et1.requestFocus();
            return;
        }
        if (flag != 3) {
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(myChildphone, null, "Hello" + "\t" + myChildname + "\t" + "your vehicle" + "\t" + number + "\t" + "Crossed the main gate right now", null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(myChildphone, null, "Hello" + "\t" + myChildname + "\t" + "your vehicle" + "\t" + number + "Crossed the main gate right now", null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void btn_home(View v) {
        Intent i = new Intent(DriverClass.this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}
