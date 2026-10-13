package kigali.clinic.rw.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.repository.OfficeRepository;

@Service
public class OfficeService {

    @Autowired
    private OfficeRepository officeRepo;

    // CREATE
    public String saveOffice(Office office) {
        Optional<Office> existing =
                officeRepo.findByOfficeNumber(
                        office.getOfficeNumber());

        if (existing.isPresent()) {
            return "That office already exists!";
        }

        officeRepo.save(office);
        return "Office is saved successfully";
    }

    // READ ALL
    public List<Office> getAllOffices() {
        return officeRepo.findAll();
    }

    // READ ONE
    public Optional<Office> getOfficeById(UUID id) {
        return officeRepo.findById(id);
    }

    // A3 - Free offices
    public List<Office> getFreeOffices() {
        return officeRepo.findFreeOffices();
    }

    // Existing C3 - Busiest office
    public String getBusiestOffice() {
        List<Object[]> results =
                officeRepo.findBusiestOffice();

        if (results.isEmpty()) {
            return "No appointments yet";
        }

        Object[] row = results.get(0);

        return "Office Name: " + row[0]
                + ", Office Number: " + row[1]
                + ", Appointment Count: " + row[2];
    }

    // UPDATE
    public String updateOffice(UUID id, Office newOffice) {
        Optional<Office> existing =
                officeRepo.findById(id);

        if (existing.isEmpty()) {
            return "Office not found";
        }

        Office office = existing.get();
        office.setName(newOffice.getName());
        office.setOfficeNumber(newOffice.getOfficeNumber());

        officeRepo.save(office);
        return "Office is updated successfully";
    }

    // DELETE
    public String deleteOffice(UUID id) {
        Optional<Office> existing =
                officeRepo.findById(id);

        if (existing.isEmpty()) {
            return "Office not found";
        }

        officeRepo.delete(existing.get());
        return "Office is deleted successfully";
    }
}