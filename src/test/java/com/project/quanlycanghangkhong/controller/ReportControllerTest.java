package com.project.quanlycanghangkhong.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quanlycanghangkhong.dto.request.ReportRequest;
import com.project.quanlycanghangkhong.model.ReportType;
import com.project.quanlycanghangkhong.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

/**
 * Unit Tests cho ReportController
 */
@WebMvcTest(ReportController.class)
@DisplayName("ReportController Tests")
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReportRequest sampleReportRequest;

    @BeforeEach
    void setUp() {
        sampleReportRequest = ReportRequest.builder()
            .reportType(ReportType.TASK_REPORT)
            .startDate(LocalDateTime.now().minusDays(7))
            .endDate(LocalDateTime.now())
            .selectedFields(Arrays.asList("task.title", "task.status", "task.createdAt"))
            .build();
    }

    @Test
    @DisplayName("GET /api/reports/types - Should return available report types for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void getAvailableReportTypes_WithAdminRole_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/reports/types")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/reports/types - Should return 403 for USER role")
    @WithMockUser(roles = "USER")
    void getAvailableReportTypes_WithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/reports/types")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/reports/fields - Should return report fields for MANAGER")
    @WithMockUser(roles = "MANAGER")
    void getReportFields_WithManagerRole_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/reports/fields")
                .param("reportType", "TASK_REPORT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpected(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /api/reports/generate - Should generate report for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void generateReport_WithValidRequest_ShouldReturnSuccess() throws Exception {
        String requestJson = objectMapper.writeValueAsString(sampleReportRequest);

        mockMvc.perform(post("/api/reports/generate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /api/reports/export/excel - Should export to Excel for MANAGER")
    @WithMockUser(roles = "MANAGER")
    void exportReportToExcel_WithValidRequest_ShouldReturnExcelFile() throws Exception {
        String requestJson = objectMapper.writeValueAsString(sampleReportRequest);

        mockMvc.perform(post("/api/reports/export/excel")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/reports/quick/task-report - Should return quick task report")
    @WithMockUser(roles = "ADMIN")
    void getQuickTaskReport_WithAdminRole_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/reports/quick/task-report")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/reports/quick/performance-report - Should return quick performance report")
    @WithMockUser(roles = "MANAGER")
    void getQuickPerformanceReport_WithManagerRole_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/reports/quick/performance-report")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/reports/quick/overdue-report - Should return quick overdue report")
    @WithMockUser(roles = "ADMIN")
    void getQuickOverdueReport_WithAdminRole_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/reports/quick/overdue-report")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /api/reports/generate - Should return 400 for invalid request")
    @WithMockUser(roles = "ADMIN")
    void generateReport_WithInvalidRequest_ShouldReturn400() throws Exception {
        // Request without required fields
        ReportRequest invalidRequest = ReportRequest.builder().build();
        String requestJson = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(post("/api/reports/generate")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/reports/export/pdf - Should export to PDF for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void exportReportToPdf_WithValidRequest_ShouldReturnPdfFile() throws Exception {
        String requestJson = objectMapper.writeValueAsString(sampleReportRequest);

        mockMvc.perform(post("/api/reports/export/pdf")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/reports/export/csv - Should export to CSV for MANAGER")
    @WithMockUser(roles = "MANAGER")
    void exportReportToCsv_WithValidRequest_ShouldReturnCsvFile() throws Exception {
        String requestJson = objectMapper.writeValueAsString(sampleReportRequest);

        mockMvc.perform(post("/api/reports/export/csv")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }
}
