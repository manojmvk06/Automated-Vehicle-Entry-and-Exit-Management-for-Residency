package com.google.android.gms.samples.vision.ocrreader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private TextView mTextMessage, tvloginfo, tvinfo, tvfeedback;
    private ImageView iv2, iv3;
    private Button b5, adlogin, buttonFeedback;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // ... (no changes here)
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTextMessage = findViewById(R.id.textView5);
        iv2 = findViewById(R.id.imageView2);
        iv3 = findViewById(R.id.imageView3);
        b5 = findViewById(R.id.button5);
        adlogin = findViewById(R.id.adlogin);
        tvloginfo = findViewById(R.id.tvloginfo);
        buttonFeedback = findViewById(R.id.buttonFeedback);
        tvinfo = findViewById(R.id.tvinfo);
        tvinfo.setVisibility(View.INVISIBLE);
        tvloginfo.setVisibility(View.INVISIBLE);
        buttonFeedback.setVisibility(View.INVISIBLE);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    public void btn_scan(View v) {
        Intent i = new Intent(HomeActivity.this, OcrCaptureActivity.class);
        startActivity(i);
    }
    public  void adlogincall(View v)
    {
        Intent j = new Intent(HomeActivity.this, AdminLogin.class);
        startActivity(j);
    }

    public void btn_contact(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 0);
            }
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:8763964692"));
            startActivity(callIntent);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:8763964692"));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Call faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
    public void btn_feedback(View v)
    {
//        Toast.makeText(this,"Mailing code here",Toast.LENGTH_SHORT).show();
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ "sambitgulu@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        email.putExtra(Intent.EXTRA_TEXT, "write your valuable feedback / suggestions here...");

//need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(1);
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());

    }
}
