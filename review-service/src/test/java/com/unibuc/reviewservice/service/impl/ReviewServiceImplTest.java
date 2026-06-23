package com.unibuc.reviewservice.service.impl;

import com.unibuc.reviewservice.client.dto.MenuItemDto;
import com.unibuc.reviewservice.dto.request.ReviewRequest;
import com.unibuc.reviewservice.dto.response.ReviewResponse;
import com.unibuc.reviewservice.model.Review;
import com.unibuc.reviewservice.repository.ReviewRepository;
import com.unibuc.reviewservice.security.AuthenticatedUser;
import com.unibuc.reviewservice.service.RemoteDataResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private RemoteDataResolver remoteDataResolver;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(Long userId) {
        AuthenticatedUser principal = new AuthenticatedUser(userId, "user@mail.com");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of()));
    }

    @Test
    void create_capturesUserAndMenuItemSnapshots() {
        authenticateAs(7L);
        ReviewRequest request = new ReviewRequest(100L, 5, "Excellent!");
        when(remoteDataResolver.requireMenuItem(100L))
                .thenReturn(MenuItemDto.builder().id(100L).storeId(1L).name("Margherita").build());
        when(remoteDataResolver.resolveUserFullName(7L)).thenReturn("Jane Roe");
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review r = invocation.getArgument(0);
            r.setId(3L);
            return r;
        });

        ReviewResponse response = reviewService.create(request);

        assertEquals(3L, response.getId());
        assertEquals("Jane Roe", response.getUserFullName());
        assertEquals("Margherita", response.getMenuItemName());
        assertEquals(5, response.getRating());
    }

    @Test
    void update_deniesNonAuthor() {
        authenticateAs(7L);
        Review existing = Review.builder().id(3L).userId(99L).menuItemId(100L).rating(4).build();
        when(reviewRepository.findById(3L)).thenReturn(Optional.of(existing));

        assertThrows(AccessDeniedException.class,
                () -> reviewService.update(3L, new ReviewRequest(100L, 2, "changed")));
    }
}
