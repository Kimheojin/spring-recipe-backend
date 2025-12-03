package com.HeoJin.RecipeSearchEngine.guest.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guest {

    @Id
    @Column(name = "guest_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 쿠키 저장된 UUID
    @Column(name = "guest_uuid", nullable = false, unique = true, length = 36)
    private String guestUuid;
    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;
    @Column(name = "last_seen_at" )
    private LocalDateTime lastSeenAt;
}
