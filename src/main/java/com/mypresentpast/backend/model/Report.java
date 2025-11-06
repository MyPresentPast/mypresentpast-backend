package com.mypresentpast.backend.model;

import com.mypresentpast.backend.enums.ReportStatus;
import com.mypresentpast.backend.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports", uniqueConstraints = {
        // Asegura que un usuario (reporter_id) solo pueda reportar el mismo post (post_id) una vez.
        @UniqueConstraint(columnNames = {"post_id", "reporter_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Publicación reportada. Relación ManyToOne con la entidad Post.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * Usuario que realiza el reporte.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    /**
     * Motivo/razón del reporte provisto por el usuario.
     */
    @Column(nullable = false)
    private String reason;

    /**
     * Tipo de reporte: SPAM, ABUSE, COPYRIGHT, MISINFORMATION, OTHER.
     * Se persiste como string para mayor legibilidad en la BD.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ReportType type;

    /**
     * Estado del reporte en el flujo de revisión.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    /**
     * Fecha y hora de creación del reporte. Se establece automáticamente en persist.
     */
    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Se encarga de ser un inicializador para los valores por defecto de las columnas.
    @PrePersist
    private void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = ReportStatus.PENDING;
        }
    }
}
