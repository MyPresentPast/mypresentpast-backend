package com.mypresentpast.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad que representa la verificación de un post por parte de una institución.
 * Una institución puede verificar posts de otros usuarios (no propios).
 * Solo puede haber una verificación activa por post.
 */
@Entity
@Table(name = "post_verification")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_id", nullable = false)
    private User verifiedBy;

    @Column(name = "verified_at", nullable = false)
    private LocalDateTime verifiedAt;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
