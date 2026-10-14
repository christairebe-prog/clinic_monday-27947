package kigali.clinic.rw.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinic.rw.domain.Appointment;
import kigali.clinic.rw.domain.AppointmentStatus;
import kigali.clinic.rw.domain.Doctor;
import kigali.clinic.rw.domain.Patient;
import kigali.clinic.rw.dto.OverdueAppointmentDto;

@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByDoctor(Doctor doctor);

    List<Appointment> findByPatient(Patient patient);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByDoctorId(UUID doctorId);

    List<Appointment> findByPatientId(UUID patientId);

    // A2
    List<Appointment> findByStatusOrderByAppointmentDateAsc(
            AppointmentStatus status);

    // A3
    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            LocalDate start,
            LocalDate end);

    // A4
    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(
            UUID doctorId,
            LocalDate appointmentDate,
            AppointmentStatus status);

    // C1
    @Query("SELECT a.status, COUNT(a) " +
           "FROM Appointment a " +
           "GROUP BY a.status")
    List<Object[]> countAppointmentsByStatus();

    // C4
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a " +
           "SET a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED " +
           "WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDate = :date " +
           "AND a.status <> kigali.clinic.rw.domain.AppointmentStatus.COMPLETED")
    int cancelAppointmentsForDoctorOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("date") LocalDate date);

    // Bonus 1 - pagination
    Page<Appointment> findAll(Pageable pageable);

    // Existing Exercise 4
    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAsc(
            UUID doctorId,
            AppointmentStatus status);

    @Query("SELECT new kigali.clinic.rw.dto.OverdueAppointmentDto(" +
           "CONCAT(p.firstName, ' ', p.lastName), " +
           "CONCAT(d.firstName, ' ', d.lastName), " +
           "a.appointmentDate) " +
           "FROM Appointment a " +
           "JOIN a.patient p " +
           "JOIN a.doctor d " +
           "WHERE a.status = :status " +
           "AND a.appointmentDate < CURRENT_DATE " +
           "ORDER BY a.appointmentDate")
    List<OverdueAppointmentDto> findPastAppointmentsByStatus(
            @Param("status") AppointmentStatus status);

    @Query("SELECT d.id, d.firstName, d.lastName, COUNT(a) " +
           "FROM Doctor d " +
           "LEFT JOIN d.appointments a " +
           "GROUP BY d.id, d.firstName, d.lastName " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> countAppointmentsPerDoctor();

    @Query("SELECT a FROM Appointment a " +
           "JOIN a.doctor d " +
           "JOIN d.specializations s " +
           "WHERE LOWER(s.name) = LOWER(:specializationName) " +
           "ORDER BY a.appointmentDate")
    List<Appointment> findByDoctorSpecialization(
            @Param("specializationName") String specializationName);
// Bonus 2
@Modifying
@Transactional
@Query("DELETE FROM Appointment a " +
       "WHERE a.status = kigali.clinic.rw.domain.AppointmentStatus.CANCELLED " +
       "AND a.appointmentDate < :date")
int deleteCancelledBefore(
        @Param("date") LocalDate date);
    Page<Appointment> findByDoctorId(
            UUID doctorId,
            Pageable pageable);
           // D1 - Doctor's agenda for a specific day
List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNot(
        UUID doctorId,
        LocalDate appointmentDate,
        AppointmentStatus status);

        // D2 - Upcoming appointments for a patient
List<Appointment> findByPatient_SsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
        String ssNumber,
        LocalDate today,
        List<AppointmentStatus> statuses);
       
        long countByAppointmentDate(LocalDate appointmentDate);
}
