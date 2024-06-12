package com.rhu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }
    MaterialButton btnGoToTelemedicine, btnGoToAppointment, btnGoToAuthentication;
    ConstraintLayout clMedicalServices, clTelemedicine, clAppointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeFirebase();
        checkPreviousLoggedSession();
        initializeViews();
        handleUserInteraction();
    }

    private void checkPreviousLoggedSession() {
        if (USER != null) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    private void handleUserInteraction() {
        btnGoToTelemedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clMedicalServices.setVisibility(View.GONE);
                clTelemedicine.setVisibility(View.VISIBLE);
            }
        });

        btnGoToAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clTelemedicine.setVisibility(View.GONE);
                clAppointment.setVisibility(View.VISIBLE);
            }
        });

        btnGoToAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void initializeViews() {
        btnGoToTelemedicine = findViewById(R.id.btnGoToTelemedicine);
        btnGoToAppointment = findViewById(R.id.btnGoToAppointment);
        btnGoToAuthentication = findViewById(R.id.btnGoToAuthentication);
        clMedicalServices = findViewById(R.id.clMedicalServices);
        clTelemedicine = findViewById(R.id.clTelemedicine);
        clAppointment = findViewById(R.id.clAppointment);
    }
}