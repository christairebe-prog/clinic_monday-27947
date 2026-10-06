package kigali.clinic.rw.dto;

import java.time.LocalDate;

public class OverdueAppointmentDto {

    private final String patientName;
    private final String doctorName;
    private final LocalDate appointmentDate;

    public OverdueAppointmentDto(String patientName, String doctorName, LocalDate appointmentDate) {
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }
}
