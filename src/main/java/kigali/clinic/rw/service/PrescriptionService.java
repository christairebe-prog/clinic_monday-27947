
package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.PatientRepository;
import kigali.clinic.rw.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    // C1: Save a prescription
    public String savePrescription(UUID appointmentId, Prescription prescription) {
        Optional<Appointment> existing = appointmentRepo.findById(appointmentId);

        if (existing.isEmpty()) {
            return "The appointment with that id does not exist";
        }

        Appointment appointment = existing.get();

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }

        if (!isPrescriptionValid(prescription)) {
            return "Medicine name, dosage, and a positive duration are required";
        }

        prescription.setId(null);
        prescription.setAppointment(appointment);
        prescription.setIssuedDate(LocalDate.now());
        prescriptionRepo.save(prescription);

        return "Prescription saved successfully";
    }

    // C2: Get prescriptions for an appointment
    public List<Prescription> getPrescriptionsByAppointment(UUID appointmentId) {
        Optional<Appointment> appointment = appointmentRepo.findById(appointmentId);

        if (appointment.isEmpty()) {
            return null;
        }

        return prescriptionRepo.findByAppointment_Id(appointmentId);
    }

    // C3: Get prescriptions for a patient
    public List<Prescription> getPrescriptionsByPatient(String ssNumber) {
        Optional<Patient> patient = patientRepo.findBySsNumber(ssNumber);

        if (patient.isEmpty()) {
            return null;
        }

        return prescriptionRepo.findByPatientSsNumberOrderByIssuedDateDesc(ssNumber);
    }

    // C4: Update a prescription
    public String updatePrescription(UUID id, Prescription updatedPrescription) {
        Optional<Prescription> existing = prescriptionRepo.findById(id);

        if (existing.isEmpty()) {
            return "The prescription with that id does not exist";
        }

        if (!isPrescriptionValid(updatedPrescription)) {
            return "Medicine name, dosage, and a positive duration are required";
        }

        Prescription prescription = existing.get();
        prescription.setMedicineName(updatedPrescription.getMedicineName());
        prescription.setDosage(updatedPrescription.getDosage());
        prescription.setDurationDays(updatedPrescription.getDurationDays());

        prescriptionRepo.save(prescription);

        return "Prescription updated successfully";
    }

    // C4: Delete a prescription
    public String deletePrescription(UUID id) {
        Optional<Prescription> existing = prescriptionRepo.findById(id);

        if (existing.isEmpty()) {
            return "The prescription with that id does not exist";
        }

        prescriptionRepo.delete(existing.get());

        return "Prescription deleted successfully";
    }

    // Validate required prescription fields
    private boolean isPrescriptionValid(Prescription prescription) {
        return prescription != null
                && prescription.getMedicineName() != null
                && !prescription.getMedicineName().isBlank()
                && prescription.getDosage() != null
                && !prescription.getDosage().isBlank()
                && prescription.getDurationDays() > 0;
    }
}
