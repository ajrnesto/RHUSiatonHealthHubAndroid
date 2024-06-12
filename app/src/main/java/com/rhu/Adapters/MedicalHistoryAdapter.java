package com.rhu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.rhu.Objects.MedicalEntry;
import com.rhu.Objects.MedicalEntry;
import com.rhu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MedicalHistoryAdapter extends RecyclerView.Adapter<MedicalHistoryAdapter.medicalHistoryViewHolder> {

    private static final FirebaseFirestore DB = FirebaseFirestore.getInstance();

    Context context;
    ArrayList<MedicalEntry> arrMedicalHistory;
    private OnMedicalHistoryListener mOnMedicalHistoryListener;

    public MedicalHistoryAdapter(Context context, ArrayList<MedicalEntry> arrMedicalHistory, OnMedicalHistoryListener onMedicalHistoryListener) {
        this.context = context;
        this.arrMedicalHistory = arrMedicalHistory;
        this.mOnMedicalHistoryListener = onMedicalHistoryListener;
    }

    @NonNull
    @Override
    public medicalHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_medical_history, parent, false);
        return new medicalHistoryViewHolder(view, mOnMedicalHistoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull medicalHistoryViewHolder holder, int position) {
        MedicalEntry medicalEntry = arrMedicalHistory.get(position);
        String id = medicalEntry.getId();
        String userUid = medicalEntry.getUserUid();
        String bloodPressure = medicalEntry.getBloodPressure();
        String respiratoryRate = medicalEntry.getRespiratoryRate();
        String bodyTemperature = medicalEntry.getBodyTemperature();
        String pulseRate = medicalEntry.getPulseRate();
        String o2sat = medicalEntry.getO2sat();
        String height = medicalEntry.getHeight();
        String weight = medicalEntry.getWeight();
        String chiefComplaint = medicalEntry.getChiefComplaint();
        String diagnosis = medicalEntry.getDiagnosis();
        String medications = medicalEntry.getMedications();
        String treatmentPlan = medicalEntry.getTreatmentPlan();
        long timestamp = medicalEntry.getTimestamp();

        if (bloodPressure == null) {
            holder.tvVitalInformationTitle.setVisibility(View.GONE);
            holder.tvBloodPressure.setVisibility(View.GONE);
            holder.tvRespiratoryRate.setVisibility(View.GONE);
            holder.tvBodyTemperature.setVisibility(View.GONE);
            holder.tvPulseRate.setVisibility(View.GONE);
            holder.tvO2SAT.setVisibility(View.GONE);
            holder.tvHeight.setVisibility(View.GONE);
            holder.tvWeight.setVisibility(View.GONE);
        }
        if (chiefComplaint == null) {
            holder.tvDiagnosisTitle.setVisibility(View.GONE);
            holder.tvChiefComplaint.setVisibility(View.GONE);
            holder.tvDiagnosis.setVisibility(View.GONE);
            holder.tvMedications.setVisibility(View.GONE);
            holder.tvTreatmentPlan.setVisibility(View.GONE);
        }

        holder.tvBloodPressure.setText("Blood Pressure: " + bloodPressure + " mmHg");
        holder.tvRespiratoryRate.setText("Respiratory Rate: " + respiratoryRate + " breaths per minute");
        holder.tvBodyTemperature.setText("Body Temperature: " + bodyTemperature + " °C");
        holder.tvPulseRate.setText("Pulse Rate: " + pulseRate + " bpm");
        holder.tvO2SAT.setText("O2SAT: " + o2sat + "%");
        holder.tvHeight.setText("Height: " + height + "cm");
        holder.tvWeight.setText("Weight: " + weight + "kg");
        holder.tvChiefComplaint.setText("Chief Complaint: " + chiefComplaint);
        holder.tvDiagnosis.setText("Diagnosis: " + diagnosis);
        holder.tvMedications.setText("Medications: " + medications);
        holder.tvTreatmentPlan.setText("Treatment Plan: " + treatmentPlan);
        loadTimestamp(holder, timestamp);
    }

    private void loadTimestamp(medicalHistoryViewHolder holder, long schedule) {
        SimpleDateFormat sdfTimestamp = new SimpleDateFormat("MMM d yyyy, hh:mm aa");
        holder.tvTimestamp.setText(sdfTimestamp.format(schedule));
    }

    @Override
    public int getItemCount() {
        return arrMedicalHistory.size();
    }

    public class medicalHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnMedicalHistoryListener onMedicalHistoryListener;
        TextView tvVitalInformationTitle, tvDiagnosisTitle, tvBloodPressure, tvRespiratoryRate, tvBodyTemperature, tvPulseRate, tvO2SAT, tvHeight, tvWeight;
        TextView tvTimestamp, tvChiefComplaint, tvDiagnosis, tvMedications, tvTreatmentPlan;

        public medicalHistoryViewHolder(@NonNull View itemView, OnMedicalHistoryListener onMedicalHistoryListener) {
            super(itemView);

            tvVitalInformationTitle = itemView.findViewById(R.id.tvVitalInformationTitle);
            tvDiagnosisTitle = itemView.findViewById(R.id.tvDiagnosisTitle);
            tvBloodPressure = itemView.findViewById(R.id.tvBloodPressure);
            tvRespiratoryRate = itemView.findViewById(R.id.tvRespiratoryRate);
            tvBodyTemperature = itemView.findViewById(R.id.tvBodyTemperature);
            tvPulseRate = itemView.findViewById(R.id.tvPulseRate);
            tvO2SAT = itemView.findViewById(R.id.tvO2SAT);
            tvHeight = itemView.findViewById(R.id.tvHeight);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvChiefComplaint = itemView.findViewById(R.id.tvChiefComplaint);
            tvDiagnosis = itemView.findViewById(R.id.tvDiagnosis);
            tvMedications = itemView.findViewById(R.id.tvMedications);
            tvTreatmentPlan = itemView.findViewById(R.id.tvTreatmentPlan);

            this.onMedicalHistoryListener = onMedicalHistoryListener;

        }

        @Override
        public void onClick(View view) {
            onMedicalHistoryListener.onMedicalHistoryClick(getAdapterPosition());
        }
    }

    public interface OnMedicalHistoryListener{
        void onMedicalHistoryClick(int position);
    }
}
