package org.example.echo01.common.services;

import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.ChapterReactionRequest;
import org.example.echo01.common.dto.response.ChapterReactionResponse;

import java.util.List;
import java.util.Map;

public interface IChapterReactionService {
    ChapterReactionResponse addReaction(ChapterReactionRequest request, User currentUser);
    void removeReaction(Long chapterId, User currentUser);
    Map<String, Integer> getReactionCounts(Long chapterId);
    List<ChapterReactionResponse> getChapterReactions(Long chapterId);
} 