package com.HeoJin.RecipeSearchEngine.guest.repository;

import com.HeoJin.RecipeSearchEngine.guest.entity.GuestRecipeBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRecipeBookmarkRepository extends JpaRepository<GuestRecipeBookmark, Long> {
    List<GuestRecipeBookmark> findAllByGuestIdAndRecipeIdIn(Long guestId, List<String> recipeIds);
    List<GuestRecipeBookmark> findAllByGuestId(Long guestId);
    Optional<GuestRecipeBookmark> findByGuestIdAndRecipeId(Long guestId, String recipeId);
    void deleteByGuestIdAndRecipeId(Long guestId, String recipeId);
}
