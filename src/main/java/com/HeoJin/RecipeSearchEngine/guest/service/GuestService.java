package com.HeoJin.RecipeSearchEngine.guest.service;

import com.HeoJin.RecipeSearchEngine.guest.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final GuestRepository guestRepository;
}

