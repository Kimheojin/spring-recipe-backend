package com.HeoJin.RecipeSearchEngine.guest.controller;

import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeBookmarkRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeLikeRequest;
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

    // 좋아요 추가
    @PostMapping("/recipe/like")
    public ResponseEntity<String> addRecipeLike(
            @RequestBody RecipeLikeRequest request,
            @CookieValue(value = "GUEST_UUID", required = false) String guestUuid
    ){
        String result = guestService.addRecipeLike(request, guestUuid);
        return ResponseEntity.ok(result);
    }


    // 북마크 추가
    @PostMapping("/recipe/bookmark")
    public ResponseEntity<String> addBookmark(
            @RequestBody RecipeBookmarkRequest request,
            @CookieValue(value = "GUEST_UUID", required = false ) String guestUuid
    ){
        String result = guestService.addBookmark(request, guestUuid);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recipe/status")
    public ResponseEntity<RecipeStatusListResponseDto> getStatusWithRecipeList(
            @RequestParam("recipeId") List<String> recipeIds,
            @CookieValue(value = "GUEST_UUID", required = false) String guestUuid
    ){
        RecipeStatusListResponseDto response = guestService.getStatusWithRecipeList(recipeIds, guestUuid);
        return ResponseEntity.ok(response);
    }



}
