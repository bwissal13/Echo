package com.readify.api.user.controller;

import com.readify.api.user.dto.AuthorPublicDTO;
import com.readify.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    @GetMapping("/authors")
    public ResponseEntity<Page<AuthorPublicDTO>> getAllAuthors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sort)));
        Page<AuthorPublicDTO> authors = userService.findAllAuthors(pageable);
        return ResponseEntity.ok(authors);
    }
    
    private Sort.Order[] createSortOrder(String[] sort) {
        Sort.Order[] orders = new Sort.Order[sort.length];
        for (int i = 0; i < sort.length; i++) {
            String[] parts = sort[i].split(",");
            orders[i] = new Sort.Order(
                parts.length > 1 && parts[1].equalsIgnoreCase("desc") ? 
                    Sort.Direction.DESC : Sort.Direction.ASC,
                parts[0]
            );
        }
        return orders;
    }
} 