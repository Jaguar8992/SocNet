package main.controller;

import main.api.request.TagRequest;
import main.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
public class ApiTagController {

    private final TagService tagService;

    @Autowired
    public ApiTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<?> getTags(@RequestParam(defaultValue = "") String tag,
                                     @RequestParam(defaultValue = "0") Integer offset,
                                     @RequestParam(defaultValue = "20") Integer itemPerPage) {
        return tagService.getTags(offset, itemPerPage, tag);
    }

    @PostMapping
    public ResponseEntity<?> postTag(@RequestBody TagRequest tag) {
        return tagService.postTag(tag.getTag());
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTag(@RequestParam Integer id) {
        return tagService.deleteTag(id);
    }
}
