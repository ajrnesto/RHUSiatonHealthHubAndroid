package com.rhu.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rhu.R;

import java.util.HashMap;

public class FormHealthRecordFragment extends Fragment {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }
    View view;
    AutoCompleteTextView menuTypeOfRecord;
    TextInputEditText etDescription;
    MaterialButton btnAddRecord;
    // Spinner items
    String[] itemsRecordType;
    ArrayAdapter<String> adapterRecordType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_form_health_record, container, false);

        initializeFirebase();
        initializeViews();
        initializeSpinners();
        handleUserInteraction();

        return view;
    }

    private void initializeViews() {
        menuTypeOfRecord = view.findViewById(R.id.menuTypeOfRecord);
        etDescription = view.findViewById(R.id.etDescription);
        btnAddRecord = view.findViewById(R.id.btnAddRecord);
    }

    private void initializeSpinners() {
        // hour type
        itemsRecordType = new String[]{
                "Family Health History",
                "Social/Lifestyle Record",
                "Immunization Record",
                "Laboratory and Diagnostics Record",
                "Hospitalization Record"
        };

        adapterRecordType = new ArrayAdapter<>(requireContext(), R.layout.list_item, itemsRecordType);
        menuTypeOfRecord.setAdapter(adapterRecordType);
    }

    private void handleUserInteraction() {
        btnAddRecord.setOnClickListener(view -> {
            btnAddRecord.setEnabled(false);

            DocumentReference refNewHealthRecord = DB.collection("healthRecords").document();

            HashMap<String, Object> record = new HashMap<>();
            record.put("id", refNewHealthRecord.getId());
            record.put("typeOfRecord", menuTypeOfRecord.getText().toString());
            record.put("description", etDescription.getText().toString());
            record.put("userUid", AUTH.getCurrentUser().getUid());
            record.put("timestamp", System.currentTimeMillis());

            refNewHealthRecord.set(record).addOnCompleteListener(task -> requireActivity().onBackPressed());
        });
    }
}