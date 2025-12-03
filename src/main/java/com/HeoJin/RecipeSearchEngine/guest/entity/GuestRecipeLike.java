package com.HeoJin.RecipeSearchEngine.guest.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "guest_recipe_like")
public class GuestRecipeLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_recipe_like_id")
    private Long id;

    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "recipe_id")
    private String recipeId;

    @Column(name = "create_at")
    private LocalDateTime createAt;
}
