package com.blazeb.camel.enrolment;

import com.blazeb.camel.enrolment.model.EnrolmentRequest;
import com.blazeb.camel.enrolment.service.EnrolmentIngestService;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@CamelSpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@UseAdviceWith
@DirtiesContext
@Testcontainers(disabledWithoutDocker = true)
class CourseEnrolmentPipelineIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("enrolment")
            .withUsername("enrolment")
            .withPassword("enrolment");

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("enrolment.kafka.brokers", kafka::getBootstrapServers);
        registry.add("enrolment.poll.period", () -> "2000");
        registry.add("camel.springboot.main-run-controller", () -> "false");
    }

    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EnrolmentIngestService ingestService;

    @Test
    void endToEndIngestPollPublishAndAudit() throws Exception {
        AdviceWith.adviceWith(camelContext, "enrolment-poll", a -> a.replaceFromWith("direct:force-poll"));
        camelContext.start();

        EnrolmentRequest request = new EnrolmentRequest();
        request.setStudentId("STU-9001");
        request.setCourseCode("CRS-CAM-301");
        request.setEnrolledDate(LocalDate.of(2026, 3, 1));
        long id = ingestService.ingest(request);

        producerTemplate.sendBody("direct:force-poll", null);

        boolean published = false;
        boolean audited = false;
        for (int i = 0; i < 60; i++) {
            Integer pub = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM enrolment WHERE id = ? AND status = 'PUBLISHED'",
                    Integer.class, id);
            Integer audits = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM enrolment_audit WHERE enrolment_id = ?",
                    Integer.class, id);
            published = pub != null && pub == 1;
            audited = audits != null && audits >= 1;
            if (published && audited) {
                break;
            }
            Thread.sleep(1000L);
        }

        assertTrue(published, "enrolment should be PUBLISHED");
        assertTrue(audited, "audit row should exist");
    }
}
