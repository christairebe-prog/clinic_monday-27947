package kigali.clinic.rw.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.dto.OverdueAppointmentDto;
import kigali.clinic.rw.repository.AppointmentRepository;
import kigali.clinic.rw.repository.DoctorRepository;
import kigali.clinic.rw.repository.PatientRepository;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appRepo;

    @Autowired
    private DoctorRepository docRepo;

    @Autowired
    private PatientRepository patRepo;

    public String saveAppointment(
            Appointment appointment,
            UUID patientId,
            UUID doctorId) {

        if (patientId == null && appointment.getPatient() != null) {
            patientId = appointment.getPatient().getId();
        }

        if (doctorId == null && appointment.getDoctor() != null) {
            doctorId = appointment.getDoctor().getId();
        }

        if (patientId == null || doctorId == null) {
            return "Patient id and doctor id are required";
        }

        Optional<Doctor> getDoctor =
                docRepo.findById(doctorId);

        Optional<Patient> getPatient =
                patRepo.findById(patientId);

        if (getPatient.isEmpty() || getDoctor.isEmpty()) {
            return "That patient or doctor is not available";
        }

        if (appointment.getAppointmentDate() == null) {
            return "Appointment date is required";
        }

        boolean alreadyBooked =
                appRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                        doctorId,
                        appointment.getAppointmentDate(),
                        AppointmentStatus.CANCELLED);

        if (alreadyBooked) {
            return "Doctor is already booked on that date";
        }

        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatus.PENDING);
        }

        appointment.setId(null);
        appointment.setDoctor(getDoctor.get());
        appointment.setPatient(getPatient.get());

        appRepo.save(appointment);

        return "Appointment is saved successfully";
    }

    public List<Appointment> getAllAppointments() {
        return appRepo.findAll();
    }

    public Optional<Appointment> getAppointmentById(UUID id) {
        return appRepo.findById(id);
    }

    public List<Appointment> getAppointmentsByDoctor(UUID doctorId) {
        return appRepo.findByDoctorId(doctorId);
    }

    public List<Appointment> getAppointmentsByPatient(UUID patientId) {
        return appRepo.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByStatus(
            AppointmentStatus status) {

        return appRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getAppointmentsBetweenDates(
            LocalDate start,
            LocalDate end) {

        return appRepo
                .findByAppointmentDateBetweenOrderByAppointmentDateAsc(
                        start,
                        end);
    }

    // C1
    public List<Map<String, Object>> getAppointmentStatsByStatus() {

        List<Object[]> results =
                appRepo.countAppointmentsByStatus();

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (Object[] row : results) {

            Map<String, Object> item =
                    new LinkedHashMap<>();

            item.put("status", row[0]);
            item.put("count", row[1]);

            response.add(item);
        }

        return response;
    }

    // C4
    public String cancelAppointmentsForDoctorOnDate(
            UUID doctorId,
            LocalDate date) {

        int cancelled =
                appRepo.cancelAppointmentsForDoctorOnDate(
                        doctorId,
                        date);

        return cancelled + " appointments cancelled";
    }

    // Bonus 1
    public Page<Appointment> getAppointmentsPage(
            int page,
            int size,
            String sort) {

        String[] sortParts = sort.split(",");

        String property = sortParts[0];

        Sort.Direction direction =
                Sort.Direction.ASC;

        if (sortParts.length > 1
                && sortParts[1].equalsIgnoreCase("desc")) {

            direction = Sort.Direction.DESC;
        }

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(direction, property));

        return appRepo.findAll(pageable);
    }

    public String updateAppointment(
            UUID id,
            Appointment newAppointment) {

        Optional<Appointment> existing =
                appRepo.findById(id);

        if (existing.isEmpty()) {
            return "Appointment not found";
        }

        Appointment appointment = existing.get();

        appointment.setAppointmentDate(
                newAppointment.getAppointmentDate());

        appointment.setReason(
                newAppointment.getReason());

        appointment.setStatus(
                newAppointment.getStatus());

        if (newAppointment.getPatient() != null
                && newAppointment.getPatient().getId() != null) {

            Optional<Patient> patient =
                    patRepo.findById(
                            newAppointment.getPatient().getId());

            if (patient.isEmpty()) {
                return "That patient is not available";
            }

            appointment.setPatient(patient.get());
        }

        if (newAppointment.getDoctor() != null
                && newAppointment.getDoctor().getId() != null) {

            Optional<Doctor> doctor =
                    docRepo.findById(
                            newAppointment.getDoctor().getId());

            if (doctor.isEmpty()) {
                return "That doctor is not available";
            }

            appointment.setDoctor(doctor.get());
        }

        appRepo.save(appointment);

        return "Appointment is updated successfully";
    }

    public String deleteAppointment(UUID id) {

        Optional<Appointment> existing =
                appRepo.findById(id);

        if (existing.isEmpty()) {
            return "Appointment not found";
        }

        appRepo.delete(existing.get());

        return "Appointment is deleted successfully";
    }

    public List<Appointment> getPendingAppointmentsOfDoctor(
            UUID doctorId) {

        return appRepo
                .findByDoctorIdAndStatusOrderByAppointmentDateAsc(
                        doctorId,
                        AppointmentStatus.PENDING);
    }

    public List<OverdueAppointmentDto>
    getOverduePendingAppointments() {

        return appRepo.findPastAppointmentsByStatus(
                AppointmentStatus.PENDING);
    }

    public List<Map<String, Object>>
    getAppointmentCountPerDoctor() {

        List<Object[]> results =
                appRepo.countAppointmentsPerDoctor();

        List<Map<String, Object>> response =
                new ArrayList<>();

        for (Object[] row : results) {

            Map<String, Object> item =
                    new LinkedHashMap<>();

            item.put("doctorId", row[0]);
            item.put("firstName", row[1]);
            item.put("lastName", row[2]);
            item.put("appointmentCount", row[3]);

            response.add(item);
        }

        return response;
    }

    public List<Appointment>
    getAppointmentsBySpecialization(
            String specializationName) {

        return appRepo.findByDoctorSpecialization(
                specializationName);
    }
// Bonus 2
public String deleteCancelledBefore(LocalDate date) {

    int deleted =
            appRepo.deleteCancelledBefore(date);

    return deleted + " cancelled appointments deleted";
}
    public Page<Appointment> getDoctorAppointmentsPage(
            UUID doctorId,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.ASC,
                                "appointmentDate"));

        return appRepo.findByDoctorId(
                doctorId,
                pageable);
    }
}