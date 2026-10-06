package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patRepo;

    @Autowired
    private DoctorRepository docRepo;

    // CREATE
    public String savePatient(Patient patient) {

        Optional<Patient> checkPatient =
                patRepo.findByFirstNameAndLastNameAndDateOfBirth(
                        patient.getFirstName(),
                        patient.getLastName(),
                        patient.getDateOfBirth());

        if (checkPatient.isPresent()) {
            return "That patient already exists!";
        }

        patRepo.save(patient);

        return "Patient is saved successfully";
    }

    // READ ALL
    public List<Patient> getAllPatients() {
        return patRepo.findAll();
    }

    // READ ONE
    public Optional<Patient> getPatientById(UUID id) {
        return patRepo.findById(id);
    }

    // A1
    public List<Patient> getPatientsByLastName(
            String lastName) {

        return patRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(
                lastName);
    }

    // B4
    public List<Patient> getPatientsOfDoctor(
            UUID doctorId) {

        if (docRepo.findById(doctorId).isEmpty()) {
            return null;
        }

        return patRepo.findPatientsOfDoctor(doctorId);
    }

    // C2
    public List<Patient> getFrequentPatients(long min) {

        return patRepo.findFrequentPatients(min);
    }

    // UPDATE
    public String updatePatient(
            UUID id,
            Patient newPatient) {

        Optional<Patient> existing =
                patRepo.findById(id);

        if (existing.isEmpty()) {
            return "Patient not found";
        }

        Patient patient = existing.get();

        patient.setFirstName(
                newPatient.getFirstName());

        patient.setLastName(
                newPatient.getLastName());

        patient.setDateOfBirth(
                newPatient.getDateOfBirth());

        patRepo.save(patient);

        return "Patient is updated successfully";
    }

    // DELETE
    public String deletePatient(UUID id) {

        Optional<Patient> existing =
                patRepo.findById(id);

        if (existing.isEmpty()) {
            return "Patient not found";
        }

        if (!existing.get().getAppointments().isEmpty()) {
            return "Patient has appointments, delete them first";
        }

        patRepo.delete(existing.get());

        return "Patient is deleted successfully";
    }
}