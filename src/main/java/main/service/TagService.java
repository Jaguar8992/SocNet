package main.service;

import main.api.dto.*;
import main.api.response.error.ErrorResponse;
import main.api.response.tags.TagsResponse;
import main.model.entity.Tag;
import main.model.repository.TagRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Optional;


@Service
public class TagService {

    private final TagRepository tagRepository;
    private final UserService userService;
    private Logger log = Logger.getLogger(TagService.class.getName());

    @Autowired
    public TagService(TagRepository tagRepository, UserService userService) {
        this.tagRepository = tagRepository;
        this.userService = userService;
    }

    public ResponseEntity<?> getTags(int offset, int perPage, String tag) {
        try {
            userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Page<Tag> tags = tagRepository.getTags(tag, PageRequest.of(offset / perPage, perPage));
        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();
        HashMap<Integer, String> tagList = new HashMap<>();
        tags.forEach(tagItem -> {
            tagList.put(tagItem.getId(), tagItem.getName());
        });

        return ResponseEntity.ok(new TagsResponse("string",
                timestamp,
                tags.getTotalElements(),
                offset,
                perPage,
                tagList));
    }

    public ResponseEntity<?> postTag(String tagName) {
        try {
            userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        Tag tag;
        Optional<Tag> optionalTag = tagRepository.findTagByName(tagName);
        tag = optionalTag.orElseGet(() -> createTag(tagName));
        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();

        return ResponseEntity.ok(new DTOSuccessfully("string",
                timestamp,
                new DTOTag(tag.getId(), tag.getName())));
    }

    public ResponseEntity<?> deleteTag(int id) {
        try {
            userService.getCurrentUser();
        } catch (UsernameNotFoundException ex) {
            log.error(DTOError.UNAUTHORIZED.get());
            return ResponseEntity.status(401).body(
                    new ErrorResponse(DTOError.UNAUTHORIZED.get(), DTOErrorDescription.UNAUTHORIZED.get()));
        }

        long timestamp = LocalDateTime.now().atZone(ZoneId.of("Europe/Moscow")).toEpochSecond();
        Optional<Tag> optionalTag = tagRepository.findById(id);
        optionalTag.ifPresent(tagRepository::delete);

        return ResponseEntity.ok(new DTOSuccessfully(
                "string",
                timestamp,
                new DTOMessage()));
    }

    private Tag createTag(String tagName) {
        Tag newTag = new Tag();
        newTag.setName(tagName);
        tagRepository.save(newTag);

        return newTag;
    }
}
