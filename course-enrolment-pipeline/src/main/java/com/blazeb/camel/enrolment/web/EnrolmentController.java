package com.blazeb.camel.enrolment.web;

import com.blazeb.camel.enrolment.model.EnrolmentRequest;
import com.blazeb.camel.enrolment.service.EnrolmentIngestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/enrolments")
public class EnrolmentController {

    private final EnrolmentIngestService ingestService;

    public EnrolmentController(EnrolmentIngestService ingestService) {
        this.ingestService = ingestService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody EnrolmentRequest request) {
        long id = ingestService.ingest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", id,
                "status", "PENDING",
                "studentId", request.getStudentId(),
                "courseCode", request.getCourseCode()
        ));
    }
}
