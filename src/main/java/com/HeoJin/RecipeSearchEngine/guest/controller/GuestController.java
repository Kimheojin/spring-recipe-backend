package com.HeoJin.RecipeSearchEngine.guest.controller;

import com.HeoJin.RecipeSearchEngine.guest.dto.response.MessageResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeBookmarkRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeLikeRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeBookmarkListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeLikeListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeStatusListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seo")
@RequiredArgsConstructor
public class GuestController {
    private final GuestService guestService;

    // 좋아요 추가/취소 (토글)
    @PostMapping("/recipe/like")
    public ResponseEntity<MessageResponseDto> toggleRecipeLike(
            @RequestBody RecipeLikeRequest request,
            @CookieValue(value = "GUEST_UUID", required = false) String guestUuid
    ){
        MessageResponseDto result = guestService.toggleRecipeLike(request, guestUuid);
        return ResponseEntity.ok(result);
    }


    // 북마크 추가/취소 (토글)
    @PostMapping("/recipe/bookmark")
    public ResponseEntity<MessageResponseDto> toggleBookmark(
            @RequestBody RecipeBookmarkRequest request,
            @CookieValue(value = "GUEST_UUID", required = false ) String guestUuid
    ){
        MessageResponseDto result = guestService.toggleBookmark(request, guestUuid);
        return ResponseEntity.ok(result);
    }
    // 리스트 추가 엔드포인트 -> 리스트 -> 좋아요, 북마크 매핑
    @GetMapping("/recipe/status")
    public ResponseEntity<RecipeStatusListResponseDto> getStatusWithRecipeList(
            @RequestParam("recipeId") List<String> recipeIds,
            @CookieValue(value = "GUEST_UUID", required = false) String guestUuid
    ){
        RecipeStatusListResponseDto response = guestService.getStatusWithRecipeList(recipeIds, guestUuid);
        return ResponseEntity.ok(response);
    }
    // 좋아요 목록 반환
    @GetMapping("/recipe/likes")
    public ResponseEntity<RecipeLikeListResponseDto> getLikeList(
            @CookieValue(value = "GUEST_UUID", required = false) String guestUuid
    ){
        RecipeLikeListResponseDto likeList = guestService.getLikeList(guestUuid);
        return ResponseEntity.ok(likeList);
    }
    // 북마크 목록
    @GetMapping("/recipe/bookmark")
    public ResponseEntity<RecipeBookmarkListResponseDto> getBookmarkList(
            @CookieValue(value = "GUEST_UUID", required = false) String guestUuid
    ){
        RecipeBookmarkListResponseDto bookmarkList = guestService.getBookmarkList(guestUuid);
        return ResponseEntity.ok(bookmarkList);
    }

}
