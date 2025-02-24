package org.example.echo01.common.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.echo01.common.repositories.BookRepository;
import org.example.echo01.common.repositories.ChapterRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteScheduler {
    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;

    @Scheduled(cron = "0 0 0 * * *") // Run at midnight every day
    @Transactional
    public void cleanupSoftDeletedEntities() {
        LocalDateTime now = LocalDateTime.now();
        
        log.info("Starting cleanup of soft-deleted entities...");
        
        try {
            int deletedBooks = bookRepository.deleteByDeletedTrueAndPermanentDeleteAtBefore(now);
            log.info("Permanently deleted {} books", deletedBooks);
            
            int deletedChapters = chapterRepository.deleteByDeletedTrueAndPermanentDeleteAtBefore(now);
            log.info("Permanently deleted {} chapters", deletedChapters);
        } catch (Exception e) {
            log.error("Error during cleanup of soft-deleted entities", e);
        }
        
        log.info("Finished cleanup of soft-deleted entities");
    }
} 