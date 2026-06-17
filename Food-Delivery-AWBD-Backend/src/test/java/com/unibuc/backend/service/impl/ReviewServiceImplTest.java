package com.unibuc.backend.service.impl;

import com.unibuc.backend.dto.request.ReviewRequest;
import com.unibuc.backend.dto.response.ReviewResponse;
import com.unibuc.backend.exception.MenuItemNotFoundException;
import com.unibuc.backend.exception.ReviewNotFoundException;
import com.unibuc.backend.model.MenuItem;
import com.unibuc.backend.model.Review;
import com.unibuc.backend.model.Store;
import com.unibuc.backend.model.User;
import com.unibuc.backend.repository.MenuItemRepository;
import com.unibuc.backend.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock ReviewRepository reviewRepository;
    @Mock MenuItemRepository menuItemRepository;

    @InjectMocks ReviewServiceImpl reviewService;

    private User author;
    private User otherUser;
    private MenuItem menuItem;

    @BeforeEach
    void setUp() {
        author    = User.builder().id(1L).fullName("Author").email("author@mail.com").password("pw").build();
        otherUser = User.builder().id(2L).fullName("Other").email("other@mail.com").password("pw").build();
        Store store = Store.builder().id(10L).name("Some Store").owner(otherUser).build();
        menuItem = MenuItem.builder().id(5L).name("Burger").price(java.math.BigDecimal.TEN)
                .isAvailable(true).store(store).build();
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(User user) {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    private Review reviewBy(User user) {
        return Review.builder().id(1L).user(user).menuItem(menuItem).rating(4).comment("Good").build();
    }


    @Test
    void findByMenuItemId_whenMenuItemNotFound_throwsMenuItemNotFoundException() {
        when(menuItemRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.findByMenuItemId(99L))
                .isInstanceOf(MenuItemNotFoundException.class);
    }

    @Test
    void findByMenuItemId_whenMenuItemExists_returnsMappedReviews() {
        Review review = reviewBy(author);
        when(menuItemRepository.existsById(5L)).thenReturn(true);
        when(reviewRepository.findByMenuItemId(5L)).thenReturn(List.of(review));

        List<ReviewResponse> result = reviewService.findByMenuItemId(5L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRating()).isEqualTo(4);
    }


    @Test
    void findById_whenReviewNotFound_throwsReviewNotFoundException() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.findById(99L))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    void findById_whenFound_returnsReviewResponse() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewBy(author)));

        ReviewResponse response = reviewService.findById(1L);

        assertThat(response.getMenuItemId()).isEqualTo(5L);
        assertThat(response.getRating()).isEqualTo(4);
    }

    @Test
    void create_whenMenuItemNotFound_throwsMenuItemNotFoundException() {
        authenticateAs(author);
        when(menuItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(new ReviewRequest(99L, 5, "Great")))
                .isInstanceOf(MenuItemNotFoundException.class);
    }

    @Test
    void create_whenValid_setsCurrentUserAsAuthorAndSaves() {
        authenticateAs(author);
        when(menuItemRepository.findById(5L)).thenReturn(Optional.of(menuItem));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            return Review.builder().id(1L).user(r.getUser()).menuItem(r.getMenuItem())
                    .rating(r.getRating()).comment(r.getComment()).build();
        });

        ReviewResponse response = reviewService.create(new ReviewRequest(5L, 5, "Cea mai buna mancare!!1!"));

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getComment()).isEqualTo("Cea mai buna mancare!!1!");
    }

    @Test
    void update_whenReviewNotFound_throwsReviewNotFoundException() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.update(99L, new ReviewRequest(5L, 3, "Ok")))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    void update_whenNotAuthor_throwsAccessDeniedException() {
        authenticateAs(otherUser);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewBy(author)));

        assertThatThrownBy(() -> reviewService.update(1L, new ReviewRequest(5L, 2, "Bad")))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void update_whenAuthor_updatesRatingAndComment() {
        authenticateAs(author);
        Review existing = reviewBy(author);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        ReviewResponse response = reviewService.update(1L, new ReviewRequest(5L, 3, "ok"));

        assertThat(response.getRating()).isEqualTo(3);
        assertThat(response.getComment()).isEqualTo("ok");
    }

    @Test
    void delete_whenReviewNotFound_throwsReviewNotFoundException() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.delete(99L))
                .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    void delete_whenNotAuthor_throwsAccessDeniedException() {
        authenticateAs(otherUser);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewBy(author)));

        assertThatThrownBy(() -> reviewService.delete(1L))
                .isInstanceOf(AccessDeniedException.class);
        verify(reviewRepository, never()).delete(any());
    }

    @Test
    void delete_whenAuthor_deletesReviewFromRepository() {
        authenticateAs(author);
        Review existing = reviewBy(author);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(existing));

        reviewService.delete(1L);

        verify(reviewRepository).delete(existing);
    }
}
