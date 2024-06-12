package com.rhu.Objects;

public class MedicalEntry {
    String id;
    String userUid;
    String bloodPressure;
    String respiratoryRate;
    String bodyTemperature;
    String pulseRate;
    String o2sat;
    String height;
    String weight;
    String chiefComplaint;
    String diagnosis;
    String medications;
    String treatmentPlan;
    long timestamp;

    public MedicalEntry() {
    }

    public MedicalEntry(String id, String userUid, String bloodPressure, String respiratoryRate, String bodyTemperature, String pulseRate, String o2sat, String height, String weight, String chiefComplaint, String diagnosis, String medications, String treatmentPlan, long timestamp) {
        this.id = id;
        this.userUid = userUid;
        this.bloodPressure = bloodPressure;
        this.respiratoryRate = respiratoryRate;
        this.bodyTemperature = bodyTemperature;
        this.pulseRate = pulseRate;
        this.o2sat = o2sat;
        this.height = height;
        this.weight = weight;
        this.chiefComplaint = chiefComplaint;
        this.diagnosis = diagnosis;
        this.medications = medications;
        this.treatmentPlan = treatmentPlan;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getRespiratoryRate() {
        return respiratoryRate;
    }

    public void setRespiratoryRate(String respiratoryRate) {
        this.respiratoryRate = respiratoryRate;
    }

    public String getBodyTemperature() {
        return bodyTemperature;
    }

    public void setBodyTemperature(String bodyTemperature) {
        this.bodyTemperature = bodyTemperature;
    }

    public String getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(String pulseRate) {
        this.pulseRate = pulseRate;
    }

    public String getO2sat() {
        return o2sat;
    }

    public void setO2sat(String o2sat) {
        this.o2sat = o2sat;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
