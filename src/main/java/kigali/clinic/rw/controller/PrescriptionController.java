
package kigali.clinic.rw.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Prescription;
import kigali.clinic.rw.service.PrescriptionService;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    // C1: Save a prescription
    @PostMapping("/save")
    public ResponseEntity<String> savePrescription(
            @RequestParam UUID appointmentId,
            @RequestBody Prescription prescription) {

        String response = prescriptionService.savePrescription(appointmentId, prescription);

        if (response.equals("The appointment with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (response.equals("Prescriptions are only for COMPLETED appointments")
                || response.equals("Medicine name, dosage, and a positive duration are required")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // C2: Get prescriptions for an appointment
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<?> getPrescriptionsByAppointment(
            @PathVariable UUID appointmentId) {

        List<Prescription> prescriptions =
                prescriptionService.getPrescriptionsByAppointment(appointmentId);

        if (prescriptions == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("The appointment with that id does not exist");
        }

        return ResponseEntity.ok(prescriptions);
    }

    // C3: Get all prescriptions for a patient
    @GetMapping("/patient/{ssNumber}")
    public ResponseEntity<?> getPrescriptionsByPatient(
            @PathVariable String ssNumber) {

        List<Prescription> prescriptions =
                prescriptionService.getPrescriptionsByPatient(ssNumber);

        if (prescriptions == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("That patient with that ssnumber does not exist");
        }

        return ResponseEntity.ok(prescriptions);
    }

    // C4: Update a prescription
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updatePrescription(
            @PathVariable UUID id,
            @RequestBody Prescription prescription) {

        String response = prescriptionService.updatePrescription(id, prescription);

        if (response.equals("The prescription with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (response.equals("Medicine name, dosage, and a positive duration are required")) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    // C4: Delete a prescription
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePrescription(@PathVariable UUID id) {

        String response = prescriptionService.deletePrescription(id);

        if (response.equals("The prescription with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }
}
