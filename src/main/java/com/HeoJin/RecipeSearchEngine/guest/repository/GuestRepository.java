package com.HeoJin.RecipeSearchEngine.guest.repository;


import com.HeoJin.RecipeSearchEngine.guest.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest,Long> {
}
