package com.mypresentpast.backend.controller.impl;

import com.mypresentpast.backend.controller.FollowController;
import com.mypresentpast.backend.dto.UserDto;
import com.mypresentpast.backend.dto.response.ApiResponse;
import com.mypresentpast.backend.dto.response.FollowStatsResponse;
import com.mypresentpast.backend.service.FollowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Implementación del controlador de Follow.
 */
@RestController
@RequiredArgsConstructor
public class FollowControllerImpl implements FollowController {

    private final FollowService followService;

    @Override
    public ResponseEntity<ApiResponse> follow(Long followeeId) {
        ApiResponse response = followService.follow(followeeId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ApiResponse> unfollow(Long followeeId) {
        ApiResponse response = followService.unfollow(followeeId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<UserDto>> getMyFollowing() {
        List<UserDto> following = followService.getMyFollowing();
        return ResponseEntity.ok(following);
    }

    @Override
    public ResponseEntity<List<UserDto>> getMyFollowers() {
        List<UserDto> followers = followService.getMyFollowers();
        return ResponseEntity.ok(followers);
    }

    @Override
    public ResponseEntity<List<UserDto>> getFollowingByUserId(Long userId) {
        List<UserDto> following = followService.getFollowingByUserId(userId);
        return ResponseEntity.ok(following);

    }

    @Override
    public ResponseEntity<List<UserDto>> getFollowersByUserId(Long userId) {
        List<UserDto> followers = followService.getFollowersByUserId(userId);
        return ResponseEntity.ok(followers);
    }

    @Override
    public ResponseEntity<Boolean> isFollowing(Long userId) {
        Boolean isFollowing = followService.isFollowing(userId);
        return ResponseEntity.ok(isFollowing);
    }

    @Override
    public ResponseEntity<FollowStatsResponse> getFollowStats(Long userId) {
        FollowStatsResponse stats = followService.getFollowStats(userId);
        return ResponseEntity.ok(stats);
    }
}
