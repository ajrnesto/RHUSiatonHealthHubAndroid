package com.rhu.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rhu.Adapters.AppointmentAdapter;
import com.rhu.Adapters.HealthRecordsAdapter;
import com.rhu.Objects.Appointment;
import com.rhu.Objects.HealthRecord;
import com.rhu.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class HealthRecordsFragment extends Fragment implements HealthRecordsAdapter.OnHealthRecordsListener {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }
    View view;
    RecyclerView rvHealthRecords;
    MaterialButton btnAddRecord;
    TextView tvEmpty;

    ArrayList<HealthRecord> arrHealthRecords;
    HealthRecordsAdapter appointmentAdapter;
    HealthRecordsAdapter.OnHealthRecordsListener onHealthRecordsListener = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_health_records, container, false);

        initializeFirebase();
        initializeViews();
        handleUserInteraction();
        loadRecyclerView();

        return view;
    }

    private void handleUserInteraction() {
        btnAddRecord.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Fragment formHealthRecordFragment = new FormHealthRecordFragment();
            fragmentTransaction.replace(R.id.fragmentHolder, formHealthRecordFragment, "HEALTH_RECORD_FORM_FRAGMENT");
            fragmentTransaction.addToBackStack("HEALTH_RECORD_FORM_FRAGMENT");
            fragmentTransaction.commit();
        });
    }

    private void initializeViews() {
        rvHealthRecords = view.findViewById(R.id.rvHealthRecords);
        btnAddRecord = view.findViewById(R.id.btnAddRecord);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void loadRecyclerView() {
        arrHealthRecords = new ArrayList<>();
        rvHealthRecords.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        rvHealthRecords.setLayoutManager(linearLayoutManager);

        CollectionReference refHealthRecords = DB.collection("healthRecords");
        Query qryHealthRecords = refHealthRecords.whereEqualTo("userUid", USER.getUid());

        qryHealthRecords.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                arrHealthRecords.clear();

                if (queryDocumentSnapshots == null) {
                    return;
                }

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    HealthRecord healthRecord = documentSnapshot.toObject(HealthRecord.class);

                    arrHealthRecords.add(healthRecord);
                    appointmentAdapter.notifyDataSetChanged();
                }

                if (arrHealthRecords.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvHealthRecords.setVisibility(View.GONE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                    rvHealthRecords.setVisibility(View.VISIBLE);
                }
            }
        });

        appointmentAdapter = new HealthRecordsAdapter(getContext(), arrHealthRecords, onHealthRecordsListener);
        rvHealthRecords.setAdapter(appointmentAdapter);
        appointmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHealthRecordsClick(int position) {
        
    }
}