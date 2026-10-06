package kigali.clinic.rw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Specialization;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.SpecializationRepository;

@Service
public class SpecializationService {

    @Autowired
    private SpecializationRepository specRepo;

    @Autowired
    private DoctorRepository docRepo;

    // CREATE
    public String saveSpecialization(Specialization specialization) {

        Optional<Specialization> checkSpec =
                specRepo.findByNameIgnoreCase(
                        specialization.getName());

        if (checkSpec.isPresent()) {
            return "That specialization already exists!";
        }

        specialization.setId(null);
        specialization.setDoctors(new ArrayList<>());

        specRepo.save(specialization);

        return "Specialization is saved successfully";
    }

    // READ ALL
    public List<Specialization> getAllSpecializations() {
        return specRepo.findAll();
    }

    // READ ONE
    public Optional<Specialization> getSpecializationById(UUID id) {
        return specRepo.findById(id);
    }

    // B3
    public List<Specialization> getUnusedSpecializations() {
        return specRepo.findUnusedSpecializations();
    }

    // UPDATE
    public String updateSpecialization(
            UUID id,
            Specialization newSpec) {

        Optional<Specialization> existing =
                specRepo.findById(id);

        if (existing.isEmpty()) {
            return "Specialization not found";
        }

        Optional<Specialization> sameName =
                specRepo.findByNameIgnoreCase(
                        newSpec.getName());

        if (sameName.isPresent()
                && !sameName.get().getId().equals(id)) {

            return "That specialization already exists!";
        }

        Specialization specialization = existing.get();

        specialization.setName(newSpec.getName());

        specRepo.save(specialization);

        return "Specialization is updated successfully";
    }

    // DELETE
    public String deleteSpecialization(UUID id) {

        Optional<Specialization> existing =
                specRepo.findById(id);

        if (existing.isEmpty()) {
            return "Specialization not found";
        }

        specRepo.delete(existing.get());

        return "Specialization is deleted successfully";
    }

    // MANY-TO-MANY: assign doctor
    public String assignDoctor(
            UUID specializationId,
            UUID doctorId) {

        Optional<Specialization> spec =
                specRepo.findById(specializationId);

        if (spec.isEmpty()) {
            return "Specialization not found";
        }

        Optional<Doctor> doctor =
                docRepo.findById(doctorId);

        if (doctor.isEmpty()) {
            return "Doctor not found";
        }

        boolean alreadyLinked =
                spec.get().getDoctors().stream()
                        .anyMatch(d ->
                                d.getId().equals(doctorId));

        if (alreadyLinked) {
            return "Doctor already has that specialization";
        }

        spec.get().getDoctors().add(doctor.get());

        specRepo.save(spec.get());

        return "Doctor is assigned to the specialization successfully";
    }

    // MANY-TO-MANY: remove doctor
    public String removeDoctor(
            UUID specializationId,
            UUID doctorId) {

        Optional<Specialization> spec =
                specRepo.findById(specializationId);

        if (spec.isEmpty()) {
            return "Specialization not found";
        }

        boolean removed =
                spec.get().getDoctors()
                        .removeIf(d ->
                                d.getId().equals(doctorId));

        if (!removed) {
            return "Doctor does not have that specialization";
        }

        specRepo.save(spec.get());

        return "Doctor is removed from the specialization successfully";
    }
}