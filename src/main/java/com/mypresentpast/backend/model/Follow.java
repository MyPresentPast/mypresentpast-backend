package com.mypresentpast.backend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad representante de Follow (relación de seguimiento entre usuarios).
 */
@Entity
@Table(name = "follow",
    uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;
}