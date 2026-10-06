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
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;

@RestController
@RequestMapping("/api/office")
public class OfficeController {

    @Autowired
    private OfficeService officeServe;

    // CREATE
    @PostMapping("/save")
    public ResponseEntity<String> saveOffice(
            @RequestBody Office office) {

        String response =
                officeServe.saveOffice(office);

        if (response.equals(
                "Office is saved successfully")) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK);

        } else {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.CONFLICT);
        }
    }

    // C3
    @GetMapping("/busiest")
    public ResponseEntity<String> getBusiestOffice() {

        String response =
                officeServe.getBusiestOffice();

        return new ResponseEntity<>(
                response,
                HttpStatus.OK);
    }

    // READ ALL
    @GetMapping("/all")
    public ResponseEntity<List<Office>> getAllOffices() {

        return new ResponseEntity<>(
                officeServe.getAllOffices(),
                HttpStatus.OK);
    }

    // READ ONE
    @GetMapping("/{id:[0-9a-fA-F-]{36}}")
    public ResponseEntity<?> getOfficeById(
            @PathVariable UUID id) {

        Optional<Office> office =
                officeServe.getOfficeById(id);

        if (office.isEmpty()) {

            return new ResponseEntity<>(
                    "Office not found",
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                office.get(),
                HttpStatus.OK);
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateOffice(
            @PathVariable UUID id,
            @RequestBody Office newOffice) {

        String response =
                officeServe.updateOffice(
                        id,
                        newOffice);

        if (response.equals(
                "Office not found")) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                response,
                HttpStatus.OK);
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteOffice(
            @PathVariable UUID id) {

        String response =
                officeServe.deleteOffice(id);

        if (response.equals(
                "Office not found")) {

            return new ResponseEntity<>(
                    response,
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(
                response,
                HttpStatus.OK);
    }
}