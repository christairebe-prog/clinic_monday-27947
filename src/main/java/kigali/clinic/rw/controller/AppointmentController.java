package kigali.clinic.rw.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.dto.OverdueAppointmentDto;
import kigali.clinic.rw.service.AppointmentService;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appServe;

    @PostMapping("/save")
    public ResponseEntity<String> saveAppointment(
            @RequestBody Appointment appointment,
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID doctorId) {

        String response =
                appServe.saveAppointment(
                        appointment,
                        patientId,
                        doctorId);

        if (response.equals(
                "Doctor is already booked on that date")) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.CONFLICT);
        }

        if (response.equals(
                "Patient id and doctor id are required")) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST);
        }

        if (response.equals(
                "That patient or doctor is not available")) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND);
        }

        if (response.equals(
                "Appointment date is required")) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Appointment>>
    getAllAppointments() {

        return new ResponseEntity<>(
                appServe.getAllAppointments(),
                HttpStatus.OK);
    }

    @GetMapping("/by-status")
    public ResponseEntity<?> getAppointmentsByStatus(
            @RequestParam("status") String status) {

        try {

            AppointmentStatus appointmentStatus =
                    AppointmentStatus.valueOf(
                            status.toUpperCase());

            return new ResponseEntity<>(
                    appServe.getAppointmentsByStatus(
                            appointmentStatus),
                    HttpStatus.OK);

        } catch (IllegalArgumentException e) {

            return new ResponseEntity<>(
                    "Invalid appointment status",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/between")
    public ResponseEntity<List<Appointment>>
    getAppointmentsBetweenDates(
            @RequestParam("start") LocalDate start,
            @RequestParam("end") LocalDate end) {

        return new ResponseEntity<>(
                appServe.getAppointmentsBetweenDates(
                        start,
                        end),
                HttpStatus.OK);
    }

    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Map<String, Object>>>
    getAppointmentStatsByStatus() {

        return new ResponseEntity<>(
                appServe.getAppointmentStatsByStatus(),
                HttpStatus.OK);
    }

    // C4
    @PatchMapping("/cancel-day")
    public ResponseEntity<String>
    cancelAppointmentsForDoctorOnDate(
            @RequestParam("doctorId") UUID doctorId,
            @RequestParam("date") LocalDate date) {

        String response =
                appServe.cancelAppointmentsForDoctorOnDate(
                        doctorId,
                        date);

        return new ResponseEntity<>(
                response,
                HttpStatus.OK);
    }

    // Bonus 1
    @GetMapping("/page")
    public ResponseEntity<Page<Appointment>>
    getAppointmentsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appointmentDate,asc")
            String sort) {

        return new ResponseEntity<>(
                appServe.getAppointmentsPage(
                        page,
                        size,
                        sort),
                HttpStatus.OK);
    }

    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public ResponseEntity<?> getAppointmentById(
            @PathVariable UUID id) {

        Optional<Appointment> appointment =
                appServe.getAppointmentById(id);

        if (appointment.isEmpty()) {

            return new ResponseEntity<>(
                    "Appointment not found",
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                appointment.get(),
                HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>>
    getAppointmentsByDoctor(
            @PathVariable UUID doctorId) {

        return new ResponseEntity<>(
                appServe.getAppointmentsByDoctor(doctorId),
                HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>>
    getAppointmentsByPatient(
            @PathVariable UUID patientId) {

        return new ResponseEntity<>(
                appServe.getAppointmentsByPatient(patientId),
                HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateAppointment(
            @PathVariable UUID id,
            @RequestBody Appointment newAppointment) {

        String response =
                appServe.updateAppointment(
                        id,
                        newAppointment);

        if (response.equals("Appointment not found")) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND);
        }

        if (response.equals(
                "That patient is not available")
                || response.equals(
                "That doctor is not available")) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                response,
                HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAppointment(
            @PathVariable UUID id) {

        String response =
                appServe.deleteAppointment(id);

        if (response.equals("Appointment not found")) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                response,
                HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}/pending")
    public ResponseEntity<List<Appointment>>
    getPendingAppointmentsOfDoctor(
            @PathVariable UUID doctorId) {

        return new ResponseEntity<>(
                appServe.getPendingAppointmentsOfDoctor(
                        doctorId),
                HttpStatus.OK);
    }

    @GetMapping("/pending/overdue")
    public ResponseEntity<List<OverdueAppointmentDto>>
    getOverduePendingAppointments() {

        return new ResponseEntity<>(
                appServe.getOverduePendingAppointments(),
                HttpStatus.OK);
    }

    @GetMapping("/count-per-doctor")
    public ResponseEntity<List<Map<String, Object>>>
    getAppointmentCountPerDoctor() {

        return new ResponseEntity<>(
                appServe.getAppointmentCountPerDoctor(),
                HttpStatus.OK);
    }

    @GetMapping("/specialization/{name}")
    public ResponseEntity<List<Appointment>>
    getAppointmentsBySpecialization(
            @PathVariable String name) {

        return new ResponseEntity<>(
                appServe.getAppointmentsBySpecialization(name),
                HttpStatus.OK);
    }

    @GetMapping("/doctor/{doctorId}/page")
    public ResponseEntity<Page<Appointment>>
    getDoctorAppointmentsPage(
            @PathVariable UUID doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return new ResponseEntity<>(
                appServe.getDoctorAppointmentsPage(
                        doctorId,
                        page,
                        size),
                HttpStatus.OK);
    }
    // Bonus 2
@DeleteMapping("/cancelled-before")
public ResponseEntity<String> deleteCancelledBefore(
        @RequestParam("date") LocalDate date) {

    String response =
            appServe.deleteCancelledBefore(date);

    return new ResponseEntity<>(
            response,
            HttpStatus.OK);
}
}