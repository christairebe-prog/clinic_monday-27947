package kigali.clinic.rw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinic.rw.domain.Office;
import kigali.clinic.rw.service.OfficeService;

@RestController
@RequestMapping("/api/offices")
public class FreeOfficeController {

    @Autowired
    private OfficeService officeService;

    // A3 - Get offices without a doctor
    @GetMapping("/free")
    public ResponseEntity<List<Office>> getFreeOffices() {
        return ResponseEntity.ok(
                officeService.getFreeOffices());
    }
}