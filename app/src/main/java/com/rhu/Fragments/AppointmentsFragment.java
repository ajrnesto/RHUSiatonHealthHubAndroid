package com.rhu.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
import com.rhu.Objects.Appointment;
import com.rhu.Objects.Hour;
import com.rhu.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AppointmentsFragment extends Fragment implements AppointmentAdapter.OnAppointmentsListener {

    FirebaseFirestore DB;
    FirebaseAuth AUTH;
    FirebaseUser USER;

    private void initializeFirebase() {
        DB = FirebaseFirestore.getInstance();
        AUTH = FirebaseAuth.getInstance();
        USER = AUTH.getCurrentUser();
    }
    View view;

    ArrayList<Appointment> arrAppointments;
    AppointmentAdapter appointmentAdapter;
    AppointmentAdapter.OnAppointmentsListener onAppointmentsListener = this;

    RecyclerView rvAppointments;
    TextView tvEmpty;
    ExtendedFloatingActionButton btnBookAnAppointment;
    TabLayout tabAppointments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_appointments, container, false);

        initializeFirebase();
        initializeViews();
        loadRecyclerView(tabAppointments.getSelectedTabPosition());
        handleUserInteractions();

        return view;
    }

    private void initializeViews() {
        tabAppointments = view.findViewById(R.id.tabAppointments);
        btnBookAnAppointment = view.findViewById(R.id.btnBookAnAppointment);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        rvAppointments = view.findViewById(R.id.rvAppointments);
    }

    private void loadRecyclerView(int tabIndex) {
        arrAppointments = new ArrayList<>();
        rvAppointments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        rvAppointments.setLayoutManager(linearLayoutManager);

        /*String statusFilter = "";
        if (tabIndex == 1) {
            statusFilter = "Pending";
        }
        else if (tabIndex == 2) {
            statusFilter = "Pending";
        }
        else if (tabIndex == 3) {
            statusFilter = "Completed";
        }
        else if (tabIndex == 4) {
            statusFilter = "Cancelled";
        }*/

        CollectionReference refAppointments = DB.collection("appointments");
        Query qryAppointments = null;

        Calendar cTomorrow12AM = Calendar.getInstance();
        cTomorrow12AM.add(Calendar.DAY_OF_MONTH, 1);
        cTomorrow12AM.set(Calendar.HOUR_OF_DAY, 0);
        cTomorrow12AM.set(Calendar.MINUTE, 0);
        cTomorrow12AM.set(Calendar.SECOND, 0);
        cTomorrow12AM.set(Calendar.MILLISECOND, 0);

        Calendar cToday12AM = Calendar.getInstance();
        cToday12AM.set(Calendar.HOUR_OF_DAY, 0);
        cToday12AM.set(Calendar.MINUTE, 0);
        cToday12AM.set(Calendar.SECOND, 0);
        cToday12AM.set(Calendar.MILLISECOND, 0);

        if (tabIndex == 0) {
            qryAppointments = refAppointments.whereEqualTo("userUid", AUTH.getUid())
                    .whereLessThan("schedule", cTomorrow12AM.getTimeInMillis())
                    .whereGreaterThanOrEqualTo("schedule", cToday12AM.getTimeInMillis())
                    .orderBy("schedule", Query.Direction.DESCENDING);
        }
        else if (tabIndex == 1) {
            qryAppointments = refAppointments.whereEqualTo("userUid", AUTH.getUid())
                    .orderBy("schedule", Query.Direction.DESCENDING);
        }
        else if (tabIndex == 2) {
            qryAppointments = refAppointments.whereEqualTo("userUid", AUTH.getUid())
                    .whereIn("status", Arrays.asList("Pending", "Accepted"))
                    .whereGreaterThanOrEqualTo("schedule", cTomorrow12AM.getTimeInMillis())
                    .orderBy("schedule", Query.Direction.DESCENDING);
        }
        else if (tabIndex == 3) {
            qryAppointments = refAppointments.whereEqualTo("userUid", AUTH.getUid())
                    .whereEqualTo("status", "Completed")
                    .orderBy("schedule", Query.Direction.ASCENDING);
        }
        else if (tabIndex == 4) {
            qryAppointments = refAppointments.whereEqualTo("userUid", AUTH.getUid())
                    .whereIn("status", Arrays.asList("Pending", "Accepted"))
                    .whereLessThan("schedule", cToday12AM.getTimeInMillis())
                    .orderBy("schedule", Query.Direction.ASCENDING);
        }
        else if (tabIndex == 5) {
            qryAppointments = refAppointments.whereEqualTo("userUid", AUTH.getUid())
                    .whereEqualTo("status", "Declined")
                    .orderBy("schedule", Query.Direction.ASCENDING);
        }

        qryAppointments.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                arrAppointments.clear();

                if (queryDocumentSnapshots == null) {
                    return;
                }

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Appointment appointment = documentSnapshot.toObject(Appointment.class);

                    arrAppointments.add(appointment);
                    appointmentAdapter.notifyDataSetChanged();
                }

                if (arrAppointments.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvAppointments.setVisibility(View.GONE);
                }
                else {
                    tvEmpty.setVisibility(View.GONE);
                    rvAppointments.setVisibility(View.VISIBLE);
                }
            }
        });

        appointmentAdapter = new AppointmentAdapter(getContext(), arrAppointments, onAppointmentsListener);
        rvAppointments.setAdapter(appointmentAdapter);
        appointmentAdapter.notifyDataSetChanged();
    }

    private void handleUserInteractions() {
        btnBookAnAppointment.setOnClickListener(view -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Bundle args = new Bundle();
            args.putString("selected_service", "");

            Fragment formAppointmentFragment = new FormAppointmentFragment();
            formAppointmentFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragmentHolder, formAppointmentFragment, "APPOINTMENT_FORM_FRAGMENT");
            fragmentTransaction.addToBackStack("APPOINTMENT_FORM_FRAGMENT");
            fragmentTransaction.commit();
        });

        tabAppointments.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadRecyclerView(tabAppointments.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onAppointmentsClick(int position) {

    }
}