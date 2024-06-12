package com.rhu.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firestore.v1.DocumentTransform;
import com.rhu.Adapters.HourAdapter;
import com.rhu.Objects.DoctorSchedule;
import com.rhu.Objects.Hour;
import com.rhu.R;
import com.rhu.Utils.Utils;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FormAppointmentFragment extends Fragment implements HourAdapter.OnHourListener {

    FirebaseFirestore DB;
    FirebaseStorage STORAGE;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        STORAGE = FirebaseStorage.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }

    ArrayList<Hour> arrHours;
    HourAdapter hourAdapter;
    HourAdapter.OnHourListener onHourListener = this;

    View view;
    ScrollView svHourForm;
    ConstraintLayout clLocationAndServiceType, clScheduleDate, clScheduleTime, clPatientForm;
    TextInputLayout tilTypeOfHour;
    TextInputEditText etDate;
    TextView tvNoAvailableSchedule, tvAvailableSlots, tvNoSlots;
    AutoCompleteTextView menuTypeOfService, menuLocation;
    MaterialButton btnPatientForm;
    RecyclerView rvHours;

    // date picker items
    MaterialDatePicker.Builder<Long> date;
    MaterialDatePicker<Long> dpdate;
    long dpdateSelection = 0;

    // time picker items
    MaterialTimePicker timePicker;
    long timePickerSelection = 0;

    // Spinner items
    String[] itemsServiceType, itemsLocation;
    ArrayAdapter<String> adapterServiceType, adapterLocation;
    String selectedService;
    String serviceCode;
    int slotsLeft = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_form_appointment, container, false);

        initializeFirebase();
        initializeViews();
        initializeSpinners();
        initializeDatePicker();
        loadSelectedService();
        handleUserInteraction();

        return view;
    }

    private void loadSelectedService() {
        selectedService = requireArguments().getString("selected_service");

        if (Objects.equals(selectedService, "") || selectedService == null) {
            return;
        }

        menuTypeOfService.setText(selectedService, false);
    }

    private void initializeViews() {
        svHourForm = view.findViewById(R.id.svAppointmentForm);
        clLocationAndServiceType = view.findViewById(R.id.clLocationAndAppointmentType);
        clScheduleDate = view.findViewById(R.id.clScheduleDate);
        clScheduleTime = view.findViewById(R.id.clScheduleTime);
        clPatientForm = view.findViewById(R.id.clPatientForm);
        etDate = view.findViewById(R.id.etDate);
        tilTypeOfHour = view.findViewById(R.id.tilTypeOfAppointment);
        menuTypeOfService = view.findViewById(R.id.menuTypeOfAppointment);
        menuLocation = view.findViewById(R.id.menuLocation);
        tvNoAvailableSchedule = view.findViewById(R.id.tvNoAvailableSchedule);
        btnPatientForm = view.findViewById(R.id.btnPatientForm);
        tvAvailableSlots = view.findViewById(R.id.tvAvailableSlots);
        tvNoSlots = view.findViewById(R.id.tvNoSlots);
    }

    private void loadHoursRecyclerView(String scheduleCode) {
        arrHours = new ArrayList<>();
        rvHours = view.findViewById(R.id.rvHours);
        rvHours.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        rvHours.setLayoutManager(linearLayoutManager);

        DocumentReference refDoctorSchedule = DB.collection("doctorSchedule").document(scheduleCode);
        refDoctorSchedule.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();

                            rvHours.setVisibility(View.VISIBLE);
                            tvNoAvailableSchedule.setVisibility(View.GONE);

                            String currentlySelectedService = menuTypeOfService.getText().toString();

                            if (snapshot.exists()) {
                                // before proceeding to render hours recyclerview
                                // first, check the number of slots available
                                DoctorSchedule doctorSchedule = snapshot.toObject(DoctorSchedule.class);
                                if (currentlySelectedService.equals("Consultation")) {
                                    selectedService = "Consultation";
                                    serviceCode = "consultation";
                                    slotsLeft = doctorSchedule.getConsultation();
                                }
                                else if (currentlySelectedService.equals("Family Planning")) {
                                    selectedService = "Family Planning";
                                    serviceCode = "familyPlanning";
                                    slotsLeft = doctorSchedule.getFamilyPlanning();
                                }
                                else if (selectedService.equals("X-Ray")) {
                                    selectedService = "X-Ray";
                                    serviceCode = "xRay";
                                    slotsLeft = doctorSchedule.getxRay();
                                }
                                else if (selectedService.equals("Labor and Delivery")) {
                                    selectedService = "Labor and Delivery";
                                    serviceCode = "laborAndDelivery";
                                    slotsLeft = doctorSchedule.getLaborAndDelivery();;
                                }
                                else if (currentlySelectedService.equals("TB-DOTS")) {
                                    selectedService = "TB-DOTS";
                                    serviceCode = "tbdots";
                                    slotsLeft = doctorSchedule.getTbdots();
                                }
                                else if (selectedService.equals("Circumcision")) {
                                    selectedService = "Circumcision";
                                    serviceCode = "circumcision";
                                    slotsLeft = doctorSchedule.getCircumcision();
                                }
                                else if (currentlySelectedService.equals("Medical Certificate")) {
                                    selectedService = "Medical Certificate";
                                    serviceCode = "medicalCertificate";
                                    slotsLeft = doctorSchedule.getMedicalCertificate();
                                }
                                else if (currentlySelectedService.equals("Micronutrient Supplementation")) {
                                    selectedService = "Micronutrient Supplementation";
                                    serviceCode = "micronutrientSupplementation";
                                    slotsLeft = doctorSchedule.getMicronutrientSupplementation();
                                }
                                else if (selectedService.equals("Vaccination")) {
                                    selectedService = "Vaccination";
                                    serviceCode = "vaccination";
                                    slotsLeft = view.getAccessibilityLiveRegion();
                                }
                                else if (currentlySelectedService.equals("Wound Care")) {
                                    selectedService = "Wound Care";
                                    serviceCode = "woundCare";
                                    slotsLeft = doctorSchedule.getWoundCare();
                                }
                                else if (selectedService.equals("Sputum Test")) {
                                    selectedService = "Sputum Test";
                                    serviceCode = "sputumTest";
                                    slotsLeft = doctorSchedule.getSputumTest();
                                }
                                else if (selectedService.equals("Hemoglobin")) {
                                    selectedService = "Hemoglobin";
                                    serviceCode = "hemoglobin";
                                    slotsLeft = doctorSchedule.getHemoglobin();
                                }
                                else if (selectedService.equals("Urinalysis")) {
                                    selectedService = "Urinalysis";
                                    serviceCode = "urinalysis";
                                    slotsLeft = doctorSchedule.getUrinalysis();
                                }
                                else if (selectedService.equals("Fasting Blood Sugar")) {
                                    selectedService = "Fasting Blood Sugar";
                                    serviceCode = "fastingBloodSugar";
                                    slotsLeft = doctorSchedule.getFastingBloodSugar();
                                }
                                else if (selectedService.equals("Hepatitis B Blood Test")) {
                                    selectedService = "Hepatitis B Blood Test";
                                    serviceCode = "hepatitisBBloodTest";
                                    slotsLeft = doctorSchedule.getHepatitisBBloodTest();
                                }
                                else if (selectedService.equals("HIV Screening")) {
                                    selectedService = "HIV Screening";
                                    serviceCode = "hivScreening";
                                    slotsLeft = doctorSchedule.getHivScreening();
                                }
                                else if (selectedService.equals("Newborn Care")) {
                                    selectedService = "Newborn Care";
                                    serviceCode = "newbornCare";
                                    slotsLeft = doctorSchedule.getNewbornCare();
                                }
                                else if (selectedService.equals("Newborn Screening")) {
                                    selectedService = "Newborn Screening";
                                    serviceCode = "newbornScreening";
                                    slotsLeft = doctorSchedule.getNewbornScreening();
                                }

                                tvAvailableSlots.setText("Available Slots: " + (slotsLeft==-1?"No limit":slotsLeft));

                                for (Hour hour : doctorSchedule.getHours()) {
                                    arrHours.add(hour);
                                }
                                hourAdapter.notifyDataSetChanged();
                            }
                            else {
                                // create docSched node before proceeding to render hours recyclerview
                                DoctorSchedule doctorSchedule = new DoctorSchedule();
                                    ArrayList<Hour> hoursData = new ArrayList<>();
                                    hoursData.add(new Hour("OPEN","",""));
                                    hoursData.add(new Hour("OPEN","",""));
                                    hoursData.add(new Hour("OPEN","",""));
                                    hoursData.add(new Hour("OPEN","",""));
                                    hoursData.add(new Hour("OPEN","",""));
                                    hoursData.add(new Hour("OPEN","",""));
                                    hoursData.add(new Hour("OPEN","",""));
                                    hoursData.add(new Hour("OPEN","",""));
                                doctorSchedule.setHours(hoursData);
                                doctorSchedule.setConsultation(20);
                                doctorSchedule.setFamilyPlanning(-1);
                                doctorSchedule.setxRay(30);
                                doctorSchedule.setLaborAndDelivery(-1);
                                doctorSchedule.setTbdots(-1);
                                doctorSchedule.setCircumcision(25);
                                doctorSchedule.setMedicalCertificate(20);
                                doctorSchedule.setMicronutrientSupplementation(-1);
                                doctorSchedule.setVaccination(50);
                                doctorSchedule.setWoundCare(-1);
                                doctorSchedule.setSputumTest(15);
                                doctorSchedule.setHemoglobin(15);
                                doctorSchedule.setUrinalysis(15);
                                doctorSchedule.setFastingBloodSugar(15);
                                doctorSchedule.setHepatitisBBloodTest(15);
                                doctorSchedule.setHivScreening(15);
                                doctorSchedule.setNewbornCare(-1);
                                doctorSchedule.setNewbornScreening(-1);
                                if (currentlySelectedService.equals("Consultation")) {
                                    selectedService = "Consultation";
                                    serviceCode = "consultation";
                                    slotsLeft = 20;
                                }
                                else if (currentlySelectedService.equals("Family Planning")) {
                                    selectedService = "Family Planning";
                                    serviceCode = "familyPlanning";
                                    slotsLeft = -1;
                                }
                                else if (selectedService.equals("X-Ray")) {
                                    selectedService = "X-Ray";
                                    serviceCode = "xRay";
                                    slotsLeft = 30;
                                }
                                else if (selectedService.equals("Labor and Delivery")) {
                                    selectedService = "Labor and Delivery";
                                    serviceCode = "laborAndDelivery";
                                    slotsLeft = -1;
                                }
                                else if (currentlySelectedService.equals("TB-DOTS")) {
                                    selectedService = "TB-DOTS";
                                    serviceCode = "tbdots";
                                    slotsLeft = -1;
                                }
                                else if (selectedService.equals("Circumcision")) {
                                    selectedService = "Circumcision";
                                    serviceCode = "circumcision";
                                    slotsLeft = 25;
                                }
                                else if (currentlySelectedService.equals("Medical Certificate")) {
                                    selectedService = "Medical Certificate";
                                    serviceCode = "medicalCertificate";
                                    slotsLeft = 20;
                                }
                                else if (currentlySelectedService.equals("Micronutrient Supplementation")) {
                                    selectedService = "Micronutrient Supplementation";
                                    serviceCode = "micronutrientSupplementation";
                                    slotsLeft = -1;
                                }
                                else if (selectedService.equals("Vaccination")) {
                                    selectedService = "Vaccination";
                                    serviceCode = "vaccination";
                                    slotsLeft = 50;
                                }
                                else if (currentlySelectedService.equals("Wound Care")) {
                                    selectedService = "Wound Care";
                                    serviceCode = "woundCare";
                                    slotsLeft = -1;
                                }
                                else if (selectedService.equals("Sputum Test")) {
                                    selectedService = "Sputum Test";
                                    serviceCode = "sputumTest";
                                    slotsLeft = 15;
                                }
                                else if (selectedService.equals("Hemoglobin")) {
                                    selectedService = "Hemoglobin";
                                    serviceCode = "hemoglobin";
                                    slotsLeft = 15;
                                }
                                else if (selectedService.equals("Urinalysis")) {
                                    selectedService = "Urinalysis";
                                    serviceCode = "urinalysis";
                                    slotsLeft = 15;
                                }
                                else if (selectedService.equals("Fasting Blood Sugar")) {
                                    selectedService = "Fasting Blood Sugar";
                                    serviceCode = "fastingBloodSugar";
                                    slotsLeft = 15;
                                }
                                else if (selectedService.equals("Hepatitis B Blood Test")) {
                                    selectedService = "Hepatitis B Blood Test";
                                    serviceCode = "hepatitisBBloodTest";
                                    slotsLeft = 15;
                                }
                                else if (selectedService.equals("HIV Screening")) {
                                    selectedService = "HIV Screening";
                                    serviceCode = "hivScreening";
                                    slotsLeft = 15;
                                }
                                else if (selectedService.equals("Newborn Care")) {
                                    selectedService = "Newborn Care";
                                    serviceCode = "newbornCare";
                                    slotsLeft = -1;
                                }
                                else if (selectedService.equals("Newborn Screening")) {
                                    selectedService = "Newborn Screening";
                                    serviceCode = "newbornScreening";
                                    slotsLeft = -1;
                                }

                                DB.collection("doctorSchedule").document(scheduleCode)
                                        .set(doctorSchedule)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    arrHours.add(new Hour("OPEN","",""));
                                                    arrHours.add(new Hour("OPEN","",""));
                                                    arrHours.add(new Hour("OPEN","",""));
                                                    arrHours.add(new Hour("OPEN","",""));
                                                    arrHours.add(new Hour("OPEN","",""));
                                                    arrHours.add(new Hour("OPEN","",""));
                                                    arrHours.add(new Hour("OPEN","",""));
                                                    arrHours.add(new Hour("OPEN","",""));
                                                    hourAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });

                                if (slotsLeft == 0) {
                                    rvHours.setVisibility(View.GONE);
                                    tvNoSlots.setVisibility(View.VISIBLE);
                                }
                                else {
                                    rvHours.setVisibility(View.VISIBLE);
                                    tvNoSlots.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                });

        hourAdapter = new HourAdapter(getContext(), arrHours, onHourListener);
        rvHours.setAdapter(hourAdapter);
        hourAdapter.notifyDataSetChanged();
    }

    private void initializeSpinners() {
        // hour type
        itemsServiceType = new String[]{
                "Consultation",
                "Family Planning",
                "X-Ray",
                "TB-DOTS",
                "Circumcision",
                "Medical Certificate",
                "Micronutrient Supplementation",
                "Vaccination",
                "Wound Care",
                "Sputum Test",
                "Hemoglobin",
                "Urinalysis",
                "Fasting Blood Sugar",
                "Hepatitis B Blood Test",
                "HIV Screening",
                "Newborn Care",
                "Newborn Screening"
        };
                
        adapterServiceType = new ArrayAdapter<>(requireContext(), R.layout.list_item, itemsServiceType);
        menuTypeOfService.setAdapter(adapterServiceType);

        // health center location
        // itemsLocation = new String[]{"VIRTUAL/ONLINE", "ALBIGA", "APOLOY", "BONAWON", "BONBONON", "CABANGAHAN", "CANAWAY", "CASALA-AN", "CATICUGAN", "DATAG", "GILIGA-ON", "INALAD", "MALABUHAN", "MALOH", "MANTIQUIL", "MANTUYOP", "NAPACAO", "POBLACION I", "POBLACION II", "POBLACION III", "POBLACION IV", "SALAG", "SAN JOSE", "SANDULOT", "SI-IT", "SUMALIRING", "TAYAK"};
        itemsLocation = new String[]{"ALBIGA", "APOLOY", "BONAWON", "BONBONON", "CABANGAHAN", "CANAWAY", "CASALA-AN", "CATICUGAN", "DATAG", "GILIGA-ON", "INALAD", "MALABUHAN", "MALOH", "MANTIQUIL", "MANTUYOP", "NAPACAO", "POBLACION I", "POBLACION II", "POBLACION III", "POBLACION IV", "SALAG", "SAN JOSE", "SANDULOT", "SI-IT", "SUMALIRING", "TAYAK"};
        adapterLocation = new ArrayAdapter<>(requireContext(), R.layout.list_item, itemsLocation);
        menuLocation.setAdapter(adapterLocation);
    }

    private void initializeDatePicker() {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(System.currentTimeMillis());
        today.set(Calendar.HOUR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        date = MaterialDatePicker.Builder.datePicker();
        date.setTitleText("Select date")
                // .setCalendarConstraints(new CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(today.getTimeInMillis() + 259200000)).build())
                .setSelection(System.currentTimeMillis());
        dpdate = date.build();
        dpdate.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
            dpdateSelection = dpdate.getSelection();
            etDate.setEnabled(true);

            SimpleDateFormat sdfScheduleCode = new SimpleDateFormat("yyyyMMdd");
            
            // check if user has selected a service
            String selectedService = menuTypeOfService.getText().toString(); 
            String selectedLocation = menuLocation.getText().toString();
            ArrayList<String> servicesThatNeedADoctor = new ArrayList<>();
            servicesThatNeedADoctor.add("Consultation");
            servicesThatNeedADoctor.add("Medical Certificate");
            servicesThatNeedADoctor.add("Micronutrient Supplementation");
            servicesThatNeedADoctor.add("Newborn Care");
            servicesThatNeedADoctor.add("Newborn Screening");
            servicesThatNeedADoctor.add("TB-DOTS");

            if (selectedService.isEmpty()) {
                Utils.simpleDialog(requireContext(), "Failed to Set Appointment", "Please select a service before setting an appointment date.", "Okay");
                return;
            }
            // check if user has selected a health center location
            if (selectedLocation.isEmpty()) {
                Utils.simpleDialog(requireContext(), "Failed to Set Appointment", "Please select a health center location before setting an appointment date.", "Okay");
                return;
            }
            // check for availabilities
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dpdate.getSelection());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) + 1;
            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH) + 1;
            if (selectedService.equals("Family Planning")) {
                if (!(dayOfWeek == 1 && weekOfMonth == 3)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Family Planning is Unavailable", "The Family Planning service is only available every 3rd monday of the month.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("X-Ray")) {
                if (!(dayOfWeek == 1 && weekOfMonth == 1)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "X-Ray is Unavailable", "The X-Ray service is only available every 1st monday of the month.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("TB-DOTS")) {
                if (!(dayOfWeek == 1 && weekOfMonth == 2)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "X-Ray is Unavailable", "The X-Ray service is only available every 2nd monday of the month.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Circumcision")) {
                if (!(dayOfWeek == 1 && weekOfMonth == 4)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Circumcision is Unavailable", "The Circumcision service is only available every 4th monday of the month.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Medical Certificate")) {
                if (!(dayOfWeek >= 1 && dayOfWeek <= Calendar.FRIDAY)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Medical Certificate is Unavailable", "Medical Certificate is only available every monday to friday.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Micronutrient Supplementation")) {
                if (!(dayOfWeek == 1 && weekOfMonth == 1)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Micronutrient Supplementation is Unavailable", "The Micronutrient Supplementation service is only available every 1st monday of the month.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Vaccination")) {
                if (!(dayOfWeek == 1 && weekOfMonth == 3)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Vaccination is Unavailable", "The Vaccination service is only available every 3rd monday of the month.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Sputum Test")) {
                if (!(dayOfWeek == 1)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Sputum Test is Unavailable", "The Sputum Test service is only available every monday.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Hemoglobin")) {
                if (!(dayOfWeek == 1)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Hemoglobin Profiling is Unavailable", "The Hemoglobin Profiling service is only available every monday.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Urinalysis")) {
                if (!(dayOfWeek == 1)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Urinalysis is Unavailable", "The Urinalysis service is only available every monday.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Fasting Blood Sugar")) {
                if (!(dayOfWeek == 1)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Fasting Blood Sugar is Unavailable", "The Fasting Blood Sugar service is only available every monday.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Hepatitis B Blood Test")) {
                if (!(dayOfWeek == 1)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Hepatitis B Blood Test is Unavailable", "The Hepatitis B Blood Test service is only available every monday.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("HIV Screening")) {
                if (!(dayOfWeek == 1)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "HIV Screening is Unavailable", "The HIV Screening service is only available every monday.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Newborn Care ")) {
                if (!(dayOfWeek == 1 && weekOfMonth == 3)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Newborn Care is Unavailable", "The Newborn Care service is only available every 3rd monday of the month.", "Okay");
                    return;
                }
            }
            else if (selectedService.equals("Newborn Screening ")) {
                if (!(dayOfWeek == 1 && weekOfMonth == 3)) {
                    clScheduleTime.setVisibility(View.GONE);
                    Utils.simpleDialog(requireContext(), "Newborn Screening is Unavailable", "The Newborn Screening service is only available every 3rd monday of the month.", "Okay");
                    return;
                }
            }
            // check if selected service needs a doctor
            if (servicesThatNeedADoctor.contains(selectedService)) {
                // check if doctor is available on selected date
                DB.collection("doctorLocation").document(sdfScheduleCode.format(dpdateSelection))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot doctorLocation = task.getResult();

                                            if (doctorLocation.exists() && Objects.equals(doctorLocation.getString("location").toUpperCase(), selectedLocation)) {
                                                clScheduleTime.setVisibility(View.VISIBLE);
                                                svHourForm.fullScroll(ScrollView.FOCUS_DOWN);
                                                etDate.setText(sdf.format(dpdateSelection).toUpperCase(Locale.ROOT));
                                                loadHoursRecyclerView(sdfScheduleCode.format(dpdateSelection));
                                            }
                                            else {
                                                clScheduleTime.setVisibility(View.GONE);
                                                Utils.simpleDialog(requireContext(), "Failed to Set Appointment", "Sorry, the service you selected requires a doctor's presence, and there is currently no doctor available at your chosen date and/or health center location. Please select a different date for your appointment when a doctor is available.", "Okay");
                                            }
                                        }
                                    }
                                });
            }
            else {
                clScheduleTime.setVisibility(View.VISIBLE);
                svHourForm.fullScroll(ScrollView.FOCUS_DOWN);
                etDate.setText(sdf.format(dpdateSelection).toUpperCase(Locale.ROOT));
                loadHoursRecyclerView(sdfScheduleCode.format(dpdateSelection));
            }
        });
        dpdate.addOnNegativeButtonClickListener(view -> {
            etDate.setEnabled(true);
        });
        dpdate.addOnCancelListener(dialogInterface -> {
            etDate.setEnabled(true);
        });
    }

    private void handleUserInteraction() {
        menuTypeOfService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedService = menuTypeOfService.getText().toString();
                if (position == 0) {
                    menuLocation.setEnabled(true);
                    svHourForm.fullScroll(ScrollView.FOCUS_DOWN);

                    if (!menuLocation.getText().toString().isEmpty()) {
                        clScheduleDate.setVisibility(View.VISIBLE);
                        svHourForm.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }
//                else if (position == 1) {
//                    menuLocation.setText("VIRTUAL/ONLINE", false);
//                    menuLocation.setEnabled(false);
//                    clScheduleDate.setVisibility(View.VISIBLE);
//                    svHourForm.fullScroll(ScrollView.FOCUS_DOWN);
//                }
                etDate.setText("");
                clScheduleTime.setVisibility(View.GONE);
            }
        });

        menuLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!menuTypeOfService.getText().toString().isEmpty()) {
                    clScheduleDate.setVisibility(View.VISIBLE);
                }
                etDate.setText("");
                clScheduleTime.setVisibility(View.GONE);
            }
        });

        btnPatientForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
                Fragment patientFormFragment = new PatientFormFragment();
                fragmentTransaction.replace(R.id.fragmentHolder, patientFormFragment, "PATIENT_FORM_FRAGMENT");
                fragmentTransaction.addToBackStack("PATIENT_FORM_FRAGMENT");
                fragmentTransaction.commit();
            }
        });

        etDate.setOnClickListener(view -> {
            etDate.setEnabled(false);
            dpdate.show(requireActivity().getSupportFragmentManager(), "INCIDENT_DATE_PICKER");
        });
    }

    @Override
    public void onHourClick(int position) {
        String typeOfService = menuTypeOfService.getText().toString();
        String location = menuLocation.getText().toString();

        if (typeOfService.equals("") || location.equals("")) {
            Toast.makeText(requireContext(), "Please fill in all required fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> appointment = new HashMap<>();
        appointment.put("userUid", USER.getUid());
        appointment.put("appointmentType", typeOfService);
        appointment.put("location", location);

        SimpleDateFormat sdfScheduleCode = new SimpleDateFormat("yyyyMMdd");
        String dateCode = sdfScheduleCode.format(dpdateSelection);
        appointment.put("dateCode", dateCode);
        appointment.put("hourIndex", position);

        Calendar cSchedule = Calendar.getInstance();
        cSchedule.setTimeInMillis(dpdateSelection);
        cSchedule.set(Calendar.HOUR_OF_DAY, (position < 4) ? position + 8 : position + 9);
        cSchedule.set(Calendar.MINUTE, 0);
        cSchedule.set(Calendar.SECOND, 0);
        cSchedule.set(Calendar.MILLISECOND, 0);
        appointment.put("schedule", cSchedule.getTimeInMillis());
        appointment.put("timestamp", System.currentTimeMillis());
        appointment.put("status", "Pending");
        appointment.put("idNumber", Utils.Cache.getString(requireContext(), "id_number"));

        DocumentReference refNewAppointment = DB.collection("appointments").document();
        appointment.put("uid", refNewAppointment.getId());

        refNewAppointment.set(appointment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    String timeCode = String.valueOf((position < 4) ? position + 8 : position + 9);
//                    DocumentReference refSchedHour = DB.collection("doctorSchedule").document(sdfScheduleCode.format(dpdateSelection));
//
//                    refSchedHour.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            DoctorSchedule doctorSchedule = task.getResult().toObject(DoctorSchedule.class);
//
//                            ArrayList<Hour> hours = doctorSchedule.getHours();
//                            hours.get(position).setStatus("BOOKED");
//                            hours.get(position).setUserUid(AUTH.getUid());
//                            hours.get(position).setBookingUid(refNewAppointment.getId());
//
////                            refSchedHour.update("hours", hours).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                @Override
////                                public void onComplete(@NonNull Task<Void> task) {
////
////                                }
////                            });
//
//
//                        }
//                    });

                    if (slotsLeft > 0) {
                        DB.collection("doctorSchedule").document(dateCode)
                                .update(serviceCode, FieldValue.increment(-1))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(requireContext(), "Successfully booked appointment", Toast.LENGTH_SHORT).show();
                                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                                            Fragment appointmentsFragment = new AppointmentsFragment();
                                            fragmentTransaction.replace(R.id.fragmentHolder, appointmentsFragment, "APPOINTMENTS_FRAGMENT");
                                            fragmentTransaction.addToBackStack("APPOINTMENTS_FRAGMENT");
                                            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                            fragmentTransaction.commit();
                                        }
                                    }
                                });
                    }
                    else {
                        Toast.makeText(requireContext(), "Successfully booked appointment", Toast.LENGTH_SHORT).show();
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        Fragment appointmentsFragment = new AppointmentsFragment();
                        fragmentTransaction.replace(R.id.fragmentHolder, appointmentsFragment, "APPOINTMENTS_FRAGMENT");
                        fragmentTransaction.addToBackStack("APPOINTMENTS_FRAGMENT");
                        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        fragmentTransaction.commit();
                    }
                }
            }
        });
    }
}