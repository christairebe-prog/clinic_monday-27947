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

    // B1
    public List<Doctor> getDoctorsBySpecialization(String name) {
        return docRepo.findDoctorsBySpecialization(name);
    }

    // B2
    public List<Doctor> getDoctorsWithoutOffice() {
        return docRepo.findDoctorsWithoutOffice();
    }

    // UPDATE
    public String updateDoctor(
            UUID id,
            Doctor newDoctor) {

        Optional<Doctor> existing =
                docRepo.findById(id);

        if (existing.isEmpty()) {
            return "Doctor not found";
        }

        Doctor doctor = existing.get();

        String officeMessage =
                attachOffice(
                        doctor,
                        newDoctor.getOffice(),
                        id);

        if (officeMessage != null) {
            return officeMessage;
        }

        doctor.setFirstName(
                newDoctor.getFirstName());

        doctor.setLastName(
                newDoctor.getLastName());

        doctor.setDateOfBirth(
                newDoctor.getDateOfBirth());

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

        for (Specialization spec :
                doctor.getSpecializations()) {

            spec.getDoctors().removeIf(
                    d -> d.getId().equals(id));

            specRepo.save(spec);
        }

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
                offRepo.findById(
                        requestedOffice.getId());

        if (office.isEmpty()) {
            return "Office not found";
        }

        Doctor occupant =
                office.get().getDoctor();

        if (occupant != null
                && !occupant.getId().equals(doctorId)) {

            return "That office is already assigned to another doctor";
        }

        doctor.setOffice(office.get());

        return null;
    }
}