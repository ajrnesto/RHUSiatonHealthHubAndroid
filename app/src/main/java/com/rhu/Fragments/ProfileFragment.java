package com.rhu.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rhu.AuthenticationActivity;
import com.rhu.MainActivity;
import com.rhu.R;
import com.rhu.Utils.Utils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    View view;
    ConstraintLayout clLoading;
    TextView tvFullname, tvPatientId, tvEmail;
    TextInputEditText etFirstName, etMiddleName, etLastName, etBirthdate, etAddressPurok, etAddressBarangay, etMobile;
    MaterialButton btnSave, btnLogOut;

    // date picker items
    MaterialDatePicker.Builder<Long> bSchedule;
    MaterialDatePicker<Long> dpSchedule;
    long dpScheduleSelection = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeFirebase();
        initializeViews();
        initializeDatePicker();
        loadUserInformation();
        handleUserInteraction();

        return view;
    }

    private void initializeViews() {
        clLoading = view.findViewById(R.id.clLoading);
        tvFullname = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPatientId = view.findViewById(R.id.tvPatientId);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        etFirstName = view.findViewById(R.id.etFirstName);
        etMiddleName = view.findViewById(R.id.etMiddleName);
        etLastName = view.findViewById(R.id.etLastName);
        etBirthdate = view.findViewById(R.id.etBirthdate);
        etAddressPurok = view.findViewById(R.id.etAddressPurok);
        etAddressBarangay = view.findViewById(R.id.etAddressBarangay);
        etMobile = view.findViewById(R.id.etMobile);
        btnSave = view.findViewById(R.id.btnSave);
    }

    private void loadUserInformation() {
        DB.collection("users").document(USER.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    clLoading.setVisibility(View.GONE);

                    String firstName = snapshot.getString("firstName");
                    String middleName = snapshot.getString("middleName");
                    String lastName = snapshot.getString("lastName");
                    long birthdate = snapshot.getLong("birthdate");
                    String mobile = snapshot.getString("mobile");
                    String addressPurok = snapshot.getString("addressPurok");
                    String addressBarangay = snapshot.getString("addressBarangay");
                    String idNumber = snapshot.getString("idNumber");

                    dpScheduleSelection = birthdate;

                    tvFullname.setText(firstName + " " + lastName);
                    tvEmail.setText(USER.getEmail());
                    tvPatientId.setText("ID: " + idNumber);
                    etFirstName.setText(firstName);
                    etMiddleName.setText(middleName);
                    etLastName.setText(lastName);
                    etBirthdate.setText(new SimpleDateFormat("MMMM dd, yyyy").format(birthdate));
                    etAddressPurok.setText(addressPurok);
                    etAddressBarangay.setText(addressBarangay);
                    etMobile.setText(mobile);
                });
    }

    private void initializeDatePicker() {
        bSchedule = MaterialDatePicker.Builder.datePicker();
        bSchedule.setTitleText("Select Birthdate")
                .setSelection(System.currentTimeMillis());
        dpSchedule = bSchedule.build();
        dpSchedule.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
            dpScheduleSelection = dpSchedule.getSelection();
            etBirthdate.setText(sdf.format(dpScheduleSelection).toUpperCase(Locale.ROOT));
            etBirthdate.setEnabled(true);
        });
        dpSchedule.addOnNegativeButtonClickListener(view -> {
            etBirthdate.setEnabled(true);
        });
        dpSchedule.addOnCancelListener(dialogInterface -> {
            etBirthdate.setEnabled(true);
        });
    }

    private void handleUserInteraction() {
        btnSave.setOnClickListener(view -> validateUserInformationForm());

        btnLogOut.setOnClickListener(view -> signOut());

        etBirthdate.setOnClickListener(view -> {
            etBirthdate.setEnabled(false);
            dpSchedule.show(requireActivity().getSupportFragmentManager(), "SCHEDULE_DATE_PICKER");
        });
    }

    private void signOut() {
        AUTH.signOut();
        Utils.Cache.removeKey(requireContext(), "id_number");
        requireActivity().startActivity(new Intent(requireActivity(), AuthenticationActivity.class));
        requireActivity().finish();
    }

    private void validateUserInformationForm() {
        if (etFirstName.getText().toString().isEmpty() ||
                etMiddleName.getText().toString().isEmpty() ||
                etLastName.getText().toString().isEmpty() ||
                etBirthdate.getText().toString().isEmpty() ||
                etMobile.getText().toString().isEmpty() ||
                etAddressPurok.getText().toString().isEmpty() ||
                etAddressBarangay.getText().toString().isEmpty())
        {
            Toast.makeText(requireContext(), "Please fill out all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = etFirstName.getText().toString().toUpperCase();
        String middleName = etMiddleName.getText().toString().toUpperCase();
        String lastName = etLastName.getText().toString().toUpperCase();
        long birthdate = dpScheduleSelection;
        String mobile = etMobile.getText().toString().toUpperCase();
        String addressPurok = etAddressPurok.getText().toString().toUpperCase();
        String addressBarangay = etAddressBarangay.getText().toString().toUpperCase();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("firstName", firstName);
        userInfo.put("middleName", middleName);
        userInfo.put("lastName", lastName);
        userInfo.put("birthdate", birthdate);
        userInfo.put("mobile", mobile);
        userInfo.put("addressPurok", addressPurok);
        userInfo.put("addressBarangay", addressBarangay);

        DB.collection("users").document(AUTH.getUid())
                .set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(requireActivity(), MainActivity.class));
                        Utils.Cache.setInt(requireActivity(), "user_type", 0);
                        requireActivity().onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireActivity(), "Registration error: "+e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}