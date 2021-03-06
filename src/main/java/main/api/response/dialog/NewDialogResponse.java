package main.api.response.dialog;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.api.dto.DTO;

@Data
@AllArgsConstructor
public class NewDialogResponse {

    private String error;
    private long timestamp;
    private DTO data;
 }
