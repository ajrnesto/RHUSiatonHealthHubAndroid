package com.rhu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rhu.Fragments.AppointmentsFragment;
import com.rhu.Fragments.EmptyFragment;
import com.rhu.Fragments.FormAppointmentFragment;
import com.rhu.Fragments.FormHealthRecordFragment;
import com.rhu.Fragments.HealthRecordsFragment;
import com.rhu.Fragments.MedicalHistoryFragment;
import com.rhu.Fragments.MenuFragment;
import com.rhu.Fragments.PatientFormFragment;
import com.rhu.Fragments.ProfileFragment;
import com.rhu.Fragments.ServicesFragment;
import com.rhu.Utils.Utils;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    BottomNavigationView bottom_navbar;
    TextView tvActivityTitle;
    View actionBarBackground;
    MaterialButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeFirebase();
        initializeViews();
        handleUserInteraction();
        backstackListener();
        softKeyboardListener();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount == 1) { // if navigation is at first backstack entry
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void initializeViews() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        bottom_navbar = findViewById(R.id.bottom_navbar);
        actionBarBackground = findViewById(R.id.actionBarBackground);
        btnBack = findViewById(R.id.btnBack);

        // load up default fragment
        Fragment servicesFragment = new ServicesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentHolder, servicesFragment, "SERVICES_FRAGMENT");
        fragmentTransaction.addToBackStack("SERVICES_FRAGMENT");
        fragmentTransaction.commit();
        tvActivityTitle.setText("RHU Siaton Services");
    }

    private void handleUserInteraction() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        bottom_navbar.setOnItemSelectedListener(item -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (item.getItemId() == R.id.miMenu) {
                Utils.hideKeyboard(this);
                if (USER != null) {
                    if (bottom_navbar.getSelectedItemId() != R.id.miMenu) {
                        Fragment menuFragment = new MenuFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, menuFragment, "MENU_FRAGMENT");
                        fragmentTransaction.addToBackStack("MENU_FRAGMENT");
                        fragmentTransaction.commit();
                    }
                }
                else {
                    Utils.loginRequiredDialog(MainActivity.this, bottom_navbar, "Please sign in to manage your patient form.");
                    bottom_navbar.getMenu().getItem(0).setChecked(true);
                }
            }
            else if (item.getItemId() == R.id.miAppointments) {
                Utils.hideKeyboard(this);
                if (USER != null) {
                    tvActivityTitle.setText("My Appointments");
                    if (bottom_navbar.getSelectedItemId() != R.id.miAppointments) {
                        Fragment appointmentsFragment = new AppointmentsFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, appointmentsFragment, "APPOINTMENTS_FRAGMENT");
                        fragmentTransaction.addToBackStack("APPOINTMENTS_FRAGMENT");
                        fragmentTransaction.commit();
                    }
                }
                else {
                    Utils.loginRequiredDialog(MainActivity.this, bottom_navbar, "Please sign in to schedule appointments.");
                    bottom_navbar.getMenu().getItem(0).setChecked(true);
                }
            }
            else if (item.getItemId() == R.id.miServices) {
                tvActivityTitle.setText("RHU Siaton Services");
                Utils.hideKeyboard(this);
                if (bottom_navbar.getSelectedItemId() != R.id.miServices) {
                    Fragment servicesFragment = new ServicesFragment();
                    fragmentTransaction.replace(R.id.fragmentHolder, servicesFragment, "SERVICES_FRAGMENT");
                    fragmentTransaction.addToBackStack("SERVICES_FRAGMENT");
                    fragmentTransaction.commit();
                }
            }
            /*else {
                Utils.hideKeyboard(this);
                if (bottom_navbar.getSelectedItemId() != R.id.miIncidentReport) {
                    Fragment incidentReportFragment = new IncidentReportFragment();
                    fragmentTransaction.replace(R.id.fragmentHolder, incidentReportFragment, "INCIDENT_REPORT_FRAGMENT");
                    fragmentTransaction.addToBackStack("INCIDENT_REPORT_FRAGMENT");
                    fragmentTransaction.commit();
                }
            }*/
            return true;
        });
    }

    private void backstackListener() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(() -> {
            ServicesFragment servicesFragment = (ServicesFragment) getSupportFragmentManager().findFragmentByTag("SERVICES_FRAGMENT");
            ProfileFragment profileFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("PROFILE_FRAGMENT");
            FormAppointmentFragment formAppointmentFragment = (FormAppointmentFragment) getSupportFragmentManager().findFragmentByTag("APPOINTMENT_FORM_FRAGMENT");
            PatientFormFragment patientFormFragment = (PatientFormFragment) getSupportFragmentManager().findFragmentByTag("PATIENT_FORM_FRAGMENT");
            MenuFragment menuFragment = (MenuFragment) getSupportFragmentManager().findFragmentByTag("MENU_FRAGMENT");
            HealthRecordsFragment healthRecordsFragment = (HealthRecordsFragment) getSupportFragmentManager().findFragmentByTag("HEALTH_RECORDS_FRAGMENT");
            FormHealthRecordFragment formHealthRecordFragment = (FormHealthRecordFragment) getSupportFragmentManager().findFragmentByTag("HEALTH_RECORD_FORM_FRAGMENT");
            MedicalHistoryFragment medicalHistoryFragment = (MedicalHistoryFragment) getSupportFragmentManager().findFragmentByTag("MEDICAL_HISTORY_FRAGMENT");

            if (menuFragment != null && menuFragment.isVisible()) {
                tvActivityTitle.setVisibility(View.GONE);
                actionBarBackground.setVisibility(View.GONE);
            }
            else {
                tvActivityTitle.setVisibility(View.VISIBLE);
                actionBarBackground.setVisibility(View.VISIBLE);
            }

            if (servicesFragment != null && servicesFragment.isVisible()) {
                tvActivityTitle.setText("RHU Siaton Services");
            }
            else if (profileFragment != null && profileFragment.isVisible()) {
                tvActivityTitle.setText("My Profile");
            }
            else if (formAppointmentFragment != null && formAppointmentFragment.isVisible()) {
                tvActivityTitle.setText("Book Your Appointment");
            }
            else if (patientFormFragment != null && patientFormFragment.isVisible()) {
                tvActivityTitle.setText("Review Patient Form");
            }
            else if (healthRecordsFragment != null && healthRecordsFragment.isVisible()) {
                tvActivityTitle.setText("My Health Records");
            }
            else if (formHealthRecordFragment != null && formHealthRecordFragment.isVisible()) {
                tvActivityTitle.setText("Add New Health Record");
            }
            else if (medicalHistoryFragment != null && medicalHistoryFragment.isVisible()) {
                tvActivityTitle.setText("My Medical History");
            }
        });
    }

    private void softKeyboardListener() {
        getWindow().getDecorView().setOnApplyWindowInsetsListener((view, windowInsets) -> {
            WindowInsetsCompat insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(windowInsets, view);
            if (insetsCompat.isVisible(WindowInsetsCompat.Type.ime())) {
                bottom_navbar.setVisibility(View.GONE);
            }
            else {
                bottom_navbar.setVisibility(View.VISIBLE);
            }
            return windowInsets;
        });
    }
}