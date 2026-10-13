package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.OfficeRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository docRepo;

    @Autowired
    private OfficeRepository offRepo;

    @Autowired
    private SpecializationRepository specRepo;

    // CREATE
    public String saveDoctor(Doctor doctor) {
        Optional<Doctor> checkDoctor =
                docRepo.findByFirstNameAndLastNameAndDateOfBirthAndOffice(
                        doctor.getFirstName(),
                        doctor.getLastName(),
                        doctor.getDateOfBirth(),
                        doctor.getOffice());

        if (checkDoctor.isPresent()) {
            return "That doctor already exists!";
        }

        if (doctor.getOffice() != null
                && docRepo.existsByOffice(doctor.getOffice())) {
            return "That office is already occupied";
        }

        String officeMessage =
                attachOffice(doctor, doctor.getOffice(), null);

        if (officeMessage != null) {
            return officeMessage;
        }

        doctor.setId(null);
        docRepo.save(doctor);

        return "Doctor is saved successfully";
    }

    // READ ALL
    public List<Doctor> getAllDoctors() {
        return docRepo.findAll();
    }

    // READ ONE
    public Optional<Doctor> getDoctorById(UUID id) {
        return docRepo.findById(id);
    }

    // B1 - Doctors by specialization
    public List<Doctor> getDoctorsBySpecialization(String name) {
        return docRepo.findDoctorsBySpecialization(name);
    }

    // B2 - Doctors without an office
    public List<Doctor> getDoctorsWithoutOffice() {
        return docRepo.findDoctorsWithoutOffice();
    }

    // A1 - Add specialization to doctor
    @Transactional
    public String addSpecialization(
            UUID doctorId,
            UUID specializationId) {

        Optional<Doctor> doctorResult =
                docRepo.findById(doctorId);

        if (doctorResult.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> specializationResult =
                specRepo.findById(specializationId);

        if (specializationResult.isEmpty()) {
            return "That specialization does not exist";
        }

        Doctor doctor = doctorResult.get();
        Specialization specialization = specializationResult.get();

        boolean alreadyExists = doctor.getSpecializations()
                .stream()
                .anyMatch(s -> s.getId().equals(specializationId));

        if (alreadyExists) {
            return "The doctor already has that specialization";
        }

        doctor.getSpecializations().add(specialization);
        docRepo.save(doctor);

        return "Specialization added to the doctor";
    }

    // A2 - Remove specialization from doctor
    @Transactional
    public String removeSpecialization(
            UUID doctorId,
            UUID specializationId) {

        Optional<Doctor> doctorResult =
                docRepo.findById(doctorId);

        if (doctorResult.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Specialization> specializationResult =
                specRepo.findById(specializationId);

        if (specializationResult.isEmpty()) {
            return "That specialization does not exist";
        }

        Doctor doctor = doctorResult.get();

        boolean removed = doctor.getSpecializations()
                .removeIf(s -> s.getId().equals(specializationId));

        if (!removed) {
            return "The doctor does not have that specialization";
        }

        docRepo.save(doctor);

        return "Specialization removed from the doctor";
    }

    // A4 - Change doctor's office by office number
    @Transactional
    public String changeDoctorOffice(
            UUID doctorId,
            Integer officeNumber) {

        Optional<Doctor> doctorResult =
                docRepo.findById(doctorId);

        if (doctorResult.isEmpty()) {
            return "The doctor with that id does not exist";
        }

        Optional<Office> officeResult =
                offRepo.findByOfficeNumber(officeNumber);

        if (officeResult.isEmpty()) {
            return "The office with that office number does not exist";
        }

        Doctor doctor = doctorResult.get();
        Office newOffice = officeResult.get();

        Doctor occupant = newOffice.getDoctor();

        if (occupant != null
                && !occupant.getId().equals(doctorId)) {
            return "That office is taken by other doctor";
        }

        doctor.setOffice(newOffice);
        docRepo.save(doctor);

        return "Office changed successfully";
    }

    // UPDATE
    public String updateDoctor(UUID id, Doctor newDoctor) {
        Optional<Doctor> existing =
                docRepo.findById(id);

        if (existing.isEmpty()) {
            return "Doctor not found";
        }

        Doctor doctor = existing.get();

        String officeMessage =
                attachOffice(doctor, newDoctor.getOffice(), id);

        if (officeMessage != null) {
            return officeMessage;
        }

        doctor.setFirstName(newDoctor.getFirstName());
        doctor.setLastName(newDoctor.getLastName());
        doctor.setDateOfBirth(newDoctor.getDateOfBirth());

        docRepo.save(doctor);

        return "Doctor is updated successfully";
    }

    // DELETE
    @Transactional
    public String deleteDoctor(UUID id) {
        Optional<Doctor> existing =
                docRepo.findById(id);

        if (existing.isEmpty()) {
            return "Doctor not found";
        }

        Doctor doctor = existing.get();

        if (!doctor.getAppointments().isEmpty()) {
            return "Doctor has appointments, delete them first";
        }

        doctor.getSpecializations().clear();
        docRepo.save(doctor);
        docRepo.delete(doctor);

        return "Doctor is deleted successfully";
    }

    // OFFICE HANDLING
    private String attachOffice(
            Doctor doctor,
            Office requestedOffice,
            UUID doctorId) {

        if (requestedOffice == null
                || requestedOffice.getId() == null) {
            doctor.setOffice(null);
            return null;
        }

        Optional<Office> office =
                offRepo.findById(requestedOffice.getId());

        if (office.isEmpty()) {
            return "Office not found";
        }

        Doctor occupant = office.get().getDoctor();

        if (occupant != null
                && !occupant.getId().equals(doctorId)) {
            return "That office is already assigned to another doctor";
        }

        doctor.setOffice(office.get());
        return null;
    }
}