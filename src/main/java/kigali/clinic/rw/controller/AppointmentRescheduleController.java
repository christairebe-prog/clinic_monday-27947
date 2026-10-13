package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentRescheduleController {

    @Autowired
    private AppointmentService appServe;

    @PatchMapping("/{id}/reschedule")
    public ResponseEntity<String> rescheduleAppointment(
            @PathVariable UUID id,
            @RequestParam LocalDate newDate) {

        String response = appServe.rescheduleAppointment(id, newDate);

        if (response.equals("The appointment with that id does not exist")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (response.equals("A COMPLETED appointment cannot be rescheduled")
                || response.equals("A CANCELLED appointment cannot be rescheduled")
                || response.equals("Appointment date cannot be in the past")
                || response.equals("Appointment date is required")) {
            return ResponseEntity.badRequest().body(response);
        }

        if (response.equals("Doctor is already booked on that date")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        return ResponseEntity.ok(response);
    }
}