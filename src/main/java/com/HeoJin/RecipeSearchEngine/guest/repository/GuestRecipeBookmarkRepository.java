package com.HeoJin.RecipeSearchEngine.guest.repository;

import com.HeoJin.RecipeSearchEngine.guest.entity.GuestRecipeBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRecipeBookmarkRepository extends JpaRepository<GuestRecipeBookmark, Long> {
}
