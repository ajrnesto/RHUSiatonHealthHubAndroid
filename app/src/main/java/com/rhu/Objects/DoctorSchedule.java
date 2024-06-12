package com.rhu.Objects;

import java.util.ArrayList;

public class DoctorSchedule {
    int consultation;
    int familyPlanning;
    int xRay;
    int laborAndDelivery;
    int tbdots;
    int circumcision;
    int medicalCertificate;
    int micronutrientSupplementation;
    int vaccination;
    int woundCare;
    int sputumTest;
    int hemoglobin;
    int urinalysis;
    int fastingBloodSugar;
    int hepatitisBBloodTest;
    int hivScreening;
    int newbornCare;
    int newbornScreening;

    ArrayList<Hour> hours;

    public DoctorSchedule() {
    }

    public DoctorSchedule(int consultation, int familyPlanning, int xRay, int laborAndDelivery, int tbdots, int circumcision, int medicalCertificate, int micronutrientSupplementation, int vaccination, int woundCare, int sputumTest, int hemoglobin, int urinalysis, int fastingBloodSugar, int hepatitisBBloodTest, int hivScreening, int newbornCare, int newbornScreening, ArrayList<Hour> hours) {
        this.consultation = consultation;
        this.familyPlanning = familyPlanning;
        this.xRay = xRay;
        this.laborAndDelivery = laborAndDelivery;
        this.tbdots = tbdots;
        this.circumcision = circumcision;
        this.medicalCertificate = medicalCertificate;
        this.micronutrientSupplementation = micronutrientSupplementation;
        this.vaccination = vaccination;
        this.woundCare = woundCare;
        this.sputumTest = sputumTest;
        this.hemoglobin = hemoglobin;
        this.urinalysis = urinalysis;
        this.fastingBloodSugar = fastingBloodSugar;
        this.hepatitisBBloodTest = hepatitisBBloodTest;
        this.hivScreening = hivScreening;
        this.newbornCare = newbornCare;
        this.newbornScreening = newbornScreening;
        this.hours = hours;
    }

    public int getConsultation() {
        return consultation;
    }

    public void setConsultation(int consultation) {
        this.consultation = consultation;
    }

    public int getFamilyPlanning() {
        return familyPlanning;
    }

    public void setFamilyPlanning(int familyPlanning) {
        this.familyPlanning = familyPlanning;
    }

    public int getxRay() {
        return xRay;
    }

    public void setxRay(int xRay) {
        this.xRay = xRay;
    }

    public int getLaborAndDelivery() {
        return laborAndDelivery;
    }

    public void setLaborAndDelivery(int laborAndDelivery) {
        this.laborAndDelivery = laborAndDelivery;
    }

    public int getTbdots() {
        return tbdots;
    }

    public void setTbdots(int tbdots) {
        this.tbdots = tbdots;
    }

    public int getCircumcision() {
        return circumcision;
    }

    public void setCircumcision(int circumcision) {
        this.circumcision = circumcision;
    }

    public int getMedicalCertificate() {
        return medicalCertificate;
    }

    public void setMedicalCertificate(int medicalCertificate) {
        this.medicalCertificate = medicalCertificate;
    }

    public int getMicronutrientSupplementation() {
        return micronutrientSupplementation;
    }

    public void setMicronutrientSupplementation(int micronutrientSupplementation) {
        this.micronutrientSupplementation = micronutrientSupplementation;
    }

    public int getVaccination() {
        return vaccination;
    }

    public void setVaccination(int vaccination) {
        this.vaccination = vaccination;
    }

    public int getWoundCare() {
        return woundCare;
    }

    public void setWoundCare(int woundCare) {
        this.woundCare = woundCare;
    }

    public int getSputumTest() {
        return sputumTest;
    }

    public void setSputumTest(int sputumTest) {
        this.sputumTest = sputumTest;
    }

    public int getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(int hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public int getUrinalysis() {
        return urinalysis;
    }

    public void setUrinalysis(int urinalysis) {
        this.urinalysis = urinalysis;
    }

    public int getFastingBloodSugar() {
        return fastingBloodSugar;
    }

    public void setFastingBloodSugar(int fastingBloodSugar) {
        this.fastingBloodSugar = fastingBloodSugar;
    }

    public int getHepatitisBBloodTest() {
        return hepatitisBBloodTest;
    }

    public void setHepatitisBBloodTest(int hepatitisBBloodTest) {
        this.hepatitisBBloodTest = hepatitisBBloodTest;
    }

    public int getHivScreening() {
        return hivScreening;
    }

    public void setHivScreening(int hivScreening) {
        this.hivScreening = hivScreening;
    }

    public int getNewbornCare() {
        return newbornCare;
    }

    public void setNewbornCare(int newbornCare) {
        this.newbornCare = newbornCare;
    }

    public int getNewbornScreening() {
        return newbornScreening;
    }

    public void setNewbornScreening(int newbornScreening) {
        this.newbornScreening = newbornScreening;
    }

    public ArrayList<Hour> getHours() {
        return hours;
    }

    public void setHours(ArrayList<Hour> hours) {
        this.hours = hours;
    }
}
