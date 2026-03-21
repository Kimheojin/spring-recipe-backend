package com.HeoJin.RecipeSearchEngine.guest.service;

import com.HeoJin.RecipeSearchEngine.guest.dto.response.MessageResponseDto;
import com.HeoJin.RecipeSearchEngine.global.exception.AuthException;
import com.HeoJin.RecipeSearchEngine.global.exception.BusinessException;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeBookmarkRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.request.RecipeLikeRequest;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeBookmarkListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeLikeListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.dto.response.RecipeStatusListResponseDto;
import com.HeoJin.RecipeSearchEngine.guest.entity.Guest;
import com.HeoJin.RecipeSearchEngine.guest.entity.GuestRecipeBookmark;
import com.HeoJin.RecipeSearchEngine.guest.entity.GuestRecipeLike;
import com.HeoJin.RecipeSearchEngine.guest.repository.GuestRecipeBookmarkRepository;
import com.HeoJin.RecipeSearchEngine.guest.repository.GuestRecipeLikeRepository;
import com.HeoJin.RecipeSearchEngine.guest.repository.GuestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceTest {

    @InjectMocks
    private GuestService guestService;
    @Mock
    private GuestRepository guestRepository;
    @Mock
    private GuestRecipeLikeRepository guestRecipeLikeRepository;
    @Mock
    private GuestRecipeBookmarkRepository guestRecipeBookmarkRepository;

    private final String GUEST_UUID = "test-guest-uuid";
    private final String RECIPE_ID = "test-recipe-id";

    @Test
    @DisplayName("좋아요 토글 - 추가 성공 (기존 없음)")
    void test1() {
        // given
        RecipeLikeRequest request = RecipeLikeRequest.builder().recipeId(RECIPE_ID).build();
        Guest guest = Guest.builder().id(1L).guestUuid(GUEST_UUID).build();

        when(guestRepository.findByGuestUuid(GUEST_UUID)).thenReturn(Optional.of(guest));
        when(guestRecipeLikeRepository.findByGuestIdAndRecipeId(1L, RECIPE_ID)).thenReturn(Optional.empty());

        // when
        MessageResponseDto result = guestService.toggleRecipeLike(request, GUEST_UUID);

        // then
        assertThat(result.message()).isEqualTo("좋아요 목록에 추가되었습니다.");
        verify(guestRecipeLikeRepository, times(1)).save(any(GuestRecipeLike.class));
        verify(guestRecipeLikeRepository, never()).deleteByGuestIdAndRecipeId(any(), any());
    }

    @Test
    @DisplayName("좋아요 토글 - 취소 성공 (기존 있음)")
    void test2() {
        // given
        RecipeLikeRequest request = RecipeLikeRequest.builder().recipeId(RECIPE_ID).build();
        Guest guest = Guest.builder().id(1L).guestUuid(GUEST_UUID).build();

        when(guestRepository.findByGuestUuid(GUEST_UUID)).thenReturn(Optional.of(guest));
        when(guestRecipeLikeRepository.findByGuestIdAndRecipeId(1L, RECIPE_ID))
                .thenReturn(Optional.of(GuestRecipeLike.builder().build()));

        // when
        MessageResponseDto result = guestService.toggleRecipeLike(request, GUEST_UUID);

        // then
        assertThat(result.message()).isEqualTo("좋아요가 취소되었습니다.");
        verify(guestRecipeLikeRepository, never()).save(any(GuestRecipeLike.class));
        verify(guestRecipeLikeRepository, times(1)).deleteByGuestIdAndRecipeId(1L, RECIPE_ID);
    }

    @Test
    @DisplayName("좋아요 토글 - 신규 게스트 생성 후 추가")
    void test3() {
        // given
        RecipeLikeRequest request = RecipeLikeRequest.builder().recipeId(RECIPE_ID).build();
        Guest newGuest = Guest.builder().id(1L).guestUuid(GUEST_UUID).build();

        when(guestRepository.findByGuestUuid(GUEST_UUID)).thenReturn(Optional.empty());
        when(guestRepository.save(any(Guest.class))).thenReturn(newGuest);
        when(guestRecipeLikeRepository.findByGuestIdAndRecipeId(1L, RECIPE_ID)).thenReturn(Optional.empty());

        // when
        MessageResponseDto result = guestService.toggleRecipeLike(request, GUEST_UUID);

        // then
        assertThat(result.message()).isEqualTo("좋아요 목록에 추가되었습니다.");
        verify(guestRepository, times(1)).save(any(Guest.class));
        verify(guestRecipeLikeRepository, times(1)).save(any(GuestRecipeLike.class));
    }

    @Test
    @DisplayName("좋아요 실패 - 요청값 검증 실패 (레시피 ID 없음)")
    void test4() {
        // given
        RecipeLikeRequest request = RecipeLikeRequest.builder().build(); // id null

        // when + then
        assertThatThrownBy(() -> guestService.toggleRecipeLike(request, GUEST_UUID))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("북마크 토글 - 추가 성공 (기존 없음)")
    void test5() {
        // given
        RecipeBookmarkRequest request = RecipeBookmarkRequest.builder().recipeId(RECIPE_ID).build();
        Guest guest = Guest.builder().id(1L).guestUuid(GUEST_UUID).build();

        when(guestRepository.findByGuestUuid(GUEST_UUID)).thenReturn(Optional.of(guest));
        when(guestRecipeBookmarkRepository.findByGuestIdAndRecipeId(1L, RECIPE_ID)).thenReturn(Optional.empty());

        // when
        MessageResponseDto result = guestService.toggleBookmark(request, GUEST_UUID);

        // then
        assertThat(result.message()).isEqualTo("북마크 목록에 추가되었습니다.");
        verify(guestRecipeBookmarkRepository, times(1)).save(any(GuestRecipeBookmark.class));
    }

    @Test
    @DisplayName("북마크 토글 - 취소 성공 (기존 있음)")
    void test6_toggle() {
        // given
        RecipeBookmarkRequest request = RecipeBookmarkRequest.builder().recipeId(RECIPE_ID).build();
        Guest guest = Guest.builder().id(1L).guestUuid(GUEST_UUID).build();

        when(guestRepository.findByGuestUuid(GUEST_UUID)).thenReturn(Optional.of(guest));
        when(guestRecipeBookmarkRepository.findByGuestIdAndRecipeId(1L, RECIPE_ID))
                .thenReturn(Optional.of(GuestRecipeBookmark.builder().build()));

        // when
        MessageResponseDto result = guestService.toggleBookmark(request, GUEST_UUID);

        // then
        assertThat(result.message()).isEqualTo("북마크가 취소되었습니다.");
        verify(guestRecipeBookmarkRepository, times(1)).deleteByGuestIdAndRecipeId(1L, RECIPE_ID);
    }

    @Test
    @DisplayName("레시피 상태 리스트 조회 - 성공 (좋아요/북마크 섞임)")
    void test10() {
        // given
        List<String> recipeIds = Arrays.asList("recipe-1", "recipe-2", "recipe-3");
        Guest guest = Guest.builder().id(1L).guestUuid(GUEST_UUID).build();

        when(guestRepository.findByGuestUuid(GUEST_UUID)).thenReturn(Optional.of(guest));

        when(guestRecipeLikeRepository.findAllByGuestIdAndRecipeIdIn(eq(1L), anyList()))
                .thenReturn(Collections.singletonList(
                        GuestRecipeLike.builder().recipeId("recipe-1").build()
                ));

        when(guestRecipeBookmarkRepository.findAllByGuestIdAndRecipeIdIn(eq(1L), anyList()))
                .thenReturn(Collections.singletonList(
                        GuestRecipeBookmark.builder().recipeId("recipe-2").build()
                ));

        // when
        RecipeStatusListResponseDto response = guestService.getStatusWithRecipeList(recipeIds, GUEST_UUID);

        // then
        assertThat(response.recipeStatuses()).hasSize(3);
        

        assertThat(response.recipeStatuses().get(0).getRecipeId()).isEqualTo("recipe-1");
        assertThat(response.recipeStatuses().get(0).isLiked()).isTrue();
        assertThat(response.recipeStatuses().get(0).isBookmarked()).isFalse();

        assertThat(response.recipeStatuses().get(1).getRecipeId()).isEqualTo("recipe-2");
        assertThat(response.recipeStatuses().get(1).isLiked()).isFalse();
        assertThat(response.recipeStatuses().get(1).isBookmarked()).isTrue();


        assertThat(response.recipeStatuses().get(2).getRecipeId()).isEqualTo("recipe-3");
        assertThat(response.recipeStatuses().get(2).isLiked()).isFalse();
        assertThat(response.recipeStatuses().get(2).isBookmarked()).isFalse();
    }

    @Test
    @DisplayName("레시피 상태 리스트 조회 - 해당 쿠키 존재 X")
    void test7() {
        // given
        List<String> recipeIds = Arrays.asList("recipe-1");
        when(guestRepository.findByGuestUuid(GUEST_UUID)).thenReturn(Optional.empty());

        // when
        RecipeStatusListResponseDto response = guestService.getStatusWithRecipeList(recipeIds, GUEST_UUID);

        // then
        assertThat(response.recipeStatuses()).hasSize(1);
        assertThat(response.recipeStatuses().get(0).isLiked()).isFalse();
        assertThat(response.recipeStatuses().get(0).isBookmarked()).isFalse();
    }

    @Test
    @DisplayName("레시피 상태 리스트 조회 - 쿠키 없음 (예외)")
    void test8() {
        assertThatThrownBy(() -> guestService.getStatusWithRecipeList(Arrays.asList("id"), null))
                .isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("좋아요 목록 조회 - 성공")
    void test9() {
        // given
        Guest guest = Guest.builder().id(1L).guestUuid(GUEST_UUID).build();
        when(guestRepository.findByGuestUuid(GUEST_UUID)).thenReturn(Optional.of(guest));
        when(guestRecipeLikeRepository.findAllByGuestId(1L)).thenReturn(Arrays.asList(
                GuestRecipeLike.builder().recipeId("r1").build(),
                GuestRecipeLike.builder().recipeId("r2").build()
        ));

        // when
        RecipeLikeListResponseDto response = guestService.getLikeList(GUEST_UUID);

        // then
        assertThat(response.recipeIds()).containsExactly("r1", "r2");
    }

    @Test
    @DisplayName("북마크 목록 조회 - 성공")
    void test11() {
        // given
        Guest guest = Guest.builder().id(1L).guestUuid(GUEST_UUID).build();
        when(guestRepository.findByGuestUuid(GUEST_UUID)).thenReturn(Optional.of(guest));
        when(guestRecipeBookmarkRepository.findAllByGuestId(1L)).thenReturn(Arrays.asList(
                GuestRecipeBookmark.builder().recipeId("b1").build()
        ));

        // when
        RecipeBookmarkListResponseDto response = guestService.getBookmarkList(GUEST_UUID);

        // then
        assertThat(response.recipeIds()).containsExactly("b1");
    }

    @Test
    @DisplayName("좋아요 목록 조회 - 쿠키 없음 (예외)")
    void test12() {
        assertThatThrownBy(() -> guestService.getLikeList(""))
                .isInstanceOf(AuthException.class);
    }
}
