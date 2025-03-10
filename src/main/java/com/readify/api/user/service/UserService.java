package com.readify.api.user.service;

import com.readify.api.user.dto.AuthorPublicDTO;
import com.readify.api.user.entity.User;
import com.readify.api.user.enums.Role;
import com.readify.api.user.repository.UserRepository;
import com.readify.api.book.repository.BookRepository;
import com.readify.api.follower.repository.FollowerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final FollowerRepository followerRepository;
    
    public Page<AuthorPublicDTO> findAllAuthors(Pageable pageable) {
        return userRepository.findAllByRoleAndEnabled(Role.AUTHOR, true, pageable)
            .map(this::toAuthorPublicDTO);
    }
    
    private AuthorPublicDTO toAuthorPublicDTO(User user) {
        return AuthorPublicDTO.builder()
            .id(user.getId())
            .firstname(user.getFirstname())
            .lastname(user.getLastname())
            .bio(user.getBio())
            .profilePicture(user.getProfilePicture())
            .totalBooks(bookRepository.countByAuthorId(user.getId()))
            .totalFollowers(followerRepository.countByAuthorId(user.getId()))
            .build();
    }
} 