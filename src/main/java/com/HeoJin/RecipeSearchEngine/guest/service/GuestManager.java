package com.HeoJin.RecipeSearchEngine.guest.service;

import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeBookmarkRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeLikeRequest;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

// 생성자 막음
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GuestManager {

    // dto 상속 처리? 해서 코드 줄이는 게 맞나

    public static void validateLikeRequest(RecipeLikeRequest request, String guestUuid) {
        if (request == null || !StringUtils.hasText(request.getRecipeId())) {
            // 나중에 CustomException 으로 수정하기
            throw new IllegalArgumentException("레시피 ID는 필수 값입니다.");
        }
        if (!StringUtils.hasText(guestUuid)) {
            throw new IllegalStateException("식별 쿠키가 존재하지 않습니다.");
        }
    }


    public static void validateBookmarkRequest(RecipeBookmarkRequest request, String guestUuid) {
        if (request == null || !StringUtils.hasText(request.getRecipeId())) {
            // 나중에 CustomException 으로 수정하기
            throw new IllegalArgumentException("레시피 ID는 필수 값입니다.");
        }
        if (!StringUtils.hasText(guestUuid)) {
            //
            throw new IllegalStateException("식별 쿠키가 존재하지 않습니다.");
        }
    }
}
