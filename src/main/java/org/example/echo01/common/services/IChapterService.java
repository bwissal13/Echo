package org.example.echo01.common.services;

import org.example.echo01.common.dto.request.ChapterRequest;
import org.example.echo01.common.dto.response.ChapterResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IChapterService {
    ChapterResponse createChapter(ChapterRequest request);
    ChapterResponse getChapterById(Long id);
    Page<ChapterResponse> getChaptersByBook(Long bookId, Pageable pageable);
    Page<ChapterResponse> searchInBook(Long bookId, String query, Pageable pageable);
    ChapterResponse updateChapter(Long id, ChapterRequest request);
    void deleteChapter(Long id);
    ChapterResponse restoreChapter(Long id);
} 