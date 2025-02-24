package org.example.echo01.common.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.echo01.auth.entities.User;
import org.example.echo01.common.dto.request.ChapterReactionRequest;
import org.example.echo01.common.dto.response.ChapterReactionResponse;
import org.example.echo01.common.entities.Chapter;
import org.example.echo01.common.entities.ChapterReaction;
import org.example.echo01.common.mappers.ChapterReactionMapper;
import org.example.echo01.common.repositories.ChapterReactionRepository;
import org.example.echo01.common.repositories.ChapterRepository;
import org.example.echo01.common.services.IChapterReactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterReactionServiceImpl implements IChapterReactionService {
    private final ChapterReactionRepository reactionRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterReactionMapper reactionMapper;

    @Override
    @Transactional
    public ChapterReactionResponse addReaction(ChapterReactionRequest request, User currentUser) {
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        reactionRepository.findByChapterIdAndUserId(chapter.getId(), currentUser.getId())
                .ifPresent(reaction -> {
                    throw new IllegalStateException("You have already reacted to this chapter");
                });

        ChapterReaction reaction = reactionMapper.toEntity(request, chapter, currentUser);
        reaction = reactionRepository.save(reaction);
        return reactionMapper.toResponse(reaction);
    }

    @Override
    @Transactional
    public void removeReaction(Long chapterId, User currentUser) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new IllegalArgumentException("Chapter not found");
        }

        if (!reactionRepository.existsByChapterIdAndUserId(chapterId, currentUser.getId())) {
            throw new IllegalStateException("You haven't reacted to this chapter");
        }

        reactionRepository.deleteByChapterIdAndUserId(chapterId, currentUser.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getReactionCounts(Long chapterId) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new IllegalArgumentException("Chapter not found");
        }

        List<Object[]> results = reactionRepository.countReactionsByType(chapterId);
        return results.stream()
                .collect(Collectors.toMap(
                    result -> ((Enum<?>) result[0]).name(),
                    result -> ((Long) result[1]).intValue()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterReactionResponse> getChapterReactions(Long chapterId) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new IllegalArgumentException("Chapter not found");
        }

        return reactionRepository.findByChapterId(chapterId).stream()
                .map(reactionMapper::toResponse)
                .collect(Collectors.toList());
    }
} 