package com.HeoJin.RecipeSearchEngine.guest.repository;

import com.HeoJin.RecipeSearchEngine.guest.entity.GuestRecipeBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRecipeBookmarkRepository extends JpaRepository<GuestRecipeBookmark, Long> {
    List<GuestRecipeBookmark> findAllByGuestIdAndRecipeIdIn(Long guestId, List<String> recipeIds);
    List<GuestRecipeBookmark> findAllByGuestId(Long guestId);
}
