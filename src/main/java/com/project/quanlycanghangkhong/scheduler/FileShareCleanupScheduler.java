package com.project.quanlycanghangkhong.scheduler;

import com.project.quanlycanghangkhong.service.FileShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FileShareCleanupScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(FileShareCleanupScheduler.class);
    
    @Autowired
    private FileShareService fileShareService;
    
    /**
     * Chạy cleanup expired file shares mỗi ngày lúc 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredFileShares() {
        try {
            logger.info("Starting cleanup of expired file shares...");
            fileShareService.cleanupExpiredShares();
            logger.info("Completed cleanup of expired file shares");
        } catch (Exception e) {
            logger.error("Error during cleanup of expired file shares", e);
        }
    }
    
    /**
     * Chạy cleanup expired file shares mỗi 6 tiếng (optional - cho cleanup thường xuyên hơn)
     */
    @Scheduled(fixedRate = 21600000) // 6 tiếng = 6 * 60 * 60 * 1000ms
    public void cleanupExpiredFileSharesFrequent() {
        try {
            logger.debug("Running frequent cleanup of expired file shares...");
            fileShareService.cleanupExpiredShares();
        } catch (Exception e) {
            logger.error("Error during frequent cleanup of expired file shares", e);
        }
    }
}