package org.example.echo01.common.services;

import org.example.echo01.common.dto.request.AddToBookmarkRequest;
import org.example.echo01.common.dto.request.CreateBookmarkGroupRequest;
import org.example.echo01.common.dto.response.BookmarkGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBookmarkService {
    BookmarkGroupResponse createBookmarkGroup(CreateBookmarkGroupRequest request);
    Page<BookmarkGroupResponse> getBookmarkGroups(Pageable pageable);
    BookmarkGroupResponse addToBookmark(AddToBookmarkRequest request);
    void removeFromBookmark(Long groupId, Long bookId);
    void deleteBookmarkGroup(Long groupId);
} 