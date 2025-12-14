package com.HeoJin.RecipeSearchEngine.guest.service;

import com.HeoJin.RecipeSearchEngine.global.exception.AuthException;
import com.HeoJin.RecipeSearchEngine.global.exception.ErrorCode.EnumErrorCode;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeBookmarkRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeLikeRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeBookmarkListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeLikeListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeStatusDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeStatusListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.entity.Guest;
import com.HeoJin.RecipeSearchEngine.guest.entity.GuestRecipeBookmark;
import com.HeoJin.RecipeSearchEngine.guest.entity.GuestRecipeLike;
import com.HeoJin.RecipeSearchEngine.guest.repository.GuestRecipeBookmarkRepository;
import com.HeoJin.RecipeSearchEngine.guest.repository.GuestRecipeLikeRepository;
import com.HeoJin.RecipeSearchEngine.guest.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Transactional
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

    public RecipeStatusListResponseDto getStatusWithRecipeList(List<String> recipeIds, String guestUuid) {
        if (recipeIds == null || recipeIds.isEmpty()) {
            return new RecipeStatusListResponseDto(Collections.emptyList());
        }
        if (!StringUtils.hasText(guestUuid)) {
            throw new AuthException(EnumErrorCode.FORBIDDEN_ACTION, "쿠키가 존재하지 않습니다.");
        }

        Guest guest = guestRepository.findByGuestUuid(guestUuid)
                .orElseGet(
                        null
                        // 저장할 필요가 있나
                );

        if (guest == null) {
            List<RecipeStatusDto> defaultStatus = recipeIds.stream()
                    .map(recipeId -> new RecipeStatusDto(recipeId, false, false))
                    .toList();
            return new RecipeStatusListResponseDto(defaultStatus);
        }

        List<GuestRecipeLike> likedRecipes = guestRecipeLikeRepository
                .findAllByGuestIdAndRecipeIdIn(guest.getId(), recipeIds);

        Set<String> likedRecipeIds = likedRecipes.stream()
                .map(GuestRecipeLike::getRecipeId)
                .collect(Collectors.toSet());

        List<GuestRecipeBookmark> bookmarkedRecipes = guestRecipeBookmarkRepository
                .findAllByGuestIdAndRecipeIdIn(guest.getId(), recipeIds);

        Set<String> bookmarkedRecipeIds = bookmarkedRecipes.stream()
                .map(GuestRecipeBookmark::getRecipeId)
                .collect(Collectors.toSet());

        List<RecipeStatusDto> statuses = recipeIds.stream()
                .map(recipeId -> new RecipeStatusDto(
                        recipeId,
                        likedRecipeIds.contains(recipeId),
                        bookmarkedRecipeIds.contains(recipeId)
                ))
                .toList();

        return new RecipeStatusListResponseDto(statuses);
    }

    public RecipeLikeListResponseDto getLikeList(String guestUuid) {
        validateGuestUuid(guestUuid);

        Guest guest = guestRepository.findByGuestUuid(guestUuid)
                .orElse(null);

        if (guest == null) {
            return new RecipeLikeListResponseDto(Collections.emptyList());
        }

        List<String> recipeIds = guestRecipeLikeRepository.findAllByGuestId(guest.getId())
                .stream()
                .map(GuestRecipeLike::getRecipeId)
                .toList();

        return new RecipeLikeListResponseDto(recipeIds);
    }
    public RecipeBookmarkListResponseDto getBookmarkList(String guestUuid) {
        validateGuestUuid(guestUuid);

        Guest guest = guestRepository.findByGuestUuid(guestUuid)
                .orElse(null);

        if (guest == null) {
            return new RecipeBookmarkListResponseDto(Collections.emptyList());
        }

        List<String> recipeIds = guestRecipeBookmarkRepository.findAllByGuestId(guest.getId())
                .stream()
                .map(GuestRecipeBookmark::getRecipeId)
                .toList();

        return new RecipeBookmarkListResponseDto(recipeIds);
    }

    private void validateGuestUuid(String guestUuid) {
        if (!StringUtils.hasText(guestUuid)) {
            throw new AuthException(EnumErrorCode.FORBIDDEN_ACTION, "쿠키가 존재하지 않습니다.");
        }
    }
}
