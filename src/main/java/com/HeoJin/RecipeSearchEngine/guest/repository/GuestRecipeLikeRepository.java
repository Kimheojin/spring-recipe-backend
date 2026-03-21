package com.HeoJin.RecipeSearchEngine.guest.repository;

import com.HeoJin.RecipeSearchEngine.guest.entity.GuestRecipeLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestRecipeLikeRepository extends JpaRepository<GuestRecipeLike, Long>{
    // in -> 임시테이블? 나중에 보고 ㅎ확인
    // 한번 더 확인해 보기
    List<GuestRecipeLike> findAllByGuestIdAndRecipeIdIn(Long guestId, List<String> recipeIds);
    List<GuestRecipeLike> findAllByGuestId(Long guestId);
    Optional<GuestRecipeLike> findByGuestIdAndRecipeId(Long guestId, String recipeId);
    void deleteByGuestIdAndRecipeId(Long guestId, String recipeId);
}
