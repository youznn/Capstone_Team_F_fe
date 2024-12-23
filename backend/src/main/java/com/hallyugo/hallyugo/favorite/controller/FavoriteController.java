package com.hallyugo.hallyugo.favorite.controller;

import com.hallyugo.hallyugo.auth.annotation.AuthUser;
import com.hallyugo.hallyugo.favorite.domain.response.FavoriteResponseDto;
import com.hallyugo.hallyugo.favorite.service.FavoriteService;
import com.hallyugo.hallyugo.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping("/favorite")
    public ResponseEntity<FavoriteResponseDto> getUserFavorite(
            @AuthUser User user,
            @RequestParam(name = "limit", defaultValue = "2") int limit
    ) {
        FavoriteResponseDto result = favoriteService.getFavoritesByUser(user, limit);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/favorite/on?location_id={locationId}")
    public ResponseEntity<Void> increaseFavoriteCount(
            @AuthUser User user,
            @PathVariable Long locationId
    ) {
        favoriteService.increaseFavoriteCountAndSave(user, locationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorite/off?location_id={locationId}")
    public ResponseEntity<Void> decreaseFavoriteCount(
            @AuthUser User user,
            @PathVariable Long locationId
    ) {
        favoriteService.decreaseFavoriteCountAndDelete(user, locationId);
        return ResponseEntity.ok().build();
    }
}
