package main.service.files;

import lombok.AllArgsConstructor;
import main.api.response.CommonResponseList;
import main.model.entity.FileInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StorageService {

    public ResponseEntity<?> response(FileInfo fileInfo) {

        return new ResponseEntity<>(
                new CommonResponseList<>("string", fileInfo), HttpStatus.OK);
    }
}
