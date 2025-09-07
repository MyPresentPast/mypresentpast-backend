package com.mypresentpast.backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mypresentpast.backend.enums.Category;
import com.mypresentpast.backend.enums.PostStatus;
import org.junit.jupiter.api.Test;

/**
 * Tests simples para verificar que los modelos funcionan
 */
class ModelTest {

    @Test
    void user_CanBeCreated() {
        // When
        User user = new User();

        // Then
        assertNotNull(user);
    }

    @Test
    void location_CanBeCreated() {
        // When
        Location location = new Location();

        // Then
        assertNotNull(location);
    }

    @Test
    void post_CanBeCreated() {
        // When
        Post post = new Post();

        // Then
        assertNotNull(post);
    }

    @Test
    void category_HasCorrectValues() {
        // When & Then
        assertEquals(3, Category.values().length);
        assertTrue(contains(Category.values(), Category.STORY));
        assertTrue(contains(Category.values(), Category.INFORMATION));
        assertTrue(contains(Category.values(), Category.MYTH));
    }

    @Test
    void postStatus_HasCorrectValues() {
        // When & Then
        assertTrue(contains(PostStatus.values(), PostStatus.ACTIVE));
        assertTrue(contains(PostStatus.values(), PostStatus.DELETED));
    }

    @Test
    void collection_CanBeCreated() {
        // When
        Collection collection = new Collection();
        
        // Then
        assertNotNull(collection);
    }

    @Test
    void collection_CanBeBuilt() {
        // Given
        User author = new User();
        author.setId(1L);
        
        // When
        Collection collection = Collection.builder()
                .name("Test Collection")
                .description("Test Description")
                .author(author)
                .build();
        
        // Then
        assertNotNull(collection);
        assertEquals("Test Collection", collection.getName());
        assertEquals("Test Description", collection.getDescription());
        assertEquals(author, collection.getAuthor());
    }

    @Test
    void collectionPost_CanBeCreated() {
        // When
        CollectionPost collectionPost = new CollectionPost();
        
        // Then
        assertNotNull(collectionPost);
    }

    @Test
    void collectionPost_CanBeBuilt() {
        // Given
        Collection collection = new Collection();
        collection.setId(1L);
        
        Post post = new Post();
        post.setId(1L);
        
        // When
        CollectionPost collectionPost = CollectionPost.builder()
                .collection(collection)
                .post(post)
                .build();
        
        // Then
        assertNotNull(collectionPost);
        assertEquals(collection, collectionPost.getCollection());
        assertEquals(post, collectionPost.getPost());
        assertNotNull(collectionPost.getAddedAt());
    }

    private boolean contains(Object[] array, Object value) {
        for (Object item : array) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
} 