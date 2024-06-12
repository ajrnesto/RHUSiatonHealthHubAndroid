package com.rhu.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rhu.Adapters.DoctorLocationAdapter;
import com.rhu.Objects.MedicalEntry;
import com.rhu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ServicesFragment extends Fragment implements DoctorLocationAdapter.OnDoctorLocationListener {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }
    View view;
    ScrollView scrollView;

    // general services
    MaterialCardView cvConsultation, cvFamilyPlanning, cvXray, cvLaborAndDelivery, cvTBDOTS, cvCircumcision, cvMedicalCertificate, cvSupplementation, cvVaccination, cvWoundCare, cvSputumTest;
    // laboratory services
    MaterialCardView cvLaboratory, cvLaboratoryContainer, cvHemoglobin, cvUrinalysis, cvFastingBloodSugar, cvHepatitisBloodTest, cvHIVScreening;
    // immunization services
    MaterialCardView cvImmunization, cvImmunizationContainer, cvNewBornCare, cvNewBornScreening;
    MaterialButton btnNext, btnPrev;

    RecyclerView rvDoctorLocation;

    ArrayList<String> arrDoctorLocation;
    ArrayList<Long> arrTimestamps;
    DoctorLocationAdapter doctorLocationAdapter;
    DoctorLocationAdapter.OnDoctorLocationListener onDoctorLocationListener = this;
    int rvScrollCounter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_services, container, false);

        initializeFirebase();
        initializeViews();
        handleUserInteraction();
        loadDoctorLocation();

        return view;
    }

    private void loadDoctorLocation() {
        arrDoctorLocation = new ArrayList<>();
        arrTimestamps = new ArrayList<>();
        rvDoctorLocation.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        rvDoctorLocation.setLayoutManager(linearLayoutManager);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvDoctorLocation);

        Query qryDoctorLocation = DB.collection("doctorLocation");

        qryDoctorLocation.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapDoctorLocations = task.getResult();

                            if (!snapDoctorLocations.isEmpty()) {
                                int counter = 0;

                                for (DocumentSnapshot snapDoctorLocation : snapDoctorLocations) {
                                    counter++;
                                    String doctorLocation = snapDoctorLocation.getString("location");
                                    long timestamp = snapDoctorLocation.getLong("timestamp");

                                    Calendar cal = Calendar.getInstance();
                                    cal.set(Calendar.HOUR_OF_DAY, 0);
                                    cal.set(Calendar.MINUTE, 0);
                                    cal.set(Calendar.SECOND, 0);
                                    cal.set(Calendar.MILLISECOND, 0);

                                    Log.d("DEBUG", "Comparing " + timestamp + " and " + cal.getTimeInMillis());

                                    if (timestamp == cal.getTimeInMillis()) {
                                        if (counter > 0) {
                                            rvScrollCounter = counter - 1;
                                            btnNext.setEnabled(true);
                                            btnPrev.setEnabled(true);
                                        }
                                        else {
                                            rvScrollCounter = 0;
                                            btnNext.setEnabled(false);
                                            btnPrev.setEnabled(false);
                                        }
                                        rvDoctorLocation.scrollToPosition(rvScrollCounter);
                                        Log.d("DEBUG", "Location is " + doctorLocation);
                                    }

                                    arrDoctorLocation.add(doctorLocation);
                                    arrTimestamps.add(timestamp);
                                    doctorLocationAdapter.notifyDataSetChanged();
                                }
                            }
                            else {
                                arrDoctorLocation.add("Not Set");
                                arrTimestamps.add(System.currentTimeMillis());
                                doctorLocationAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        doctorLocationAdapter = new DoctorLocationAdapter(getContext(), arrDoctorLocation, arrTimestamps, onDoctorLocationListener);
        rvDoctorLocation.setAdapter(doctorLocationAdapter);
        doctorLocationAdapter.notifyDataSetChanged();
    }

    private void handleUserInteraction() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        rvDoctorLocation.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dx != 0) {
                    rvScrollCounter = ((LinearLayoutManager)rvDoctorLocation.getLayoutManager()).findFirstVisibleItemPosition();
                    if (rvScrollCounter == rvDoctorLocation.getAdapter().getItemCount() - 1) {
                        btnNext.setEnabled(false);
                    }
                    else {
                        btnNext.setEnabled(true);
                    }
                    if (rvScrollCounter == 0) {
                        btnPrev.setEnabled(false);
                    }
                    else {
                        btnPrev.setEnabled(true);
                    }
                }
            }
        });

        btnNext.setOnClickListener(view -> {
            rvScrollCounter++;
            rvDoctorLocation.smoothScrollToPosition(rvScrollCounter);

            if (rvScrollCounter == rvDoctorLocation.getAdapter().getItemCount() - 1) {
                btnNext.setEnabled(false);
            }
            if (rvScrollCounter != 0) {
                btnPrev.setEnabled(true);
            }
        });

        btnPrev.setOnClickListener(view -> {
            rvScrollCounter--;
            rvDoctorLocation.smoothScrollToPosition(rvScrollCounter);

            if (rvScrollCounter == 0) {
                btnPrev.setEnabled(false);
            }
            if (rvScrollCounter != rvDoctorLocation.getAdapter().getItemCount() - 1) {
                btnNext.setEnabled(true);
            }
        });

        // general services
        cvConsultation.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Medical Consultation");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvFamilyPlanning.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Family Planning and Well-Being");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvXray.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Radiology Imaging (X-Ray)");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvLaborAndDelivery.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Labor and Delivery of Normal Pregnancy");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvTBDOTS.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "TB-DOTS Treatment Program");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvCircumcision.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Circumcision (Tuli)");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvMedicalCertificate.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Medical Certificate Issuance");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvSupplementation.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Micronutrient Supplementation");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvVaccination.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Vaccination");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvWoundCare.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Wound Care");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvSputumTest.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Sputum Test");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvLaboratory.setOnClickListener(view -> {
            if (cvLaboratoryContainer.getVisibility() == View.VISIBLE) {
                cvLaboratoryContainer.setVisibility(View.GONE);
            } else {
                cvLaboratoryContainer.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 500);
            }
        });

        cvImmunization.setOnClickListener(view -> {
            if (cvImmunizationContainer.getVisibility() == View.VISIBLE) {
                cvImmunizationContainer.setVisibility(View.GONE);
            } else {
                cvImmunizationContainer.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                }, 500);
            }
        });

        // laboratory services
        cvHemoglobin.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Hemoglobin Profiling");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvUrinalysis.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Comprehensive Urine Analysis");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvFastingBloodSugar.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Glucose Fasting/Blood Sugar Analysis");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvHepatitisBloodTest.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Hepatitis B Blood Test");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvHIVScreening.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "HIV Screening");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        // immunization services
        cvNewBornCare.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Newborn Care");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });

        cvNewBornScreening.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("service", "Newborn Screening");

            Fragment viewServiceFragment = new ViewServiceFragment();
            viewServiceFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, viewServiceFragment, "VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.addToBackStack("VIEW_SERVICE_FRAGMENT");
            fragmentTransaction.commit();
        });
    }

    private void initializeViews() {
        scrollView = view.findViewById(R.id.scrollView);
        rvDoctorLocation = view.findViewById(R.id.rvDoctorLocation);
        btnNext = view.findViewById(R.id.btnNext);
        btnPrev = view.findViewById(R.id.btnPrev);
        cvConsultation = view.findViewById(R.id.cvConsultation);
        cvFamilyPlanning = view.findViewById(R.id.cvFamilyPlanning);
        cvXray = view.findViewById(R.id.cvXray);
        cvLaborAndDelivery = view.findViewById(R.id.cvLaborAndDelivery);
        cvTBDOTS = view.findViewById(R.id.cvTBDOTS);
        cvCircumcision = view.findViewById(R.id.cvCircumcision);
        cvMedicalCertificate = view.findViewById(R.id.cvMedicalCertificate);
        cvSupplementation = view.findViewById(R.id.cvSupplementation);
        cvVaccination = view.findViewById(R.id.cvVaccination);
        cvWoundCare = view.findViewById(R.id.cvWoundCare);
        cvSputumTest = view.findViewById(R.id.cvSputumTest);
        cvLaboratory = view.findViewById(R.id.cvLaboratory);
        cvLaboratoryContainer = view.findViewById(R.id.cvLaboratoryContainer);
        cvHemoglobin = view.findViewById(R.id.cvHemoglobin);
        cvUrinalysis = view.findViewById(R.id.cvUrinalysis);
        cvFastingBloodSugar = view.findViewById(R.id.cvFastingBloodSugar);
        cvHepatitisBloodTest = view.findViewById(R.id.cvHepatitisBloodTest);
        cvHIVScreening = view.findViewById(R.id.cvHIVScreening);
        cvImmunization = view.findViewById(R.id.cvImmunization);
        cvImmunizationContainer = view.findViewById(R.id.cvImmunizationContainer);
        cvNewBornCare = view.findViewById(R.id.cvNewBornCare);
        cvNewBornScreening = view.findViewById(R.id.cvNewBornScreening);
    }

    @Override
    public void onDoctorLocationClick(int position) {

    }
}