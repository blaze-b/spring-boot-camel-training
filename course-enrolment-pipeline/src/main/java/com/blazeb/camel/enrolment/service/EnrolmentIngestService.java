package com.blazeb.camel.enrolment.service;

import com.blazeb.camel.enrolment.model.EnrolmentRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;

@Service
public class EnrolmentIngestService {

    private final JdbcTemplate jdbcTemplate;

    public EnrolmentIngestService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public long ingest(EnrolmentRequest request) {
        String idempotencyKey = request.getStudentId() + "|" + request.getCourseCode() + "|" + request.getEnrolledDate();
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        """
                        INSERT INTO enrolment (student_id, course_code, enrolled_date, status, idempotency_key)
                        VALUES (?, ?, ?, 'PENDING', ?)
                        """,
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, request.getStudentId());
                ps.setString(2, request.getCourseCode());
                ps.setDate(3, Date.valueOf(request.getEnrolledDate()));
                ps.setString(4, idempotencyKey);
                return ps;
            }, keyHolder);
            Number key = keyHolder.getKey();
            if (key == null) {
                throw new IllegalStateException("Insert did not return an id");
            }
            return key.longValue();
        } catch (DuplicateKeyException duplicate) {
            Long existing = jdbcTemplate.queryForObject(
                    "SELECT id FROM enrolment WHERE idempotency_key = ?",
                    Long.class,
                    idempotencyKey);
            if (existing == null) {
                throw duplicate;
            }
            return existing;
        }
    }
}
