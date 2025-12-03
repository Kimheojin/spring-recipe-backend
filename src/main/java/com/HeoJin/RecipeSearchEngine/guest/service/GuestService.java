package com.HeoJin.RecipeSearchEngine.guest.service;

import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeBookmarkRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeLikeRequest;
import com.HeoJin.RecipeSearchEngine.guest.entity.Guest;
import com.HeoJin.RecipeSearchEngine.guest.entity.GuestRecipeBookmark;
import com.HeoJin.RecipeSearchEngine.guest.entity.GuestRecipeLike;
import com.HeoJin.RecipeSearchEngine.guest.repository.GuestRecipeBookmarkRepository;
import com.HeoJin.RecipeSearchEngine.guest.repository.GuestRecipeLikeRepository;
import com.HeoJin.RecipeSearchEngine.guest.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRepository guestRepository;
    private final GuestRecipeLikeRepository guestRecipeLikeRepository;
    private final GuestRecipeBookmarkRepository guestRecipeBookmarkRepository;

    @Transactional
    public String addRecipeLike(RecipeLikeRequest request, String guestUuid) {
        // 검증
        GuestManager.validateLikeRequest(request, guestUuid);

        LocalDateTime currentTime = LocalDateTime.now();

        Guest guest = guestRepository.findByGuestUuid(guestUuid)
                // 비어 있는 경우
                // orElse 랑 다름
                .orElseGet(() -> guestRepository.save(
                        Guest.builder()
                                .guestUuid(guestUuid)
                                .createAt(currentTime)
                                .lastSeenAt(currentTime)
                                .build()
                ));

        guestRecipeLikeRepository.save(
                GuestRecipeLike.builder()
                        .guestId(guest.getId())
                        .recipeId(request.getRecipeId())
                        .createAt(currentTime)
                        .build()
        );

        return "hello";
    }

    public String addBookmark(RecipeBookmarkRequest request, String guestUuid) {
        // 검증
        GuestManager.validateBookmarkRequest(request, guestUuid);

        LocalDateTime currentTime = LocalDateTime.now();

        Guest guest = guestRepository.findByGuestUuid(guestUuid)
                // 비어 있는 경우
                // orElse 랑 다름
                .orElseGet(() -> guestRepository.save(
                        Guest.builder()
                                .guestUuid(guestUuid)
                                .createAt(currentTime)
                                .lastSeenAt(currentTime)
                                .build()
                ));

        guestRecipeBookmarkRepository.save(
                GuestRecipeBookmark.builder()
                        .guestId(guest.getId())
                        .recipeId(request.getRecipeId())
                        .createAt(currentTime)
                        .build()
        );

        return "hello";
    }
}
