package com.android.onlineshoppingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SettingsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // click on back button
        ivBack = findViewById(R.id.ivBackToProfile);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // click on logout button
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //log out

                // navigate to login activity
                finishAffinity();
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}