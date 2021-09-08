package main.service.files;

import main.model.entity.FileInfo;
import main.model.entity.User;
import main.model.repository.FileInfoRepository;
import main.model.repository.UserRepository;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Locale;

@Service
public class FileService {

    private final String PATH_FILE = "profile_avatars/";
    private final UserService userService;
    private final FileInfoRepository fileInfoRepository;
    private final UserRepository userRepository;
    private final String URL;

    public FileService(UserService userService, FileInfoRepository fileInfoRepository,
                       UserRepository userRepository,
                       @Value("${application.server.host}") String url) {
        this.userService = userService;
        this.fileInfoRepository = fileInfoRepository;
        this.userRepository = userRepository;
        this.URL = url;
    }

    private String generateFolderNameFromUserData() {
        User user = userService.getCurrentUser();
        System.out.println("FIleServiceImpl - getFolderNameFromUserData = " + user.getId() + "_" + user.getFirstName() + "_" + user.getLastName());

        return user.getId() + "_" + user.getFirstName() + "_" + user.getLastName();
    }

    private boolean isFormatCorrect(MultipartFile resource) {
        if (!resource.isEmpty() && resource.getContentType() != null) {
            if (resource.getContentType().contains("jpeg")
                    || resource.getContentType().contains("jpg")
                    || resource.getContentType().contains("png")) {
                return true;
            }
        }
        System.out.println("format is not correct");
        return false;
    }

    @Transactional(rollbackFor = {IOException.class})
    public FileInfo upload(MultipartFile externalFile) throws IOException {
        if (!isFormatCorrect(externalFile)) {
            System.out.println("Неправильный формат изобращения");
            throw new IOException();
        }

        String folderName = generateFolderNameFromUserData();
        File folder = new File("profile_avatars/" + folderName);
        if (!folder.exists()) {
            System.out.println("Created new folder - " + folder.getAbsolutePath());
            Files.createDirectories(Path.of(folder.getPath()));
        }

        String fullFolderName = folder.getAbsolutePath();

        String generatedFileName = toHexString(externalFile.getBytes());
        StringBuilder hashPath = new StringBuilder();

        for (int i = 0; i < 3; i++) {
            hashPath.append(generatedFileName, 20 + i, 70 + i);
        }

        String fileName = externalFile.getOriginalFilename().toLowerCase(Locale.ROOT)
                .substring(externalFile.getOriginalFilename().lastIndexOf('.'), externalFile.getOriginalFilename().length());

        String url = URL + "/" + folder.getPath() + "/" + hashPath + fileName;


        FileInfo createdFile = FileInfo.builder()
                .ownerId(userService.getCurrentUser())
                .hashFile(hashPath.toString())
                .fileName(externalFile.getOriginalFilename())
                .relativeFilePath(url)
                .rawFileURL(fullFolderName + "\\" + generateFolderNameFromUserData())
                .fileFormat(externalFile.getContentType())
                .bytes(externalFile.getSize())
                .fileType("IMAGE")
                .createdAt(LocalDate.now())
                .build();

        var fileInfoOptional = fileInfoRepository.getFileByHashAndUser(hashPath.toString(), userService.getCurrentUser().getId());
        System.out.println("----------- File created ------------");
        if (fileInfoOptional.isEmpty()) {
            fileInfoRepository.save(createdFile);
            userRepository.updateUserPhoto(url.replace('\\', '/'), userService.getCurrentUser().getId());

            createFile(externalFile.getBytes(), hashPath + fileName);

        }
        return createdFile;
    }

    public void createFile(byte[] resource, String keyName) throws IOException {

        Path path = Paths.get(PATH_FILE + generateFolderNameFromUserData(), keyName);
        System.out.println("FIleManager - DIRECTORY_PATH = " + PATH_FILE + " keyName = " + keyName);
        if (Files.exists(path)) {
            return;
        }

        Path file = Files.createFile(path);

        try (FileOutputStream stream = new FileOutputStream(file.toString())) {
            stream.write(resource);
        }
    }

    private String toHexString(byte[] hash) {
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 32) {
            hexString.append('0');
        }
        return hexString.toString().replaceAll("[0-9]+", "");
    }
}
