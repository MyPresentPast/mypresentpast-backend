package com.mypresentpast.backend.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el estado de un post en las colecciones del usuario.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCollectionStatusResponse {

    private Long postId;
    private List<CollectionInfo> collectionsContaining;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CollectionInfo {
        private Long collectionId;
        private String collectionName;
    }
}
