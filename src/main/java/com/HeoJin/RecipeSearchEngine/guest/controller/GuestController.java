package com.HeoJin.RecipeSearchEngine.guest.controller;

import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeBookmarkRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeLikeRequest;
import com.HeoJin.RecipeSearchEngine.guest.service.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seo")
@RequiredArgsConstructor
public class GuestController {
    private final GuestService guestService;

    // 좋아요 추가
    @PostMapping("/recipe/like")
    public ResponseEntity<String> addRecipeLike(
            @RequestBody RecipeLikeRequest request,
            // 순수 문자열만 가능 한듯, service 단에서 검증만 한번 하기
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

}
