package kigali.clinic.rw.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;
import kigali.clinic.rw.domain.Appointment;

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

import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.service.DoctorService;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService docServe;

    // CREATE
    @PostMapping("/save")
    public ResponseEntity<?> saveDoctor(
            @RequestBody Doctor doctor) {

        String returnedMessage =
                docServe.saveDoctor(doctor);

        if (returnedMessage.equals(
                "Doctor is saved successfully")) {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.OK);

        } else if (returnedMessage.equals(
                "Office not found")) {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.NOT_FOUND);

        } else {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.CONFLICT);
        }
    }

    // GET ALL
    @GetMapping("/all")
    public ResponseEntity<List<Doctor>> getAllDoctors() {

        return new ResponseEntity<>(
                docServe.getAllDoctors(),
                HttpStatus.OK);
    }

    // B1
    @GetMapping("/by-specialization")
    public ResponseEntity<List<Doctor>>
    getDoctorsBySpecialization(
            @RequestParam("name") String name) {

        return new ResponseEntity<>(
                docServe.getDoctorsBySpecialization(name),
                HttpStatus.OK);
    }

    // B2
    @GetMapping("/without-office")
    public ResponseEntity<List<Doctor>>
    getDoctorsWithoutOffice() {

        return new ResponseEntity<>(
                docServe.getDoctorsWithoutOffice(),
                HttpStatus.OK);
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<?> getDoctorById(
            @PathVariable UUID id) {

        Optional<Doctor> doctor =
                docServe.getDoctorById(id);

        if (doctor.isPresent()) {

            return new ResponseEntity<>(
                    doctor.get(),
                    HttpStatus.OK);

        } else {

            return new ResponseEntity<>(
                    "Doctor not found",
                    HttpStatus.NOT_FOUND);
        }
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateDoctor(
            @PathVariable UUID id,
            @RequestBody Doctor doctor) {

        String returnedMessage =
                docServe.updateDoctor(id, doctor);

        if (returnedMessage.equals(
                "Doctor is updated successfully")) {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.OK);

        } else if (returnedMessage.endsWith(
                "not found")) {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.NOT_FOUND);

        } else {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.CONFLICT);
        }
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDoctor(
            @PathVariable UUID id) {

        String returnedMessage =
                docServe.deleteDoctor(id);

        if (returnedMessage.equals(
                "Doctor is deleted successfully")) {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.OK);

        } else if (returnedMessage.equals(
                "Doctor not found")) {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.NOT_FOUND);

        } else {

            return new ResponseEntity<>(
                    returnedMessage,
                    HttpStatus.CONFLICT);
        }
    }

    // D1 - Doctor's agenda for a specific day
@GetMapping("/{doctorId}/agenda")
public ResponseEntity<?> getDoctorAgenda(
        @PathVariable UUID doctorId,
        @RequestParam("date") LocalDate date) {

    return docServe.getDoctorAgenda(doctorId, date);
}
}