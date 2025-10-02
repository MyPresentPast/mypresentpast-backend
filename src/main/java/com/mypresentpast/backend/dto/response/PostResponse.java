package com.mypresentpast.backend.dto.response;

import com.mypresentpast.backend.dto.LocationDto;
import com.mypresentpast.backend.dto.MediaDto;
import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.enums.Category;
import com.mypresentpast.backend.enums.PostStatus;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for post response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDate postedAt;
    private LocalDate date;
    private Boolean isByIA;
    private Boolean isVerified;
    private UserDto author;
    private Category category;
    private LocationDto location;
    private PostStatus status;
    private List<MediaDto> media;
    private Long totalLikes;
    private Boolean isLiked;
}