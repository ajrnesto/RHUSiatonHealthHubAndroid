package com.rhu.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rhu.Fragments.ChatFragment;
import com.rhu.Fragments.FormAppointmentFragment;
import com.rhu.Objects.Appointment;
import com.rhu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.appointmentsViewHolder> {

    private static final FirebaseFirestore DB = FirebaseFirestore.getInstance();

    Context context;
    ArrayList<Appointment> arrAppointments;
    private OnAppointmentsListener mOnAppointmentsListener;

    public AppointmentAdapter(Context context, ArrayList<Appointment> arrAppointments, OnAppointmentsListener onAppointmentsListener) {
        this.context = context;
        this.arrAppointments = arrAppointments;
        this.mOnAppointmentsListener = onAppointmentsListener;
    }

    @NonNull
    @Override
    public appointmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_appointment, parent, false);
        return new appointmentsViewHolder(view, mOnAppointmentsListener);
    }

    @Override
    public void onBindViewHolder(@NonNull appointmentsViewHolder holder, int position) {
        Appointment appointments = arrAppointments.get(position);
        String uid = appointments.getUid();
        String userUid = appointments.getUserUid();
        String appointmentType = appointments.getAppointmentType();
        String location = appointments.getLocation();
        String dateCode = appointments.getDateCode();
        int timeIndex = appointments.getTimeIndex();
        long schedule = appointments.getSchedule();
        String status = appointments.getStatus();
        long timestamp = appointments.getTimestamp();

        holder.tvLocation.setText(location);
        holder.tvAppointmentType.setText(appointmentType);
        loadTimestamp(holder, schedule);
        loadStatus(holder, status);
    }

    private void loadTimestamp(appointmentsViewHolder holder, long schedule) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("MMM dd");
        SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm aa");
        holder.tvDate.setText(sdfDate.format(schedule));
        holder.tvTime.setText(sdfTime.format(schedule));
    }

    private void loadStatus(appointmentsViewHolder holder, String status){
        holder.tvStatus.setText(status);

        if (Objects.equals(status, "Pending") || Objects.equals(status, "Declined")) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.tomato));
        }
        else if (Objects.equals(status, "Accepted") || Objects.equals(status, "Completed")) {
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.emerald));
        }
    }

    @Override
    public int getItemCount() {
        return arrAppointments.size();
    }

    public class appointmentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnAppointmentsListener onAppointmentsListener;
        MaterialCardView cvAppointment;
        TextView tvLocation, tvAppointmentType, tvDate, tvTime, tvStatus;
        MaterialButton btnChat, btnCancel;

        public appointmentsViewHolder(@NonNull View itemView, OnAppointmentsListener onAppointmentsListener) {
            super(itemView);

            cvAppointment = itemView.findViewById(R.id.cvAppointment);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvAppointmentType = itemView.findViewById(R.id.tvAppointmentType);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnChat = itemView.findViewById(R.id.btnChat);
            btnCancel = itemView.findViewById(R.id.btnCancel);

            this.onAppointmentsListener = onAppointmentsListener;

                // cvAppointment.setOnClickListener(this);
    
                btnChat.setOnClickListener(view -> {
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment chatFragment = new ChatFragment();
                    fragmentTransaction.replace(R.id.fragmentHolder, chatFragment, "CHAT_FRAGMENT");
                    fragmentTransaction.addToBackStack("CHAT_FRAGMENT");
                    fragmentTransaction.commit();
                });

            btnCancel.setOnClickListener(view -> {
                MaterialAlertDialogBuilder dialogCancel = new MaterialAlertDialogBuilder(itemView.getContext());
                dialogCancel.setTitle("Cancel appointment");
                dialogCancel.setMessage("Are you sure you want to cancel your appointment?");
                dialogCancel.setPositiveButton("Cancel Appointment", (dialogInterface, i) -> {
                    int position = getAdapterPosition();
                    Appointment currentAppointment = arrAppointments.get(position);
                    String appointmentUid = currentAppointment.getUid();

                    DB.collection("appointments").document(appointmentUid).delete();
                    notifyDataSetChanged();
                });
                dialogCancel.setNeutralButton("Close", (dialogInterface, i) -> {

                });
                dialogCancel.show();
            });
        }

        @Override
        public void onClick(View view) {
            onAppointmentsListener.onAppointmentsClick(getAdapterPosition());

            Appointment appointment = arrAppointments.get(getAdapterPosition());
            if (appointment.getStatus().equals( "Pending") && appointment.getSchedule() > System.currentTimeMillis()) {
                /*Toast.makeText(context, "TODO: show appointment dialog", Toast.LENGTH_SHORT).show();*/
            }
        }
    }

    public interface OnAppointmentsListener{
        void onAppointmentsClick(int position);
    }
}
