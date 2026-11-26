package com.blazeb.camel.enrolment.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class EnrolmentRequest {

    @NotBlank
    @Pattern(regexp = "STU-[0-9]{4}", message = "studentId must look like STU-1234")
    private String studentId;

    @NotBlank
    @Pattern(regexp = "CRS-[A-Z]{3}-[0-9]{3}", message = "courseCode must look like CRS-CAM-101")
    private String courseCode;

    @NotNull
    private LocalDate enrolledDate;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public LocalDate getEnrolledDate() {
        return enrolledDate;
    }

    public void setEnrolledDate(LocalDate enrolledDate) {
        this.enrolledDate = enrolledDate;
    }
}
