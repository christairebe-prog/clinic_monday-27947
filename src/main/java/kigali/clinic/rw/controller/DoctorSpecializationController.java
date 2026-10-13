package kigali.clinic.rw.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorSpecializationController {

    @Autowired
    private DoctorService doctorService;

    @PutMapping("/{doctorId}/specializations/{specializationId}")
    public ResponseEntity<String> addSpecialization(
            @PathVariable UUID doctorId,
            @PathVariable UUID specializationId) {

        String result = doctorService.addSpecialization(
                doctorId,
                specializationId
        );

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
}