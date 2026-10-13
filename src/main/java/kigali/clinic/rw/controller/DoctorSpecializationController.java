package kigali.clinic.rw.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorSpecializationController {

    @Autowired
    private DoctorService doctorService;

    // A1 - Add specialization
    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecialization(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String result = doctorService.addSpecialization(
                doctorId, specializationId);

        if (result.equals("Specialization added to the doctor")) {
            return ResponseEntity.ok(result);
        }

        if (result.equals("The doctor with that id does not exist")
                || result.equals("That specialization does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        if (result.equals("The doctor already has that specialization")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }

        return ResponseEntity.badRequest().body(result);
    }

    // A2 - Remove specialization
    @DeleteMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> removeSpecialization(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String result = doctorService.removeSpecialization(
                doctorId, specializationId);

        if (result.equals("Specialization removed from the doctor")) {
            return ResponseEntity.ok(result);
        }

        if (result.equals("The doctor with that id does not exist")
                || result.equals("That specialization does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        if (result.equals(
                "The doctor does not have that specialization")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }

        return ResponseEntity.badRequest().body(result);
    }

    // A4 - Change doctor's office
    @PatchMapping("/{doctorId}/office")
    public ResponseEntity<String> changeDoctorOffice(
            @PathVariable UUID doctorId,
            @RequestParam Integer officeNumber) {

        String result = doctorService.changeDoctorOffice(
                doctorId, officeNumber);

        if (result.equals("Office changed successfully")) {
            return ResponseEntity.ok(result);
        }

        if (result.equals("The doctor with that id does not exist")
                || result.equals(
                        "The office with that office number does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        if (result.equals("That office is taken by other doctor")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }

        return ResponseEntity.badRequest().body(result);
    }
}