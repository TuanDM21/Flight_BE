package com.project.quanlycanghangkhong.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestExportController {

    @PostMapping("/export/simple")
    public ResponseEntity<byte[]> testSimpleExport() {
        try {
            log.info("Testing simple export");
            String testContent = "Test CSV Content\nColumn1,Column2\nValue1,Value2\n";
            byte[] data = testContent.getBytes("UTF-8");
            
            log.info("Generated data: {} bytes", data.length);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=test.csv");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(data);
        } catch (Exception e) {
            log.error("Error in test export: ", e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
