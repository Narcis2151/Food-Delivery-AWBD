package com.unibuc.reviewservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Author is a user owned by the auth-service; id + name snapshot.
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_full_name")
    private String userFullName;

    // Menu item is owned by the stores-service; id + name snapshot.
    @Column(name = "menu_item_id", nullable = false)
    private Long menuItemId;

    @Column(name = "menu_item_name")
    private String menuItemName;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "text")
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
