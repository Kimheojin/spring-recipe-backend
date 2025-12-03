package com.HeoJin.RecipeSearchEngine.guest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Builder
@Table(name = "guest_recipe_bookmark")
public class GuestRecipeBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_recipe_bookmark_id")
    private Long id;

    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "recipe_id")
    private String recipeId;

    @Column(name = "create_at")
    private LocalDateTime createAt;

}
