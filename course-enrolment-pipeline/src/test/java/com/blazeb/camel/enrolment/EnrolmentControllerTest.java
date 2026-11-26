package com.blazeb.camel.enrolment;

import com.blazeb.camel.enrolment.service.EnrolmentIngestService;
import com.blazeb.camel.enrolment.web.EnrolmentController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnrolmentController.class)
class EnrolmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrolmentIngestService ingestService;

    @Test
    void createsEnrolment() throws Exception {
        when(ingestService.ingest(any())).thenReturn(42L);

        mockMvc.perform(post("/api/enrolments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "studentId": "STU-2001",
                                  "courseCode": "CRS-CAM-101",
                                  "enrolledDate": "2026-02-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
