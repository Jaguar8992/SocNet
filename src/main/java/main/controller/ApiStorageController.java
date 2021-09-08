package main.controller;

import main.model.entity.FileInfo;
import main.service.files.FileService;
import main.service.files.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class ApiStorageController {
    private final FileService fileService;
    private final StorageService storageService;

    public ApiStorageController(FileService fileService,
                                StorageService storageService) {
        this.fileService = fileService;
        this.storageService = storageService;
    }

    @PostMapping(value = "/storage", consumes = {"multipart/form-data"})
    public ResponseEntity<?> toStoreFile(@RequestParam String type, @RequestPart("file") MultipartFile imageAvatar) throws IOException {
        System.out.println("_________________________________________________");
        System.out.println("STORAGE is ACTIVE - " + type);
        System.out.println("imageAvatar.getName() - " + imageAvatar.getName());
        System.out.println("imageAvatar.getContentType() - " + imageAvatar.getContentType());
        System.out.println("imageAvatar.getBytes() - " + imageAvatar.getBytes());
        System.out.println("imageAvatar.getOriginalFilename() - " + imageAvatar.getOriginalFilename());
        System.out.println("imageAvatar.getSize() - " + imageAvatar.getSize());
        System.out.println("imageAvatar.toString() - " + imageAvatar.toString());
        System.out.println("_________________________________________________");

        FileInfo fileInfo = fileService.upload(imageAvatar);
        fileInfo.setFileType(type);

        return storageService.response(fileInfo);
    }

}
