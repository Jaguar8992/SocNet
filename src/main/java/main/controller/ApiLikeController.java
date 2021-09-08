package main.controller;

import main.api.request.LikeRequest;
import main.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ApiLikeController {

    private final LikeService likeService;

    public ApiLikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping("/liked")
    public ResponseEntity<?> getLiked(@RequestParam(name = "user_id") Integer userId,
                                      @RequestParam(name = "item_id") Integer itemId,
                                      @RequestParam String type) {
        return likeService.getLiked(userId, itemId, type);
    }

    @GetMapping("/likes")
    public ResponseEntity<?> getLike(@RequestParam(name = "item_id") Integer itemId, @RequestParam String type) {
        return likeService.getLikes(itemId, type);
    }

    @PutMapping("/likes")
    public ResponseEntity<?> putLike(@RequestBody LikeRequest likeRequest) {
        return likeService.putLike(likeRequest.getItemId(), likeRequest.getType());
    }

    @DeleteMapping("/likes")
    public ResponseEntity<?> deleteLike(@RequestParam(name = "item_id") Integer itemId, @RequestParam String type) {
        return likeService.deleteLike(itemId, type);
    }
}
