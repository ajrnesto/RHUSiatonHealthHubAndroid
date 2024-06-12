package com.rhu.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rhu.Adapters.MedicalHistoryAdapter;
import com.rhu.Objects.MedicalEntry;
import com.rhu.R;

import java.util.ArrayList;

public class MedicalHistoryFragment extends Fragment implements MedicalHistoryAdapter.OnMedicalHistoryListener {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }
    View view;
    TextView tvEmpty;
    RecyclerView rvMedicalHistory;

    ArrayList<MedicalEntry> arrMedicalHistory;
    MedicalHistoryAdapter appointmentAdapter;
    MedicalHistoryAdapter.OnMedicalHistoryListener onMedicalHistoryListener = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_medical_history, container, false);

        initializeFirebase();
        initializeViews();
        loadRecyclerView();

        return view;
    }

    private void initializeViews() {
        tvEmpty = view.findViewById(R.id.tvEmpty);
        rvMedicalHistory = view.findViewById(R.id.rvMedicalHistory);
    }

    private void loadRecyclerView() {
        arrMedicalHistory = new ArrayList<>();
        rvMedicalHistory.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        rvMedicalHistory.setLayoutManager(linearLayoutManager);

        CollectionReference refMedicalHistory = DB.collection("medicalHistory");
        Query qryMedicalHistory = refMedicalHistory.whereEqualTo("userUid", USER.getUid());

        qryMedicalHistory.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                arrMedicalHistory.clear();

                if (queryDocumentSnapshots == null) {
                    return;
                }

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    MedicalEntry medicalEntry = documentSnapshot.toObject(MedicalEntry.class);

                    arrMedicalHistory.add(medicalEntry);
                    appointmentAdapter.notifyDataSetChanged();
                }

                if (arrMedicalHistory.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvMedicalHistory.setVisibility(View.GONE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                    rvMedicalHistory.setVisibility(View.VISIBLE);
                }
            }
        });

        appointmentAdapter = new MedicalHistoryAdapter(getContext(), arrMedicalHistory, onMedicalHistoryListener);
        rvMedicalHistory.setAdapter(appointmentAdapter);
        appointmentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMedicalHistoryClick(int position) {

    }
}