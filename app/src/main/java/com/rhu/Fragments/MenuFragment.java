package com.rhu.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rhu.AuthenticationActivity;
import com.rhu.R;

public class MenuFragment extends Fragment {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }
    View view;
    MaterialButton btnHealthRecords, btnMedicalHistory, btnProfile, btnSignOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu, container, false);

        initializeFirebase();
        initializeViews();
        handleUserInteraction();

        return view;
    }

    private void initializeViews() {
        btnHealthRecords = view.findViewById(R.id.btnHealthRecords);
        btnMedicalHistory = view.findViewById(R.id.btnMedicalHistory);
        btnProfile = view.findViewById(R.id.btnProfile);
        btnSignOut = view.findViewById(R.id.btnSignOut);
    }

    private void handleUserInteraction() {
        btnHealthRecords.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment healthRecordsFragment = new HealthRecordsFragment();
            fragmentTransaction.replace(R.id.fragmentHolder, healthRecordsFragment, "HEALTH_RECORDS_FRAGMENT");
            fragmentTransaction.addToBackStack("HEALTH_RECORDS_FRAGMENT");
            fragmentTransaction.commit();
        });

        btnMedicalHistory.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment medicalHistoryFragment = new MedicalHistoryFragment();
            fragmentTransaction.replace(R.id.fragmentHolder, medicalHistoryFragment, "MEDICAL_HISTORY_FRAGMENT");
            fragmentTransaction.addToBackStack("MEDICAL_HISTORY_FRAGMENT");
            fragmentTransaction.commit();
        });

        btnProfile.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment profileFragment = new ProfileFragment();
            fragmentTransaction.replace(R.id.fragmentHolder, profileFragment, "PROFILE_FRAGMENT");
            fragmentTransaction.addToBackStack("PROFILE_FRAGMENT");
            fragmentTransaction.commit();
        });

        btnSignOut.setOnClickListener(view -> {
            AUTH.signOut();
            requireActivity().startActivity(new Intent(requireActivity(), AuthenticationActivity.class));
            requireActivity().finish();
        });
    }
}