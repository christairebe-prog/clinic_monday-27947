package kigali.clinic.rw.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import kigali.clinic.rw.domain.Office;

@Repository
public interface OfficeRepository extends JpaRepository<Office, UUID> {

    Optional<Office> findByOfficeNumber(Integer officeNumber);

    // A3 - Find free offices, sorted by office number
    @Query("SELECT o FROM Office o " +
           "WHERE NOT EXISTS (" +
           "SELECT d.id FROM Doctor d WHERE d.office = o) " +
           "ORDER BY o.officeNumber ASC")
    List<Office> findFreeOffices();

    // Existing busiest-office query
    @Query("SELECT o.name, o.officeNumber, COUNT(a) " +
           "FROM Office o " +
           "JOIN Doctor d ON d.office.id = o.id " +
           "JOIN Appointment a ON a.doctor.id = d.id " +
           "GROUP BY o.id, o.name, o.officeNumber " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> findBusiestOffice();
}