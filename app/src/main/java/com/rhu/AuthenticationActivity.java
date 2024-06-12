package com.rhu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rhu.Utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AuthenticationActivity extends AppCompatActivity {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;
    FirebaseStorage STORAGE;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
        STORAGE = FirebaseStorage.getInstance();
    }

    // authentication views
    ConstraintLayout clLogin;
    TextInputEditText etLoginEmail, etLoginPassword;
    MaterialButton btnLogin, btnGotoSignup;

    // registration views
    ConstraintLayout clSignup;
    TextInputEditText etSignupFirstName, etSignupMiddleName, etSignupLastName, etSignupBirthdate, etAddressPurok, etAddressBarangay, etSignupMobile, etIdNumber, etSignupEmail, etSignupPassword;
    RoundedImageView imgId;
    MaterialButton btnUploadId, btnSignup, btnGotoLogin;

    // date picker items
    MaterialDatePicker.Builder<Long> bSchedule;
    MaterialDatePicker<Long> dpSchedule;
    long dpScheduleSelection = 0;

    Uri uriSelected = null;
    ActivityResultLauncher<Intent> activityResultLauncher;
    boolean userHasAccount = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        initializeFirebase();
        checkPreviousLoggedSession();
        initializeViews();
        initializeActivityResultLauncher();
        initializeDatePicker();
        handleUserInteractions();
    }

    private void checkPreviousLoggedSession() {
        if (USER != null) {
            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initializeDatePicker() {
        bSchedule = MaterialDatePicker.Builder.datePicker();
        bSchedule.setTitleText("Select Birthdate")
                .setSelection(System.currentTimeMillis());
        dpSchedule = bSchedule.build();
        dpSchedule.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
            dpScheduleSelection = dpSchedule.getSelection();
            etSignupBirthdate.setText(sdf.format(dpScheduleSelection).toUpperCase(Locale.ROOT));
            etSignupBirthdate.setEnabled(true);
        });
        dpSchedule.addOnNegativeButtonClickListener(view -> {
            etSignupBirthdate.setEnabled(true);
        });
        dpSchedule.addOnCancelListener(dialogInterface -> {
            etSignupBirthdate.setEnabled(true);
        });
    }

    private void handleUserInteractions() {
        btnUploadId.setOnClickListener(view -> {
            selectImageFromDevice();
        });

        btnSignup.setOnClickListener(view -> {
            Utils.hideKeyboard(this);
            validateRegistrationForm();
        });

        btnLogin.setOnClickListener(view -> {
            Utils.hideKeyboard(this);
            validateAuthenticationForm();
        });

        btnGotoSignup.setOnClickListener(view -> {
            clLogin.setVisibility(View.GONE);
            clSignup.setVisibility(View.VISIBLE);
        });

        btnGotoLogin.setOnClickListener(view -> {
            clLogin.setVisibility(View.VISIBLE);
            clSignup.setVisibility(View.GONE);
        });

        etSignupBirthdate.setOnClickListener(view -> {
            etSignupBirthdate.setEnabled(false);
            dpSchedule.show(getSupportFragmentManager(), "SCHEDULE_DATE_PICKER");
        });
    }

    private void validateAuthenticationForm() {
        if (etLoginEmail.getText().toString().isEmpty() ||
                etLoginPassword.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        AUTH.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DB.collection("users").document(task.getResult().getUser().getUid())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot user = task.getResult();
                                            Utils.Cache.setString(AuthenticationActivity.this, "id_number", user.getString("idNumber"));

                                            Toast.makeText(AuthenticationActivity.this, "Signed in as "+email, Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                                            finish();
                                        }
                                        else {
                                            Utils.simpleDialog(AuthenticationActivity.this, "Authentication Error", task.getException().getMessage(), "Try again");
                                        }
                                    }
                                });
                    }
                    else {
                        Utils.simpleDialog(AuthenticationActivity.this, "Authentication Error", task.getException().getMessage(), "Try again");
                        btnLogin.setEnabled(true);
                    }
                });
    }

    private void validateRegistrationForm() {
        if (etSignupFirstName.getText().toString().isEmpty() ||
                etSignupMiddleName.getText().toString().isEmpty() ||
                etSignupLastName.getText().toString().isEmpty() ||
                etSignupBirthdate.getText().toString().isEmpty() ||
                etSignupMobile.getText().toString().isEmpty() ||
                etAddressPurok.getText().toString().isEmpty() ||
                etAddressBarangay.getText().toString().isEmpty() ||
                etIdNumber.getText().toString().isEmpty() ||
                etSignupEmail.getText().toString().isEmpty() ||
                etSignupPassword.getText().toString().isEmpty())
        {
            Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uriSelected == null) {
            Toast.makeText(this, "Please upload a government-issued ID.", Toast.LENGTH_SHORT).show();
            btnSignup.setEnabled(true);
            return;
        }

        String firstName = etSignupFirstName.getText().toString().trim().toUpperCase();
        String middleName = etSignupMiddleName.getText().toString().trim().toUpperCase();
        String lastName = etSignupLastName.getText().toString().trim().toUpperCase();
        long birthdate = dpScheduleSelection;
        String mobile = etSignupMobile.getText().toString().toUpperCase();
        String addressPurok = etAddressPurok.getText().toString().toUpperCase();
        String addressBarangay = etAddressBarangay.getText().toString().toUpperCase();
        String idNumber = etIdNumber.getText().toString().toUpperCase();
        String email = etSignupEmail.getText().toString().trim();
        String password = etSignupPassword.getText().toString().trim();

        if (password.length() < 6) {
            Utils.basicDialog(this, "Please use a password with at least 6 characters.", "Okay");
            return;
        }

        Toast.makeText(this, "Creating your account. Please wait...", Toast.LENGTH_SHORT).show();

        btnSignup.setEnabled(false);

        String idFileName = String.valueOf(System.currentTimeMillis());
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("firstName", firstName);
        userInfo.put("middleName", middleName);
        userInfo.put("lastName", lastName);
        userInfo.put("birthdate", birthdate);
        userInfo.put("mobile", mobile);
        userInfo.put("addressPurok", addressPurok);
        userInfo.put("addressBarangay", addressBarangay);
        userInfo.put("idNumber", idNumber);
        userInfo.put("idFileName", idFileName);
        userInfo.put("email", email);
        userInfo.put("userType", 0);
        userInfo.put("joinDate", System.currentTimeMillis());

        AUTH.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userInfo.put("uid", AUTH.getUid());

                        StorageReference bannerReference = STORAGE.getReference().child("images/"+idFileName);
                        bannerReference.putFile(uriSelected).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Failed to upload ID. Please try again.", Toast.LENGTH_SHORT).show();
                                    clLogin.setVisibility(View.GONE);
                                    clSignup.setVisibility(View.VISIBLE);
                                }
                                else {
                                    DB.collection("users").document(AUTH.getUid())
                                            .set(userInfo)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        SimpleDateFormat sdfScheduleCode = new SimpleDateFormat("yyyyMMdd");

                                                        /*DB.collection("userCount").document(sdfScheduleCode.format(System.currentTimeMillis()))
                                                                .update("count", FieldValue.increment(1))
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                    }
                                                                });*/

                                                        if (task.isSuccessful()) {
                                                            Utils.Cache.setInt(AuthenticationActivity.this, "user_type", 0);
                                                            Utils.Cache.setString(AuthenticationActivity.this, "id_number", idNumber);
                                                            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                                                            finish();
                                                            btnSignup.setEnabled(true);
                                                        }
                                                        else {
                                                            Utils.simpleDialog(AuthenticationActivity.this, "Registration Error", task.getException().getMessage(), "Try again");
                                                            AUTH.signOut();
                                                            btnSignup.setEnabled(true);
                                                        }
                                                    }
                                                    else {
                                                        Utils.simpleDialog(AuthenticationActivity.this, "Registration Error", task.getException().getMessage(), "Try again");
                                                        AUTH.signOut();
                                                        btnSignup.setEnabled(true);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                    }
                    else {
                        Utils.simpleDialog(this, "Registration Error", task.getException().getMessage(), "Try again");
                        AUTH.signOut();
                        btnSignup.setEnabled(true);
                    }
                });
    }

    private void initializeViews() {
        clLogin = findViewById(R.id.clLogin);
        clSignup = findViewById(R.id.clSignup);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGotoSignup = findViewById(R.id.btnGotoSignup);
        etSignupFirstName = findViewById(R.id.etSignupFirstName);
        etSignupMiddleName = findViewById(R.id.etSignupMiddleName);
        etSignupLastName = findViewById(R.id.etSignupLastName);
        etSignupBirthdate = findViewById(R.id.etSignupBirthdate);
        etSignupMobile = findViewById(R.id.etSignupMobile);
        etAddressPurok = findViewById(R.id.etAddressPurok);
        etAddressBarangay = findViewById(R.id.etAddressBarangay);
        etIdNumber = findViewById(R.id.etIdNumber);
        etSignupEmail = findViewById(R.id.etSignupEmail);
        etSignupPassword = findViewById(R.id.etSignupPassword);
        imgId = findViewById(R.id.imgId);
        btnUploadId = findViewById(R.id.btnUploadId);
        btnSignup = findViewById(R.id.btnSignup);
        btnGotoLogin = findViewById(R.id.btnGotoLogin);

        if (getIntent().getBooleanExtra("user_has_account", true)) {
            clLogin.setVisibility(View.VISIBLE);
            clSignup.setVisibility(View.GONE);
        }
        else {
            clLogin.setVisibility(View.GONE);
            clSignup.setVisibility(View.VISIBLE);
        }
    }

    private void selectImageFromDevice() {
        Intent iImageSelect = new Intent();
        iImageSelect.setType("image/*");
        iImageSelect.setAction(Intent.ACTION_GET_CONTENT);

        activityResultLauncher.launch(Intent.createChooser(iImageSelect, "Select ID"));
    }

    private void initializeActivityResultLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uriRetrieved = Objects.requireNonNull(data).getData();
                        uriSelected = uriRetrieved;

                        // display selected image
                        Picasso.get().load(uriRetrieved).resize(800,0).centerCrop().into(imgId);
                    }
                }
        );
    }
}