package kigali.clinic.rw.controller;

import java.util.List;
import java.util.Optional;
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

import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.service.PatientService;

@RestController
@RequestMapping(value = "/api/patient")
public class PatientController {

    @Autowired
    private PatientService patServe;

    // CREATE
    @PostMapping(value = "/save")
    public ResponseEntity<?> savePatient(
            @RequestBody Patient patient) {

        String returnedMessage =
                patServe.savePatient(patient);

        if (returnedMessage.equals(
                "Patient is saved successfully")) {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.OK);

        } else {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.CONFLICT);
        }
    }

    // A1
    @GetMapping("/by-last-name")
    public ResponseEntity<List<Patient>>
    getPatientsByLastName(
            @RequestParam String lastName) {

        return new ResponseEntity<>(
                patServe.getPatientsByLastName(lastName),
                HttpStatus.OK);
    }

    // B4
    @GetMapping("/of-doctor/{doctorId}")
    public ResponseEntity<?> getPatientsOfDoctor(
            @PathVariable UUID doctorId) {

        List<Patient> patients =
                patServe.getPatientsOfDoctor(doctorId);

        if (patients == null) {

            return new ResponseEntity<>(
                    "The doctor with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                patients,
                HttpStatus.OK);
    }

    // C2
    @GetMapping("/frequent")
    public ResponseEntity<List<Patient>>
    getFrequentPatients(
            @RequestParam("min") long min) {

        return new ResponseEntity<>(
                patServe.getFrequentPatients(min),
                HttpStatus.OK);
    }

    // READ ALL
    @GetMapping(value = "/all")
    public ResponseEntity<List<Patient>>
    getAllPatients() {

        return new ResponseEntity<>(
                patServe.getAllPatients(),
                HttpStatus.OK);
    }

    // READ ONE
    @GetMapping(value = "/{id:[0-9a-fA-F-]{36}}")
    public ResponseEntity<?> getPatientById(
            @PathVariable UUID id) {

        Optional<Patient> patient =
                patServe.getPatientById(id);

        if (patient.isPresent()) {

            return new ResponseEntity<>(
                    patient.get(),
                    HttpStatus.OK);

        } else {

            return new ResponseEntity<>(
                    "Patient not found",
                    HttpStatus.NOT_FOUND);
        }
    }

    // UPDATE
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updatePatient(
            @PathVariable UUID id,
            @RequestBody Patient patient) {

        String returnedMessage =
                patServe.updatePatient(id, patient);

        if (returnedMessage.equals(
                "Patient is updated successfully")) {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.OK);

        } else {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.NOT_FOUND);
        }
    }

    // DELETE
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deletePatient(
            @PathVariable UUID id) {

        String returnedMessage =
                patServe.deletePatient(id);

        if (returnedMessage.equals(
                "Patient is deleted successfully")) {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.OK);

        } else if (returnedMessage.equals(
                "Patient not found")) {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.NOT_FOUND);

        } else {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.CONFLICT);
        }
    }

   
  
    // D2 - Upcoming appointments of a patient
    @GetMapping("/{ssNumber}/upcoming")
    public ResponseEntity<?> getUpcomingAppointments(
            @PathVariable String ssNumber) {

        return patServe.getUpcomingAppointments(ssNumber);
    }

}